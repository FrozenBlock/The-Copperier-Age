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
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

	private ChestVehicleRenderHelper() {
	}

	public static void submitChest(
		PoseStack poseStack,
		SubmitNodeCollector collector,
		CameraRenderState camera,
		int lightCoords,
		float openness
	) {
		final ChestRenderState chestRenderState = new ChestRenderState();
		chestRenderState.blockPos = BlockPos.ZERO;
		chestRenderState.blockEntityType = BlockEntityTypes.CHEST;
		chestRenderState.lightCoords = lightCoords;
		chestRenderState.type = ChestType.SINGLE;
		chestRenderState.facing = CHEST_FACING;
		chestRenderState.material = ChestRenderer.xmasTextures()
			? ChestRenderState.ChestMaterialType.CHRISTMAS
			: ChestRenderState.ChestMaterialType.REGULAR;
		chestRenderState.open = openness;

		Minecraft.getInstance().getBlockEntityRenderDispatcher().submit(chestRenderState, poseStack, collector, camera);
	}
}
