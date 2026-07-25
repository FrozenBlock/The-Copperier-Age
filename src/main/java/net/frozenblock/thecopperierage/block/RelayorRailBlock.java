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
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RelayorRailBlock extends BaseRailBlock {
	public static final MapCodec<RelayorRailBlock> CODEC = simpleCodec(RelayorRailBlock::new);
	public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
	public static final EnumProperty<DirectionSign> DIRECTION = EnumProperty.create("direction", DirectionSign.class);
	public static final EnumProperty<Appearance> APPEARANCE = EnumProperty.create("appearance", Appearance.class);

	private static final double BOOST_PER_TICK = 0.06D;
	private static final double LAUNCH_FROM_REST = 0.2D;
	private static final double MIN_MOVING_SPEED = 0.01D;
	private static final int MAX_CHAIN_LENGTH = 15;
	private static final int OCCUPIED_CHECK_INTERVAL = 2;

	public RelayorRailBlock(Properties properties) {
		super(true, properties);
		this.registerDefaultState(
			this.stateDefinition.any()
				.setValue(SHAPE, RailShape.NORTH_SOUTH)
				.setValue(DIRECTION, DirectionSign.NEGATIVE)
				.setValue(POWERED, false)
				.setValue(OCCUPIED, false)
				.setValue(APPEARANCE, Appearance.LOCKED_UNCONNECTED)
				.setValue(WATERLOGGED, false)
		);
	}

	@Override
	public MapCodec<RelayorRailBlock> codec() {
		return CODEC;
	}

	@Override
	public Property<RailShape> getShapeProperty() {
		return SHAPE;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(SHAPE, DIRECTION, POWERED, OCCUPIED, APPEARANCE, WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		final BlockState state = super.getStateForPlacement(context);
		final Player player = context.getPlayer();
		if (player == null) return state;

		final Direction.Axis axis = axisOf(state.getValue(SHAPE));
		final Vec3 look = player.getLookAngle();
		final double along = axis == Direction.Axis.X ? look.x : look.z;
		return state.setValue(DIRECTION, along < 0.0D ? DirectionSign.NEGATIVE : DirectionSign.POSITIVE);
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		BlockState rotated = state;
		if (rotation == Rotation.CLOCKWISE_180 || rotation == Rotation.COUNTERCLOCKWISE_90) {
			rotated = rotated.setValue(DIRECTION, rotated.getValue(DIRECTION).negate());
		}
		return rotated.setValue(SHAPE, this.rotate(rotated.getValue(SHAPE), rotation));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		BlockState mirrored = state;
		if (mirror.rotation().inverts(axisOf(state.getValue(SHAPE)))) {
			mirrored = mirrored.setValue(DIRECTION, mirrored.getValue(DIRECTION).negate());
		}
		return mirrored.setValue(SHAPE, this.mirror(mirrored.getValue(SHAPE), mirror));
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);
		if (level.isClientSide() || state.is(oldState.getBlock())) return;
		syncPower(level, pos);
		refreshChain(level, pos);
		updateChainComparators(level, pos);
	}

	@Override
	protected void neighborChanged(
		BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston
	) {
		super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
		if (level.isClientSide()) return;
		syncPower(level, pos);
		refreshChain(level, pos);
	}

	private static void syncPower(Level level, BlockPos pos) {
		final BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof RelayorRailBlock rail)) return;

		final boolean[] chainSignal = {false};
		rail.forEachInChain(level, pos, state, (chainPos, chainState) -> {
			if (level.hasNeighborSignal(chainPos)) chainSignal[0] = true;
		});
		if (chainSignal[0] == state.getValue(POWERED)) return;

		rail.forEachInChain(level, pos, state, (chainPos, chainState) -> {
			if (chainState.getValue(POWERED) != chainSignal[0]) {
				level.setBlock(chainPos, chainState.setValue(POWERED, chainSignal[0]), UPDATE_ALL);
			}
		});
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
		super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
		updateChainComparators(level, pos);
		for (Direction side : Direction.Plane.HORIZONTAL) refreshChain(level, pos.relative(side));
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!state.getValue(OCCUPIED)) return;
		releaseIfEmpty(level, pos, state);
		if (level.getBlockState(pos).getValue(OCCUPIED)) level.scheduleTick(pos, this, OCCUPIED_CHECK_INTERVAL);
	}

	@Override
	protected boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction side) {
		final int[] docked = {0};
		this.forEachInChain(level, pos, state, (chainPos, chainState) -> {
			if (chainState.getValue(OCCUPIED)) docked[0]++;
		});
		return Math.min(15, docked[0]);
	}

	private static void updateChainComparators(Level level, BlockPos pos) {
		final BlockState state = level.getBlockState(pos);
		if (state.getBlock() instanceof RelayorRailBlock rail) {
			rail.forEachInChain(level, pos, state, (chainPos, chainState) -> level.updateNeighbourForOutputSignal(chainPos, chainState.getBlock()));
		} else {
			level.updateNeighbourForOutputSignal(pos, level.getBlockState(pos).getBlock());
		}
	}

	public boolean isDockable(BlockState state) {
		return !state.getValue(OCCUPIED) && !state.getValue(POWERED);
	}

	public Direction getDirection(BlockState state) {
		final Direction.Axis axis = axisOf(state.getValue(SHAPE));
		return state.getValue(DIRECTION) == DirectionSign.NEGATIVE ? axis.getNegative() : axis.getPositive();
	}

	private static Direction.Axis axisOf(RailShape shape) {
		return switch (shape) {
			case EAST_WEST, ASCENDING_EAST, ASCENDING_WEST -> Direction.Axis.X;
			default -> Direction.Axis.Z;
		};
	}

	@Nullable
	private static Direction slopeOf(RailShape shape) {
		return switch (shape) {
			case ASCENDING_EAST -> Direction.EAST;
			case ASCENDING_WEST -> Direction.WEST;
			case ASCENDING_NORTH -> Direction.NORTH;
			case ASCENDING_SOUTH -> Direction.SOUTH;
			default -> null;
		};
	}

	@Nullable
	private BlockPos nextInChain(Level level, BlockPos pos, BlockState state, Direction towards) {
		final Direction chainDirection = this.getDirection(state);
		final Predicate<BlockState> sameChain = other ->
			other.getBlock() == this && this.getDirection(other) == chainDirection;

		final Direction slope = slopeOf(state.getValue(SHAPE));
		if (slope == towards) {
			final BlockPos up = pos.relative(towards).above();
			return sameChain.test(level.getBlockState(up)) ? up : null;
		}

		final BlockPos level0 = pos.relative(towards);
		if (sameChain.test(level.getBlockState(level0))) return level0;
		final BlockPos down = level0.below();
		return sameChain.test(level.getBlockState(down)) ? down : null;
	}

	@Nullable
	public BlockPos getAhead(Level level, BlockPos pos, BlockState state) {
		return this.nextInChain(level, pos, state, this.getDirection(state));
	}

	@Nullable
	public BlockPos getBehind(Level level, BlockPos pos, BlockState state) {
		return this.nextInChain(level, pos, state, this.getDirection(state).getOpposite());
	}

	public void forEachInChain(Level level, BlockPos pos, BlockState state, ChainVisitor visitor) {
		BlockPos cursor = pos;
		BlockState cursorState = state;
		for (int i = 0; i < MAX_CHAIN_LENGTH; i++) {
			final BlockPos behind = this.getBehind(level, cursor, cursorState);
			if (behind == null) break;
			cursor = behind;
			cursorState = level.getBlockState(cursor);
		}

		for (int i = 0; i < MAX_CHAIN_LENGTH + 1; i++) {
			visitor.visit(cursor, cursorState);
			final BlockPos ahead = this.getAhead(level, cursor, cursorState);
			if (ahead == null) break;
			cursor = ahead;
			cursorState = level.getBlockState(cursor);
		}
	}

	@FunctionalInterface
	public interface ChainVisitor {
		void visit(BlockPos pos, BlockState state);
	}

	private boolean shouldDock(Level level, BlockPos pos, BlockState state) {
		if (!this.isDockable(state)) return false;
		return !this.hasDockableAhead(level, pos, state);
	}

	private boolean hasDockableAhead(Level level, BlockPos pos, BlockState state) {
		final BlockPos ahead = this.getAhead(level, pos, state);
		return ahead != null && this.isDockable(level.getBlockState(ahead));
	}

	private void dock(ServerLevel level, BlockPos pos, BlockState state, AbstractMinecart minecart) {
		minecart.setDeltaMovement(Vec3.ZERO);
		final RailShape shape = state.getValue(SHAPE);
		minecart.setPos(Vec3.atBottomCenterOf(pos).add(0.0D, shape.isSlope() ? 0.6D : 0.1D, 0.0D));
		level.setBlock(pos, state.setValue(OCCUPIED, true), UPDATE_ALL);
		level.scheduleTick(pos, this, OCCUPIED_CHECK_INTERVAL);
		refreshChain(level, pos);
		updateChainComparators(level, pos);
	}

	private void release(ServerLevel level, BlockPos pos, BlockState state, AbstractMinecart minecart) {
		if (state.getValue(OCCUPIED)) {
			level.setBlock(pos, state.setValue(OCCUPIED, false), UPDATE_ALL);
			refreshChain(level, pos);
			updateChainComparators(level, pos);
		}

		final Direction direction = this.getDirection(state);
		final Vec3 velocity = minecart.getDeltaMovement();
		final double speed = velocity.horizontalDistance();
		final double target = speed > MIN_MOVING_SPEED ? speed + BOOST_PER_TICK : LAUNCH_FROM_REST;
		minecart.setDeltaMovement(direction.getStepX() * target, velocity.y, direction.getStepZ() * target);
	}

	public static boolean isDocked(Level level, BlockPos pos, BlockState state, AbstractMinecart minecart) {
		return state.getBlock() instanceof RelayorRailBlock
			&& state.getValue(OCCUPIED)
			&& !state.getValue(POWERED)
			&& isCartOn(pos, minecart);
	}

	private static boolean isCartOn(BlockPos pos, AbstractMinecart minecart) {
		return BlockPos.containing(minecart.position()).equals(pos);
	}

	public static boolean handleCart(ServerLevel level, BlockPos pos, BlockState state, AbstractMinecart minecart) {
		if (!(state.getBlock() instanceof RelayorRailBlock rail)) return false;

		if (state.getValue(POWERED)) {
			rail.release(level, pos, state, minecart);
			return false;
		}

		if (state.getValue(OCCUPIED)) {
			minecart.setDeltaMovement(Vec3.ZERO);
			return true;
		}

		if (rail.shouldDock(level, pos, state)) {
			rail.dock(level, pos, state, minecart);
			return true;
		}
		return false;
	}

	public static void releaseIfEmpty(ServerLevel level, BlockPos pos, BlockState state) {
		if (!(state.getBlock() instanceof RelayorRailBlock) || !state.getValue(OCCUPIED)) return;
		final boolean stillHeld = level.getEntitiesOfClass(AbstractMinecart.class, new AABB(pos).inflate(0.5D))
			.stream()
			.anyMatch(minecart -> isCartOn(pos, minecart));
		if (stillHeld) return;
		level.setBlock(pos, state.setValue(OCCUPIED, false), UPDATE_ALL);
		refreshChain(level, pos);
		updateChainComparators(level, pos);
	}

	public static void refreshChain(Level level, BlockPos pos) {
		final BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof RelayorRailBlock rail)) return;

		final java.util.List<BlockPos> chain = new java.util.ArrayList<>();
		rail.forEachInChain(level, pos, state, (chainPos, chainState) -> chain.add(chainPos));

		int occupiedCount = 0;
		for (BlockPos chainPos : chain) {
			if (level.getBlockState(chainPos).getValue(OCCUPIED)) occupiedCount++;
		}

		for (BlockPos chainPos : chain) {
			final BlockState chainState = level.getBlockState(chainPos);
			if (!(chainState.getBlock() instanceof RelayorRailBlock chainRail)) continue;

			final boolean othersOccupied = occupiedCount - (chainState.getValue(OCCUPIED) ? 1 : 0) > 0;
			final boolean hasAhead = chainRail.getAhead(level, chainPos, chainState) != null;
			final boolean hasBehind = chainRail.getBehind(level, chainPos, chainState) != null;
			final boolean engaged = chainState.getValue(OCCUPIED) || !chainRail.hasDockableAhead(level, chainPos, chainState);
			final Appearance appearance = Appearance.of(chainState.getValue(POWERED), engaged, othersOccupied, hasAhead, hasBehind);

			if (chainState.getValue(APPEARANCE) != appearance) {
				level.setBlock(chainPos, chainState.setValue(APPEARANCE, appearance), Block.UPDATE_CLIENTS);
			}
		}
	}

	public enum Appearance implements StringRepresentable {
		POWERED_UNCONNECTED, POWERED_FRONT, POWERED_MIDDLE, POWERED_END,
		LOCKED_UNCONNECTED, LOCKED_FRONT, LOCKED_MIDDLE, LOCKED_END,
		UNLOCKED_MIDDLE, UNLOCKED_END,
		UNLOCKED_SIGNAL_MIDDLE, UNLOCKED_SIGNAL_END,
		LOCKED_SIGNAL_FRONT, LOCKED_SIGNAL_MIDDLE, LOCKED_SIGNAL_END;

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase();
		}

		public static Appearance of(boolean powered, boolean engaged, boolean signalled, boolean hasAhead, boolean hasBehind) {
			final String row = hasAhead ? (hasBehind ? "MIDDLE" : "END") : (hasBehind ? "FRONT" : "UNCONNECTED");
			if (powered) return valueOf("POWERED_" + row);

			final boolean locked = engaged || !hasAhead;
			final boolean showSignal = signalled && hasAhead;
			if (locked) return valueOf((showSignal ? "LOCKED_SIGNAL_" : "LOCKED_") + row);
			return valueOf((showSignal ? "UNLOCKED_SIGNAL_" : "UNLOCKED_") + row);
		}
	}

	public enum DirectionSign implements StringRepresentable {
		NEGATIVE,
		POSITIVE;

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase();
		}

		public DirectionSign negate() {
			return this == NEGATIVE ? POSITIVE : NEGATIVE;
		}
	}
}
