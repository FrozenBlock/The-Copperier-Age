/*
 * Copyright 2025-2026 FrozenBlock
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

import net.frozenblock.thecopperierage.client.coupling.MinecartCouplingClientHandler;
import net.frozenblock.thecopperierage.registry.TCAItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class MinecartCouplingInteraction {

	@Nullable
	public static InteractionResult handleInteractionWithMinecart(Player player, InteractionHand hand, Entity interacted) {
		if (!(interacted instanceof AbstractMinecart minecart)) return null;

		final ItemStack heldItem = player.getItemInHand(hand);
		if (heldItem.is(TCAItems.MINECART_COUPLING)) {
			interactWithCoupling(player, hand, minecart);
			return InteractionResult.SUCCESS;
		}

		if (heldItem.is(TCAItems.WRENCH) && interactWithWrench(player, hand, minecart)) {
			return InteractionResult.SUCCESS;
		}

		return null;
	}

	private static void interactWithCoupling(Player player, InteractionHand hand, AbstractMinecart minecart) {
		if (player.level().isClientSide()) MinecartCouplingClientHandler.onCartClicked(player, hand, minecart);
	}

	private static boolean interactWithWrench(Player player, InteractionHand hand, AbstractMinecart minecart) {
		final Level level = player.level();
		final CouplingData coupling = MinecartCouplingUtil.getCoupling(minecart);
		if (coupling.equals(CouplingData.EMPTY)) return false;
		if (level.isClientSide()) return true;

		int couplings = 0;
		if (MinecartCouplingUtil.uncoupleTo(minecart, false)) couplings += 1;
		if (MinecartCouplingUtil.uncoupleFrom(minecart, false)) couplings += 1;
		if (couplings == 0) return false;

		player.getInventory().placeItemBackInInventory(new ItemStack(TCAItems.MINECART_COUPLING, couplings));
		player.getItemInHand(hand).hurtAndBreak(1, player, hand);
		return true;
	}

	public static boolean isCouplingValidInWorld(Level level, AbstractMinecart selectedCart, Entity target, boolean checkEntity) {
		if (target == null || selectedCart == null || level == null || level != selectedCart.level()) return false;
		if (!target.isAlive() || target.isSpectator() || !selectedCart.isAlive() || selectedCart.isSpectator()) return false;
		final Vec3 startPos = selectedCart.position().add(0D, 0.1D, 0D);
		Vec3 targetPos = (!(target instanceof AbstractMinecart)) ? target.getEyePosition(1F) : target.position().add(0D, 0.1D, 0D);

		final BlockHitResult hitResult = level.clip(
			new ClipContext(
				startPos,
				targetPos,
				ClipContext.Block.COLLIDER,
				ClipContext.Fluid.NONE,
				selectedCart
			)
		);
		if (hitResult.getType() != HitResult.Type.MISS) {
			if (!checkEntity) return false;
			targetPos = hitResult.getLocation();
		} else if (!checkEntity) {
			return true;
		}

		final EntityHitResult entityResult = ProjectileUtil.getEntityHitResult(
			selectedCart,
			startPos,
			targetPos,
			selectedCart.getBoundingBox().minmax(target.getBoundingBox()),
			EntitySelector.ENTITY_STILL_ALIVE.and(EntitySelector.NO_SPECTATORS),
			64D
		);
		return entityResult != null && entityResult.getEntity() == target;
	}
}
