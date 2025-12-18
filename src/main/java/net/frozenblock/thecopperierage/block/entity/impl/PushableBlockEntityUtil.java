package net.frozenblock.thecopperierage.block.entity.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PushableBlockEntityUtil {

	public static boolean saveBlockEntity(Level level, BlockEntity blockEntity, BlockEntity pistonEntity) {
		if (!(pistonEntity instanceof PistonMovingBlockEntityInterface pistonInterface)) return false;

		final CompoundTag blockEntityTag = blockEntity.saveWithFullMetadata(level.registryAccess());
		pistonInterface.theCopperierAge$setPushedBlockEntityTag(blockEntityTag);
		return true;
	}

	public static boolean saveTag(@Nullable CompoundTag tag, BlockEntity pistonEntity) {
		if (tag == null) return true;
		if (!(pistonEntity instanceof PistonMovingBlockEntityInterface pistonInterface)) return false;

		pistonInterface.theCopperierAge$setPushedBlockEntityTag(tag);
		return true;
	}

	public static boolean setBlockAndEntity(
		boolean setBlock,
		Level level,
		BlockPos pos,
		BlockState state,
		PistonMovingBlockEntity pistonEntity
	) {
		if (!state.hasBlockEntity() || !(pistonEntity instanceof PistonMovingBlockEntityInterface pistonInterface)) return setBlock;

		final CompoundTag blockEntityTag = pistonInterface.theCopperierAge$getPushedBlockEntityTag();
		if (blockEntityTag == null) return setBlock;

		final BlockEntity blockEntity = BlockEntity.loadStatic(pos, state, blockEntityTag, level.registryAccess());
		if (blockEntity != null) level.setBlockEntity(blockEntity);

		return setBlock;
	}

}
