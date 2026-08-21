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

import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.level.block.WeatheringCopper;

public final class TCADataComponents {
	private static final DeferredRegister.DataComponents REGISTER = DeferredRegister.createDataComponents(
		TCAConstants.MOD_ID
	);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<WeatheringCopper.WeatherState>> WAXED = REGISTER.registerComponent(
		"waxed",
		builder -> builder.persistent(WeatheringCopper.WeatherState.CODEC).networkSynchronized(WeatheringCopper.WeatherState.STREAM_CODEC)
	);

	static {
		REGISTER.register();
	}

	public static void init() {}
}
