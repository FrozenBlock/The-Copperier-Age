/*
 * Copyright 2025 FrozenBlock
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

package net.frozenblock.thecopperierage.client.renderer.blockentity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class ChimeRenderState extends BlockEntityRenderState {
	private static final RenderType UNAFFECTED = RenderType.entityCutout(TCAConstants.id("textures/entity/chime/chime.png"));
	private static final RenderType EXPOSED = RenderType.entityCutout(TCAConstants.id("textures/entity/chime/exposed_chime.png"));
	private static final RenderType WEATHERED = RenderType.entityCutout(TCAConstants.id("textures/entity/chime/weathered_chime.png"));
	private static final RenderType OXIDIZED = RenderType.entityCutout(TCAConstants.id("textures/entity/chime/oxidized_chime.png"));

	public RenderType renderType;
	public float ageInTicks;
	public boolean hanging;
	public Direction direction;
	public Vec3 chimeMovement;

	public void extractTexture(BlockState state) {
		this.renderType = UNAFFECTED;
	}
}
