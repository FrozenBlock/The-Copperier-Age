/*
 * Copyright 2026 FrozenBlock
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

package net.frozenblock.thecopperierage.references;

import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;

public final class TCAEntityTypeIds {
	public static final ResourceKey<EntityType<?>> CRATE_MINECART = create("crate_minecart");
	public static final ResourceKey<EntityType<?>> DISPENSER_MINECART = create("dispenser_minecart");
	public static final ResourceKey<EntityType<?>> DROPPER_MINECART = create("dropper_minecart");
	public static final ResourceKey<EntityType<?>> JUKEBOX_MINECART = create("jukebox_minecart");

	private static ResourceKey<EntityType<?>> create(String name) {
		return ResourceKey.create(Registries.ENTITY_TYPE, TCAConstants.id(name));
	}

	private TCAEntityTypeIds() {}
}
