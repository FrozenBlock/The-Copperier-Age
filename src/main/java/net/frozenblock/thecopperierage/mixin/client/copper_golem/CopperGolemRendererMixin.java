package net.frozenblock.thecopperierage.mixin.client.copper_golem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.entity.impl.CopperGolemPressButtonInterface;
import net.minecraft.client.renderer.entity.CopperGolemRenderer;
import net.minecraft.client.renderer.entity.state.CopperGolemRenderState;
import net.minecraft.world.entity.animal.coppergolem.CopperGolem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(CopperGolemRenderer.class)
public class CopperGolemRendererMixin {

	@Inject(
		method = "extractRenderState(Lnet/minecraft/world/entity/animal/coppergolem/CopperGolem;Lnet/minecraft/client/renderer/entity/state/CopperGolemRenderState;F)V",
		at = @At("TAIL")
	)
	public void theCopperierAge$extractRenderState(CopperGolem copperGolem, CopperGolemRenderState renderState, float partialTicks, CallbackInfo info) {
		if (!(copperGolem instanceof CopperGolemPressButtonInterface copperGolemInterface)) return;
		if (!(renderState instanceof CopperGolemPressButtonInterface renderStateInterface)) return;
		renderStateInterface.theCopperierAge$getPressingButtonAnimationState().copyFrom(copperGolemInterface.theCopperierAge$getPressingButtonAnimationState());
	}

}
