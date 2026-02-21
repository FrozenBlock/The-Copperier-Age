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
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.item.impl.ItemOxidizationCacheInterface;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.WeatheringCopper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin implements ItemOxidizationCacheInterface {

	@Unique
	@Nullable
	private WeatheringCopper.WeatherState theCopperierAge$weatherState = null;
	@Unique
	private boolean theCopperierAge$waxed = false;
	@Unique
	@Nullable
	private Item theCopperierAge$baseItem = null;

	@Inject(method = "getName()Lnet/minecraft/network/chat/Component;", at = @At("HEAD"), cancellable = true)
	public void theCopperierAge$getNonWeatheringNonWaxedName(CallbackInfoReturnable<Component> info) {
		if (!TCAConfig.BETTER_COPPER_TOOLTIPS) return;
		final Item baseItem = this.theCopperierAge$baseItem();
		if (baseItem == null) return;
		info.setReturnValue(baseItem.getName());
	}

	@Inject(method = "getName(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/network/chat/Component;", at = @At("HEAD"))
	public void theCopperierAge$getNonWeatheringNonWaxedName(
		CallbackInfoReturnable<Component> info,
		@Local(argsOnly = true) LocalRef<ItemStack> stack
	) {
		if (!TCAConfig.BETTER_COPPER_TOOLTIPS) return;
		final Item baseItem = this.theCopperierAge$baseItem();
		if (baseItem == null) return;
		// I'm using transmuteCopy here in case there's a weird case where the ITEM_NAME component is different from the default.
		// For example, the Ominous Banner. Just want to cover random edge cases.
		stack.set(stack.get().transmuteCopy(baseItem));
	}

	@Override
	public void theCopperierAge$setWeatherState(WeatheringCopper.WeatherState weatherState) {
		this.theCopperierAge$weatherState = weatherState;
	}

	@Override
	public void theCopperierAge$setWaxed(boolean waxed) {
		this.theCopperierAge$waxed = waxed;
	}

	@Override
	public void theCopperierAge$setBaseItem(Item item) {
		this.theCopperierAge$baseItem = item;
	}

	@Override
	public WeatheringCopper.WeatherState theCopperierAge$weatherState() {
		return this.theCopperierAge$weatherState;
	}

	@Override
	public boolean theCopperierAge$waxed() {
		return this.theCopperierAge$waxed;
	}

	@Override
	public Item theCopperierAge$baseItem() {
		return this.theCopperierAge$baseItem;
	}

	@Override
	public void theCopperierAge$clearOxidizationCache() {
		this.theCopperierAge$weatherState = null;
		this.theCopperierAge$waxed = false;
		this.theCopperierAge$baseItem = null;
	}
}
