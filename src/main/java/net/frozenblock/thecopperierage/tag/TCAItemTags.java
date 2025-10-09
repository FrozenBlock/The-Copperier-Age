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

package net.frozenblock.thecopperierage.tag;

import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public final class TCAItemTags {
	public static final TagKey<Item> GEARBOXES = bind("gearboxes");
	public static final TagKey<Item> COPPER_FANS = bind("copper_fans");
	public static final TagKey<Item> CHIMES = bind("chimes");
	public static final TagKey<Item> COPPER_BUTTONS = bind("copper_buttons");
	public static final TagKey<Item> COPPER_PRESSURE_PLATES = bind("copper_pressure_plates");
	public static final TagKey<Item> OXIDIZABLE_EQUIPMENT = bind("oxidizable_equipment");
	public static final TagKey<Item> OXIDIZING_DOES_NOT_SCALE_ATTACK_SPEED = bind("oxidizing_does_not_scale_attack_speed");

	private TCAItemTags() {
		throw new UnsupportedOperationException("TCAItemTags contains only static declarations.");
	}

	@NotNull
	private static TagKey<Item> bind(@NotNull String path) {
		return TagKey.create(Registries.ITEM, TCAConstants.id(path));
	}
}
