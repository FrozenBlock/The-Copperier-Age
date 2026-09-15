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

package net.frozenblock.thecopperierage.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.WeatheringCopper;

public class CopperCrossRailBlock extends CrossRailBlock implements CopperRail {
	public static final MapCodec<CopperCrossRailBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
			WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(block -> block.weatherState),
			propertiesCodec()
		).apply(instance, CopperCrossRailBlock::new)
	);

	public final WeatheringCopper.WeatherState weatherState;

	public CopperCrossRailBlock(WeatheringCopper.WeatherState weatherState, Properties properties) {
		super(properties);
		this.weatherState = weatherState;
	}

	@Override
	public MapCodec<? extends CopperCrossRailBlock> codec() {
		return CODEC;
	}

	@Override
	public WeatheringCopper.WeatherState getWeatherState() {
		return this.weatherState;
	}
}
