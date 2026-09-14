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

package net.frozenblock.thecopperierage.client;

import java.util.function.Supplier;
import net.frozenblock.lib.renderer.model.ModelLayerRegistry;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.client.model.ChimeModel;
import net.frozenblock.thecopperierage.client.renderer.blockentity.ChimeRenderer;
import net.frozenblock.thecopperierage.registry.TCABlockEntityTypes;
import net.frozenblock.thecopperierage.registry.TCAEntityTypes;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.object.cart.MinecartModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.MinecartRenderer;

@ClientOnly
public final class TCAModelLayers {
	public static final ModelLayerLocation CHIME = create("chime");
	public static final ModelLayerLocation CRATE_MINECART = create("crate_minecart");
	public static final ModelLayerLocation DISPENSER_MINECART = create("dispenser_minecart");
	public static final ModelLayerLocation DROPPER_MINECART = create("dropper_minecart");
	public static final ModelLayerLocation JUKEBOX_MINECART = create("jukebox_minecart");

	public static void init() {
		ModelLayerRegistry.register(CHIME, ChimeModel::createLayerDefinition);

		final Supplier<LayerDefinition> minecartBodyLayer = MinecartModel::createBodyLayer;
		ModelLayerRegistry.register(CRATE_MINECART, minecartBodyLayer);
		ModelLayerRegistry.register(DISPENSER_MINECART, minecartBodyLayer);
		ModelLayerRegistry.register(DROPPER_MINECART, minecartBodyLayer);
		ModelLayerRegistry.register(JUKEBOX_MINECART, minecartBodyLayer);
	}

	/**
	 * Registries MUST be populated before this. Runs during NeoForge's setup event.
	 */
	public static void setup() {
		BlockEntityRenderers.register(TCABlockEntityTypes.CHIME.get(), ChimeRenderer::new);

		EntityRenderers.register(TCAEntityTypes.CRATE_MINECART.get(), context -> new MinecartRenderer(context, CRATE_MINECART));
		EntityRenderers.register(TCAEntityTypes.DISPENSER_MINECART.get(), context -> new MinecartRenderer(context, DISPENSER_MINECART));
		EntityRenderers.register(TCAEntityTypes.DROPPER_MINECART.get(), context -> new MinecartRenderer(context, DROPPER_MINECART));
		EntityRenderers.register(TCAEntityTypes.JUKEBOX_MINECART.get(), context -> new MinecartRenderer(context, JUKEBOX_MINECART));
	}

	private static ModelLayerLocation create(String name) {
		return new ModelLayerLocation(TCAConstants.id(name), "main");
	}

	private TCAModelLayers() {}
}
