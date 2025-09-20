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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.frozenblock.thecopperierage.block.gearbox.GearboxBlockEvaluator;
import net.frozenblock.thecopperierage.registry.TCASounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
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

	@Override
	public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
		final int power =  state.getValue(POWER);
		if (power % 2 == 0) return;
		if (random.nextFloat() > 0.05F) return;

		level.playLocalSound(
			pos.getX() + 0.5D,
			pos.getY() + 0.5D,
			pos.getZ() + 0.5D,
			TCASounds.BLOCK_GEARBOX_IDLE,
			SoundSource.BLOCKS,
			0.1F,
			0.15F + (power / 25F) + (random.nextFloat() * 0.1F),
			false
		);
	}

	public void updatePowerStrength(Level level, BlockPos pos, BlockState state) {
		EVALUATOR.updatePowerStrength(level, pos, state);
	}

	public void updateNeighboringBlocks(Level level, BlockPos pos, BlockState state) {
		EVALUATOR.updateNeighboringBlocks(level, pos, state);
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		this.updatePowerStrength(level, pos, state);
	}

	@Override
	protected void onPlace(@NotNull BlockState state, @NotNull Level level, BlockPos pos, @NotNull BlockState replacingState, boolean bl) {
		if (level.isClientSide() || state.is(replacingState.getBlock())) return;
		level.scheduleTick(pos, this, 1);
	}

	@Override
	protected void neighborChanged(
		BlockState state,
		@NotNull Level level,
		@NotNull BlockPos pos,
		Block block,
		@Nullable Orientation orientation,
		boolean movedByPiston
	) {
		if (level.isClientSide() || level.getBlockTicks().hasScheduledTick(pos, this)) return;
		level.scheduleTick(pos, this, 1);
	}

	@Override
	protected void affectNeighborsAfterRemoval(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, boolean bl) {
		if (!bl) this.updateNeighboringBlocks(level, pos, state);
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
	protected int getDirectSignal(@NotNull BlockState blockState, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull Direction direction) {
		return blockState.getSignal(level, pos, direction);
	}

	@Override
	protected int getSignal(@NotNull BlockState state, BlockGetter level, BlockPos pos, @NotNull Direction direction) {
		return state.getValue(POWER) > 0 && state.getValue(FACING) == direction ? 15 : 0;
	}

	@Override
	protected boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	protected int getAnalogOutputSignal(@NotNull BlockState state, Level level, BlockPos pos, @NotNull Direction direction) {
		if (direction == state.getValue(FACING).getOpposite()) return 0;
		return state.getValue(POWER);
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
