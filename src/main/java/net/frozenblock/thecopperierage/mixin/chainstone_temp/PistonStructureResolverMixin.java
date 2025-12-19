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

package net.frozenblock.thecopperierage.mixin.chainstone_temp;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PistonStructureResolver.class)
public class PistonStructureResolverMixin {

	@Shadow
	@Final
	private Direction pushDirection;

	@WrapOperation(
		method = {"resolve", "addBlockLine"},
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;isSticky(Lnet/minecraft/world/level/block/state/BlockState;)Z"
		)
	)
	private boolean pushierPistons$chainSticking(BlockState state, Operation<Boolean> original) {
		return original.call(state) || state.is(BlockTags.CHAINS);
	}

	@WrapOperation(
		method = "addBlockLine",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;canStickToEachOther(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)Z"
		)
	)
	private boolean pushierPistons$addBlockLineChainStone(BlockState state1, BlockState state2, Operation<Boolean> original) {
		return original.call(state1, state2) || pushierPistons$figureOutChainCombo(state1, state2, this.pushDirection.getAxis());
	}

	@WrapOperation(
		method = "addBranchingBlocks",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;canStickToEachOther(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)Z"
		)
	)
	private boolean pushierPistons$addBranchingBlocksChainStone(
		BlockState state1, BlockState state2, Operation<Boolean> original,
		@Local Direction direction
	) {
		return original.call(state1, state2) || pushierPistons$figureOutChainCombo(state2, state1, direction.getAxis());
	}

	@Unique
	private static boolean pushierPistons$figureOutChainCombo(BlockState state1, BlockState state2, Direction.Axis spacialAxis) {
		final boolean is1Chain = state1.is(BlockTags.CHAINS);
		final boolean is2Chain = state2.is(BlockTags.CHAINS);
		if (!is1Chain && !is2Chain) return false;

		final Direction.Axis axis1 = state1.getOptionalValue(ChainBlock.AXIS).orElse(null);
		final Direction.Axis axis2 = state2.getOptionalValue(ChainBlock.AXIS).orElse(null);

		if (is1Chain) {
			if (is2Chain) return axis1 == spacialAxis && axis2 == spacialAxis;
			return axis1 == spacialAxis;
		}

		return axis2 == spacialAxis;
	}

}
