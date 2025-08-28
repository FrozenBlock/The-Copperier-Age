/*
 * Copyright 2025 FrozenBlock
 * This file is part of Netherier Nether.
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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.frozenblock.thecopperierage.particle.CopperCampfireSmokeParticle;
import net.frozenblock.thecopperierage.particle.CopperSmokeParticle;
import net.frozenblock.thecopperierage.particle.LargeCopperSmokeParticle;
import net.frozenblock.thecopperierage.registry.TCAParticleTypes;
import net.minecraft.client.particle.LavaParticle;

@Environment(EnvType.CLIENT)
public final class TCAParticleEngine {

	public static void init() {
		ParticleFactoryRegistry particleRegistry = ParticleFactoryRegistry.getInstance();
		particleRegistry.register(TCAParticleTypes.COPPER_SMOKE, CopperSmokeParticle.Provider::new);
		particleRegistry.register(TCAParticleTypes.LARGE_COPPER_SMOKE, LargeCopperSmokeParticle.Provider::new);
		particleRegistry.register(TCAParticleTypes.COPPER_LAVA, LavaParticle.Provider::new);
		particleRegistry.register(TCAParticleTypes.COPPER_CAMPFIRE_COSY_SMOKE, CopperCampfireSmokeParticle.CosyProvider::new);
		particleRegistry.register(TCAParticleTypes.COPPER_CAMPFIRE_SIGNAL_SMOKE, CopperCampfireSmokeParticle.SignalProvider::new);
	}
}
