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

package net.frozenblock.thecopperierage.datagen.recipe;

import net.fabricmc.fabric.api.recipe.v1.ingredient.DefaultCustomIngredients;
import net.frozenblock.lib.recipe.api.ShapedRecipeBuilderExtension;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.registry.TCAItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Instruments;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.InstrumentComponent;
import net.minecraft.world.item.crafting.Ingredient;

public final class CopperHornRecipeProvider {

	static void buildRecipes(RecipeProvider provider, HolderLookup.Provider lookup, RecipeOutput exporter) {
		copperHorn(provider, lookup, exporter, "recorder", Instruments.YEARN_GOAT_HORN, TCAItems.RECORDER_COPPER_HORN);
		copperHorn(provider, lookup, exporter, "clarinet", Instruments.DREAM_GOAT_HORN, TCAItems.CLARINET_COPPER_HORN);
		copperHorn(provider, lookup, exporter, "flute", Instruments.CALL_GOAT_HORN, TCAItems.FLUTE_COPPER_HORN);
		copperHorn(provider, lookup, exporter, "oboe", Instruments.SING_GOAT_HORN, TCAItems.OBOE_COPPER_HORN);
		copperHorn(provider, lookup, exporter, "sax", Instruments.PONDER_GOAT_HORN, TCAItems.SAX_COPPER_HORN);
		copperHorn(provider, lookup, exporter, "trombone", Instruments.SEEK_GOAT_HORN, TCAItems.TROMBONE_COPPER_HORN);
		copperHorn(provider, lookup, exporter, "trumpet", Instruments.ADMIRE_GOAT_HORN, TCAItems.TRUMPET_COPPER_HORN);
		copperHorn(provider, lookup, exporter, "tuba", Instruments.FEEL_GOAT_HORN, TCAItems.TUBA_COPPER_HORN);
	}

	private static void copperHorn(
		RecipeProvider provider,
		HolderLookup.Provider lookup,
		RecipeOutput exporter,
		String name,
		ResourceKey<Instrument> goatHornInstrument,
		ResourceKey<Instrument> copperHornInstrument
	) {
		((ShapedRecipeBuilderExtension) provider.shaped(RecipeCategory.TOOLS, TCAItems.COPPER_HORN)
			.group("wilderwild_copper_horn")
			.define('C', Ingredient.of(Items.COPPER_INGOT))
			.define('G', DefaultCustomIngredients.components(
				Ingredient.of(Items.GOAT_HORN),
				DataComponentPatch.builder()
					.set(DataComponents.INSTRUMENT, new InstrumentComponent(lookup.lookupOrThrow(Registries.INSTRUMENT).getOrThrow(goatHornInstrument)))
					.build()
			))
			.pattern("CGC")
			.pattern(" C ")
			.unlockedBy("has_horn", provider.has(Items.GOAT_HORN))
		).frozenLib$patch(
			DataComponentPatch.builder().set(
				DataComponents.INSTRUMENT,
				new InstrumentComponent(lookup.lookupOrThrow(Registries.INSTRUMENT).getOrThrow(copperHornInstrument))
			).build()
		).save(exporter, TCAConstants.string(name + "_copper_horn"));
	}

}
