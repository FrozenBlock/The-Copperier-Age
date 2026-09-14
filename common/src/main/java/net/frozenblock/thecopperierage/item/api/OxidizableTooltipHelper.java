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

package net.frozenblock.thecopperierage.item.api;

import java.util.function.Consumer;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.item.impl.ItemOxidizationCacheInterface;
import net.frozenblock.thecopperierage.tag.TCAItemTags;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.WeatheringCopper;
import org.jetbrains.annotations.Nullable;

public final class OxidizableTooltipHelper {

	public static void addWeatheringAndWaxedTooltips(
		ItemStack stack,
		Item.TooltipContext context,
		TooltipDisplay display,
		@Nullable Player player,
		TooltipFlag tooltipFlag,
		Consumer<Component> builder
	) {
		if (stack.is(TCAItemTags.OXIDIZABLE_EQUIPMENT)) {
			addWeatherStateTooltip(
				builder,
				OxidizableItemHelper.getValueForOxidization(
					stack,
					WeatheringCopper.WeatherState.UNAFFECTED,
					WeatheringCopper.WeatherState.EXPOSED,
					WeatheringCopper.WeatherState.WEATHERED,
					WeatheringCopper.WeatherState.OXIDIZED
				)
			);
		}
		if (OxidizableItemHelper.hasWaxedComponent(stack)) builder.accept(OxidizableItemHelper.WAXED_TOOLTIP);

		if (!TCAConfig.BETTER_COPPER_TOOLTIPS.get()) return;
		if (!(stack.getItem() instanceof ItemOxidizationCacheInterface oxidizationCache)) return;

		final WeatheringCopper.WeatherState weatherState = oxidizationCache.theCopperierAge$weatherState();
		if (weatherState != null) addWeatherStateTooltip(builder, weatherState);
		if (oxidizationCache.theCopperierAge$waxed()) builder.accept(OxidizableItemHelper.WAXED_TOOLTIP);
	}

	private static void addWeatherStateTooltip(Consumer<Component> consumer, WeatheringCopper.WeatherState weatherState) {
		if (weatherState == WeatheringCopper.WeatherState.UNAFFECTED) return;
		consumer.accept(OxidizableItemHelper.getWeatheringStateName(weatherState));
	}

	private OxidizableTooltipHelper() {}
}
