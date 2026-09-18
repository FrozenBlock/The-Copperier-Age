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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class RedstonePumpkinBlock extends CarvedPumpkinBlock {

    public RedstonePumpkinBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

	@Override
	protected boolean shouldRedstoneWireConnectTo(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
		// Only allow connection if this direction matches the output face of the Redstone Pumpkin
		final Direction outputFace = state.getValue(FACING).getOpposite();
		return direction == outputFace;
	}

	@Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        // Output from the back face (opposite of where the pumpkin is facing) like Observers
        return state.getValue(FACING).getOpposite() == direction ? 15 : 0;
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return this.getSignal(state, level, pos, direction);
    }
}
