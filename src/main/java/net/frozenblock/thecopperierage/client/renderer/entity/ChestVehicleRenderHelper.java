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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.ChestType;

@Environment(EnvType.CLIENT)
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

	public static ChestRenderState createChestRenderState(int lightCoords, float openness) {
		final ChestRenderState chestRenderState = new ChestRenderState();
		chestRenderState.blockPos = BlockPos.ZERO;
		chestRenderState.blockState = Blocks.CHEST.defaultBlockState();
		chestRenderState.blockEntityType = BlockEntityType.CHEST;
		chestRenderState.lightCoords = lightCoords;
		chestRenderState.type = ChestType.SINGLE;
		chestRenderState.material = ChestRenderState.ChestMaterialType.REGULAR;
		chestRenderState.open = openness;
		return chestRenderState;
	}
}
