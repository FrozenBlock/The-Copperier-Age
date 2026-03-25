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

import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

public final class TCAStats {
	public static final ResourceLocation CHIME_RING = makeCustomStat("chime_ring", StatFormatter.DEFAULT);
	public static final ResourceLocation OPEN_CRATE = makeCustomStat("open_crate", StatFormatter.DEFAULT);

	public static void init() {
		TCAConstants.logWithModId("Registering Stats for", TCAConstants.UNSTABLE_LOGGING);
	}

	private static ResourceLocation makeCustomStat(String id, StatFormatter formatter) {
		final ResourceLocation location = TCAConstants.id(id);
		Registry.register(BuiltInRegistries.CUSTOM_STAT, id, location);
		Stats.CUSTOM.get(location, formatter);
		return location;
	}
}
