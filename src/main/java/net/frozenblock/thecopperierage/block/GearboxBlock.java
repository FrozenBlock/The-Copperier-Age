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
import net.frozenblock.thecopperierage.block.gearbox.GearboxBlockEvaluator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GearboxBlock extends DirectionalBlock {
    public static final MapCodec<GearboxBlock> CODEC = simpleCodec(GearboxBlock::new);
	public static final IntegerProperty POWER = BlockStateProperties.POWER;
	private static final GearboxBlockEvaluator EVALUATOR = new GearboxBlockEvaluator();

    public GearboxBlock(Properties properties) {
        super(properties);
		this.registerDefaultState(
			this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(POWER, 0)
		);
    }

    @Override
    public @NotNull MapCodec<? extends GearboxBlock> codec() {
        return CODEC;
    }

	@Override
	public BlockState getStateForPlacement(@NotNull BlockPlaceContext blockPlaceContext) {
		final Direction facing = blockPlaceContext.getNearestLookingDirection().getOpposite();
		return this.defaultBlockState().setValue(FACING, facing);
	}

	public void updatePowerStrength(Level level, BlockPos pos, BlockState state) {
		EVALUATOR.updatePowerStrength(level, pos, state);
	}

	@Override
	protected void onPlace(@NotNull BlockState state, Level level, BlockPos pos, @NotNull BlockState replacingState, boolean bl) {
		if (state.is(replacingState.getBlock()) || level.isClientSide()) return;
		this.updatePowerStrength(level, pos, state);
		this.updateNeighborsOfNeighboringGearBoxes(level, pos, state.getValue(FACING));
	}

	@Override
	protected void neighborChanged(BlockState state, @NotNull Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
		if (level.isClientSide()) return;
		this.updatePowerStrength(level, pos, state);
	}

	@Override
	protected void affectNeighborsAfterRemoval(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, boolean bl) {
		if (!bl) {
			for (Direction direction : Direction.values()) level.updateNeighborsAt(pos.relative(direction), this);
			this.updatePowerStrength(level, pos, state);
			this.updateNeighborsOfNeighboringGearBoxes(level, pos, state.getValue(FACING));
		}
	}

	private void updateNeighborsOfNeighboringGearBoxes(Level level, BlockPos pos, Direction facing) {
		for (Direction direction : this.getInputDirections(facing)) this.checkCornerChangeAt(level, pos.relative(direction), facing);
	}

	private void checkCornerChangeAt(@NotNull Level level, BlockPos pos, Direction originalFacing) {
		final BlockState state = level.getBlockState(pos);
		if (!state.is(this)) return;

		final Direction facing = state.getValue(FACING);
		if (facing != originalFacing) return;

		level.updateNeighborsAt(pos, this);
		for (Direction direction : Direction.values()) level.updateNeighborsAt(pos.relative(direction), this);
	}

	public List<Direction> getInputDirections(@NotNull Direction facing) {
		final Direction.Axis axis = facing.getAxis();
		return Arrays.stream(Direction.values())
			.filter(direction -> direction.getAxis() != axis)
			.collect(Collectors.toList());
	}

	public boolean hasBlockSignal(@NotNull Level level, BlockPos pos, @NotNull BlockState state) {
		for (Direction direction : this.getInputDirections(state.getValue(FACING))) {
			final int blockPower = level.getSignal(pos.relative(direction), direction);
			if (blockPower > 0) return true;
		}
		return false;
	}

	@Override
	protected boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	protected int getDirectSignal(@NotNull BlockState blockState, BlockGetter level, BlockPos pos, Direction direction) {
		return blockState.getSignal(level, pos, direction);
	}

	@Override
	protected int getSignal(@NotNull BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return state.getValue(POWER) > 0 && state.getValue(FACING) == direction ? 15 : 0;
	}

	@Override
	protected @NotNull BlockState rotate(@NotNull BlockState blockState, @NotNull Rotation rotation) {
		return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
	}

	@Override
	protected @NotNull BlockState mirror(@NotNull BlockState blockState, @NotNull Mirror mirror) {
		return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
		builder.add(FACING, POWER);
	}

}
