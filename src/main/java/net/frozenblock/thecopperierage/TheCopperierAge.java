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

package net.frozenblock.thecopperierage;

import net.fabricmc.loader.api.ModContainer;
import net.frozenblock.lib.entrypoint.api.FrozenModInitializer;
import net.frozenblock.lib.feature_flag.api.FeatureFlagApi;
import net.frozenblock.thecopperierage.registry.TCABlocks;
import net.frozenblock.thecopperierage.registry.TCACreativeInventorySorting;
import net.frozenblock.thecopperierage.registry.TCAItems;
import net.frozenblock.thecopperierage.registry.TCAParticleTypes;

public final class TheCopperierAge extends FrozenModInitializer {

	public TheCopperierAge() {
		super(TCAConstants.MOD_ID);
	}

	@Override
	public void onInitialize(String modId, ModContainer container) {
		TCAFeatureFlags.init();
		FeatureFlagApi.rebuild();

		TCABlocks.registerBlocks();
		TCAItems.registerItems();
		TCACreativeInventorySorting.init();

		TCAParticleTypes.registerParticles();

		TCABlocks.registerBlockProperties();
	}


}
