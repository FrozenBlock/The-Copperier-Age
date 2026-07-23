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

import java.util.Optional;
import net.frozenblock.thecopperierage.entity.impl.CouplingToEntityInterface;
import net.frozenblock.thecopperierage.registry.TCAAttachments;
import net.frozenblock.thecopperierage.registry.TCAItems;
import net.frozenblock.thecopperierage.registry.TCASounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartFurnace;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class MinecartCouplingUtil {
	private static final int MAX_COUPLING_DISTANCE = 3;
	private static final float MIN_COUPLING_LENGTH = 1.5F;
	private static final float MAX_HARD_CORRECTION_PER_TICK = 1.75F;
	private static final float EXPERIMENTAL_SOFT_CORRECTION_SCALE = 0.4F;
	private static final float EXPERIMENTAL_RELATIVE_DAMPING = 0.3F;
	private static final float EXPERIMENTAL_MAX_RELATIVE_CORRECTION = 0.08F;
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
		if (cart1Coupling.isCoupledTo() || cart2Coupling.isCoupledFrom() || cart1Coupling.hasAnyCoupling(cart2.getUUID()) || cart2Coupling.hasAnyCoupling(cart1.getUUID())) return false;

		final double distance = cart1.distanceTo(cart2);
		if (distance >= MAX_COUPLING_DISTANCE) return false;

		if (!MinecartCouplingInteraction.isCouplingValidInWorld(level, cart1, cart2, true)) return false;

		stack.consume(1, player);
		coupleTo(cart1, cart2);
		if (!cart1.isSilent()) {
			final Vec3 soundPos = Mth.lerp(0.5D, cart1.position(), cart2.position());
			level.playSound(
				null,
				soundPos.x, soundPos.y, soundPos.z,
				TCASounds.ENTITY_MINECART_COUPLE,
				cart1.getSoundSource(),
				1F,
				(cart1.getRandom().nextFloat() * 0.2F) + 0.9F
			);
		}
		return true;
	}

	public static void tickCoupling(AbstractMinecart cart) {
		final CouplingData coupling = getCoupling(cart);

		coupling.getCoupledTo(cart.level())
			.filter(entity -> entity instanceof AbstractMinecart)
			.map(AbstractMinecart.class::cast)
			.ifPresentOrElse(
				cart2 -> {
					if (!tickCoupling(cart.level(), cart, cart2)) {
						uncoupleTo(cart, true);
					} else {
						if (cart instanceof CouplingToEntityInterface coupleInterface) coupleInterface.theCopperierAge$setCoupledTo(cart2);
					}
				},
				() -> {
					uncoupleTo(cart, !cart.isFirstTick());
					if (cart instanceof CouplingToEntityInterface coupleInterface) coupleInterface.theCopperierAge$setCoupledTo(null);
				});

		coupling.getCoupledFrom(cart.level())
			.filter(entity -> entity instanceof AbstractMinecart)
			.map(AbstractMinecart.class::cast).ifPresentOrElse(
				cart2 -> {},
				() -> {
					if (coupling.isCoupledFrom()) uncoupleFrom(cart, true);
				});
	}

	private static boolean tickCoupling(Level level, AbstractMinecart cart1, AbstractMinecart cart2) {
		if (!cart1.isAlive() || !cart2.isAlive()) return false;

		final float additionalPassengerWidth = getAdditionalPassengerWidth(cart1, cart2);
		if (cart1.distanceTo(cart2) >= MAX_COUPLING_DISTANCE + additionalPassengerWidth) return false;
		if (level.tickRateManager().isEntityFrozen(cart1) && level.tickRateManager().isEntityFrozen(cart2)) return true;

		final float targetCouplingLength = MIN_COUPLING_LENGTH + additionalPassengerWidth;
		if (isUsingExperimentalMinecartPhysics(level)) {
			softCollisionStep(level, cart1, cart2, targetCouplingLength, EXPERIMENTAL_SOFT_CORRECTION_SCALE);
			dampRelativeVelocity(cart1, cart2, EXPERIMENTAL_RELATIVE_DAMPING, EXPERIMENTAL_MAX_RELATIVE_CORRECTION);
		} else {
			softCollisionStep(level, cart1, cart2, targetCouplingLength, 1F);
			hardCollisionStep(level, cart1, cart2, targetCouplingLength);
		}
		return true;
	}

	private static float getAdditionalPassengerWidth(AbstractMinecart cart1, AbstractMinecart cart2) {
		final float cartWidth = 0.98F;
		float width = 0F;
		final Entity passenger1 = cart1.getFirstPassenger();
		if (passenger1 != null) {
			final AABB boundingBox = passenger1.getBoundingBox();
			width = (float) Math.max(width, (boundingBox.getXsize() + boundingBox.getZsize()) * 0.5D);
		}

		final Entity passenger2 = cart2.getFirstPassenger();
		if (passenger2 != null) {
			final AABB boundingBox = passenger2.getBoundingBox();
			width = (float) Math.max(width, (boundingBox.getXsize() + boundingBox.getZsize()) * 0.5D);
		}

		return Math.max(0F, width - cartWidth);
	}

	private static void softCollisionStep(Level level, AbstractMinecart cart1, AbstractMinecart cart2, float couplingLength, float correctionScale) {
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

			final AbstractMinecart cart = current ? cart1 : cart2;
			final Vec3 currentPos = current ? nextCart1Pos : nextCart2Pos;
			final Vec3 otherPos = current ? nextCart2Pos : nextCart1Pos;
			final Vec3 link = otherPos.subtract(currentPos);
			if (link.lengthSqr() <= EPSILON) continue;

			float correctionMagnitude = -futureStress / 2F;
			if (!otherCanAddMotion) correctionMagnitude *= 2F;
			correctionMagnitude *= correctionScale;

			Vec3 correction;
			final RailShape shape = current ? firstShape : secondShape;
			if (shape != null) {
				final Vec3 railVec = getRailVec(shape, cart.getPosition(1F).subtract(cart.getPosition(0F)).y <= 0D);
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

	private static void dampRelativeVelocity(AbstractMinecart first, AbstractMinecart second, float dampingScale, float maxCorrection) {
		final boolean firstCanAddMotion = canAddMotion(first);
		final boolean secondCanAddMotion = canAddMotion(second);
		if (!firstCanAddMotion && !secondCanAddMotion) return;

		final Vec3 link = second.position().subtract(first.position());
		final double linkLengthSq = link.lengthSqr();
		if (linkLengthSq <= EPSILON) return;

		final Vec3 linkDirection = link.scale(1D / Math.sqrt(linkLengthSq));
		final double relativeSpeed = second.getDeltaMovement().subtract(first.getDeltaMovement()).dot(linkDirection);
		if (Math.abs(relativeSpeed) <= EPSILON) return;

		final double correction = Mth.clamp(relativeSpeed * dampingScale, -maxCorrection, maxCorrection);
		if (Math.abs(correction) <= EPSILON) return;

		if (firstCanAddMotion && secondCanAddMotion) {
			final Vec3 impulse = linkDirection.scale(correction * 0.5D);
			first.setDeltaMovement(clamp(first.getDeltaMovement().add(impulse), getMaxCartSpeed(first)));
			second.setDeltaMovement(clamp(second.getDeltaMovement().subtract(impulse), getMaxCartSpeed(second)));
		} else if (firstCanAddMotion) {
			final Vec3 impulse = linkDirection.scale(correction);
			first.setDeltaMovement(clamp(first.getDeltaMovement().add(impulse), getMaxCartSpeed(first)));
		} else {
			final Vec3 impulse = linkDirection.scale(correction);
			second.setDeltaMovement(clamp(second.getDeltaMovement().subtract(impulse), getMaxCartSpeed(second)));
		}
	}

	private static boolean isUsingExperimentalMinecartPhysics(Level level) {
		try {
			return AbstractMinecart.useExperimentalMovement(level);
		} catch (Throwable ignored) {
			return false;
		}
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
			//cart.setDeltaMovement(cart.getDeltaMovement().scale(0.95F));

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
		final double b = 2D * delta.dot(lineDirection);
		final double c = delta.lengthSqr() - sphereRadius * sphereRadius;
		final double discriminant = b * b - 4D * a * c;
		if (discriminant < 0D) return null;

		final double sqrtDiscriminant = Math.sqrt(discriminant);
		final double t1 = (-b - sqrtDiscriminant) / (2D * a);
		final double t2 = (-b + sqrtDiscriminant) / (2D * a);
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

	private static Vec3 getRailVec(RailShape shape, boolean descending) {
		return switch (shape) {
			case EAST_WEST -> new Vec3(1D, 0D, 0D);
			case ASCENDING_EAST, ASCENDING_WEST -> new Vec3(1D, descending? 1D : -1D, 0D);
			case NORTH_SOUTH -> new Vec3(0D, 0D, 1D);
			case ASCENDING_NORTH, ASCENDING_SOUTH -> new Vec3(0D, descending? 1D : -1D, 1D);
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
		final boolean isCoupled = coupling.isCoupledTo();
		if (!isCoupled) return false;

		cart.setAttached(TCAAttachments.MINECART_COUPLING, coupling.uncoupleTo());
		final Optional<Entity> coupledTo = coupling.getCoupledTo(cart.level());

		coupledTo.ifPresent(
			entity -> {
				final CouplingData fromCoupling = entity.getAttachedOrCreate(TCAAttachments.MINECART_COUPLING);
				if (fromCoupling.isCoupledFrom(cart.getUUID())) entity.setAttached(TCAAttachments.MINECART_COUPLING, fromCoupling.uncoupleFrom());
			}
		);

		if (drop) onCouplingDropped(cart, coupledTo);
		return true;
	}

	public static boolean uncoupleFrom(Entity cart, boolean drop) {
		final CouplingData coupling = cart.getAttachedOrCreate(TCAAttachments.MINECART_COUPLING);
		final boolean isCoupled = coupling.isCoupledFrom();
		if (!isCoupled) return false;

		cart.setAttached(TCAAttachments.MINECART_COUPLING, coupling.uncoupleFrom());
		final Optional<Entity> coupledFrom = coupling.getCoupledFrom(cart.level());

		coupledFrom.ifPresent(
			entity -> {
				final CouplingData toCoupling = entity.getAttachedOrCreate(TCAAttachments.MINECART_COUPLING);
				if (toCoupling.isCoupledTo(cart.getUUID())) entity.setAttached(TCAAttachments.MINECART_COUPLING, toCoupling.uncoupleTo());
			}
		);

		if (drop) onCouplingDropped(cart, coupledFrom);
		return true;
	}

	private static void onCouplingDropped(Entity cart, Optional<Entity> coupled) {
		if (!cart.isSilent()) {
			final Vec3 soundPos = coupled.map(entity -> Mth.lerp(0.5D, entity.position(), cart.position())).orElseGet(cart::position);
			cart.level().playSound(
				null,
				soundPos.x, soundPos.y, soundPos.z,
				TCASounds.ENTITY_MINECART_COUPLE_BREAK,
				cart.getSoundSource(),
				0.9F,
				(cart.getRandom().nextFloat() * 0.3F) + 0.85F
			);
		}
		if (cart.level() instanceof ServerLevel serverLevel) cart.spawnAtLocation(serverLevel, TCAItems.MINECART_COUPLING.getDefaultInstance());
	}
}
