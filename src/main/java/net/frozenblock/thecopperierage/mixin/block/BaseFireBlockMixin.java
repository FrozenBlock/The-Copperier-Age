package net.frozenblock.thecopperierage.mixin.block;

import net.frozenblock.thecopperierage.block.CupricFireBlock;
import net.frozenblock.thecopperierage.mixin.accessor.FireBlockAccessor;
import net.frozenblock.thecopperierage.registry.TCABlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.SoulFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BaseFireBlock.class)
public class BaseFireBlockMixin {

    @Inject(method = "getState(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", at = @At("HEAD"), cancellable = true)
    private static void getState(BlockGetter blockGetter, BlockPos blockPos, CallbackInfoReturnable<BlockState> cir) {
        BlockPos blockPos2 = blockPos.below();
        BlockState blockState = blockGetter.getBlockState(blockPos2);
        if (CupricFireBlock.canSurviveOnBlock(blockState)) {
            cir.setReturnValue(TCABlocks.CUPRIC_FIRE.defaultBlockState());
        } else if (SoulFireBlock.canSurviveOnBlock(blockState)) {
            cir.setReturnValue(Blocks.SOUL_FIRE.defaultBlockState());
        } else {
            cir.setReturnValue(((FireBlockAccessor)Blocks.FIRE).tca_invokeGetStateForPlacement(blockGetter, blockPos));
        }
    }
}
