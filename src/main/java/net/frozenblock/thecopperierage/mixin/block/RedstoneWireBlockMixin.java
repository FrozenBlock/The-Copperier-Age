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

package net.frozenblock.thecopperierage.mixin.block;

import net.frozenblock.thecopperierage.block.GearboxBlock;
import net.frozenblock.thecopperierage.block.RedstonePumpkinBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedStoneWireBlock.class)
public class RedstoneWireBlockMixin {

    @Inject(method = "shouldConnectTo(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z", at = @At("HEAD"), cancellable = true)
    private static void theCopperierAge$handleRedstonePumpkinConnections(BlockState blockState, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (blockState.getBlock() instanceof RedstonePumpkinBlock) {
            if (direction == null) {
                cir.setReturnValue(false);
                return;
            }
            // Only allow connection if this direction matches the output face of the redstone pumpkin
            Direction outputFace = blockState.getValue(RedstonePumpkinBlock.FACING).getOpposite();
            cir.setReturnValue(direction == outputFace);
        }
		if (blockState.getBlock() instanceof GearboxBlock) {
			if (direction == null) {
				cir.setReturnValue(false);
				return;
			}
			// Only allow connection if this direction doesn't match the top face of the gearbox
			Direction outputFace = blockState.getValue(GearboxBlock.FACING).getOpposite();
			cir.setReturnValue(direction != outputFace);
		}
    }
}
