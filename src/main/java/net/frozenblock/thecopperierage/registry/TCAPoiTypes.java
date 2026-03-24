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

package net.frozenblock.thecopperierage.registry;

import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;

public final class TCAPoiTypes {
	public static final ResourceKey<PoiType> COPPER_BUTTON_KEY = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, TCAConstants.id("copper_button"));
	public static final PoiType COPPER_BUTTON = PointOfInterestHelper.register(
		TCAConstants.id("copper_button"),
		1,
		1,
		TCABlocks.COPPER_BUTTON.asList().toArray(new Block[] {})
	);

	public static void init() {
	}
}
