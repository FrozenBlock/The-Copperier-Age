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

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public final class TCAParticleTypes {
	public static final SimpleParticleType COPPER_LAVA = register("copper_lava");

	public static void init() {}

	private static SimpleParticleType register(String name, boolean alwaysShow) {
		return Registry.register(BuiltInRegistries.PARTICLE_TYPE, TCAConstants.id(name), FabricParticleTypes.simple(alwaysShow));
	}

	private static SimpleParticleType register(String name) {
		return register(name, false);
	}
}
