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

package net.frozenblock.thecopperierage.tag;

import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class TCABlockTags {
	public static final TagKey<Block> COPPER_FIRE_BASE_BLOCKS = bind("copper_fire_base_blocks");
	public static final TagKey<Block> GEARBOXES = bind("gearboxes");
	public static final TagKey<Block> STICKY_GEARBOXES = bind("sticky_gearboxes");
	public static final TagKey<Block> COPPER_FANS = bind("copper_fans");
	public static final TagKey<Block> CHIMES = bind("chimes");
	public static final TagKey<Block> COPPER_BUTTONS = bind("copper_buttons");
	public static final TagKey<Block> COPPER_PRESSURE_PLATES = bind("copper_pressure_plates");
	public static final TagKey<Block> CANNOT_ROTATE = bind("cannot_rotate");

	private TCABlockTags() {
		throw new UnsupportedOperationException("TCABlockTags contains only static declarations.");
	}

	private static TagKey<Block> bind(String path) {
		return TagKey.create(Registries.BLOCK, TCAConstants.id(path));
	}
}
