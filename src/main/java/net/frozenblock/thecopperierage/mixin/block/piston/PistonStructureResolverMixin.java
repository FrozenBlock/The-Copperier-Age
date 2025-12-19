package net.frozenblock.thecopperierage.mixin.block.piston;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.frozenblock.thecopperierage.tag.TCABlockTags;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonStructureResolver.class)
public class PistonStructureResolverMixin {

	@WrapOperation(
		method = {"resolve", "addBlockLine"},
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/block/piston/PistonStructureResolver;isSticky(Lnet/minecraft/world/level/block/state/BlockState;)Z"
		)
	)
	private boolean theCopperierAge$doubleChestStickingA(BlockState state, Operation<Boolean> original) {
		if (state.is(ConventionalBlockTags.CHESTS) && state.is(TCABlockTags.HAS_PUSHABLE_BLOCK_ENTITY)) return true;
		return original.call(state);
	}

	@Inject(method = "canStickToEachOther", at = @At("HEAD"), cancellable = true)
	private static void theCopperierAge$doubleChestSticking(BlockState state1, BlockState state2, CallbackInfoReturnable<Boolean> info) {
		if (!state1.is(ConventionalBlockTags.CHESTS) || !state2.is(ConventionalBlockTags.CHESTS)) return;
		if (!state1.is(TCABlockTags.HAS_PUSHABLE_BLOCK_ENTITY) || !state2.is(TCABlockTags.HAS_PUSHABLE_BLOCK_ENTITY)) return;

		final ChestType chest1Type = state1.getValueOrElse(BlockStateProperties.CHEST_TYPE, ChestType.SINGLE);
		if (chest1Type == ChestType.SINGLE) return;

		final ChestType chest2Type = state2.getValueOrElse(BlockStateProperties.CHEST_TYPE, ChestType.SINGLE);
		if (chest2Type == ChestType.SINGLE) return;

		if (!state1.hasProperty(ChestBlock.FACING) || !state2.hasProperty(ChestBlock.FACING)) return;

		final Direction connectedDirection1 = ChestBlock.getConnectedDirection(state1);
		final Direction connectedDirection2 = ChestBlock.getConnectedDirection(state2);
		if (connectedDirection1 == connectedDirection2.getOpposite()) info.setReturnValue(true);
	}

}
