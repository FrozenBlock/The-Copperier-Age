/*
 * Copyright 2026 FrozenBlock
 * This file is part of The Copperier Age.
 *
 * This program is free software; you can modify it under
 * the terms of version 1 of the FrozenBlock Modding Oasis License
 * as published by FrozenBlock Modding Oasis.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * FrozenBlock Modding Oasis License for more details.
 *
 * You should have received a copy of the FrozenBlock Modding Oasis License
 * along with this program; if not, see <https://github.com/FrozenBlock/Licenses>.
 */

package net.frozenblock.thecopperierage.entity.coupling;

import net.frozenblock.thecopperierage.registry.TCAAttachments;
import net.frozenblock.thecopperierage.registry.TCAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;

public class MinecartCouplingUtil {
	private static final int MAX_COUPLING_DISTANCE = 3;
	private static final float MIN_COUPLING_LENGTH = 1.5F;
	private static final float COUPLING_WIGGLE_ROOM = 0.2F;
	private static final float TARGET_COUPLING_LENGTH = MIN_COUPLING_LENGTH + COUPLING_WIGGLE_ROOM;
	private static final float MAX_HARD_CORRECTION_PER_TICK = 1.75F;
	private static final double EPSILON = 1.0E-6D;

	public static boolean attemptCouple(Player player, Level level, InteractionHand hand, int id1, int id2) {
		final ItemStack stack = player.getItemInHand(hand);
		if (!stack.is(TCAItems.MINECART_COUPLING)) return false;

		return attemptOneWayCouple(player, level, stack, id1, id2) || attemptOneWayCouple(player, level, stack, id2, id1);
	}

	private static boolean attemptOneWayCouple(Player player, Level level, ItemStack stack, int id1, int id2) {
		if (!(level instanceof ServerLevel serverLevel)) return false;
		if (id1 == id2) return false;

		final Entity entity1 = serverLevel.getEntity(id1);
		final Entity entity2 = serverLevel.getEntity(id2);
		if (!(entity1 instanceof AbstractMinecart cart1) || !(entity2 instanceof AbstractMinecart cart2)) return false;

		final CouplingData cart1Coupling = getCoupling(cart1);
		final CouplingData cart2Coupling = getCoupling(cart2);
		if (cart1Coupling.isCoupledTo() || cart2Coupling.isCoupledFrom()) return false;

		final double distance = cart1.distanceTo(cart2);
		if (distance >= MAX_COUPLING_DISTANCE) return false;

		stack.consume(1, player);
		coupleTo(cart1, cart2);
		cart1.playSound(SoundEvents.ANVIL_USE);
		return true;
	}

	public static void tickCoupling(AbstractMinecart cart) {
		final CouplingData coupling = getCoupling(cart);
		coupling.getCoupledTo(cart.level())
			.filter(entity -> entity instanceof AbstractMinecart)
			.map(AbstractMinecart.class::cast)
			.ifPresentOrElse(
				cart2 -> {
					if (!tickCoupling(cart.level(), cart, cart2)) uncoupleTo(cart, true);
				},
				() -> {
					uncoupleTo(cart, true);
				}
			);
	}

	private static boolean tickCoupling(Level level, AbstractMinecart cart1, AbstractMinecart cart2) {
		if (!cart1.isAlive() || !cart2.isAlive()) return false;
		if (cart1.distanceTo(cart2) >= MAX_COUPLING_DISTANCE) return false;
		if (level.tickRateManager().isEntityFrozen(cart1) && level.tickRateManager().isEntityFrozen(cart2)) return true;

		softCollisionStep(level, cart1, cart2, TARGET_COUPLING_LENGTH);
		hardCollisionStep(level, cart1, cart2, TARGET_COUPLING_LENGTH);
		return true;
	}

	private static void softCollisionStep(Level level, AbstractMinecart cart1, AbstractMinecart cart2, float couplingLength) {
		final boolean firstCanAddMotion = canAddMotion(cart1);
		final boolean secondCanAddMotion = canAddMotion(cart2);
		if (!firstCanAddMotion && !secondCanAddMotion) return;

		Vec3 firstMotion = clamp(cart1.getDeltaMovement(), 1F);
		Vec3 secondMotion = clamp(cart2.getDeltaMovement(), 1F);
		final Vec3 nextCart1Pos = cart1.position().add(firstMotion);
		final Vec3 nextCart2Pos = cart2.position().add(secondMotion);

		final RailShape firstShape = getRailShape(level, nextCart1Pos);
		final RailShape secondShape = getRailShape(level, nextCart2Pos);

		final float futureStress = (float) (couplingLength - nextCart1Pos.distanceTo(nextCart2Pos));
		if (Mth.equal(futureStress, 0D)) return;

		for (boolean current : new boolean[] {true, false}) {
			final boolean currentCanAddMotion = current ? firstCanAddMotion : secondCanAddMotion;
			final boolean otherCanAddMotion = current ? secondCanAddMotion : firstCanAddMotion;
			if (!currentCanAddMotion) continue;

			final Vec3 currentPos = current ? nextCart1Pos : nextCart2Pos;
			final Vec3 otherPos = current ? nextCart2Pos : nextCart1Pos;
			final Vec3 link = otherPos.subtract(currentPos);
			if (link.lengthSqr() <= EPSILON) continue;

			float correctionMagnitude = -futureStress / 2F;
			if (!otherCanAddMotion) correctionMagnitude *= 2F;

			Vec3 correction;
			final RailShape shape = current ? firstShape : secondShape;
			if (shape != null) {
				final Vec3 railVec = getRailVec(shape);
				correction = followLinkOnRail(link, currentPos, correctionMagnitude, railVec).subtract(currentPos);
			} else {
				correction = link.normalize().scale(correctionMagnitude);
			}

			final float maxSpeed = current ? getMaxCartSpeed(cart1) : getMaxCartSpeed(cart2);
			correction = clamp(correction, maxSpeed);
			if (current) {
				firstMotion = firstMotion.add(correction);
			} else {
				secondMotion = secondMotion.add(correction);
			}
		}

		cart1.setDeltaMovement(clamp(firstMotion, getMaxCartSpeed(cart1)));
		cart2.setDeltaMovement(clamp(secondMotion, getMaxCartSpeed(cart2)));
	}

	private static void hardCollisionStep(Level level, AbstractMinecart first, AbstractMinecart second, float couplingLength) {
		AbstractMinecart firstCart = first;
		AbstractMinecart secondCart = second;
		if (!canAddMotion(secondCart) && canAddMotion(firstCart)) {
			final AbstractMinecart swap = firstCart;
			firstCart = secondCart;
			secondCart = swap;
		}

		boolean firstLoop = true;
		for (boolean current : new boolean[] {true, false, true}) {
			final AbstractMinecart cart = current ? firstCart : secondCart;
			final AbstractMinecart otherCart = current ? secondCart : firstCart;

			float stress = (float) (couplingLength - cart.position().distanceTo(otherCart.position()));
			if (Math.abs(stress) < 1F / 8F) continue;

			final Vec3 pos = cart.position();
			final Vec3 link = otherCart.position().subtract(pos);
			if (link.lengthSqr() <= EPSILON) continue;

			float correctionMagnitude = firstLoop ? -stress / 2F : -stress;
			if (!canAddMotion(cart)) correctionMagnitude /= 2F;

			Vec3 correction = link.normalize().scale(correctionMagnitude);
			correction = clamp(correction, Math.min(MAX_HARD_CORRECTION_PER_TICK, getMaxCartSpeed(cart)));
			cart.move(MoverType.SELF, correction);
			cart.setDeltaMovement(cart.getDeltaMovement().scale(0.95F));

			firstLoop = false;
		}
	}

	private static Vec3 followLinkOnRail(Vec3 link, Vec3 cartPos, float diffToReduce, Vec3 railAxis) {
		final double dotProduct = railAxis.dot(link);
		if (Double.isNaN(dotProduct) || dotProduct == 0D || diffToReduce == 0D) return cartPos;

		final Vec3 axis = railAxis.scale(-Math.signum(dotProduct));
		final Vec3 center = cartPos.add(link);
		final double radius = link.length() - diffToReduce;
		final Vec3 intersectSphere = intersectSphere(cartPos, axis, center, radius);

		if (intersectSphere == null) return cartPos.add(project(link, axis));

		return intersectSphere;
	}

	@Nullable
	private static Vec3 intersectSphere(Vec3 lineOrigin, Vec3 lineDirection, Vec3 sphereCenter, double sphereRadius) {
		final double directionLengthSq = lineDirection.lengthSqr();
		if (directionLengthSq <= EPSILON || sphereRadius <= 0) return null;

		final Vec3 delta = lineOrigin.subtract(sphereCenter);
		final double a = directionLengthSq;
		final double b = 2.0D * delta.dot(lineDirection);
		final double c = delta.lengthSqr() - sphereRadius * sphereRadius;
		final double discriminant = b * b - 4.0D * a * c;
		if (discriminant < 0D) return null;

		final double sqrtDiscriminant = Math.sqrt(discriminant);
		final double t1 = (-b - sqrtDiscriminant) / (2.0D * a);
		final double t2 = (-b + sqrtDiscriminant) / (2.0D * a);
		double t = t1;
		if (Math.abs(t2) < Math.abs(t1)) t = t2;

		return lineOrigin.add(lineDirection.scale(t));
	}

	private static boolean canAddMotion(AbstractMinecart cart) {
		if (cart instanceof MinecartFurnace furnace) return Mth.equal((float) furnace.push.x, 0) && Mth.equal((float) furnace.push.z, 0);
		return cart.isAlive() && !cart.noPhysics;
	}

	private static float getMaxCartSpeed(AbstractMinecart cart) {
		return cart.isInWater() ? 0.2F : 0.4F;
	}

	private static Vec3 clamp(Vec3 vec, float maxLength) {
		final double length = vec.length();
		if (length <= maxLength || length <= EPSILON) return vec;
		return vec.scale(maxLength / length);
	}

	@Nullable
	private static RailShape getRailShape(Level level, Vec3 vec) {
		final int x = Mth.floor(vec.x());
		final int y = Mth.floor(vec.y());
		final int z = Mth.floor(vec.z());

		BlockPos pos = new BlockPos(x, y - 1, z);
		BlockState railState = level.getBlockState(pos);
		if (!railState.is(BlockTags.RAILS)) {
			pos = pos.above();
			railState = level.getBlockState(pos);
		}
		if (!(railState.getBlock() instanceof BaseRailBlock railBlock)) return null;
		return railState.getValue(railBlock.getShapeProperty());
	}

	private static Vec3 getRailVec(RailShape shape) {
		return switch (shape) {
			case EAST_WEST, ASCENDING_EAST, ASCENDING_WEST -> new Vec3(1D, 0D, 0D);
			case NORTH_SOUTH, ASCENDING_NORTH, ASCENDING_SOUTH -> new Vec3(0D, 0D, 1D);
			case NORTH_EAST, SOUTH_WEST -> new Vec3(1D, 0D, 1D).normalize();
			case NORTH_WEST, SOUTH_EAST -> new Vec3(1D, 0D, -1D).normalize();
		};
	}

	private static Vec3 project(Vec3 vec, Vec3 onto) {
		final double denominator = onto.lengthSqr();
		if (denominator <= EPSILON) return Vec3.ZERO;
		return onto.scale(vec.dot(onto) / denominator);
	}

	public static CouplingData getCoupling(Entity entity) {
		return entity.getAttachedOrCreate(TCAAttachments.MINECART_COUPLING);
	}

	public static void coupleTo(AbstractMinecart cart1, AbstractMinecart cart2) {
		cart1.setAttached(TCAAttachments.MINECART_COUPLING, getCoupling(cart1).coupleTo(cart2.getUUID()));
		cart2.setAttached(TCAAttachments.MINECART_COUPLING, getCoupling(cart2).coupleFrom(cart1.getUUID()));
	}

	public static boolean uncoupleTo(Entity cart, boolean drop) {
		final CouplingData coupling = cart.getAttachedOrCreate(TCAAttachments.MINECART_COUPLING);
		cart.setAttached(TCAAttachments.MINECART_COUPLING, coupling.uncoupleTo());

		coupling.getCoupledTo(cart.level()).ifPresent(
			entity -> {
				final CouplingData fromCoupling = entity.getAttachedOrCreate(TCAAttachments.MINECART_COUPLING);
				if (fromCoupling.isCoupledFrom(cart.getUUID())) entity.setAttached(TCAAttachments.MINECART_COUPLING, fromCoupling.uncoupleFrom());
			}
		);

		if (coupling.isCoupledTo()) {
			if (drop && cart.level() instanceof ServerLevel serverLevel) cart.spawnAtLocation(serverLevel, TCAItems.MINECART_COUPLING);
			return true;
		}
		return false;
	}

	public static boolean uncoupleFrom(Entity cart, boolean drop) {
		final AtomicBoolean uncoupled = new AtomicBoolean(false);
		final CouplingData coupling = cart.getAttachedOrCreate(TCAAttachments.MINECART_COUPLING);
		cart.setAttached(TCAAttachments.MINECART_COUPLING, coupling.uncoupleFrom());
		coupling.getCoupledFrom(cart.level()).ifPresent(entity -> uncoupled.set(uncoupleTo(entity, drop)));
		return uncoupled.get();
	}
}
