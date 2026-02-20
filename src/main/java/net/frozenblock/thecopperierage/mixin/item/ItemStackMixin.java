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

import java.util.Optional;
import java.util.function.Consumer;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.item.api.OxidizableItemHelper;
import net.frozenblock.thecopperierage.tag.TCAItemTags;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.WeatheringCopper;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

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

	@Inject(
		method = "addDetailsToTooltip",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/core/component/DataComponents;UNBREAKABLE:Lnet/minecraft/core/component/DataComponentType;",
			opcode = Opcodes.GETSTATIC,
			ordinal = 0,
			shift = At.Shift.BEFORE
		)
	)
	public void theCopperierAge$addWeatheringAndWaxedTooltips(
		Item.TooltipContext context, TooltipDisplay display, @Nullable Player player, TooltipFlag flag, Consumer<Component> consumer, CallbackInfo info
	) {
		boolean addedOxidizedTooltip = false;
		boolean addedWaxedTooltip = false;

		final ItemStack stack = ItemStack.class.cast(this);
		if (stack.is(TCAItemTags.OXIDIZABLE_EQUIPMENT)) {
			theCopperierAge$addWeatherStateTooltip(
				consumer,
				OxidizableItemHelper.getValueForOxidization(
					stack,
					WeatheringCopper.WeatherState.UNAFFECTED,
					WeatheringCopper.WeatherState.EXPOSED,
					WeatheringCopper.WeatherState.WEATHERED,
					WeatheringCopper.WeatherState.OXIDIZED
				)
			);
			addedOxidizedTooltip = true;
		}
		if (OxidizableItemHelper.isWaxed(stack)) {
			consumer.accept(OxidizableItemHelper.WAXED_TOOLTIP);
			addedWaxedTooltip = true;
		}

		if (!TCAConfig.BETTER_COPPER_TOOLTIPS) return;
		final Optional<WeatheringCopper.WeatherState> weatherStateByTag = OxidizableItemHelper.getWeatherStateByTag(stack);
		if (!addedOxidizedTooltip && weatherStateByTag.isPresent()) {
			weatherStateByTag.ifPresent(weatherState -> theCopperierAge$addWeatherStateTooltip(consumer, weatherState));
			addedOxidizedTooltip = true;
		}
		if (!addedWaxedTooltip && stack.is(TCAItemTags.WEATHERING_WAXED)) {
			consumer.accept(OxidizableItemHelper.WAXED_TOOLTIP);
			addedWaxedTooltip = true;
		}

		// Cancel if the item is already known to be Oxidized or Waxed.
		// The only time this will cause unintended behavior is if someone added a waxed Item to a weathering/waxed tag, but not the waxed/weathering one.
		// At that point, it's their fault. So don't worry.
		if (addedOxidizedTooltip || addedWaxedTooltip) return;

		final Item item = stack.getItem();
		final Optional<Item> nonWaxedItem = OxidizableItemHelper.getNonWaxedEquivalent(item);
		if (nonWaxedItem.orElse(item) instanceof BlockItem blockItem && blockItem.getBlock() instanceof WeatheringCopper weatheringCopper) {
			theCopperierAge$addWeatherStateTooltip(consumer, weatheringCopper.getAge());
		}
		if (nonWaxedItem.isPresent()) consumer.accept(OxidizableItemHelper.WAXED_TOOLTIP);
	}

	@Unique
	private static void theCopperierAge$addWeatherStateTooltip(Consumer<Component> consumer, WeatheringCopper.WeatherState weatherState) {
		if (weatherState == WeatheringCopper.WeatherState.UNAFFECTED) return;
		consumer.accept(OxidizableItemHelper.getWeatheringStateName(weatherState));
	}
}
