package net.frozenblock.thecopperierage.mixin.block.redstone_ore;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public class RedStoneOreBlockMixin {

	@Inject(method = "isSignalSource(Lnet/minecraft/world/level/block/state/BlockState;)Z", at = @At("HEAD"), cancellable = true)
	private void theCopperierAge$isSignalSource(BlockState state, CallbackInfoReturnable<Boolean> info) {
		if ((Object) this instanceof RedStoneOreBlock) {
			info.setReturnValue(true);
		}
	}

	@Inject(method = "getSignal(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)I", at = @At("HEAD"), cancellable = true)
	private void theCopperierAge$getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> info) {
		if ((Object) this instanceof RedStoneOreBlock) {
			info.setReturnValue(state.getValue(RedStoneOreBlock.LIT) ? 15 : 0);
		}
	}
}

