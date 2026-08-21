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

package net.frozenblock.thecopperierage.client;

import net.frozenblock.lib.particle.client.api.ParticleProviderRegistry;
import net.frozenblock.thecopperierage.registry.TCAParticleTypes;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.particle.LavaParticle;

@ClientOnly
public final class TCAParticleEngine {

	public static void init() {
		ParticleProviderRegistry.register(TCAParticleTypes.COPPER_LAVA::get, LavaParticle.Provider::new);
	}
}
