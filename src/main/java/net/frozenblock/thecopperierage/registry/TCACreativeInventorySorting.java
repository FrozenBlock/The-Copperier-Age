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

import net.frozenblock.lib.item.api.FrozenCreativeTabs;
import net.frozenblock.thecopperierage.tag.TCAInstrumentTags;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public final class TCACreativeInventorySorting {

	public static void init() {
		// GEARBOX
		insertAfterInBuildingBlocks(Items.REPEATER, TCABlocks.GEARBOX.waxed());
		insertAfterInBuildingBlocks(TCABlocks.GEARBOX.waxed(), TCABlocks.STICKY_GEARBOX.waxed());

		insertAfterInBuildingBlocks(Items.COPPER_BULB, TCABlocks.GEARBOX.unaffected());
		insertAfterInBuildingBlocks(TCABlocks.GEARBOX.unaffected(), TCABlocks.STICKY_GEARBOX.unaffected());
		insertAfterInBuildingBlocks(Items.WAXED_COPPER_BULB, TCABlocks.GEARBOX.waxed());
		insertAfterInBuildingBlocks(TCABlocks.GEARBOX.waxed(), TCABlocks.STICKY_GEARBOX.waxed());
		insertAfterInBuildingBlocks(Items.EXPOSED_COPPER_BULB, TCABlocks.GEARBOX.exposed());
		insertAfterInBuildingBlocks(TCABlocks.GEARBOX.exposed(), TCABlocks.STICKY_GEARBOX.exposed());
		insertAfterInBuildingBlocks(Items.WAXED_EXPOSED_COPPER_BULB, TCABlocks.GEARBOX.waxedExposed());
		insertAfterInBuildingBlocks(TCABlocks.GEARBOX.waxedExposed(), TCABlocks.STICKY_GEARBOX.waxedExposed());
		insertAfterInBuildingBlocks(Items.WEATHERED_COPPER_BULB, TCABlocks.GEARBOX.weathered());
		insertAfterInBuildingBlocks(TCABlocks.GEARBOX.weathered(), TCABlocks.STICKY_GEARBOX.weathered());
		insertAfterInBuildingBlocks(Items.WAXED_WEATHERED_COPPER_BULB, TCABlocks.GEARBOX.waxedWeathered());
		insertAfterInBuildingBlocks(TCABlocks.GEARBOX.waxedWeathered(), TCABlocks.STICKY_GEARBOX.waxedWeathered());
		insertAfterInBuildingBlocks(Items.OXIDIZED_COPPER_BULB, TCABlocks.GEARBOX.oxidized());
		insertAfterInBuildingBlocks(TCABlocks.GEARBOX.oxidized(), TCABlocks.STICKY_GEARBOX.oxidized());
		insertAfterInBuildingBlocks(Items.WAXED_OXIDIZED_COPPER_BULB, TCABlocks.GEARBOX.waxedOxidized());
		insertAfterInBuildingBlocks(TCABlocks.GEARBOX.waxedOxidized(), TCABlocks.STICKY_GEARBOX.waxedOxidized());

		// FAN
		insertBeforeInRedstoneBlocks(Items.RAIL, TCABlocks.COPPER_FAN.waxed());

		insertAfterInBuildingBlocks(TCABlocks.STICKY_GEARBOX.unaffected(), TCABlocks.COPPER_FAN.unaffected());
		insertAfterInBuildingBlocks(TCABlocks.STICKY_GEARBOX.waxed(), TCABlocks.COPPER_FAN.waxed());
		insertAfterInBuildingBlocks(TCABlocks.STICKY_GEARBOX.exposed(), TCABlocks.COPPER_FAN.exposed());
		insertAfterInBuildingBlocks(TCABlocks.STICKY_GEARBOX.waxedExposed(), TCABlocks.COPPER_FAN.waxedExposed());
		insertAfterInBuildingBlocks(TCABlocks.STICKY_GEARBOX.weathered(), TCABlocks.COPPER_FAN.weathered());
		insertAfterInBuildingBlocks(TCABlocks.STICKY_GEARBOX.waxedWeathered(), TCABlocks.COPPER_FAN.waxedWeathered());
		insertAfterInBuildingBlocks(TCABlocks.STICKY_GEARBOX.oxidized(), TCABlocks.COPPER_FAN.oxidized());
		insertAfterInBuildingBlocks(TCABlocks.STICKY_GEARBOX.waxedOxidized(), TCABlocks.COPPER_FAN.waxedOxidized());

		// CHIME
		insertAfterInFunctionalBlocks(Items.WAXED_OXIDIZED_LIGHTNING_ROD, TCABlocks.CHIME.unaffected());
		insertAfterInFunctionalBlocks(TCABlocks.CHIME.unaffected(), TCABlocks.CHIME.exposed());
		insertAfterInFunctionalBlocks(TCABlocks.CHIME.exposed(), TCABlocks.CHIME.weathered());
		insertAfterInFunctionalBlocks(TCABlocks.CHIME.weathered(), TCABlocks.CHIME.oxidized());
		insertAfterInFunctionalBlocks(TCABlocks.CHIME.oxidized(), TCABlocks.CHIME.waxed());
		insertAfterInFunctionalBlocks(TCABlocks.CHIME.waxed(), TCABlocks.CHIME.waxedExposed());
		insertAfterInFunctionalBlocks(TCABlocks.CHIME.waxedExposed(), TCABlocks.CHIME.waxedWeathered());
		insertAfterInFunctionalBlocks(TCABlocks.CHIME.waxedWeathered(), TCABlocks.CHIME.waxedOxidized());

		insertAfterInBuildingBlocks(TCABlocks.COPPER_FAN.unaffected(), TCABlocks.CHIME.unaffected());
		insertAfterInBuildingBlocks(TCABlocks.COPPER_FAN.waxed(), TCABlocks.CHIME.waxed());
		insertAfterInBuildingBlocks(TCABlocks.COPPER_FAN.exposed(), TCABlocks.CHIME.exposed());
		insertAfterInBuildingBlocks(TCABlocks.COPPER_FAN.waxedExposed(), TCABlocks.CHIME.waxedExposed());
		insertAfterInBuildingBlocks(TCABlocks.COPPER_FAN.weathered(), TCABlocks.CHIME.weathered());
		insertAfterInBuildingBlocks(TCABlocks.COPPER_FAN.waxedWeathered(), TCABlocks.CHIME.waxedWeathered());
		insertAfterInBuildingBlocks(TCABlocks.COPPER_FAN.oxidized(), TCABlocks.CHIME.oxidized());
		insertAfterInBuildingBlocks(TCABlocks.COPPER_FAN.waxedOxidized(), TCABlocks.CHIME.waxedOxidized());

		// COPPER CRATE
		insertAfterInBuildingBlocks(Items.BARREL, TCABlocks.CRATE);

		insertAfterInRedstoneBlocks(Items.BARREL, TCABlocks.CRATE);

		// BUTTON
		insertAfterInRedstoneBlocks(Items.STONE_BUTTON, TCABlocks.COPPER_BUTTON.waxed());
		insertAfterInRedstoneBlocks(TCABlocks.COPPER_BUTTON.waxed(), TCABlocks.COPPER_BUTTON.waxedExposed());
		insertAfterInRedstoneBlocks(TCABlocks.COPPER_BUTTON.waxedExposed(), TCABlocks.COPPER_BUTTON.waxedWeathered());
		insertAfterInRedstoneBlocks(TCABlocks.COPPER_BUTTON.waxedWeathered(), TCABlocks.COPPER_BUTTON.waxedOxidized());

		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR, TCABlocks.COPPER_BUTTON.unaffected());
		insertAfterInBuildingBlocks(Items.WAXED_COPPER_TRAPDOOR, TCABlocks.COPPER_BUTTON.waxed());
		insertAfterInBuildingBlocks(Items.EXPOSED_COPPER_TRAPDOOR, TCABlocks.COPPER_BUTTON.exposed());
		insertAfterInBuildingBlocks(Items.WAXED_EXPOSED_COPPER_TRAPDOOR, TCABlocks.COPPER_BUTTON.waxedExposed());
		insertAfterInBuildingBlocks(Items.WEATHERED_COPPER_TRAPDOOR, TCABlocks.COPPER_BUTTON.weathered());
		insertAfterInBuildingBlocks(Items.WAXED_WEATHERED_COPPER_TRAPDOOR, TCABlocks.COPPER_BUTTON.waxedWeathered());
		insertAfterInBuildingBlocks(Items.OXIDIZED_COPPER_TRAPDOOR, TCABlocks.COPPER_BUTTON.oxidized());
		insertAfterInBuildingBlocks(Items.WAXED_OXIDIZED_COPPER_TRAPDOOR, TCABlocks.COPPER_BUTTON.waxedOxidized());

		// PRESSURE PLATE
		insertAfterInRedstoneBlocks(Items.HEAVY_WEIGHTED_PRESSURE_PLATE, TCABlocks.WEIGHTED_PRESSURE_PLATE.waxed());
		insertAfterInRedstoneBlocks(TCABlocks.WEIGHTED_PRESSURE_PLATE.waxed(), TCABlocks.WEIGHTED_PRESSURE_PLATE.waxedExposed());
		insertAfterInRedstoneBlocks(TCABlocks.WEIGHTED_PRESSURE_PLATE.waxedExposed(), TCABlocks.WEIGHTED_PRESSURE_PLATE.waxedWeathered());
		insertAfterInRedstoneBlocks(TCABlocks.WEIGHTED_PRESSURE_PLATE.waxedWeathered(), TCABlocks.WEIGHTED_PRESSURE_PLATE.waxedOxidized());

		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR, TCABlocks.WEIGHTED_PRESSURE_PLATE.unaffected());
		insertAfterInBuildingBlocks(Items.WAXED_COPPER_TRAPDOOR, TCABlocks.WEIGHTED_PRESSURE_PLATE.waxed());
		insertAfterInBuildingBlocks(Items.EXPOSED_COPPER_TRAPDOOR, TCABlocks.WEIGHTED_PRESSURE_PLATE.exposed());
		insertAfterInBuildingBlocks(Items.WAXED_EXPOSED_COPPER_TRAPDOOR, TCABlocks.WEIGHTED_PRESSURE_PLATE.waxedExposed());
		insertAfterInBuildingBlocks(Items.WEATHERED_COPPER_TRAPDOOR, TCABlocks.WEIGHTED_PRESSURE_PLATE.weathered());
		insertAfterInBuildingBlocks(Items.WAXED_WEATHERED_COPPER_TRAPDOOR, TCABlocks.WEIGHTED_PRESSURE_PLATE.waxedWeathered());
		insertAfterInBuildingBlocks(Items.OXIDIZED_COPPER_TRAPDOOR, TCABlocks.WEIGHTED_PRESSURE_PLATE.oxidized());
		insertAfterInBuildingBlocks(Items.WAXED_OXIDIZED_COPPER_TRAPDOOR, TCABlocks.WEIGHTED_PRESSURE_PLATE.waxedOxidized());

		insertAfterInFunctionalBlocks(Items.SOUL_CAMPFIRE, TCABlocks.COPPER_CAMPFIRE);
		insertBeforeInToolsAndUtilities(Items.BRUSH, TCAItems.WRENCH);
		insertInstrumentBefore(Items.MUSIC_DISC_13, TCAItems.COPPER_HORN, TCAInstrumentTags.COPPER_HORNS, CreativeModeTabs.TOOLS_AND_UTILITIES);
		insertAfterInNaturalBlocks(Blocks.JACK_O_LANTERN, TCABlocks.COPPER_JACK_O_LANTERN);
		insertAfterInNaturalBlocks(TCABlocks.COPPER_JACK_O_LANTERN, TCABlocks.REDSTONE_JACK_O_LANTERN);
		insertAfterInRedstoneBlocks(Blocks.REDSTONE_TORCH, TCABlocks.REDSTONE_JACK_O_LANTERN);
		insertAfterInRedstoneBlocks(Blocks.REDSTONE_BLOCK, TCABlocks.REDSTONE_GRIT);
	}

	private static void insertBeforeInBuildingBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.insertBefore(comparedItem, item, CreativeModeTabs.BUILDING_BLOCKS);
	}

	private static void insertAfterInBuildingBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.insertAfter(comparedItem, item, CreativeModeTabs.BUILDING_BLOCKS);
	}

	private static void insertBeforeInColoredBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.insertBefore(comparedItem, item, CreativeModeTabs.COLORED_BLOCKS);
	}

	private static void insertAfterInColoredBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.insertAfter(comparedItem, item, CreativeModeTabs.COLORED_BLOCKS);
	}

	private static void insertBeforeInNaturalBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.insertBefore(comparedItem, item, CreativeModeTabs.NATURAL_BLOCKS);
	}

	private static void insertAfterInNaturalBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.insertAfter(comparedItem, item, CreativeModeTabs.NATURAL_BLOCKS);
	}

	private static void insertAfterInBuildingAndNaturalBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.insertAfter(comparedItem, item, CreativeModeTabs.BUILDING_BLOCKS, CreativeModeTabs.NATURAL_BLOCKS);
	}

	private static void insertAfterInNaturalAndFunctionalBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.insertAfter(comparedItem, item, CreativeModeTabs.NATURAL_BLOCKS, CreativeModeTabs.FUNCTIONAL_BLOCKS);
	}

	private static void insertBeforeInFunctionalBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.insertBefore(comparedItem, item, CreativeModeTabs.FUNCTIONAL_BLOCKS);
	}

	private static void insertAfterInFunctionalBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.insertAfter(comparedItem, item, CreativeModeTabs.FUNCTIONAL_BLOCKS);
	}

	private static void insertBeforeInRedstoneBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.insertBefore(comparedItem, item, CreativeModeTabs.REDSTONE_BLOCKS);
	}

	private static void insertAfterInRedstoneBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.insertAfter(comparedItem, item, CreativeModeTabs.REDSTONE_BLOCKS);
	}

	private static void insertInToolsAndUtilities(ItemLike item) {
		FrozenCreativeTabs.insert(item, CreativeModeTabs.TOOLS_AND_UTILITIES);
	}

	private static void insertAfterInToolsAndUtilities(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.insertAfter(comparedItem, item, CreativeModeTabs.TOOLS_AND_UTILITIES);
	}

	private static void insertBeforeInToolsAndUtilities(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.insertBefore(comparedItem, item, CreativeModeTabs.TOOLS_AND_UTILITIES);
	}

	private static void insertBeforeInIngredients(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.insertBefore(comparedItem, item, CreativeModeTabs.INGREDIENTS);
	}

	private static void insertAfterInIngredients(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.insertAfter(comparedItem, item, CreativeModeTabs.INGREDIENTS);
	}

	private static void insertBeforeInFoodAndDrinks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.insertBefore(comparedItem, item, CreativeModeTabs.FOOD_AND_DRINKS);
	}

	private static void insertAfterInFoodAndDrinks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.insertAfter(comparedItem, item, CreativeModeTabs.FOOD_AND_DRINKS);
	}

	private static void insertAfterInCombat(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.insertAfter(comparedItem, item, CreativeModeTabs.COMBAT);
	}

	private static void insertBeforeInSpawnEggs(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.insertBefore(comparedItem, item, CreativeModeTabs.SPAWN_EGGS);
	}

	private static void insertAfterInSpawnEggs(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.insertAfter(comparedItem, item, CreativeModeTabs.SPAWN_EGGS);
	}

	@SafeVarargs
	private static void insertInstrumentBefore(
		Item comparedItem,
		Item instrument,
		TagKey<Instrument> tagKey,
		ResourceKey<CreativeModeTab>... tabs
	) {
		FrozenCreativeTabs.addInstrumentBefore(comparedItem, instrument, tagKey, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, tabs);
	}
}
