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

import net.frozenblock.lib.wind.disturbance.WindDisturbanceType;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.wind.disturbance.CopperFanWindDisturbance;

public final class TCAWindDisturbances {
	public static final WindDisturbanceType<CopperFanWindDisturbance> COPPER_FAN_WIND_DISTURBANCE = WindDisturbanceType.register(
		TCAConstants.id("copper_fan"),
		CopperFanWindDisturbance.CODEC,
		CopperFanWindDisturbance.STREAM_CODEC
	);
	public static final WindDisturbanceType<CopperFanWindDisturbance> COPPER_FAN_WIND_DISTURBANCE_REVERSE = WindDisturbanceType.register(
		TCAConstants.id("copper_fan_reverse"),
		CopperFanWindDisturbance.CODEC,
		CopperFanWindDisturbance.STREAM_CODEC
	);

	public static void init() {
	}
}
