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

package net.frozenblock.thecopperierage.mixin.item;

import net.frozenblock.thecopperierage.item.api.OxidizableItemHelper;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin { // in common mixins.json

	@Inject(method = "set(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;", at = @At("HEAD"))
	public <T> void theCopperierAge$onDamageSet(DataComponentType<T> component, @Nullable T value, CallbackInfoReturnable<T> info) {
		if (component != DataComponents.DAMAGE) return;
		int damage = value == null ? 0 : Integer.class.cast(value).intValue();
		OxidizableItemHelper.onDamageUpdated(ItemStack.class.cast(this), damage);
	}

	@Inject(method = "set(Lnet/minecraft/core/component/TypedDataComponent;)Ljava/lang/Object;", at = @At("HEAD"))
	public <T> void theCopperierAge$onDamageSet(TypedDataComponent<T> component, CallbackInfoReturnable<T> info) {
		if (component.type() != DataComponents.DAMAGE) return;
		final T value = component.value();
		int damage = value == null ? 0 : Integer.class.cast(value).intValue();
		OxidizableItemHelper.onDamageUpdated(ItemStack.class.cast(this), damage);
	}

	@Inject(method = "remove", at = @At("HEAD"))
	public <T> void theCopperierAge$onDamageSet(DataComponentType<T> component, CallbackInfoReturnable<T> info) {
		if (component != DataComponents.DAMAGE) return;
		OxidizableItemHelper.onDamageUpdated(ItemStack.class.cast(this), 0);
	}
}
