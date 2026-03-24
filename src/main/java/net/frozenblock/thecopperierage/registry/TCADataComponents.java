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

package net.frozenblock.thecopperierage.registry;

import java.util.function.UnaryOperator;
import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.WeatheringCopper;

public final class TCADataComponents {
	public static final DataComponentType<WeatheringCopper.WeatherState> WAXED = register(
		"waxed",
		builder -> builder.persistent(WeatheringCopper.WeatherState.CODEC).networkSynchronized(WeatheringCopper.WeatherState.STREAM_CODEC)
	);

	public static void init() {}

	private static <T> DataComponentType<T> register(String id, UnaryOperator<DataComponentType.Builder<T>> unaryOperator) {
		return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, TCAConstants.id(id), unaryOperator.apply(DataComponentType.builder()).build());
	}
}
