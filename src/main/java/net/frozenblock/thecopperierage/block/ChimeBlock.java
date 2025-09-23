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
import net.frozenblock.thecopperierage.block.entity.ChimeBlockEntity;
import net.frozenblock.thecopperierage.block.state.properties.ChimeAttachType;
import net.frozenblock.thecopperierage.registry.TCABlockEntityTypes;
import net.frozenblock.thecopperierage.registry.TCABlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;

public class ChimeBlock extends BaseEntityBlock {
	public static final MapCodec<ChimeBlock> CODEC = simpleCodec(ChimeBlock::new);
	public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
	public static final EnumProperty<ChimeAttachType> ATTACHMENT = TCABlockStateProperties.CHIME_ATTACHMENT;
	private static final VoxelShape SUPPORT_CHAIN_SHAPE = Block.box(7D, 12D, 7D, 9D, 16D, 9D);
	private static final VoxelShape BAR_SHAPE = Block.box(7D, 10D, 0D, 9D, 12D, 16D);
	private static final VoxelShape CHIMES_SHAPE = Shapes.or(
		makeChimeTubeShape(1D, 6D),
		makeChimeTubeShape(4D, 4D),
		makeChimeTubeShape(7D, 2D),
		makeChimeTubeShape(10D, 0D),
		makeChimeTubeShape(13D, 5D)
	);
	private static final VoxelShape ENTITY_INSIDE_SHAPE = Block.box(7D, 0D, 0D, 9D, 10D, 16D);
	private static final VoxelShape SHAPE_CEILING_NO_CHIMES = Shapes.or(SUPPORT_CHAIN_SHAPE, BAR_SHAPE);
	private static final VoxelShape SHAPE_CEILING = Shapes.or(SHAPE_CEILING_NO_CHIMES, CHIMES_SHAPE);
	private static final Map<Direction, VoxelShape> CEILING_SHAPES_NO_CHIMES = Shapes.rotateHorizontal(SHAPE_CEILING_NO_CHIMES);
	private static final Map<Direction, VoxelShape> CEILING_SHAPES = Shapes.rotateHorizontal(SHAPE_CEILING);
	private static final VoxelShape SHAPE_WALL_NO_CHIMES = BAR_SHAPE;
	private static final VoxelShape SHAPE_WALL = Shapes.or(SHAPE_WALL_NO_CHIMES, CHIMES_SHAPE);
	private static final Map<Direction, VoxelShape> WALL_SHAPES_NO_CHIMES = Shapes.rotateHorizontal(SHAPE_WALL_NO_CHIMES);
	private static final Map<Direction, VoxelShape> WALL_SHAPES = Shapes.rotateHorizontal(SHAPE_WALL);
	private static final Map<Direction, VoxelShape> ENTITY_INSIDE_SHAPES = Shapes.rotateHorizontal(ENTITY_INSIDE_SHAPE);

	public ChimeBlock(@NotNull Properties settings) {
		super(settings);
	}

	@NotNull
	@Override
	protected MapCodec<? extends ChimeBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, ATTACHMENT);
	}

	private VoxelShape getVoxelShape(@NotNull BlockState state, boolean forCollision) {
		final Direction direction = state.getValue(FACING);
		return switch (state.getValue(ATTACHMENT)) {
			case WALL -> !forCollision ? WALL_SHAPES.get(direction) : WALL_SHAPES_NO_CHIMES.get(direction);
			case CEILING -> !forCollision ? CEILING_SHAPES.get(direction) : CEILING_SHAPES_NO_CHIMES.get(direction);
		};
	}

	@Override
	protected @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, BlockGetter level, BlockPos pos, CollisionContext collisionContext) {
		return this.getVoxelShape(state, true);
	}

	@Override
	protected @NotNull VoxelShape getShape(@NotNull BlockState state, BlockGetter level, BlockPos pos, CollisionContext collisionContext) {
		return this.getVoxelShape(state, false);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		final Direction direction = context.getClickedFace();
		final BlockPos pos = context.getClickedPos();
		final Level level = context.getLevel();

		if (direction == Direction.UP) return null;
		if (direction == Direction.DOWN) {
			final BlockState state = this.defaultBlockState()
				.setValue(ATTACHMENT, ChimeAttachType.CEILING)
				.setValue(FACING, context.getHorizontalDirection());
			return state.canSurvive(level, pos) ? state : null;
		}

		BlockState state = this.defaultBlockState()
			.setValue(FACING, direction.getOpposite())
			.setValue(ATTACHMENT, ChimeAttachType.WALL);
		if (state.canSurvive(level, pos)) return state;

		final BlockPos abovePos = pos.below();
		if (!level.getBlockState(abovePos).isFaceSturdy(level, abovePos, Direction.DOWN)) return null;

		state = state.setValue(ATTACHMENT, ChimeAttachType.CEILING);
		if (state.canSurvive(level, pos)) return state;

		return null;
	}

	@Override
	protected @NotNull BlockState updateShape(
		@NotNull BlockState state,
		@NotNull LevelReader level,
		@NotNull ScheduledTickAccess scheduledTickAccess,
		@NotNull BlockPos pos,
		@NotNull Direction direction,
		@NotNull BlockPos neighborPos,
		@NotNull BlockState neighborState,
		@NotNull RandomSource random
	) {
		final Direction connectedDirection = getConnectedDirection(state);
		if (connectedDirection.getOpposite() == direction && !state.canSurvive(level, pos)) return Blocks.AIR.defaultBlockState();
		return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
	}

	@Override
	public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
		final Direction connectedDirection = getConnectedDirection(state).getOpposite();
		return connectedDirection == Direction.UP
			? Block.canSupportCenter(level, pos.above(), Direction.DOWN)
			: FaceAttachedHorizontalDirectionalBlock.canAttach(level, pos, connectedDirection);
	}

	private static Direction getConnectedDirection(@NotNull BlockState state) {
		if (state.getValue(ATTACHMENT) == ChimeAttachType.CEILING) return Direction.DOWN;
		return state.getValue(FACING).getOpposite();
	}

	@Override
	@Nullable
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new ChimeBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
		return createTickerHelper(blockEntityType, TCABlockEntityTypes.CHIME, ChimeBlockEntity::tick);
	}

	@Override
	public @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public @NotNull BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		// TODO: Sounds
	}

	protected static @NotNull VoxelShape makeChimeTubeShape(double zStart, double yStart) {
		return Block.box(7D, yStart, zStart, 9D, 10D, zStart + 2D);
	}
}
