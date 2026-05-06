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

package net.frozenblock.thecopperierage.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.frozenblock.lib.feature_flag.api.FeatureFlagApi;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.data.loot.TCABlockLootProvider;
import net.frozenblock.thecopperierage.data.model.TCAModelProvider;
import net.frozenblock.thecopperierage.data.model.TCAPackModelProvider;
import net.frozenblock.thecopperierage.data.recipe.TCARecipeProvider;
import net.frozenblock.thecopperierage.data.tag.TCABlockTagsProvider;
import net.frozenblock.thecopperierage.data.tag.TCAEntityTypeTagsProvider;
import net.frozenblock.thecopperierage.data.tag.TCAInstrumentTagsProvider;
import net.frozenblock.thecopperierage.data.tag.TCAItemTagsProvider;
import net.frozenblock.thecopperierage.registry.TCAFireTypes;
import net.frozenblock.thecopperierage.registry.TCAInstruments;
import net.frozenblock.thecopperierage.data.worldgen.structure.TCATrialChambersTemplatePools;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public final class TCADataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator dataGenerator) {
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
		pack.addProvider(TCABlockTagsProvider::new);
		pack.addProvider(TCAItemTagsProvider::new);
		pack.addProvider(TCAInstrumentTagsProvider::new);
		pack.addProvider(TCAEntityTypeTagsProvider::new);
		pack.addProvider(TCARecipeProvider::new);
	}

	@Override
	public void buildRegistry(RegistrySetBuilder registryBuilder) {
		TCAConstants.logWithModId("Generating dynamic registries for", TCAConstants.UNSTABLE_LOGGING);

		registryBuilder.add(Registries.INSTRUMENT, TCAInstruments::bootstrap);
		registryBuilder.add(Registries.TEMPLATE_POOL, TCATrialChambersTemplatePools::bootstrapTemplatePool);
		registryBuilder.add(FrozenLibRegistries.FIRE_TYPE, TCAFireTypes::bootstrap);
	}

	@Override
	public String getEffectiveModId() {
		return TCAConstants.MOD_ID;
	}
}
