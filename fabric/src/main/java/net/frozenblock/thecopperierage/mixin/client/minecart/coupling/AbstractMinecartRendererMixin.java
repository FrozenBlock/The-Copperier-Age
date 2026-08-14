/*
 * Copyright 2026 FrozenBlock
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

package net.frozenblock.thecopperierage.mixin.client.minecart.coupling;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.client.coupling.MinecartCouplingClientHandler;
import net.frozenblock.thecopperierage.client.renderer.entity.state.CouplingRenderState;
import net.frozenblock.thecopperierage.entity.impl.MinecartRotationSmoothing;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(AbstractMinecartRenderer.class)
public class AbstractMinecartRendererMixin {
	/** Disabled: see the rotation smoothing mixin. */
	private static final boolean THECOPPERIERAGE$SMOOTHING_ENABLED = false;

	@Inject(
		method = "extractRenderState(Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;F)V",
		at = @At("TAIL")
	)
	public <T extends AbstractMinecart, S extends MinecartRenderState> void theCopperierAge$extractCouplingRenderState(
		T minecart,
		S renderState,
		float partialTicks,
		CallbackInfo info
	) {
		CouplingRenderState.extract(minecart, renderState, partialTicks);

		// Rotation 2.0: replace vanilla's snappy step-lerp yaw/pitch with the eased values.
		if (THECOPPERIERAGE$SMOOTHING_ENABLED
			&& minecart instanceof MinecartRotationSmoothing smoothing
			&& smoothing.theCopperierAge$hasSmoothedRotation()) {
			renderState.yRot = smoothing.theCopperierAge$getSmoothYRot(partialTicks);
			renderState.xRot = smoothing.theCopperierAge$getSmoothXRot(partialTicks);
		}
	}

	@Inject(
		method = "submit(Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
		at = @At("HEAD")
	)
	public <T extends AbstractMinecart, S extends MinecartRenderState> void theCopperierAge$submitCoupling(
		S renderState,
		PoseStack poseStack,
		SubmitNodeCollector collector,
		CameraRenderState camera,
		CallbackInfo info
	) {
		CouplingRenderState.renderCoupling(poseStack, collector, renderState, renderState.lightCoords, CouplingRenderState.COUPLING_RENDER_STATE);
		CouplingRenderState.renderCoupling(poseStack, collector, renderState, renderState.lightCoords, CouplingRenderState.COUPLING_HELD_RENDER_STATE);
	}

	@ModifyReturnValue(
		method = "getBoundingBoxForCulling(Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;)Lnet/minecraft/world/phys/AABB;",
		at = @At("RETURN")
	)
	public AABB theCopperierAge$modifyBoundingBoxForCulling(AABB original, AbstractMinecart minecart) {
		return MinecartCouplingClientHandler.modifyBoundingBoxForCoupling(minecart, original);
	}

}
