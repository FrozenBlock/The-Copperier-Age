/*
 * Copyright 2025 FrozenBlock
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
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class WeatheringCopperFanBlock extends CopperFanBlock implements WeatheringCopper {
	public static final MapCodec<WeatheringCopperFanBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
			WeatherState.CODEC.fieldOf("weathering_state").forGetter(WeatheringCopperFanBlock::getAge),
			propertiesCodec()
		).apply(instance, WeatheringCopperFanBlock::new)
	);
	private final WeatheringCopper.WeatherState weatherState;

	@Override
	public @NotNull MapCodec<? extends WeatheringCopperFanBlock> codec() {
		return CODEC;
	}

	public WeatheringCopperFanBlock(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties properties) {
		super(properties);
		this.weatherState = weatherState;
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		this.changeOverTime(state, level, pos, random);
	}

	@Override
	protected boolean isRandomlyTicking(@NotNull BlockState blockState) {
		return WeatheringCopper.getNext(blockState.getBlock()).isPresent();
	}

	@Override
	public WeatheringCopper.@NotNull WeatherState getAge() {
		return this.weatherState;
	}
}
