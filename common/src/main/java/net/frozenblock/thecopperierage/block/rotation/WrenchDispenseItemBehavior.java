/*
 * Copyright 2026 FrozenBlock
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

package net.frozenblock.thecopperierage.block.rotation;

import net.frozenblock.thecopperierage.item.WrenchItem;
import net.frozenblock.thecopperierage.registry.TCASounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.block.state.properties.SlabType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class WrenchDispenseItemBehavior extends OptionalDispenseItemBehavior {

	public WrenchDispenseItemBehavior() {}

	protected ItemStack execute(BlockSource source, ItemStack dispensed) {
		final ServerLevel level = source.level();
		if (level.isClientSide()) return dispensed;

		final Direction facing = source.state().getValue(DispenserBlock.FACING);
		final BlockPos facingPos = source.pos().relative(facing);
		this.setSuccess(tryRotateBlock(level, facing, facingPos));
		if (this.isSuccess()) dispensed.hurtAndBreak(1, level, null, item -> {});

		return dispensed;
	}

	private static boolean tryRotateBlock(ServerLevel level, Direction facing, BlockPos pos) {
		final BlockState state = level.getBlockState(pos);
		final BlockEntity blockEntity = level.getBlockEntity(pos);

		final BlockRotationHelper.ResultType resultType = BlockRotationHelper.getRotationResultType(state, blockEntity);
		if (resultType == BlockRotationHelper.ResultType.PASS) return false;
		if (resultType == BlockRotationHelper.ResultType.FAIL) return false;

		final Optional<Runnable> rotateDoor = BlockRotationHelper.rotateDoor(level, pos, state, state1 -> state1.cycle(DoorBlock.HINGE));
		if (rotateDoor.isPresent()) return onSuccessfulWrench(level, pos, rotateDoor.get());

		if (state.getBlock() instanceof BaseRailBlock baseRailBlock) {
			final Property<RailShape> property = baseRailBlock.getShapeProperty();
			BlockState newState = state.cycle(property);
			while (newState != state) {
				if (!BaseRailBlock.shouldBeRemoved(pos, level, newState.getValue(property))) {
					return onSuccessfulWrench(level, pos, newState);
				}
				newState = newState.cycle(property);
			}
			return false;
		}

		if (state.hasProperty(BlockStateProperties.ROTATION_16)) {
			final BlockState newState = state.cycle(BlockStateProperties.ROTATION_16);
			if (newState != state && newState.canSurvive(level, pos)) {
				return onSuccessfulWrench(level, pos, newState);
			}
		}

		if (state.hasProperty(BlockStateProperties.SLAB_TYPE)) {
			final SlabType slabType = state.getValue(BlockStateProperties.SLAB_TYPE);
			if (slabType != SlabType.DOUBLE) {
				final BlockState newState = state.setValue(BlockStateProperties.SLAB_TYPE, slabType == SlabType.BOTTOM ? SlabType.TOP : SlabType.BOTTOM);
				if (newState != state && newState.canSurvive(level, pos)) {
					return onSuccessfulWrench(level, pos, newState);
				}
			}
		}

		final Optional<Runnable> swapLanternHangingState = BlockRotationHelper.swapLanternHangingState(level, pos, state);
		if (swapLanternHangingState.isPresent()) return onSuccessfulWrench(level, pos, swapLanternHangingState.get());

		final List<Direction> directionsToTry = new ArrayList<>();

		final Direction reoriented = WrenchItem.getReorientedFace(facing.getOpposite(), state);
		directionsToTry.add(reoriented);
		directionsToTry.add(reoriented.getOpposite());

		for (Direction direction : directionsToTry) {
			final List<Function<BlockState, BlockState>> states = WrenchItem.getPossibleBlockStates(state, direction);

			for (Function<BlockState, BlockState> mutator : states) {
				final BlockState newState = mutator.apply(state);
				if (newState != state && newState.canSurvive(level, pos)) return onSuccessfulWrench(level, pos, newState);
			}
		}

		return false;
	}

	private static boolean onSuccessfulWrench(ServerLevel level, BlockPos pos, Runnable output) {
		playWrenchSound(level, pos);
		output.run();
		return true;
	}

	private static boolean onSuccessfulWrench(ServerLevel level, BlockPos pos, BlockState output) {
		playWrenchSound(level, pos);
		WrenchItem.changeIntoState(level, pos, output, null);
		return true;
	}

	private static void playWrenchSound(ServerLevel level, BlockPos pos) {
		level.playSound(null, pos, TCASounds.ITEM_WRENCH_USE.get(), SoundSource.BLOCKS, 0.75F, 0.9F + (level.getRandom().nextFloat() * 0.2F));
	}
}
