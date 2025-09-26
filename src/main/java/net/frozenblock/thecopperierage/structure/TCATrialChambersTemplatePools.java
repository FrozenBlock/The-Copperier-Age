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

package net.frozenblock.thecopperierage.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import org.jetbrains.annotations.NotNull;

public class TCATrialChambersTemplatePools {

	public static void bootstrapTemplatePool(@NotNull BootstrapContext<StructureTemplatePool> pool) {
		HolderGetter<StructureTemplatePool> holderGetter = pool.lookup(Registries.TEMPLATE_POOL);
		Holder<StructureTemplatePool> empty = holderGetter.getOrThrow(Pools.EMPTY);

		register(
			pool,
			string("chamber/addon"),
			new StructureTemplatePool(
				empty,
				ImmutableList.of(
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_1")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_2")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_3")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_4")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_5")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_6")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_7")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_8")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_9")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_10")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_11")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_12")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_13")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_14")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_15")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_16")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_17")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_18")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_19")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_20")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_21")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_22")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_b_1")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_b_12")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_b_13")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_b_14")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_b_15")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_b_16")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_b_17")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("grate_wall_b_18")), 1),
					Pair.of(StructurePoolElement.single(chamberAddon("trench")), 1),
					Pair.of(StructurePoolElement.single(string("chamber/jumping_wind/ceiling_platform_and_trench")), 1),
					Pair.of(StructurePoolElement.single(string("chamber/jumping_wind/pillar_with_ditch_1")), 1),
					Pair.of(StructurePoolElement.single(string("chamber/jumping_wind/pillar_with_ditch_2")), 1),
					Pair.of(StructurePoolElement.single(string("chamber/jumping_wind/pillar_with_ditch_3")), 1)
				),
				StructureTemplatePool.Projection.RIGID
			)
		);
	}

	private static @NotNull String string(String name) {
		return TCAConstants.string("trial_chambers/" + name);
	}

	private static @NotNull String chamberAddon(String name) {
		return string("chamber/addon/" + name);
	}

	@NotNull
	private static ResourceKey<StructureProcessorList> createKey(@NotNull String string) {
		return ResourceKey.create(Registries.PROCESSOR_LIST, TCAConstants.id(string));
	}

	@NotNull
	private static Holder<StructureProcessorList> register(
		@NotNull BootstrapContext<StructureProcessorList> entries, @NotNull ResourceKey<StructureProcessorList> key, @NotNull List<StructureProcessor> list
	) {
		return entries.register(key, new StructureProcessorList(list));
	}

	public static void register(@NotNull BootstrapContext<StructureTemplatePool> pool, String location, StructureTemplatePool templatePool) {
		pool.register(Pools.parseKey(location), templatePool);
	}
}
