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

package net.frozenblock.thecopperierage.mixin.block.copper_crate;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.List;
import java.util.Optional;
import net.frozenblock.thecopperierage.block.entity.impl.PushableBlockEntityUtil;
import net.frozenblock.thecopperierage.tag.TCABlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {

	@WrapOperation(
		method = "isPushable",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/state/BlockState;hasBlockEntity()Z"
		)
	)
	private static boolean theCopperierAge$allowBlockEntityPushing(BlockState state, Operation<Boolean> original) {
		if (state.is(TCABlockTags.HAS_PUSHABLE_BLOCK_ENTITY)) return false;
		return original.call(state);
	}

	// triggerEvent

	@WrapOperation(
		method = "triggerEvent",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"
			)
		)
	)
	public boolean theCopperierAge$captureBlockEntity(
		Level instance, BlockPos pos, BlockState state, int flags, Operation<Boolean> original,
		@Share("theCopperierAge$blockEntityTag") LocalRef<CompoundTag> blockEntityTagRef
	) {
		blockEntityTagRef.set(null);
		final BlockEntity blockEntity = instance.getBlockEntity(pos);
		if (blockEntity != null) blockEntityTagRef.set(blockEntity.saveWithFullMetadata(instance.registryAccess()));
		return original.call(instance, pos, state, flags);
	}

	@ModifyExpressionValue(
		method = "triggerEvent",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/MovingPistonBlock;newMovingBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;ZZ)Lnet/minecraft/world/level/block/entity/BlockEntity;"
		)
	)
	public BlockEntity theCopperierAge$saveBlockEntityToMovingBlock(
		BlockEntity original,
		@Share("theCopperierAge$blockEntityTag") LocalRef<CompoundTag> blockEntityTagRef
	) {
		PushableBlockEntityUtil.saveTag(blockEntityTagRef.get(), original);
		return original;
	}

	//moveBlocks

	@Inject(
		method = "moveBlocks",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;getToPush()Ljava/util/List;"
		)
	)
	public void theCopperierAge$createBlockEntityList(
		Level level, BlockPos pos, Direction direction, boolean bl, CallbackInfoReturnable<Boolean> info,
		@Share("theCopperierAge$blockEntityList") LocalRef<List<Optional<BlockEntity>>> blockEntityListRef
	) {
		final List<Optional<BlockEntity>> blockEntityList = Lists.newArrayList();
		blockEntityListRef.set(blockEntityList);
	}

	@WrapOperation(
		method = "moveBlocks",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;getToPush()Ljava/util/List;"
			)
		)
	)
	public BlockState theCopperierAge$captureBlockEntities(
		Level instance, BlockPos pos, Operation<BlockState> original,
		@Share("theCopperierAge$blockEntityList") LocalRef<List<Optional<BlockEntity>>> blockEntityListRef
	) {
		final List<Optional<BlockEntity>> blockEntityList = blockEntityListRef.get();
		if (blockEntityList != null) blockEntityList.add(Optional.ofNullable(instance.getBlockEntity(pos)));
		return original.call(instance, pos);
	}

	@WrapOperation(
		method = "moveBlocks",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/List;get(I)Ljava/lang/Object;",
			ordinal = 1
		)
	)
	public Object theCopperierAge$captureBlockIndex(
		List instance, int i, Operation<Object> original,
		@Share("theCopperierAge$listIndex") LocalIntRef listIndexRef
	) {
		listIndexRef.set(i);
		return original.call(instance, i);
	}

	@ModifyExpressionValue(
		method = "moveBlocks",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/MovingPistonBlock;newMovingBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;ZZ)Lnet/minecraft/world/level/block/entity/BlockEntity;",
			ordinal = 0
		)
	)
	public BlockEntity theCopperierAge$saveBlockEntityToMovingBlocks(
		BlockEntity original,
		@Local(argsOnly = true) Level level,
		@Share("theCopperierAge$blockEntityList") LocalRef<List<Optional<BlockEntity>>> blockEntityListRef,
		@Share("theCopperierAge$listIndex") LocalIntRef listIndexRef
	) {
		final List<Optional<BlockEntity>> blockEntityList = blockEntityListRef.get();
		if (blockEntityList != null) {
			final Optional<BlockEntity> optionalBlockEntity = blockEntityList.get(listIndexRef.get());
			optionalBlockEntity.ifPresent(blockEntity -> PushableBlockEntityUtil.saveBlockEntity(level, blockEntity, original));
		}
		return original;
	}

	@WrapOperation(
		method = "moveBlocks",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "INVOKE",
				target = "Ljava/util/Map;keySet()Ljava/util/Set;"
			)
		)
	)
	public boolean theCopperierAge$saveBlockEntityToMovingBlocks(Level instance, BlockPos pos, BlockState state, int flags, Operation<Boolean> original) {
		// The AND check for 256 dictates whether `BlockEntity$preRemoveSideEffects` is called.
		// With 82, & 256 returns 0.
		// Adding 256 makes this not return 0, while keeping all other calls intact.
		if (instance.getBlockState(pos).is(TCABlockTags.HAS_PUSHABLE_BLOCK_ENTITY) && (flags & PistonBaseBlock.UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS) == 0) {
			flags += PistonBaseBlock.UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS;
		}
		return original.call(instance, pos, state, flags);
	}

}
