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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.thecopperierage.block.entity.impl.PistonMovingBlockEntityInterface;
import net.frozenblock.thecopperierage.block.entity.impl.PushableBlockEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PistonMovingBlockEntity.class)
public class PistonMovingBlockEntityMixin implements PistonMovingBlockEntityInterface {
	@Unique
	private CompoundTag theCopperierAge$getPushedBlockEntityTag = null;

	@Override
	public void theCopperierAge$setPushedBlockEntityTag(CompoundTag tag) {
		this.theCopperierAge$getPushedBlockEntityTag = tag;
	}

	@Override
	public CompoundTag theCopperierAge$getPushedBlockEntityTag() {
		return this.theCopperierAge$getPushedBlockEntityTag;
	}

	@WrapOperation(
		method = "finalTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	public boolean theCopperierAge$setBlockFinalTick(Level level, BlockPos pos, BlockState state, int flags, Operation<Boolean> original) {
		final boolean setBlock = original.call(level, pos, state, flags);
		return PushableBlockEntityUtil.setBlockAndEntity(setBlock, level, pos, state, PistonMovingBlockEntity.class.cast(this));
	}

	@WrapOperation(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
		)
	)
	private static boolean theCopperierAge$setBlockTick(
		Level level, BlockPos pos, BlockState state, int flags, Operation<Boolean> original,
		@Local(argsOnly = true) PistonMovingBlockEntity pistonEntity
	) {
		final boolean setBlock = original.call(level, pos, state, flags);
		return PushableBlockEntityUtil.setBlockAndEntity(setBlock, level, pos, state, pistonEntity);
	}

	@Inject(method = "loadAdditional", at = @At("TAIL"))
	public void theCopperierAge$loadAdditional(ValueInput input, CallbackInfo info) {
		this.theCopperierAge$getPushedBlockEntityTag = input.read("TheCopperierAge_PushedBlockEntity", CompoundTag.CODEC).orElse(null);
	}

	@Inject(method = "saveAdditional", at = @At("TAIL"))
	public void theCopperierAge$saveAdditional(ValueOutput output, CallbackInfo info) {
		output.storeNullable("TheCopperierAge_PushedBlockEntity", CompoundTag.CODEC, this.theCopperierAge$getPushedBlockEntityTag);
	}

}
