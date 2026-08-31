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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

public class CrossRailBlock extends BaseRailBlock {
	public static final MapCodec<CrossRailBlock> CODEC = simpleCodec(CrossRailBlock::new);
	public static final EnumProperty<RailShape> SHAPE = EnumProperty.create("shape", RailShape.class, RailShape.NORTH_SOUTH, RailShape.EAST_WEST);

	private static final double MIN_AXIS_SPEED = 1.0E-4D;

	public CrossRailBlock(Properties properties) {
		super(true, properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(SHAPE, RailShape.NORTH_SOUTH).setValue(WATERLOGGED, false));
	}

	@Override
	public MapCodec<CrossRailBlock> codec() {
		return CODEC;
	}

	@Override
	public Property<RailShape> getShapeProperty() {
		return SHAPE;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		final boolean waterlogged = context.getLevel().getFluidState(context.getClickedPos()).is(Fluids.WATER);
		final Direction.Axis axis = context.getHorizontalDirection().getAxis();
		return this.defaultBlockState()
			.setValue(SHAPE, axis == Direction.Axis.X ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH)
			.setValue(WATERLOGGED, waterlogged);
	}

	@Override
	protected BlockState updateDir(Level level, BlockPos pos, BlockState state, boolean first) {
		return state;
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		if (rotation != Rotation.CLOCKWISE_90 && rotation != Rotation.COUNTERCLOCKWISE_90) return state;
		final RailShape shape = state.getValue(SHAPE);
		return state.setValue(SHAPE, shape == RailShape.NORTH_SOUTH ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH);
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(SHAPE, WATERLOGGED);
	}

	public static RailShape travelShape(Level level, BlockPos pos, AbstractMinecart minecart) {
		final boolean eastWestTrack = hasRailBeside(level, pos, Direction.EAST) || hasRailBeside(level, pos, Direction.WEST);
		final boolean northSouthTrack = hasRailBeside(level, pos, Direction.NORTH) || hasRailBeside(level, pos, Direction.SOUTH);

		final Vec3 velocity = minecart.getDeltaMovement();
		final double absX = Math.abs(velocity.x);
		final double absZ = Math.abs(velocity.z);
		final boolean wantsEastWest = Math.max(absX, absZ) < MIN_AXIS_SPEED
			? minecart.getMotionDirection().getAxis() == Direction.Axis.X
			: absX > absZ;

		if (wantsEastWest && !eastWestTrack && northSouthTrack) return RailShape.NORTH_SOUTH;
		if (!wantsEastWest && !northSouthTrack && eastWestTrack) return RailShape.EAST_WEST;
		return wantsEastWest ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH;
	}

	private static boolean hasRailBeside(Level level, BlockPos pos, Direction direction) {
		final BlockPos side = pos.relative(direction);
		return BaseRailBlock.isRail(level.getBlockState(side))
			|| BaseRailBlock.isRail(level.getBlockState(side.below()))
			|| BaseRailBlock.isRail(level.getBlockState(side.above()));
	}
}
