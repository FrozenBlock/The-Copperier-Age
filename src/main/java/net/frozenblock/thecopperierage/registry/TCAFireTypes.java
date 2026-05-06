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

import net.frozenblock.lib.block.api.fire.FireTypes;
import net.frozenblock.lib.block.impl.fire.FireType;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.tag.TCABlockTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public final class TCAFireTypes {
	public static final ResourceKey<FireType> COPPER_FIRE = FireTypes.createKey(TCAConstants.id("copper_fire"));

	public static void init() {}

	public static void bootstrap(BootstrapContext<FireType> context) {
		final HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);
		FireTypes.register(
			context,
			COPPER_FIRE,
			blocks.getOrThrow(TCABlockTags.COPPER_FIRE_BLOCKS),
			TCAConstants.id("copper_fire_0"),
			TCAConstants.id("copper_fire_1")
		);
	}

}
