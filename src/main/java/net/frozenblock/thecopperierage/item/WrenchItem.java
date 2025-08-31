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

package net.frozenblock.thecopperierage.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class WrenchItem extends Item {

	public WrenchItem(@NotNull Properties settings) {
		super(settings);
	}

	@Override
	public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
		final Level level = context.getLevel();
		final BlockPos pos = context.getClickedPos();
		final BlockState state = level.getBlockState(pos);
		final Block block = state.getBlock();

		if (block instanceof DoorBlock doorBlock && !doorBlock.type().canOpenByHand()) return InteractionResult.FAIL;
		if (block instanceof TrapDoorBlock trapDoorBlock && !trapDoorBlock.getType().canOpenByHand()) return InteractionResult.FAIL;

		if (state.is(BlockTags.DOORS)) {
			Optional<DoubleBlockHalf> optionalHalf = state.getOptionalValue(DoorBlock.HALF);
			if (optionalHalf.isPresent()) {
				final DoubleBlockHalf doubleBlockHalf = optionalHalf.get();
				final BlockPos otherPos = pos.relative(doubleBlockHalf.getDirectionToOther());
				Function<BlockState, BlockState> halfStateMutator = otherState -> otherState.trySetValue(DoorBlock.HALF, doubleBlockHalf.getOtherHalf());
				final BlockState flippedHingeState = state.cycle(DoorBlock.HINGE);
				if (level.getBlockState(otherPos).is(state.getBlock())) {
					return onSuccessfulWrench(
						context,
						level,
						pos,
						() -> {
							level.setBlock(pos, flippedHingeState, Block.UPDATE_ALL);
							level.setBlock(otherPos, halfStateMutator.apply(flippedHingeState), Block.UPDATE_ALL);
						}
					);
				}
			}
		}

		if (block instanceof RailBlock railBlock) {
			final Property<RailShape> property = railBlock.getShapeProperty();
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
			if (newState != state && newState.canSurvive(level, pos)) {
				return onSuccessfulWrench(context, level, pos, () -> changeIntoState(context, newState));
			}
		}

		if (state.hasProperty(BlockStateProperties.SLAB_TYPE)) {
			final SlabType slabType = state.getValue(BlockStateProperties.SLAB_TYPE);
			if (slabType != SlabType.DOUBLE) {
				final BlockState newState = state.setValue(BlockStateProperties.SLAB_TYPE, slabType == SlabType.BOTTOM ? SlabType.TOP : SlabType.BOTTOM);
				if (newState != state && newState.canSurvive(level, pos)) {
					return onSuccessfulWrench(context, level, pos, () -> changeIntoState(context, newState));
				}
			}
		}

		List<Direction> directionsToTry = new ArrayList<>();

		final Direction reorientedA = getReorientedFace(context.getClickedFace(), state);
		directionsToTry.add(reorientedA);
		directionsToTry.add(reorientedA.getOpposite());

		final Direction reorientedB = getReorientedFace(context.getHorizontalDirection(), state);
		directionsToTry.add(reorientedB);
		directionsToTry.add(reorientedB.getOpposite());

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

	public static InteractionResult onSuccessfulWrench(@NotNull UseOnContext context, @NotNull Level level, BlockPos pos, Runnable serverRunnable) {
		final Player player = context.getPlayer();
		level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1F, 1F);
		if (!level.isClientSide()) {
			serverRunnable.run();
			if (player != null) context.getItemInHand().hurtAndBreak(1, player, context.getHand());
		}

		return InteractionResult.SUCCESS;
	}

	public static Direction getReorientedFace(Direction direction, @NotNull BlockState state) {
		if (state.is(BlockTags.FENCE_GATES)) return direction.getOpposite();
		return direction;
	}

	public static @NotNull List<Function<BlockState, BlockState>> getPossibleBlockStates(@NotNull BlockState state, Direction clickedFace) {
		List<Function<BlockState, BlockState>> stateMutators = new ArrayList<>();

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

	@Contract(pure = true)
	public static void changeIntoState(@NotNull UseOnContext context, BlockState state) {
		final Level level = context.getLevel();
		final BlockPos pos = context.getClickedPos();

		BlockState newState = Block.updateFromNeighbourShapes(state, level, pos);
		level.setBlock(pos, newState, 276);
		level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(context.getPlayer(), state));
	}
}
