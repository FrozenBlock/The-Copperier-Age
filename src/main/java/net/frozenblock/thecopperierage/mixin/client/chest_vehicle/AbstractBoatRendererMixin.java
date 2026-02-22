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

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.frozenblock.thecopperierage.client.renderer.entity.ChestVehicleRenderHelper;
import net.frozenblock.thecopperierage.client.renderer.entity.state.ChestLidRenderStateInterface;
import net.frozenblock.thecopperierage.client.renderer.entity.state.ChestVehicleRenderStateAccess;
import net.frozenblock.thecopperierage.entity.impl.ChestVehicleInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.RaftRenderer;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.entity.vehicle.AbstractChestBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBoatRenderer.class)
public class AbstractBoatRendererMixin {

	@Inject(
		method = "extractRenderState(Lnet/minecraft/world/entity/vehicle/AbstractBoat;Lnet/minecraft/client/renderer/entity/state/BoatRenderState;F)V",
		at = @At("TAIL")
	)
	public void theCopperierAge$extractRenderState(AbstractBoat boat, BoatRenderState state, float partialTicks, CallbackInfo info) {
		final boolean isChestBoat = boat instanceof AbstractChestBoat;
		if (state instanceof ChestVehicleRenderStateAccess chestVehicleState) chestVehicleState.theCopperierAge$setChestVehicle(isChestBoat);

		if (!isChestBoat || !(boat instanceof ChestVehicleInterface lidAnimating)) return;
		if (!(state instanceof ChestLidRenderStateInterface chestState)) return;

		chestState.theCopperierAge$setLidOpenness(lidAnimating.theCopperierAge$getLidOpenness(partialTicks));
	}

	@Inject(
		method = "submit(Lnet/minecraft/client/renderer/entity/state/BoatRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/entity/AbstractBoatRenderer;submitTypeAdditions(Lnet/minecraft/client/renderer/entity/state/BoatRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
			shift = At.Shift.AFTER
		)
	)
	public void theCopperierAge$submitVanillaChest(BoatRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera, CallbackInfo info) {
		if (!(state instanceof ChestVehicleRenderStateAccess chestVehicleState) || !chestVehicleState.theCopperierAge$isChestVehicle()) return;
		if (!(state instanceof ChestLidRenderStateInterface chestState)) return;
		final float raftYOffset = AbstractBoatRenderer.class.cast(this) instanceof RaftRenderer ? ChestVehicleRenderHelper.CHEST_RAFT_Y_OFFSET : 0F;

		poseStack.pushPose();
		poseStack.translate(ChestVehicleRenderHelper.CHEST_BASE_X, ChestVehicleRenderHelper.CHEST_BASE_Y + raftYOffset, ChestVehicleRenderHelper.CHEST_BASE_Z);
		poseStack.mulPose(Axis.YN.rotation(Mth.HALF_PI));
		poseStack.scale(ChestVehicleRenderHelper.CHEST_SCALE, ChestVehicleRenderHelper.CHEST_SCALE, ChestVehicleRenderHelper.CHEST_SCALE);
		poseStack.translate(-ChestVehicleRenderHelper.CHEST_PAD, 0F, -ChestVehicleRenderHelper.CHEST_PAD);
		poseStack.translate(ChestVehicleRenderHelper.HALF_BLOCK, ChestVehicleRenderHelper.HALF_BLOCK, ChestVehicleRenderHelper.HALF_BLOCK);
		poseStack.mulPose(Axis.XP.rotation(Mth.PI));
		poseStack.translate(-ChestVehicleRenderHelper.HALF_BLOCK, -ChestVehicleRenderHelper.HALF_BLOCK, -ChestVehicleRenderHelper.HALF_BLOCK);

		final ChestRenderState chestRenderState = ChestVehicleRenderHelper.createChestRenderState(state.lightCoords, chestState.theCopperierAge$getLidOpenness());
		Minecraft.getInstance().getBlockEntityRenderDispatcher().submit(chestRenderState, poseStack, collector, camera);
		poseStack.popPose();
	}
}
