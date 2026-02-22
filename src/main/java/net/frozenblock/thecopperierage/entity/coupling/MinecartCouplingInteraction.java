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

import java.util.ArrayList;
import java.util.List;
import net.frozenblock.thecopperierage.client.coupling.TCAMinecartCouplingClientHandler;
import net.frozenblock.thecopperierage.registry.TCAItems;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class MinecartCouplingInteraction {

	@Nullable
	public static InteractionResult handleInteractionWithMinecart(Player player, InteractionHand hand, Entity interacted) {
		if (!(interacted instanceof AbstractMinecart minecart)) return null;

		final ItemStack heldItem = player.getItemInHand(hand);
		if (heldItem.is(ItemTags.CHAINS)) {
			interactWithCoupling(player, hand, minecart);
			return InteractionResult.SUCCESS;
		}

		if (heldItem.is(TCAItems.WRENCH) && interactWithWrench(player, hand, minecart)) {
			return InteractionResult.SUCCESS;
		}

		return null;
	}

	private static void interactWithCoupling(Player player, InteractionHand hand, AbstractMinecart minecart) {
		if (player.level().isClientSide()) TCAMinecartCouplingClientHandler.onCartClicked(player, hand, minecart);
	}

	private static boolean interactWithWrench(Player player, InteractionHand hand, AbstractMinecart minecart) {
		final Level level = player.level();
		final CouplingData coupling = MinecartCouplingUtil.getCoupling(minecart);
		if (coupling.equals(CouplingData.EMPTY)) return false;
		if (level.isClientSide()) return true;

		final List<ItemStack> couplings = new ArrayList<>();
		if (MinecartCouplingUtil.uncoupleTo(minecart, false)) coupling.getCoupledToItem().ifPresent(item -> couplings.add(item.copyAndClear()));
		if (MinecartCouplingUtil.uncoupleFrom(minecart, false)) coupling.getCoupledFromItem().ifPresent(item -> couplings.add(item.copyAndClear()));
		if (couplings.isEmpty()) return false;

		couplings.forEach(item -> player.getInventory().placeItemBackInInventory(item));
		player.getItemInHand(hand).hurtAndBreak(1, player, hand);
		return true;
	}
}
