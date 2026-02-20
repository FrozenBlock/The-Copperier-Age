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

package net.frozenblock.thecopperierage.mixin.item;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.Optional;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.item.api.OxidizableItemHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

	@Inject(method = "getName()Lnet/minecraft/network/chat/Component;", at = @At("HEAD"), cancellable = true)
	public void theCopperierAge$getNonWeatheringNonWaxedName(CallbackInfoReturnable<Component> info) {
		if (!TCAConfig.BETTER_COPPER_TOOLTIPS) return;
		final Optional<Item> baseItem = OxidizableItemHelper.getNonWeatheringNonWaxedEquivalent(Item.class.cast(this));
		if (baseItem.isEmpty()) return;
		info.setReturnValue(baseItem.get().getName());
	}

	@Inject(method = "getName(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/network/chat/Component;", at = @At("HEAD"))
	public void theCopperierAge$getNonWeatheringNonWaxedName(
		CallbackInfoReturnable<Component> info,
		@Local(argsOnly = true) LocalRef<ItemStack> stack
	) {
		if (!TCAConfig.BETTER_COPPER_TOOLTIPS) return;
		final Optional<Item> baseItem = OxidizableItemHelper.getNonWeatheringNonWaxedEquivalent(stack.get().getItem());
		if (baseItem.isEmpty()) return;
		stack.set(stack.get().transmuteCopy(baseItem.get()));
	}
}
