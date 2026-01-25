package net.frozenblock.thecopperierage.mixin.client.copper_golem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.client.animation.definitions.TCACopperGolemAnimation;
import net.frozenblock.thecopperierage.entity.impl.CopperGolemPressButtonInterface;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.CopperGolemModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.CopperGolemRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(CopperGolemModel.class)
public class CopperGolemModelMixin {

	@Unique
	private KeyframeAnimation theCopperierAge$pressingButton;

	@Inject(method = "<init>", at = @At("TAIL"))
	public void theCopperierAge$init(ModelPart root, CallbackInfo info) {
		this.theCopperierAge$pressingButton = TCACopperGolemAnimation.COPPER_GOLEM_PRESS_BUTTON.bake(root);
	}

	@Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/CopperGolemRenderState;)V", at = @At("TAIL"))
	public void theCopperierAge$extractRenderState(CopperGolemRenderState renderState, CallbackInfo info) {
		if (!(renderState instanceof CopperGolemPressButtonInterface renderStateInterface)) return;
		this.theCopperierAge$pressingButton.apply(renderStateInterface.theCopperierAge$getPressingButtonAnimationState(), renderState.ageInTicks);
	}

}
