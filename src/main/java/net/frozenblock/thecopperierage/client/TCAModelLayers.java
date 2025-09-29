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

package net.frozenblock.thecopperierage.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.client.model.ChimeModel;
import net.frozenblock.thecopperierage.client.renderer.blockentity.ChimeRenderer;
import net.frozenblock.thecopperierage.registry.TCABlockEntityTypes;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

@Environment(EnvType.CLIENT)
public final class TCAModelLayers {
	public static final ModelLayerLocation CHIME = new ModelLayerLocation(TCAConstants.id("chime"), "main");

	public static void init() {
		BlockEntityRenderers.register(TCABlockEntityTypes.CHIME, ChimeRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(CHIME, ChimeModel::createLayerDefinition);
	}
}
