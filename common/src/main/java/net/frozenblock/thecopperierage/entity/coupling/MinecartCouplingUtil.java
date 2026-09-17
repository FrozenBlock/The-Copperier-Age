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
import net.frozenblock.thecopperierage.entity.impl.MinecartImpacts;
import net.frozenblock.thecopperierage.entity.impl.CouplingToEntityInterface;
import net.frozenblock.thecopperierage.registry.TCAAttachmentTypes;
import net.frozenblock.thecopperierage.registry.TCAItems;
import net.frozenblock.thecopperierage.registry.TCASounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class MinecartCouplingUtil {
	private static final double MAX_COUPLING_DISTANCE = 3D;
	private static final double MAX_COUPLING_LENGTH = 1.5D;
	private static final double CART_WIDTH = 0.98D;
	private static final int MISSING_PARTNER_GRACE_TICKS = 10;

	public static boolean tryCouple(Player player, Level level, InteractionHand hand, int id1, int id2) {
		final ItemStack stack = player.getItemInHand(hand);
		if (!stack.is(TCAItems.MINECART_COUPLING.get())) return false;

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

		if (!MinecartCouplingInteraction.isCouplingValidInLevel(level, cart1, cart2, true)) return false;

		stack.consume(1, player);
		coupleTo(cart1, cart2);
		if (!cart1.isSilent()) {
			final Vec3 soundPos = Mth.lerp(0.5D, cart1.position(), cart2.position());
			level.playSound(
				null,
				soundPos.x, soundPos.y, soundPos.z,
				TCASounds.ENTITY_MINECART_COUPLE.get(),
				cart1.getSoundSource(),
				1F,
				(cart1.getRandom().nextFloat() * 0.2F) + 0.9F
			);
		}
		return true;
	}

	public static void tickCoupling(AbstractMinecart cart) {
		if (!(cart instanceof CouplingToEntityInterface access)) return;

		final CouplingData coupling = getCoupling(cart);
		if (!(cart.level() instanceof ServerLevel level)) {
			access.theCopperierAge$setCoupledTo(coupling.getCoupledTo(cart.level()).filter(Entity::isAlive).orElse(null));
			return;
		}

		access.theCopperierAge$setCoupledTo(null);
		if (!coupling.hasAnyCoupling()) access.theCopperierAge$setTrainSize(1);
		boolean queued = false;
		if (coupling.isCoupledTo()) {
			final Entity partner = coupling.getCoupledTo(level).orElse(null);
			if (partner == null) {
				if (access.theCopperierAge$incrementMissingCoupledTo() > MISSING_PARTNER_GRACE_TICKS) uncoupleTo(cart, true);
			} else {
				access.theCopperierAge$resetMissingCoupledTo();
				if (partner instanceof AbstractMinecart other && isLinkIntact(level, cart, other)) {
					access.theCopperierAge$setCoupledTo(other);
					MinecartCouplingPhysics.queue(level, cart);
					queued = true;
				} else {
					uncoupleTo(cart, true);
				}
			}
		} else {
			access.theCopperierAge$resetMissingCoupledTo();
		}

		if (coupling.isCoupledFrom()) {
			final Entity partner = coupling.getCoupledFrom(level).orElse(null);
			if (partner == null) {
				if (access.theCopperierAge$incrementMissingCoupledFrom() > MISSING_PARTNER_GRACE_TICKS) uncoupleFrom(cart, true);
			} else {
				access.theCopperierAge$resetMissingCoupledFrom();
				if (!(partner instanceof AbstractMinecart other) || !isLinkIntact(level, cart, other)) uncoupleFrom(cart, true);
			}
		} else {
			access.theCopperierAge$resetMissingCoupledFrom();
		}

		if (!queued && MinecartImpacts.enabled()) MinecartCouplingPhysics.queue(level, cart);
	}

	private static boolean isLinkIntact(ServerLevel level, AbstractMinecart cart1, AbstractMinecart cart2) {
		if (!cart1.isAlive() || !cart2.isAlive() || cart1.level() != level || cart2.level() != level) return false;
		return cart1.distanceTo(cart2) < MAX_COUPLING_DISTANCE + getCouplingPadding(cart1, cart2);
	}

	@Nullable
	public static AbstractMinecart getCoupledToCart(AbstractMinecart cart) {
		return getCoupling(cart).getCoupledTo(cart.level())
			.filter(entity -> entity instanceof AbstractMinecart)
			.map(AbstractMinecart.class::cast)
			.orElse(null);
	}

	public static boolean areCoupledTogether(Entity first, Entity second) {
		if (!(first instanceof AbstractMinecart) || !(second instanceof AbstractMinecart)) return false;
		return getCoupling(first).hasAnyCoupling(second.getUUID()) || getCoupling(second).hasAnyCoupling(first.getUUID());
	}

	public static double getMaxCouplingLength(AbstractMinecart cart1, AbstractMinecart cart2) {
		return MAX_COUPLING_LENGTH + getCouplingPadding(cart1, cart2);
	}

	public static double getMinCouplingLength(AbstractMinecart cart1, AbstractMinecart cart2) {
		return MinecartCouplingPhysics.CONTACT_DISTANCE + getCouplingPadding(cart1, cart2);
	}

	public static double getCouplingPadding(AbstractMinecart cart1, AbstractMinecart cart2) {
		final double width = Math.max(getPassengerWidth(cart1), getPassengerWidth(cart2));
		return Math.max(0D, width - CART_WIDTH);
	}

	private static double getPassengerWidth(AbstractMinecart cart) {
		final Entity passenger = cart.getFirstPassenger();
		if (passenger == null) return 0D;

		final AABB boundingBox = passenger.getBoundingBox();
		return (boundingBox.getXsize() + boundingBox.getZsize()) * 0.5D;
	}

	public static CouplingData getCoupling(Entity entity) {
		return TCAAttachmentTypes.MINECART_COUPLING.getAttachedOrCreate(entity);
	}

	public static void coupleTo(AbstractMinecart cart1, AbstractMinecart cart2) {
		TCAAttachmentTypes.MINECART_COUPLING.set(cart1, getCoupling(cart1).coupleTo(cart2.getUUID()));
		TCAAttachmentTypes.MINECART_COUPLING.set(cart2, getCoupling(cart2).coupleFrom(cart1.getUUID()));
	}

	public static boolean uncoupleTo(Entity cart, boolean drop) {
		final CouplingData coupling = TCAAttachmentTypes.MINECART_COUPLING.getAttachedOrCreate(cart);
		final boolean isCoupled = coupling.isCoupledTo();
		if (!isCoupled) return false;

		TCAAttachmentTypes.MINECART_COUPLING.set(cart, coupling.uncoupleTo());
		final Optional<Entity> coupledTo = coupling.getCoupledTo(cart.level());

		coupledTo.ifPresent(
			entity -> {
				final CouplingData fromCoupling = TCAAttachmentTypes.MINECART_COUPLING.getAttachedOrCreate(entity);
				if (fromCoupling.isCoupledFrom(cart.getUUID())) TCAAttachmentTypes.MINECART_COUPLING.set(entity, fromCoupling.uncoupleFrom());
			}
		);

		if (drop) onCouplingDropped(cart, coupledTo);
		return true;
	}

	public static boolean uncoupleFrom(Entity cart, boolean drop) {
		final CouplingData coupling = TCAAttachmentTypes.MINECART_COUPLING.getAttachedOrCreate(cart);
		final boolean isCoupled = coupling.isCoupledFrom();
		if (!isCoupled) return false;

		TCAAttachmentTypes.MINECART_COUPLING.set(cart, coupling.uncoupleFrom());
		final Optional<Entity> coupledFrom = coupling.getCoupledFrom(cart.level());

		coupledFrom.ifPresent(
			entity -> {
				final CouplingData toCoupling = TCAAttachmentTypes.MINECART_COUPLING.getAttachedOrCreate(entity);
				if (toCoupling.isCoupledTo(cart.getUUID())) TCAAttachmentTypes.MINECART_COUPLING.set(entity, toCoupling.uncoupleTo());
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
				TCASounds.ENTITY_MINECART_COUPLE_BREAK.get(),
				cart.getSoundSource(),
				0.9F,
				(cart.getRandom().nextFloat() * 0.3F) + 0.85F
			);
		}
		if (cart.level() instanceof ServerLevel serverLevel) cart.spawnAtLocation(serverLevel, TCAItems.MINECART_COUPLING.get().getDefaultInstance());
	}

	private MinecartCouplingUtil() {}
}
