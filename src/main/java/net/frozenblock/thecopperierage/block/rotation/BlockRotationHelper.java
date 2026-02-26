package net.frozenblock.thecopperierage.block.rotation;

import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.frozenblock.thecopperierage.item.WrenchItem;
import net.frozenblock.thecopperierage.tag.TCABlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class BlockRotationHelper {

	public static ResultType getRotationResultType(BlockState state, BlockEntity blockEntity) {
		final Block block = state.getBlock();
		if (state.is(TCABlockTags.CANNOT_ROTATE)) return ResultType.PASS;
		if (block instanceof PistonBaseBlock && state.getValue(PistonBaseBlock.EXTENDED)) return ResultType.PASS;

		if (block instanceof DoorBlock doorBlock && !doorBlock.type().canOpenByHand()) return ResultType.FAIL;
		if (block instanceof TrapDoorBlock trapDoorBlock && !trapDoorBlock.getType().canOpenByHand()) return ResultType.FAIL;
		if (block instanceof ShelfBlock && state.getValue(ShelfBlock.POWERED)) return ResultType.FAIL;
		if (state.is(ConventionalBlockTags.CHESTS) && state.getValueOrElse(ChestBlock.TYPE, ChestType.SINGLE) != ChestType.SINGLE) return ResultType.FAIL;
		if (blockEntity instanceof LidBlockEntity lidBlockEntity && lidBlockEntity.getOpenNess(1F) > 0F) return ResultType.FAIL;
		if (blockEntity instanceof Container container && !container.getEntitiesWithContainerOpen().isEmpty()) return ResultType.FAIL;
		if (blockEntity instanceof ShulkerBoxBlockEntity shulkerBox && !shulkerBox.isClosed()) return ResultType.FAIL;

		return ResultType.ATTEMPT;
	}

	public static Optional<Runnable> rotateDoor(Level level, BlockPos pos, BlockState state, Function<BlockState, BlockState> rotator) {
		if (!state.is(BlockTags.DOORS)) return Optional.empty();

		final Optional<DoubleBlockHalf> optionalHalf = state.getOptionalValue(DoorBlock.HALF);
		if (optionalHalf.isEmpty()) return Optional.empty();

		final DoubleBlockHalf doubleBlockHalf = optionalHalf.get();
		final BlockPos otherPos = pos.relative(doubleBlockHalf.getDirectionToOther());
		Function<BlockState, BlockState> halfStateMutator = otherState -> otherState.trySetValue(DoorBlock.HALF, doubleBlockHalf.getOtherHalf());
		final BlockState rotatedState = rotator.apply(state);
		if (!level.getBlockState(otherPos).is(state.getBlock())) return Optional.empty();

		return Optional.of(() -> {
			level.setBlock(pos, rotatedState, Block.UPDATE_ALL);
			level.setBlock(otherPos, halfStateMutator.apply(rotatedState), Block.UPDATE_ALL);
		});
	}

	public static Optional<Runnable> swapLanternHangingState(Level level, BlockPos pos, BlockState state) {
		if (!state.is(BlockTags.LANTERNS) || !state.hasProperty(LanternBlock.HANGING)) return Optional.empty();

		final BlockState newState = state.cycle(LanternBlock.HANGING);
		if (newState.canSurvive(level, pos)) return Optional.of(() -> WrenchItem.changeIntoState(level, pos, newState, null));
		return Optional.empty();
	}

	public enum ResultType {
		PASS,
		FAIL,
		ATTEMPT
	}
}
