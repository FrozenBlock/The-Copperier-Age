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
import net.frozenblock.thecopperierage.registry.TCABlocks;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class ChimeRenderState extends BlockEntityRenderState {
	private static final ResourceLocation UNAFFECTED = TCAConstants.id("textures/entity/chime/chime.png");
	private static final ResourceLocation EXPOSED = TCAConstants.id("textures/entity/chime/exposed_chime.png");
	private static final ResourceLocation WEATHERED = TCAConstants.id("textures/entity/chime/weathered_chime.png");
	private static final ResourceLocation OXIDIZED = TCAConstants.id("textures/entity/chime/oxidized_chime.png");

	public ResourceLocation texture;
	public float animationProgress;
	public boolean hanging;
	public Direction direction;
	public Direction visualDirection;
	public Vec3 influence;
	public Vec3 relativeInfluence;

	public void extractTexture(@NotNull BlockState state) {
		ResourceLocation texture = UNAFFECTED;
		if (state.is(TCABlocks.CHIME.exposed()) || state.is(TCABlocks.CHIME.waxedExposed())) {
			texture = EXPOSED;
		} else if (state.is(TCABlocks.CHIME.weathered()) || state.is(TCABlocks.CHIME.waxedWeathered())) {
			texture = WEATHERED;
		} if (state.is(TCABlocks.CHIME.oxidized()) || state.is(TCABlocks.CHIME.waxedOxidized())) {
			texture = OXIDIZED;
		}
		this.texture = texture;
	}
}
