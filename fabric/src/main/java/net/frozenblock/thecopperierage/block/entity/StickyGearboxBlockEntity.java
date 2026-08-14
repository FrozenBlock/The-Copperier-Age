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

package net.frozenblock.thecopperierage.block.entity;

import java.util.Optional;
import net.frozenblock.thecopperierage.block.StickyGearboxBlock;
import net.frozenblock.thecopperierage.block.rotation.BlockRotationHelper;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.item.WrenchItem;
import net.frozenblock.thecopperierage.registry.TCABlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class StickyGearboxBlockEntity extends BlockEntity {
	private int ticksSinceActive;

	public StickyGearboxBlockEntity(BlockPos pos, BlockState state) {
		super(TCABlockEntityTypes.STICKY_GEARBOX, pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, StickyGearboxBlockEntity stickyGearbox) {
		final int power = state.getValue(StickyGearboxBlock.POWER);
		if (power <= 0) {
			stickyGearbox.ticksSinceActive = 0;
			return;
		}

		final int interval = TCAConfig.STICKY_GEARBOX_ROTATION_INTERVAL.get();
		stickyGearbox.ticksSinceActive += 1;
		if (stickyGearbox.ticksSinceActive % interval != 0) return;
		final boolean isDoubleActivation = (stickyGearbox.ticksSinceActive % (interval * 2)) != 0;
		final Direction facing = state.getValue(StickyGearboxBlock.FACING);
		final Direction.Axis facingAxis = facing.getAxis();
		final BlockPos facingPos = pos.relative(facing);
		final BlockState facingState = level.getBlockState(facingPos);
		final BlockEntity facingBlockEntity = level.getBlockEntity(facingPos);

		final BlockRotationHelper.ResultType resultType = BlockRotationHelper.getRotationResultType(state, facingBlockEntity);
		if (resultType == BlockRotationHelper.ResultType.PASS) return;
		if (resultType == BlockRotationHelper.ResultType.FAIL) return;

		final boolean isCounterClockwise = isCounterClockwise(facing, power);
		awkwardRotations: {
			if (facingAxis == Direction.Axis.Y) break awkwardRotations;

			final Optional<Runnable> flipDoor = BlockRotationHelper.rotateDoor(
				level,
				facingPos,
				facingState,
				facingState1 -> facingState1.cycle(DoorBlock.HINGE)
			);
			if (flipDoor.isPresent()) {
				if (isDoubleActivation) flipDoor.get().run();
				return;
			}

			final boolean flip = (power & 1) != 0;
			final Block block = facingState.getBlock();
			if (block instanceof StairBlock) {
				final Direction stairFacing = facingState.getValue(StairBlock.FACING);
				final Half stairHalf = facingState.getValue(StairBlock.HALF);
				BlockState rotState;
				if (stairFacing.getAxis() == facingAxis) {
					if (!isDoubleActivation) return;
					rotState = facingState.cycle(StairBlock.HALF);
				} else {
					final Direction flipDir = stairHalf == Half.TOP
						? flip ? facing.getClockWise() : facing.getCounterClockWise()
						: flip ? facing.getCounterClockWise() : facing.getClockWise();
					rotState = stairFacing == flipDir
						? facingState.cycle(StairBlock.HALF)
						: facingState.setValue(StairBlock.FACING, flipDir);
				}
				WrenchItem.changeIntoState(level, facingPos, rotState, null);
				return;
			} else if (block instanceof SlabBlock) {
				if (!isDoubleActivation) return;
				final SlabType type = facingState.getValue(SlabBlock.TYPE);
				if (type == SlabType.DOUBLE) return;
				WrenchItem.changeIntoState(level, facingPos, facingState.setValue(SlabBlock.TYPE, type == SlabType.TOP ? SlabType.BOTTOM : SlabType.TOP), null);
				return;
			} else if (block instanceof TrapDoorBlock) {
				if (!isDoubleActivation) return;
				WrenchItem.changeIntoState(level, facingPos, facingState.cycle(TrapDoorBlock.HALF), null);
				return;
			} else if (facingState.hasProperty(BlockStateProperties.FACING)) {
				final Direction blockFacing = facingState.getValue(BlockStateProperties.FACING);
				if (blockFacing.getAxis() == facingAxis) return;

				final BlockState rotState = facingState.setValue(
					BlockStateProperties.FACING,
					isCounterClockwise ? blockFacing.getCounterClockWise(facingAxis) : blockFacing.getClockWise(facingAxis)
				);
				if (!rotState.canSurvive(level, facingPos)) return;
				WrenchItem.changeIntoState(level, facingPos, rotState, null);
				return;
			} else if (facingState.hasProperty(BlockStateProperties.AXIS)) {
				final Direction.Axis blockAxis = facingState.getValue(BlockStateProperties.AXIS);
				if (blockAxis == facingAxis) return;

				final BlockState rotState = facingState.setValue(
					BlockStateProperties.AXIS,
					blockAxis.getPositive().getClockWise(facingAxis).getAxis()
				);
				if (!rotState.canSurvive(level, facingPos)) return;
				WrenchItem.changeIntoState(level, facingPos, rotState, null);
				return;
			}

			final Optional<Runnable> swapLanternHangingState = BlockRotationHelper.swapLanternHangingState(level, facingPos, facingState);
			if (swapLanternHangingState.isPresent() && isDoubleActivation) {
				swapLanternHangingState.get().run();
				return;
			}
		}

		final Optional<Runnable> rotateDoor = BlockRotationHelper.rotateDoor(
			level,
			facingPos,
			facingState,
			facingState1 -> {
				Direction newDirection = facingState1.getValue(DoorBlock.FACING);
				newDirection = isCounterClockwise ? newDirection.getCounterClockWise() : newDirection.getClockWise();
				return facingState1.setValue(DoorBlock.FACING, newDirection);
			}
		);
		if (rotateDoor.isPresent()) {
			rotateDoor.get().run();
			return;
		}

		final Rotation rotation = getRotation(facing, power);
		final BlockState rotatedState = facingState.rotate(rotation);
		if (rotatedState == facingState || !rotatedState.canSurvive(level, facingPos)) return;

		WrenchItem.changeIntoState(level, facingPos, rotatedState, null);
	}

	private static Rotation getRotation(Direction facing, int power) {
		return isCounterClockwise(facing, power) ? Rotation.COUNTERCLOCKWISE_90 : Rotation.CLOCKWISE_90;
	}

	private static boolean isCounterClockwise(Direction facing, int power) {
		return (facing == Direction.DOWN) == ((power & 1) == 0);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putInt("ticks_since_active", this.ticksSinceActive);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		this.ticksSinceActive = input.getIntOr("ticks_since_active", 0);
	}
}
