package net.frozenblock.thecopperierage.mixin.client.copper_crate;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.client.renderer.blockentity.state.impl.MovingBlockRenderStateImpl;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(MovingBlockRenderState.class)
public class MovingBlockRenderStateMixin implements MovingBlockRenderStateImpl {

	@Unique
	private BlockEntityRenderState theCopperierAge$blockEntityRenderState = null;

	@Override
	public void theCopperierAge$setBlockEntityRenderState(BlockEntityRenderState renderState) {
		this.theCopperierAge$blockEntityRenderState = renderState;
	}

	@Override
	public BlockEntityRenderState theCopperierAge$getBlockEntityRenderState() {
		return this.theCopperierAge$blockEntityRenderState;
	}
}
