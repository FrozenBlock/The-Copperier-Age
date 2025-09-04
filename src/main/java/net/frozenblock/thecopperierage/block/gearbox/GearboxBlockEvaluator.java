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

package net.frozenblock.thecopperierage.block.gearbox;

import net.frozenblock.thecopperierage.block.GearboxBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.NotNull;

public class GearboxBlockEvaluator {

	public GearboxBlockEvaluator() {
	}

	public void updatePowerStrength(Level level, BlockPos pos, @NotNull BlockState state) {
		final int newPower = this.calculateTargetStrength(level, pos, state);
		if (state.getValue(GearboxBlock.POWER) != newPower) {
			if (level.getBlockState(pos) == state) level.setBlock(pos, state.setValue(GearboxBlock.POWER, newPower), Block.UPDATE_CLIENTS);
			this.updateNeighboringBlocks(level, pos, state);
		}
	}

	public void updateNeighboringBlocks(Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
		final Direction facing = state.getValue(GearboxBlock.FACING);
		final Direction behind = facing.getOpposite();
		final Orientation orientation = ExperimentalRedstoneUtils.initialOrientation(level, behind, null);

		level.updateNeighborsAtExceptFromFacing(pos.relative(behind), state.getBlock(), facing, orientation);
		level.updateNeighborsAtExceptFromFacing(pos, state.getBlock(), facing, orientation);
	}

	protected boolean hasBlockSignal(Level level, BlockPos pos, @NotNull BlockState state) {
		if (!(state.getBlock() instanceof GearboxBlock gearboxBlock)) return false;
		return gearboxBlock.hasBlockSignal(level, pos, state);
	}

	protected int getGearboxPower(BlockPos pos, @NotNull BlockState state, Direction facing) {
		if (!(state.getBlock() instanceof GearboxBlock)) return 0;
		return state.getValue(GearboxBlock.FACING) == facing ? state.getValue(GearboxBlock.POWER) : 0;
	}

	protected int getIncomingGearboxPower(Level level, BlockPos pos, @NotNull BlockState state) {
		if (!(state.getBlock() instanceof GearboxBlock gearboxBlock)) return 0;
		int power = 0;

		final Direction facing = state.getValue(GearboxBlock.FACING);
		for (Direction direction : gearboxBlock.getInputDirections(facing)) {
			final BlockPos offsetPos = pos.relative(direction);
			final BlockState offsetState = level.getBlockState(offsetPos);
			power = Math.max(power, this.getGearboxPower(offsetPos, offsetState, facing));
		}

		return Math.max(0, power - 1);
	}

	private int calculateTargetStrength(Level level, BlockPos pos, BlockState state) {
		if (this.hasBlockSignal(level, pos, state)) return 15;
		return this.getIncomingGearboxPower(level, pos, state);
	}
}
