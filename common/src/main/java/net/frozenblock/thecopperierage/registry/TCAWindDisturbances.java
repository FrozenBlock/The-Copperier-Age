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

import com.mojang.serialization.MapCodec;
import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.frozenblock.lib.wind.disturbance.WindDisturbanceType;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.wind.disturbance.CopperFanWindDisturbance;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public final class TCAWindDisturbances {
	private static final DeferredRegister<WindDisturbanceType<?>> REGISTER = DeferredRegister.create(
		FrozenLibRegistries.WIND_DISTURBANCE_TYPE,
		TCAConstants.MOD_ID
	);

	public static final DeferredHolder<WindDisturbanceType<?>, WindDisturbanceType<CopperFanWindDisturbance>> COPPER_FAN_WIND_DISTURBANCE = REGISTER.register(
		"copper_fan",
		() -> new WindDisturbanceType<>() {
			@Override
			public MapCodec<CopperFanWindDisturbance> codec() {
				return CopperFanWindDisturbance.CODEC;
			}

			@Override
			public StreamCodec<RegistryFriendlyByteBuf, CopperFanWindDisturbance> streamCodec() {
				return CopperFanWindDisturbance.STREAM_CODEC;
			}
		}
	);
	public static final DeferredHolder<WindDisturbanceType<?>, WindDisturbanceType<CopperFanWindDisturbance>> COPPER_FAN_WIND_DISTURBANCE_REVERSE = REGISTER.register(
		"copper_fan_reverse",
		() -> new WindDisturbanceType<>() {
			@Override
			public MapCodec<CopperFanWindDisturbance> codec() {
				return CopperFanWindDisturbance.CODEC;
			}

			@Override
			public StreamCodec<RegistryFriendlyByteBuf, CopperFanWindDisturbance> streamCodec() {
				return CopperFanWindDisturbance.STREAM_CODEC;
			}
		}
	);

	static {
		REGISTER.register();
	}

	public static void init() {}

	private TCAWindDisturbances() {}
}
