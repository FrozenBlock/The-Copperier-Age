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

package net.frozenblock.thecopperierage.mixin.client.chest_vehicle;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.frozenblock.thecopperierage.client.renderer.entity.ChestVehicleRenderHelper;
import net.frozenblock.thecopperierage.client.renderer.entity.state.ChestLidRenderStateInterface;
import net.frozenblock.thecopperierage.entity.impl.ChestVehicleInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartRenderer.class)
public class AbstractMinecartRendererMixin {

	@WrapOperation(
		method = "submit(Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/entity/AbstractMinecartRenderer;submitMinecartContents(Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;Lnet/minecraft/world/level/block/state/BlockState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V"
		)
	)
	public <S extends MinecartRenderState> void theCopperierAge$submitChest(
		AbstractMinecartRenderer instance, S renderState, BlockState state, PoseStack poseStack, SubmitNodeCollector collector, int lightCoords, Operation<Void> original,
		@Local(argsOnly = true) CameraRenderState camera
	) {
		if (!(state instanceof ChestLidRenderStateInterface chestState) || !state.is(Blocks.CHEST)) {
			original.call(instance, renderState, state, poseStack, collector, lightCoords);
			return;
		}

		final ChestRenderState chestRenderState = ChestVehicleRenderHelper.createChestRenderState(lightCoords, chestState.theCopperierAge$getLidOpenness());
		Minecraft.getInstance().getBlockEntityRenderDispatcher().submit(chestRenderState, poseStack, collector, camera);
	}

	@Inject(
		method = "extractRenderState(Lnet/minecraft/world/entity/vehicle/AbstractMinecart;Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;F)V",
		at = @At("TAIL")
	)
	private void theCopperierAge$extractRenderState(AbstractMinecart minecart, MinecartRenderState state, float partialTicks, CallbackInfo info) {
		if (!(minecart instanceof ChestVehicleInterface chestVehicle) || !(state instanceof ChestLidRenderStateInterface chestState)) return;

		final float openness = chestVehicle.theCopperierAge$getLidOpenness(partialTicks);
		chestState.theCopperierAge$setLidOpenness(openness);
		state.displayBlockState = state.displayBlockState.trySetValue(BlockStateProperties.OPEN, openness > 0F);
	}
}
