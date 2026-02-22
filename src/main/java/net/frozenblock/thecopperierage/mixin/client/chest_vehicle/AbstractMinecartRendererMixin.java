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

import net.frozenblock.thecopperierage.client.renderer.entity.state.ChestLidRenderStateAccess;
import net.frozenblock.thecopperierage.entity.impl.ChestVehicleInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartRenderer.class)
public class AbstractMinecartRendererMixin {
	@Unique
	private static final ThreadLocal<CameraRenderState> theCopperierAge$cameraState = new ThreadLocal<>();

	@Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V", at = @At("HEAD"))
	private void theCopperierAge$submitHead(MinecartRenderState state, com.mojang.blaze3d.vertex.PoseStack poseStack, SubmitNodeCollector output, CameraRenderState cameraRenderState, CallbackInfo info) {
		theCopperierAge$cameraState.set(cameraRenderState);
	}

	@Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V", at = @At("TAIL"))
	private void theCopperierAge$submitTail(MinecartRenderState state, com.mojang.blaze3d.vertex.PoseStack poseStack, SubmitNodeCollector output, CameraRenderState cameraRenderState, CallbackInfo info) {
		theCopperierAge$cameraState.remove();
	}

	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/vehicle/AbstractMinecart;Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;F)V", at = @At("TAIL"))
	private void theCopperierAge$extractRenderState(AbstractMinecart minecart, MinecartRenderState state, float partialTicks, CallbackInfo info) {
		if (!(minecart instanceof ChestVehicleInterface lidAnimating)) return;
		if (!(state instanceof ChestLidRenderStateAccess chestState)) return;

		lidAnimating.theCopperierAge$tickLidController();
		float openness = lidAnimating.theCopperierAge$getLidOpenness(partialTicks);
		chestState.theCopperierAge$setLidOpenness(openness);

		if (state.displayBlockState.hasProperty(BlockStateProperties.OPEN)) {
			state.displayBlockState = state.displayBlockState.setValue(BlockStateProperties.OPEN, openness > 0.0F);
		}
	}

	@Inject(method = "submitMinecartContents(Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;Lnet/minecraft/world/level/block/state/BlockState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V", at = @At("HEAD"), cancellable = true)
	private void theCopperierAge$submitMinecartContents(MinecartRenderState state, net.minecraft.world.level.block.state.BlockState displayState, com.mojang.blaze3d.vertex.PoseStack poseStack, SubmitNodeCollector output, int packedLight, CallbackInfo info) {
		if (!(state instanceof ChestLidRenderStateAccess chestState)) return;
		if (!displayState.is(Blocks.CHEST)) return;

		final CameraRenderState cameraRenderState = theCopperierAge$cameraState.get();
		if (cameraRenderState == null) return;

		ChestRenderState chestRenderState = new ChestRenderState();
		chestRenderState.blockPos = BlockPos.ZERO;
		chestRenderState.blockState = Blocks.CHEST.defaultBlockState();
		chestRenderState.blockEntityType = BlockEntityType.CHEST;
		chestRenderState.lightCoords = packedLight;
		chestRenderState.type = net.minecraft.world.level.block.state.properties.ChestType.SINGLE;
		chestRenderState.material = ChestRenderState.ChestMaterialType.REGULAR;
		chestRenderState.open = chestState.theCopperierAge$getLidOpenness();
		chestRenderState.angle = 0.0F;

		Minecraft.getInstance().getBlockEntityRenderDispatcher().submit(chestRenderState, poseStack, output, cameraRenderState);
		info.cancel();
	}
}
