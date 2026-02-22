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
import net.frozenblock.thecopperierage.client.renderer.entity.ChestVehicleRenderConstants;
import net.frozenblock.thecopperierage.client.renderer.entity.state.ChestVehicleRenderStateAccess;
import net.frozenblock.thecopperierage.client.renderer.entity.state.ChestLidRenderStateAccess;
import net.frozenblock.thecopperierage.entity.impl.ChestVehicleInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractChestBoat;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.entity.vehicle.ChestRaft;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBoatRenderer.class)
public class AbstractBoatRendererMixin {
	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/vehicle/AbstractBoat;Lnet/minecraft/client/renderer/entity/state/BoatRenderState;F)V", at = @At("TAIL"))
	private void theCopperierAge$extractRenderState(AbstractBoat boat, BoatRenderState state, float partialTicks, CallbackInfo info) {
		final boolean isChestBoat = boat instanceof AbstractChestBoat;
		final boolean isChestRaft = boat instanceof ChestRaft;

		if (state instanceof ChestVehicleRenderStateAccess chestVehicleState) {
			chestVehicleState.theCopperierAge$setChestVehicle(isChestBoat);
			chestVehicleState.theCopperierAge$setChestRaft(isChestRaft);
		}

		if (!isChestBoat || !(boat instanceof ChestVehicleInterface lidAnimating)) return;
		if (!(state instanceof ChestLidRenderStateAccess chestState)) return;

		lidAnimating.theCopperierAge$tickLidController();
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
	private void theCopperierAge$submitVanillaChest(BoatRenderState state, PoseStack poseStack, SubmitNodeCollector output, CameraRenderState cameraRenderState, CallbackInfo info) {
		if (!(state instanceof ChestVehicleRenderStateAccess chestVehicleState) || !chestVehicleState.theCopperierAge$isChestVehicle()) return;
		if (!(state instanceof ChestLidRenderStateAccess chestState)) return;
		final float raftYOffset = chestVehicleState.theCopperierAge$isChestRaft() ? ChestVehicleRenderConstants.CHEST_RAFT_EXTRA_Y : 0.0F;

		poseStack.pushPose();
		poseStack.translate(ChestVehicleRenderConstants.CHEST_BASE_X, ChestVehicleRenderConstants.CHEST_BASE_Y + raftYOffset, ChestVehicleRenderConstants.CHEST_BASE_Z);
		poseStack.mulPose(Axis.YN.rotation(Mth.HALF_PI));
		poseStack.scale(ChestVehicleRenderConstants.CHEST_SCALE, ChestVehicleRenderConstants.CHEST_SCALE, ChestVehicleRenderConstants.CHEST_SCALE);
		poseStack.translate(-ChestVehicleRenderConstants.CHEST_PAD, 0.0F, -ChestVehicleRenderConstants.CHEST_PAD);
		poseStack.translate(ChestVehicleRenderConstants.HALF_BLOCK, ChestVehicleRenderConstants.HALF_BLOCK, ChestVehicleRenderConstants.HALF_BLOCK);
		poseStack.mulPose(Axis.XP.rotation(Mth.PI));
		poseStack.translate(-ChestVehicleRenderConstants.HALF_BLOCK, -ChestVehicleRenderConstants.HALF_BLOCK, -ChestVehicleRenderConstants.HALF_BLOCK);

		ChestRenderState chestRenderState = new ChestRenderState();
		chestRenderState.blockPos = BlockPos.ZERO;
		chestRenderState.blockState = Blocks.CHEST.defaultBlockState();
		chestRenderState.blockEntityType = BlockEntityType.CHEST;
		chestRenderState.lightCoords = state.lightCoords;
		chestRenderState.type = ChestType.SINGLE;
		chestRenderState.material = ChestRenderState.ChestMaterialType.REGULAR;
		chestRenderState.open = chestState.theCopperierAge$getLidOpenness();
		chestRenderState.angle = 0.0F;

		Minecraft.getInstance().getBlockEntityRenderDispatcher().submit(chestRenderState, poseStack, output, cameraRenderState);
		poseStack.popPose();
	}
}
