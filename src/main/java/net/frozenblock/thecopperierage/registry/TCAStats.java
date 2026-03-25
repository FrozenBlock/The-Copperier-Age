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
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

public final class TCAStats {
	public static final Identifier CHIME_RING = makeCustomStat("chime_ring", StatFormatter.DEFAULT);
	public static final Identifier OPEN_CRATE = makeCustomStat("open_crate", StatFormatter.DEFAULT);

	public static void init() {
		TCAConstants.logWithModId("Registering Stats for", TCAConstants.UNSTABLE_LOGGING);
	}

	private static Identifier makeCustomStat(String id, StatFormatter formatter) {
		final Identifier identifier = TCAConstants.id(id);
		Registry.register(BuiltInRegistries.CUSTOM_STAT, id, identifier);
		Stats.CUSTOM.get(identifier, formatter);
		return identifier;
	}
}
