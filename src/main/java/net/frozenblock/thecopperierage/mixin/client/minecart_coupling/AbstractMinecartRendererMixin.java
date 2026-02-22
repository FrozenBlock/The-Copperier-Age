package net.frozenblock.thecopperierage.mixin.client.minecart_coupling;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.client.renderer.entity.state.CouplingRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(AbstractMinecartRenderer.class)
public class AbstractMinecartRendererMixin {

	@Inject(
		method = "extractRenderState(Lnet/minecraft/world/entity/vehicle/AbstractMinecart;Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;F)V",
		at = @At("TAIL")
	)
	public <T extends AbstractMinecart, S extends MinecartRenderState> void theCopperierAge$extractCouplingRenderState(
		T minecart,
		S renderState,
		float partialTicks,
		CallbackInfo info
	) {
		CouplingRenderState.extract(minecart, renderState, partialTicks);
	}

	@Inject(
		method = "submit(Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
		at = @At("HEAD")
	)
	public <T extends AbstractMinecart, S extends MinecartRenderState> void theCopperierAge$submitCoupling(
		S renderState,
		PoseStack poseStack,
		SubmitNodeCollector collector,
		CameraRenderState camera,
		CallbackInfo info
	) {
		CouplingRenderState.renderCoupling(poseStack, collector, renderState, renderState.lightCoords);
	}

}
