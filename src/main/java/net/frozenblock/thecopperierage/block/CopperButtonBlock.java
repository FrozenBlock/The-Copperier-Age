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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class CopperButtonBlock extends ButtonBlock {

	public CopperButtonBlock(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties properties) {
		super(BlockSetType.COPPER, getPressTicks(weatherState), properties);
	}

	@Contract(pure = true)
	protected static int getPressTicks(WeatheringCopper.@NotNull WeatherState weatherState) {
		return switch (weatherState) {
			case UNAFFECTED -> 5;
			case EXPOSED -> 10;
			case WEATHERED -> 20;
			case OXIDIZED -> 40;
		};
	}

}
