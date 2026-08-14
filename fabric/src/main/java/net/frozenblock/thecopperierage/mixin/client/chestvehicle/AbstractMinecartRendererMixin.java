/*
 * Copyright 2025-2026 FrozenBlock
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

package net.frozenblock.thecopperierage.mixin.client.chestvehicle;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.client.renderer.entity.ChestVehicleRenderHelper;
import net.frozenblock.thecopperierage.client.renderer.entity.state.ChestVehicleRenderStateAccess;
import net.frozenblock.thecopperierage.entity.ChestVehicleOpeners;
import net.frozenblock.thecopperierage.entity.impl.ChestVehicleLidInterface;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(AbstractMinecartRenderer.class)
public class AbstractMinecartRendererMixin {

	@Inject(
		method = "extractRenderState(Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;F)V",
		at = @At("TAIL")
	)
	public <T extends AbstractMinecart, S extends MinecartRenderState> void theCopperierAge$extractChestState(
		T minecart,
		S renderState,
		float partialTicks,
		CallbackInfo info
	) {
		if (!(renderState instanceof ChestVehicleRenderStateAccess chestState)) return;

		final boolean isChestVehicle = minecart instanceof ChestVehicleLidInterface && ChestVehicleOpeners.hasChest(minecart);

		chestState.theCopperierAge$setChestVehicle(isChestVehicle);
		chestState.theCopperierAge$setLidOpenness(
			isChestVehicle ? ((ChestVehicleLidInterface) minecart).theCopperierAge$getLidOpenness(partialTicks) : 0F
		);
	}

	@WrapOperation(
		method = "submit(Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/entity/AbstractMinecartRenderer;submitMinecartContents(Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;Lnet/minecraft/client/renderer/block/BlockModelRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V"
		)
	)
	public void theCopperierAge$submitChestInsteadOfDisplayBlock(
		AbstractMinecartRenderer<?, ?> instance,
		MinecartRenderState renderState,
		BlockModelRenderState displayBlockModel,
		PoseStack poseStack,
		SubmitNodeCollector collector,
		int lightCoords,
		Operation<Void> original,
		@Local(argsOnly = true) CameraRenderState camera
	) {
		if (!(renderState instanceof ChestVehicleRenderStateAccess chestState) || !chestState.theCopperierAge$isChestVehicle()) {
			original.call(instance, renderState, displayBlockModel, poseStack, collector, lightCoords);
			return;
		}

		ChestVehicleRenderHelper.submitChest(
			poseStack,
			collector,
			camera,
			lightCoords,
			chestState.theCopperierAge$getLidOpenness()
		);
	}
}
