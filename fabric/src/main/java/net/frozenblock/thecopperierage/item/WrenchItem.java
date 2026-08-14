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

package net.frozenblock.thecopperierage.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.frozenblock.thecopperierage.block.rotation.BlockRotationHelper;
import net.frozenblock.thecopperierage.registry.TCASounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import org.jetbrains.annotations.Nullable;

public class WrenchItem extends Item {

	public WrenchItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		final Level level = context.getLevel();
		final BlockPos pos = context.getClickedPos();
		final BlockState state = level.getBlockState(pos);
		final BlockEntity blockEntity = level.getBlockEntity(pos);

		final BlockRotationHelper.ResultType resultType = BlockRotationHelper.getRotationResultType(state, blockEntity);
		if (resultType == BlockRotationHelper.ResultType.PASS) return InteractionResult.PASS;
		if (resultType == BlockRotationHelper.ResultType.FAIL) return InteractionResult.FAIL;

		final Optional<Runnable> rotateDoor = BlockRotationHelper.rotateDoor(level, pos, state, state1 -> state1.cycle(DoorBlock.HINGE));
		if (rotateDoor.isPresent()) return onSuccessfulWrench(context, level, pos, rotateDoor.get());

		if (state.getBlock() instanceof BaseRailBlock baseRailBlock) {
			final Property<RailShape> property = baseRailBlock.getShapeProperty();
			BlockState newState = state.cycle(property);
			while (newState != state) {
				if (!BaseRailBlock.shouldBeRemoved(pos, level, newState.getValue(property))) {
					final BlockState finalNewState = newState;
					return onSuccessfulWrench(context, level, pos, () -> changeIntoState(context, finalNewState));
				}
				newState = newState.cycle(property);
			}
			return InteractionResult.FAIL;
		}

		if (state.hasProperty(BlockStateProperties.ROTATION_16)) {
			final BlockState newState = state.cycle(BlockStateProperties.ROTATION_16);
			if (newState != state && newState.canSurvive(level, pos)) return onSuccessfulWrench(context, level, pos, () -> changeIntoState(context, newState));
		}

		if (state.hasProperty(BlockStateProperties.SLAB_TYPE)) {
			final SlabType slabType = state.getValue(BlockStateProperties.SLAB_TYPE);
			if (slabType != SlabType.DOUBLE) {
				final BlockState newState = state.setValue(BlockStateProperties.SLAB_TYPE, slabType == SlabType.BOTTOM ? SlabType.TOP : SlabType.BOTTOM);
				if (newState != state && newState.canSurvive(level, pos)) return onSuccessfulWrench(context, level, pos, () -> changeIntoState(context, newState));
			}
		}

		final Optional<Runnable> swapLanternHangingState = BlockRotationHelper.swapLanternHangingState(level, pos, state);
		if (swapLanternHangingState.isPresent()) return onSuccessfulWrench(context, level, pos, swapLanternHangingState.get());

		final List<Direction> directionsToTry = new ArrayList<>();

		final Direction reorientedA = getReorientedFace(context.getClickedFace(), state);
		directionsToTry.add(reorientedA);
		directionsToTry.add(reorientedA.getOpposite());

		final Direction reorientedB = getReorientedFace(context.getHorizontalDirection(), state);
		if (reorientedA.getAxis() != reorientedB.getAxis()) {
			directionsToTry.add(reorientedB);
			directionsToTry.add(reorientedB.getOpposite());
		}

		boolean triedToSet = false;
		for (Direction direction : directionsToTry) {
			final List<Function<BlockState, BlockState>> states = getPossibleBlockStates(state, direction);

			for (Function<BlockState, BlockState> mutator : states) {
				final BlockState newState = mutator.apply(state);
				triedToSet = true;
				if (newState != state && newState.canSurvive(level, pos)) {
					return onSuccessfulWrench(context, level, pos, () -> changeIntoState(context, newState));
				}
			}
		}

		return triedToSet ? InteractionResult.FAIL : InteractionResult.PASS;
	}

	public static InteractionResult onSuccessfulWrench(UseOnContext context, Level level, BlockPos pos, Runnable serverRunnable) {
		final Player player = context.getPlayer();
		level.playSound(player, pos, TCASounds.ITEM_WRENCH_USE, SoundSource.BLOCKS, 0.75F, 0.9F + (level.getRandom().nextFloat() * 0.2F));
		if (!level.isClientSide()) {
			serverRunnable.run();
			if (player != null) context.getItemInHand().hurtAndBreak(1, player, context.getHand());
		}

		return InteractionResult.SUCCESS;
	}

	public static Direction getReorientedFace(Direction direction, BlockState state) {
		if (state.is(BlockTags.FENCE_GATES)) return direction.getOpposite();
		return direction;
	}

	public static List<Function<BlockState, BlockState>> getPossibleBlockStates(BlockState state, Direction clickedFace) {
		final List<Function<BlockState, BlockState>> stateMutators = new ArrayList<>();

		if (state.hasProperty(BlockStateProperties.ATTACH_FACE)) {
			final AttachFace attachFace = state.getValue(BlockStateProperties.ATTACH_FACE);
			if (clickedFace == Direction.UP) {
				if (attachFace == AttachFace.FLOOR) {
					stateMutators.add(mutatedState -> mutatedState.setValue(BlockStateProperties.ATTACH_FACE, AttachFace.WALL));
				} else if (attachFace == AttachFace.WALL) {
					stateMutators.add(mutatedState -> mutatedState.setValue(BlockStateProperties.ATTACH_FACE, AttachFace.CEILING));
				}
			} else if (clickedFace == Direction.DOWN) {
				if (attachFace == AttachFace.CEILING) {
					stateMutators.add(mutatedState -> mutatedState.setValue(BlockStateProperties.ATTACH_FACE, AttachFace.WALL));
				} else if (attachFace == AttachFace.WALL) {
					stateMutators.add(mutatedState -> mutatedState.setValue(BlockStateProperties.ATTACH_FACE, AttachFace.FLOOR));
				}
			}
		}

		for (Property property : state.getProperties()) {
			final List values = property.getPossibleValues();
			for (Object value : values) {
				if (value instanceof Direction direction) {
					if (direction == clickedFace) stateMutators.add(mutatedState -> mutatedState.setValue(property, direction));
				} else if (value instanceof Direction.Axis axis) {
					if (axis == clickedFace.getAxis()) stateMutators.add(mutatedState -> mutatedState.setValue(property, axis));
				}
			}
		}

		return stateMutators;
	}

	public static void changeIntoState(UseOnContext context, BlockState state) {
		changeIntoState(context.getLevel(), context.getClickedPos(), state, context.getPlayer());
	}

	public static void changeIntoState(Level level, BlockPos pos, BlockState state, @Nullable Player player) {
		final BlockState newState = Block.updateFromNeighbourShapes(state, level, pos);
		if (!level.setBlockAndUpdate(pos, newState)) return;
		if (level.isClientSide()) return;

		level.updateNeighborsAt(pos, newState.getBlock());
		for (Direction direction : Direction.values()) {
			final BlockPos offsetPos = pos.relative(direction);
			final BlockState offsetState = level.getBlockState(offsetPos);
			level.neighborChanged(pos, offsetState.getBlock(), ExperimentalRedstoneUtils.initialOrientation(level, direction.getOpposite(), null));
		}

		level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state));
	}
}
