/*
 * Copyright 2025 FrozenBlock
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

package net.frozenblock.thecopperierage.mixin.block.copper_crate;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.frozenblock.thecopperierage.block.entity.inventory.CrateSlot;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {

	@WrapOperation(
		method = "tryItemClickBehaviourOverride",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/item/ItemStack;overrideOtherStackedOnMe(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/inventory/Slot;Lnet/minecraft/world/inventory/ClickAction;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/SlotAccess;)Z"
		)
	)
	private boolean theCopperierAge$preventBundlingInCrateSlots(
		ItemStack instance, ItemStack otherStack, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess, Operation<Boolean> original
	) {
		if (slot instanceof CrateSlot) {
			if (instance.has(DataComponents.BUNDLE_CONTENTS) && !otherStack.isEmpty() && !otherStack.has(DataComponents.BUNDLE_CONTENTS)) BundleItem.playInsertFailSound(player);
			return false;
		}
		return original.call(instance, otherStack, slot, clickAction, player, slotAccess);
	}

}
