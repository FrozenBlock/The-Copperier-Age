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

package net.frozenblock.thecopperierage.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.frozenblock.lib.renderer.RenderStateDataKey;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.entity.ChestVehicleOpeners;
import net.frozenblock.thecopperierage.entity.impl.ChestVehicleLidInterface;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.state.properties.ChestType;

@ClientOnly
public final class ChestVehicleRenderHelper {
	public static final String CHEST_BOTTOM_PART = "chest_bottom";
	public static final String CHEST_LID_PART = "chest_lid";
	public static final String CHEST_LOCK_PART = "chest_lock";
	public static final float CHEST_BASE_X = -0.125F;
	public static final float CHEST_BASE_Y = -0.671875F;
	public static final float CHEST_BASE_Z = -0.375F;
	public static final float CHEST_RAFT_Y_OFFSET = -0.3125F;
	public static final float CHEST_SCALE = 12F / 14F;
	public static final float CHEST_PAD = 0.0625F;
	public static final float HALF_BLOCK = 0.5F;
	private static final Direction CHEST_FACING = Direction.SOUTH;
	public static final RenderStateDataKey<Float> VEHICLE_CHEST_OPENNESS = RenderStateDataKey.create(TCAConstants.id("vehicle_chest_openness"));

	public static void extract(Entity entity, EntityRenderState state, float partialTicks) {
		if (!(entity instanceof ChestVehicleLidInterface lidInterface && ChestVehicleOpeners.hasChest(entity))) return;
		state.frozenLib$setData(VEHICLE_CHEST_OPENNESS, lidInterface.theCopperierAge$getLidOpenness(partialTicks));
	}

	public static void submit(
		PoseStack poseStack,
		SubmitNodeCollector submitNodeCollector,
		CameraRenderState camera,
		int lightCoords,
		float openness
	) {
		final ChestRenderState chestState = new ChestRenderState();
		chestState.blockPos = BlockPos.ZERO;
		chestState.blockEntityType = BlockEntityTypes.CHEST;
		chestState.lightCoords = lightCoords;
		chestState.type = ChestType.SINGLE;
		chestState.facing = CHEST_FACING;
		chestState.material = ChestRenderer.xmasTextures()
			? ChestRenderState.ChestMaterialType.CHRISTMAS
			: ChestRenderState.ChestMaterialType.REGULAR;
		chestState.open = openness;

		Minecraft.getInstance().getBlockEntityRenderDispatcher().submit(chestState, poseStack, submitNodeCollector, camera);
	}

	public static void submitBoat(
		BoatRenderState state,
		PoseStack poseStack,
		SubmitNodeCollector submitNodeCollector,
		CameraRenderState camera,
		boolean raft
	) {
		final Float chestOpenness = state.frozenLib$getData(VEHICLE_CHEST_OPENNESS);
		if (chestOpenness == null) return;

		final float raftYOffset = raft ? CHEST_RAFT_Y_OFFSET : 0F;

		poseStack.pushPose();
		poseStack.translate(CHEST_BASE_X, CHEST_BASE_Y + raftYOffset, CHEST_BASE_Z);
		poseStack.mulPose(Axis.YN.rotation(Mth.HALF_PI));
		poseStack.scale(CHEST_SCALE, CHEST_SCALE, CHEST_SCALE);
		poseStack.translate(-CHEST_PAD, 0F, -CHEST_PAD);
		poseStack.translate(HALF_BLOCK, HALF_BLOCK, HALF_BLOCK);
		poseStack.mulPose(Axis.XP.rotation(Mth.PI));
		poseStack.translate(-HALF_BLOCK, -HALF_BLOCK, -HALF_BLOCK);

		submit(
			poseStack,
			submitNodeCollector,
			camera,
			state.lightCoords,
			chestOpenness
		);
		poseStack.popPose();
	}

	private ChestVehicleRenderHelper() {}
}
