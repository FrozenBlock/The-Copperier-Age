/*
 * Copyright 2025 FrozenBlock
 * This file is part of Wilder Wild.
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

package net.frozenblock.thecopperierage.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.frozenblock.lib.feature_flag.api.FeatureFlagApi;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.datagen.loot.TCABlockLootProvider;
import net.frozenblock.thecopperierage.datagen.model.TCAModelProvider;
import net.frozenblock.thecopperierage.datagen.model.TCAPackModelProvider;
import net.frozenblock.thecopperierage.datagen.recipe.TCARecipeProvider;
import net.frozenblock.thecopperierage.datagen.tag.TCABlockTagProvider;
import net.frozenblock.thecopperierage.datagen.tag.TCAInstrumentTagProvider;
import net.frozenblock.thecopperierage.datagen.tag.TCAItemTagProvider;
import net.frozenblock.thecopperierage.registry.TCAInstruments;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import org.jetbrains.annotations.NotNull;

public final class TCADataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(@NotNull FabricDataGenerator dataGenerator) {
		FeatureFlagApi.rebuild();
		final FabricDataGenerator.Pack pack = dataGenerator.createPack();
		final FabricDataGenerator.Pack copperierCopper = dataGenerator.createBuiltinResourcePack(TCAConstants.id("copperier_copper"));

		// ASSETS
		pack.addProvider(TCAModelProvider::new);
		copperierCopper.addProvider(TCAPackModelProvider::new);

		// DATA

		// When adding a registry to generate, don't forget this!
		pack.addProvider(TCARegistryProvider::new);

		pack.addProvider(TCABlockLootProvider::new);
		pack.addProvider(TCABlockTagProvider::new);
		pack.addProvider(TCAItemTagProvider::new);
		pack.addProvider(TCAInstrumentTagProvider::new);
		pack.addProvider(TCARecipeProvider::new);
	}

	@Override
	public void buildRegistry(@NotNull RegistrySetBuilder registryBuilder) {
		TCAConstants.logWithModId("Generating dynamic registries for", TCAConstants.UNSTABLE_LOGGING);

		registryBuilder.add(Registries.INSTRUMENT, TCAInstruments::bootstrap);
	}

	@Override
	public @NotNull String getEffectiveModId() {
		return TCAConstants.MOD_ID;
	}
}
