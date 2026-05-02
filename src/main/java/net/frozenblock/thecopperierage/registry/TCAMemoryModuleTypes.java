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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.golem.CopperGolem;

public final class TCAMemoryModuleTypes {
	public static final MemoryModuleType<Set<GlobalPos>> UNREACHABLE_BUTTON_PRESS_BLOCK_POSITIONS = register(
		"unreachable_button_press_block_positions",
		GlobalPos.CODEC.listOf().xmap(Sets::newHashSet, Lists::newArrayList)
	);
	public static final MemoryModuleType<GlobalPos> TARGETED_BUTTON = register("targeted_button");
	public static final MemoryModuleType<List<CopperGolem>> NEARBY_COPPER_GOLEMS = register("nearby_copper_golems");
	public static final MemoryModuleType<Integer> BUTTON_PRESS_COOLDOWN_TICKS = register("button_press_cooldown_ticks", Codec.INT);
	public static final MemoryModuleType<Integer> NEARBY_BUTTON_SEARCH_TICKS = register("nearby_button_search_ticks", Codec.INT);

	public static void init() {}

	private static <U> MemoryModuleType<U> register(String name, Codec<U> codec) {
		return Registry.register(BuiltInRegistries.MEMORY_MODULE_TYPE, TCAConstants.id(name), new MemoryModuleType<>(Optional.of(codec)));
	}

	private static <U> MemoryModuleType<U> register(String name) {
		return Registry.register(BuiltInRegistries.MEMORY_MODULE_TYPE, TCAConstants.id(name), new MemoryModuleType<>(Optional.empty()));
	}
}
