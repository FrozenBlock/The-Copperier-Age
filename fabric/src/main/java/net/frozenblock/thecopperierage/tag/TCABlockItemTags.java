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
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockItemTagId;

public final class TCABlockItemTags {
	public static final BlockItemTagId GEARBOXES = bind("gearboxes");
	public static final BlockItemTagId STICKY_GEARBOXES = bind("sticky_gearboxes");
	public static final BlockItemTagId COPPER_FANS = bind("copper_fans");
	public static final BlockItemTagId CHIMES = bind("chimes");
	public static final BlockItemTagId COPPER_BUTTONS = bind("copper_buttons");
	public static final BlockItemTagId COPPER_PRESSURE_PLATES = bind("copper_pressure_plates");

	private static BlockItemTagId bind(String name) {
		final Identifier id = TCAConstants.id(name);
		return BlockItemTagId.create(id, id);
	}
}
