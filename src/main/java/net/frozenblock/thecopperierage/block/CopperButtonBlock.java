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

import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class CopperButtonBlock extends ButtonBlock implements WeatheringCopper {
	private final WeatherState weatherState;

	public CopperButtonBlock(WeatherState weatherState, BlockBehaviour.Properties properties) {
		super(BlockSetType.COPPER, getPressTicks(weatherState), properties);
		this.weatherState = weatherState;
	}

	public CopperButtonBlock(BlockBehaviour.Properties properties) {
		this(WeatherState.UNAFFECTED, properties);
	}

	@Override
	public WeatherState getAge() {
		return this.weatherState;
	}

	private static int getPressTicks(WeatherState weatherState) {
		return switch (weatherState) {
			case UNAFFECTED -> 5;
			case EXPOSED -> 10;
			case WEATHERED -> 20;
			case OXIDIZED -> 40;
		};
	}

	public static class Waxed extends ButtonBlock {
		private final WeatherState weatherState;

		public Waxed(WeatherState weatherState, BlockBehaviour.Properties properties) {
			super(BlockSetType.COPPER, getPressTicks(weatherState), properties);
			this.weatherState = weatherState;
		}

		public Waxed(BlockBehaviour.Properties properties) {
			this(WeatherState.UNAFFECTED, properties);
		}

		public WeatherState getAge() {
			return this.weatherState;
		}
	}
}
