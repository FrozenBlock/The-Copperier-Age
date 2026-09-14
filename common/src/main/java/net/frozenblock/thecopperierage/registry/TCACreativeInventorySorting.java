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

import net.frozenblock.lib.item.api.creative.CreativeModeTabSorter;
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

	public static void setup() {
		// GEARBOX
		insertAfterInRedstoneBlocks(Items.LIGHTNING_ROD.waxed().unaffected(), TCAItems.GEARBOX.waxed().unaffected());
		insertAfterInRedstoneBlocks(TCAItems.GEARBOX.waxed().unaffected(), TCAItems.STICKY_GEARBOX.waxed().unaffected());

		insertAfterInBuildingBlocks(Items.COPPER_BULB.weathering().unaffected(), TCAItems.GEARBOX.weathering().unaffected());
		insertAfterInBuildingBlocks(TCAItems.GEARBOX.weathering().unaffected(), TCAItems.STICKY_GEARBOX.weathering().unaffected());
		insertAfterInBuildingBlocks(Items.COPPER_BULB.waxed().unaffected(), TCAItems.GEARBOX.waxed().unaffected());
		insertAfterInBuildingBlocks(TCAItems.GEARBOX.waxed().unaffected(), TCAItems.STICKY_GEARBOX.waxed().unaffected());
		insertAfterInBuildingBlocks(Items.COPPER_BULB.weathering().exposed(), TCAItems.GEARBOX.weathering().exposed());
		insertAfterInBuildingBlocks(TCAItems.GEARBOX.weathering().exposed(), TCAItems.STICKY_GEARBOX.weathering().exposed());
		insertAfterInBuildingBlocks(Items.COPPER_BULB.waxed().exposed(), TCAItems.GEARBOX.waxed().exposed());
		insertAfterInBuildingBlocks(TCAItems.GEARBOX.waxed().exposed(), TCAItems.STICKY_GEARBOX.waxed().exposed());
		insertAfterInBuildingBlocks(Items.COPPER_BULB.weathering().weathered(), TCAItems.GEARBOX.weathering().weathered());
		insertAfterInBuildingBlocks(TCAItems.GEARBOX.weathering().weathered(), TCAItems.STICKY_GEARBOX.weathering().weathered());
		insertAfterInBuildingBlocks(Items.COPPER_BULB.waxed().weathered(), TCAItems.GEARBOX.waxed().weathered());
		insertAfterInBuildingBlocks(TCAItems.GEARBOX.waxed().weathered(), TCAItems.STICKY_GEARBOX.waxed().weathered());
		insertAfterInBuildingBlocks(Items.COPPER_BULB.weathering().oxidized(), TCAItems.GEARBOX.weathering().oxidized());
		insertAfterInBuildingBlocks(TCAItems.GEARBOX.weathering().oxidized(), TCAItems.STICKY_GEARBOX.weathering().oxidized());
		insertAfterInBuildingBlocks(Items.COPPER_BULB.waxed().oxidized(), TCAItems.GEARBOX.waxed().oxidized());
		insertAfterInBuildingBlocks(TCAItems.GEARBOX.waxed().oxidized(), TCAItems.STICKY_GEARBOX.waxed().oxidized());

		// FAN
		insertBeforeInRedstoneBlocks(Items.RAIL, TCAItems.COPPER_FAN.waxed().unaffected());

		insertAfterInBuildingBlocks(TCAItems.STICKY_GEARBOX.weathering().unaffected(), TCAItems.COPPER_FAN.weathering().unaffected());
		insertAfterInBuildingBlocks(TCAItems.STICKY_GEARBOX.waxed().unaffected(), TCAItems.COPPER_FAN.waxed().unaffected());
		insertAfterInBuildingBlocks(TCAItems.STICKY_GEARBOX.weathering().exposed(), TCAItems.COPPER_FAN.weathering().exposed());
		insertAfterInBuildingBlocks(TCAItems.STICKY_GEARBOX.waxed().exposed(), TCAItems.COPPER_FAN.waxed().exposed());
		insertAfterInBuildingBlocks(TCAItems.STICKY_GEARBOX.weathering().weathered(), TCAItems.COPPER_FAN.weathering().weathered());
		insertAfterInBuildingBlocks(TCAItems.STICKY_GEARBOX.waxed().weathered(), TCAItems.COPPER_FAN.waxed().weathered());
		insertAfterInBuildingBlocks(TCAItems.STICKY_GEARBOX.weathering().oxidized(), TCAItems.COPPER_FAN.weathering().oxidized());
		insertAfterInBuildingBlocks(TCAItems.STICKY_GEARBOX.waxed().oxidized(), TCAItems.COPPER_FAN.waxed().oxidized());

		// CHIME
		insertBeforeInRedstoneBlocks(Items.SCULK_SENSOR, TCAItems.CHIME.waxed().unaffected());

		insertAfterInFunctionalBlocks(Items.LIGHTNING_ROD.waxed().oxidized(), TCAItems.CHIME.weathering().unaffected());
		insertAfterInFunctionalBlocks(TCAItems.CHIME.weathering().unaffected(), TCAItems.CHIME.weathering().exposed());
		insertAfterInFunctionalBlocks(TCAItems.CHIME.weathering().exposed(), TCAItems.CHIME.weathering().weathered());
		insertAfterInFunctionalBlocks(TCAItems.CHIME.weathering().weathered(), TCAItems.CHIME.weathering().oxidized());
		insertAfterInFunctionalBlocks(TCAItems.CHIME.weathering().oxidized(), TCAItems.CHIME.waxed().unaffected());
		insertAfterInFunctionalBlocks(TCAItems.CHIME.waxed().unaffected(), TCAItems.CHIME.waxed().exposed());
		insertAfterInFunctionalBlocks(TCAItems.CHIME.waxed().exposed(), TCAItems.CHIME.waxed().weathered());
		insertAfterInFunctionalBlocks(TCAItems.CHIME.waxed().weathered(), TCAItems.CHIME.waxed().oxidized());

		insertAfterInBuildingBlocks(TCAItems.COPPER_FAN.weathering().unaffected(), TCAItems.CHIME.weathering().unaffected());
		insertAfterInBuildingBlocks(TCAItems.COPPER_FAN.waxed().unaffected(), TCAItems.CHIME.waxed().unaffected());
		insertAfterInBuildingBlocks(TCAItems.COPPER_FAN.weathering().exposed(), TCAItems.CHIME.weathering().exposed());
		insertAfterInBuildingBlocks(TCAItems.COPPER_FAN.waxed().exposed(), TCAItems.CHIME.waxed().exposed());
		insertAfterInBuildingBlocks(TCAItems.COPPER_FAN.weathering().weathered(), TCAItems.CHIME.weathering().weathered());
		insertAfterInBuildingBlocks(TCAItems.COPPER_FAN.waxed().weathered(), TCAItems.CHIME.waxed().weathered());
		insertAfterInBuildingBlocks(TCAItems.COPPER_FAN.weathering().oxidized(), TCAItems.CHIME.weathering().oxidized());
		insertAfterInBuildingBlocks(TCAItems.COPPER_FAN.waxed().oxidized(), TCAItems.CHIME.waxed().oxidized());

		// CRATE
		insertAfterInFunctionalBlocks(Items.BARREL, TCAItems.CRATE);
		insertAfterInRedstoneBlocks(Items.BARREL, TCAItems.CRATE);

		// BUTTON
		insertAfterInRedstoneBlocks(Items.STONE_BUTTON, TCAItems.COPPER_BUTTON.waxed().unaffected());
		insertAfterInRedstoneBlocks(TCAItems.COPPER_BUTTON.waxed().unaffected(), TCAItems.COPPER_BUTTON.waxed().exposed());
		insertAfterInRedstoneBlocks(TCAItems.COPPER_BUTTON.waxed().exposed(), TCAItems.COPPER_BUTTON.waxed().weathered());
		insertAfterInRedstoneBlocks(TCAItems.COPPER_BUTTON.waxed().weathered(), TCAItems.COPPER_BUTTON.waxed().oxidized());

		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.weathering().unaffected(), TCAItems.COPPER_BUTTON.weathering().unaffected());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.waxed().unaffected(), TCAItems.COPPER_BUTTON.waxed().unaffected());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.weathering().exposed(), TCAItems.COPPER_BUTTON.weathering().exposed());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.waxed().exposed(), TCAItems.COPPER_BUTTON.waxed().exposed());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.weathering().weathered(), TCAItems.COPPER_BUTTON.weathering().weathered());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.waxed().weathered(), TCAItems.COPPER_BUTTON.waxed().weathered());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.weathering().oxidized(), TCAItems.COPPER_BUTTON.weathering().oxidized());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.waxed().oxidized(), TCAItems.COPPER_BUTTON.waxed().oxidized());

		// PRESSURE PLATE
		insertAfterInRedstoneBlocks(Items.HEAVY_WEIGHTED_PRESSURE_PLATE, TCAItems.WEIGHTED_PRESSURE_PLATE.waxed().unaffected());
		insertAfterInRedstoneBlocks(TCAItems.WEIGHTED_PRESSURE_PLATE.waxed().unaffected(), TCAItems.WEIGHTED_PRESSURE_PLATE.waxed().exposed());
		insertAfterInRedstoneBlocks(TCAItems.WEIGHTED_PRESSURE_PLATE.waxed().exposed(), TCAItems.WEIGHTED_PRESSURE_PLATE.waxed().weathered());
		insertAfterInRedstoneBlocks(TCAItems.WEIGHTED_PRESSURE_PLATE.waxed().weathered(), TCAItems.WEIGHTED_PRESSURE_PLATE.waxed().oxidized());

		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.weathering().unaffected(), TCAItems.WEIGHTED_PRESSURE_PLATE.weathering().unaffected());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.waxed().unaffected(), TCAItems.WEIGHTED_PRESSURE_PLATE.waxed().unaffected());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.weathering().exposed(), TCAItems.WEIGHTED_PRESSURE_PLATE.weathering().exposed());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.waxed().exposed(), TCAItems.WEIGHTED_PRESSURE_PLATE.waxed().exposed());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.weathering().exposed(), TCAItems.WEIGHTED_PRESSURE_PLATE.weathering().weathered());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.waxed().weathered(), TCAItems.WEIGHTED_PRESSURE_PLATE.waxed().weathered());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.weathering().oxidized(), TCAItems.WEIGHTED_PRESSURE_PLATE.weathering().oxidized());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.waxed().oxidized(), TCAItems.WEIGHTED_PRESSURE_PLATE.waxed().oxidized());

		insertAfterInFunctionalBlocks(Items.FURNACE, TCAItems.KILN);

		// MINECARTS
		insertAfterInToolsAndUtilities(Items.CHEST_MINECART, TCAItems.CRATE_MINECART);
		insertAfterInToolsAndUtilities(Items.FURNACE_MINECART, TCAItems.JUKEBOX_MINECART);
		insertBeforeInToolsAndUtilities(Items.GOAT_HORN, TCAItems.MINECART_COUPLING);
		insertAfterInRedstoneBlocks(Items.CHEST_MINECART, TCAItems.CRATE_MINECART);
		insertAfterInRedstoneBlocks(Items.FURNACE_MINECART, TCAItems.JUKEBOX_MINECART);

		insertAfterInRedstoneBlocks(Items.ACTIVATOR_RAIL, TCAItems.COPPER_RAIL.waxed().unaffected());
		insertAfterInRedstoneBlocks(TCAItems.COPPER_RAIL.waxed().unaffected(), TCAItems.COPPER_RAIL.waxed().exposed());
		insertAfterInRedstoneBlocks(TCAItems.COPPER_RAIL.waxed().exposed(), TCAItems.COPPER_RAIL.waxed().weathered());
		insertAfterInRedstoneBlocks(TCAItems.COPPER_RAIL.waxed().weathered(), TCAItems.COPPER_RAIL.waxed().oxidized());
		insertAfterInRedstoneBlocks(TCAItems.COPPER_RAIL.waxed().oxidized(), TCAItems.CROSS_RAIL);
		insertAfterInRedstoneBlocks(TCAItems.CROSS_RAIL, TCAItems.RELAYOR_RAIL);

		insertAfterInToolsAndUtilities(Items.ACTIVATOR_RAIL, TCAItems.COPPER_RAIL.weathering().unaffected());
		insertAfterInToolsAndUtilities(TCAItems.COPPER_RAIL.weathering().unaffected(), TCAItems.COPPER_RAIL.weathering().exposed());
		insertAfterInToolsAndUtilities(TCAItems.COPPER_RAIL.weathering().exposed(), TCAItems.COPPER_RAIL.weathering().weathered());
		insertAfterInToolsAndUtilities(TCAItems.COPPER_RAIL.weathering().weathered(), TCAItems.COPPER_RAIL.weathering().oxidized());
		insertAfterInToolsAndUtilities(TCAItems.COPPER_RAIL.weathering().oxidized(), TCAItems.COPPER_RAIL.waxed().unaffected());
		insertAfterInToolsAndUtilities(TCAItems.COPPER_RAIL.waxed().unaffected(), TCAItems.COPPER_RAIL.waxed().exposed());
		insertAfterInToolsAndUtilities(TCAItems.COPPER_RAIL.waxed().exposed(), TCAItems.COPPER_RAIL.waxed().weathered());
		insertAfterInToolsAndUtilities(TCAItems.COPPER_RAIL.waxed().weathered(), TCAItems.COPPER_RAIL.waxed().oxidized());
		insertAfterInToolsAndUtilities(TCAItems.COPPER_RAIL.waxed().oxidized(), TCAItems.CROSS_RAIL);
		insertAfterInToolsAndUtilities(TCAItems.CROSS_RAIL, TCAItems.RELAYOR_RAIL);

		insertAfterInFunctionalBlocks(Items.SOUL_CAMPFIRE, TCAItems.COPPER_CAMPFIRE);
		insertAfterInFunctionalBlocks(Items.SOUL_LANTERN, TCAItems.CUPRIC_LANTERN);
		insertBeforeInToolsAndUtilities(Items.BRUSH, TCAItems.WRENCH);
		insertInstrumentBefore(Items.MUSIC_DISC_13, TCAItems.COPPER_HORN.get(), TCAInstrumentTags.COPPER_HORNS, CreativeModeTabs.TOOLS_AND_UTILITIES);
		insertAfterInNaturalBlocks(Blocks.JACK_O_LANTERN, TCAItems.COPPER_JACK_O_LANTERN);
		insertAfterInNaturalBlocks(TCAItems.COPPER_JACK_O_LANTERN, TCAItems.REDSTONE_JACK_O_LANTERN);
		insertAfterInRedstoneBlocks(Blocks.REDSTONE_TORCH, TCAItems.REDSTONE_JACK_O_LANTERN);
		insertAfterInRedstoneBlocks(Blocks.REDSTONE_BLOCK, TCAItems.REDSTONE_GRIT);
	}

	private static void insertBeforeInBuildingBlocks(ItemLike comparedItem, ItemLike item) {
		CreativeModeTabSorter.insertBefore(comparedItem, item, CreativeModeTabs.BUILDING_BLOCKS);
	}

	private static void insertAfterInBuildingBlocks(ItemLike comparedItem, ItemLike item) {
		CreativeModeTabSorter.insertAfter(comparedItem, item, CreativeModeTabs.BUILDING_BLOCKS);
	}

	private static void insertBeforeInColoredBlocks(ItemLike comparedItem, ItemLike item) {
		CreativeModeTabSorter.insertBefore(comparedItem, item, CreativeModeTabs.COLORED_BLOCKS);
	}

	private static void insertAfterInColoredBlocks(ItemLike comparedItem, ItemLike item) {
		CreativeModeTabSorter.insertAfter(comparedItem, item, CreativeModeTabs.COLORED_BLOCKS);
	}

	private static void insertBeforeInNaturalBlocks(ItemLike comparedItem, ItemLike item) {
		CreativeModeTabSorter.insertBefore(comparedItem, item, CreativeModeTabs.NATURAL_BLOCKS);
	}

	private static void insertAfterInNaturalBlocks(ItemLike comparedItem, ItemLike item) {
		CreativeModeTabSorter.insertAfter(comparedItem, item, CreativeModeTabs.NATURAL_BLOCKS);
	}

	private static void insertAfterInBuildingAndNaturalBlocks(ItemLike comparedItem, ItemLike item) {
		CreativeModeTabSorter.insertAfter(comparedItem, item, CreativeModeTabs.BUILDING_BLOCKS, CreativeModeTabs.NATURAL_BLOCKS);
	}

	private static void insertAfterInNaturalAndFunctionalBlocks(ItemLike comparedItem, ItemLike item) {
		CreativeModeTabSorter.insertAfter(comparedItem, item, CreativeModeTabs.NATURAL_BLOCKS, CreativeModeTabs.FUNCTIONAL_BLOCKS);
	}

	private static void insertBeforeInFunctionalBlocks(ItemLike comparedItem, ItemLike item) {
		CreativeModeTabSorter.insertBefore(comparedItem, item, CreativeModeTabs.FUNCTIONAL_BLOCKS);
	}

	private static void insertAfterInFunctionalBlocks(ItemLike comparedItem, ItemLike item) {
		CreativeModeTabSorter.insertAfter(comparedItem, item, CreativeModeTabs.FUNCTIONAL_BLOCKS);
	}

	private static void insertBeforeInRedstoneBlocks(ItemLike comparedItem, ItemLike item) {
		CreativeModeTabSorter.insertBefore(comparedItem, item, CreativeModeTabs.REDSTONE_BLOCKS);
	}

	private static void insertAfterInRedstoneBlocks(ItemLike comparedItem, ItemLike item) {
		CreativeModeTabSorter.insertAfter(comparedItem, item, CreativeModeTabs.REDSTONE_BLOCKS);
	}

	private static void insertInToolsAndUtilities(ItemLike item) {
		CreativeModeTabSorter.insert(item, CreativeModeTabs.TOOLS_AND_UTILITIES);
	}

	private static void insertAfterInToolsAndUtilities(ItemLike comparedItem, ItemLike item) {
		CreativeModeTabSorter.insertAfter(comparedItem, item, CreativeModeTabs.TOOLS_AND_UTILITIES);
	}

	private static void insertBeforeInToolsAndUtilities(ItemLike comparedItem, ItemLike item) {
		CreativeModeTabSorter.insertBefore(comparedItem, item, CreativeModeTabs.TOOLS_AND_UTILITIES);
	}

	private static void insertBeforeInIngredients(ItemLike comparedItem, ItemLike item) {
		CreativeModeTabSorter.insertBefore(comparedItem, item, CreativeModeTabs.INGREDIENTS);
	}

	private static void insertAfterInIngredients(ItemLike comparedItem, ItemLike item) {
		CreativeModeTabSorter.insertAfter(comparedItem, item, CreativeModeTabs.INGREDIENTS);
	}

	private static void insertBeforeInFoodAndDrinks(ItemLike comparedItem, ItemLike item) {
		CreativeModeTabSorter.insertBefore(comparedItem, item, CreativeModeTabs.FOOD_AND_DRINKS);
	}

	private static void insertAfterInFoodAndDrinks(ItemLike comparedItem, ItemLike item) {
		CreativeModeTabSorter.insertAfter(comparedItem, item, CreativeModeTabs.FOOD_AND_DRINKS);
	}

	private static void insertAfterInCombat(ItemLike comparedItem, ItemLike item) {
		CreativeModeTabSorter.insertAfter(comparedItem, item, CreativeModeTabs.COMBAT);
	}

	private static void insertBeforeInSpawnEggs(ItemLike comparedItem, ItemLike item) {
		CreativeModeTabSorter.insertBefore(comparedItem, item, CreativeModeTabs.SPAWN_EGGS);
	}

	private static void insertAfterInSpawnEggs(ItemLike comparedItem, ItemLike item) {
		CreativeModeTabSorter.insertAfter(comparedItem, item, CreativeModeTabs.SPAWN_EGGS);
	}

	@SafeVarargs
	private static void insertInstrumentBefore(
		Item comparedItem,
		Item instrument,
		TagKey<Instrument> tagKey,
		ResourceKey<CreativeModeTab>... tabs
	) {
		CreativeModeTabSorter.addInstrumentBefore(comparedItem, instrument, tagKey, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, tabs);
	}

	private TCACreativeInventorySorting() {}
}
