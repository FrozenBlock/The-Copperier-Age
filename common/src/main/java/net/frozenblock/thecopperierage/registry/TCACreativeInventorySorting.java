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
import net.frozenblock.lib.platform.ModLoader;
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
		// TODO FIX NEOFORGE
		if (ModLoader.isNeoForge()) return;
		// GEARBOX
		insertAfterInBuildingBlocks(Items.REPEATER, TCABlocks.GEARBOX.waxed().unaffected());
		insertAfterInBuildingBlocks(TCABlocks.GEARBOX.waxed().unaffected(), TCABlocks.STICKY_GEARBOX.waxed().unaffected());

		insertAfterInBuildingBlocks(Items.COPPER_BULB.weathering().unaffected(), TCABlocks.GEARBOX.weathering().unaffected());
		insertAfterInBuildingBlocks(TCABlocks.GEARBOX.weathering().unaffected(), TCABlocks.STICKY_GEARBOX.weathering().unaffected());
		insertAfterInBuildingBlocks(Items.COPPER_BULB.waxed().unaffected(), TCABlocks.GEARBOX.waxed().unaffected());
		insertAfterInBuildingBlocks(TCABlocks.GEARBOX.waxed().unaffected(), TCABlocks.STICKY_GEARBOX.waxed().unaffected());
		insertAfterInBuildingBlocks(Items.COPPER_BULB.weathering().exposed(), TCABlocks.GEARBOX.weathering().exposed());
		insertAfterInBuildingBlocks(TCABlocks.GEARBOX.weathering().exposed(), TCABlocks.STICKY_GEARBOX.weathering().exposed());
		insertAfterInBuildingBlocks(Items.COPPER_BULB.waxed().exposed(), TCABlocks.GEARBOX.waxed().exposed());
		insertAfterInBuildingBlocks(TCABlocks.GEARBOX.waxed().exposed(), TCABlocks.STICKY_GEARBOX.waxed().exposed());
		insertAfterInBuildingBlocks(Items.COPPER_BULB.weathering().weathered(), TCABlocks.GEARBOX.weathering().weathered());
		insertAfterInBuildingBlocks(TCABlocks.GEARBOX.weathering().weathered(), TCABlocks.STICKY_GEARBOX.weathering().weathered());
		insertAfterInBuildingBlocks(Items.COPPER_BULB.waxed().weathered(), TCABlocks.GEARBOX.waxed().weathered());
		insertAfterInBuildingBlocks(TCABlocks.GEARBOX.waxed().weathered(), TCABlocks.STICKY_GEARBOX.waxed().weathered());
		insertAfterInBuildingBlocks(Items.COPPER_BULB.weathering().oxidized(), TCABlocks.GEARBOX.weathering().oxidized());
		insertAfterInBuildingBlocks(TCABlocks.GEARBOX.weathering().oxidized(), TCABlocks.STICKY_GEARBOX.weathering().oxidized());
		insertAfterInBuildingBlocks(Items.COPPER_BULB.waxed().oxidized(), TCABlocks.GEARBOX.waxed().oxidized());
		insertAfterInBuildingBlocks(TCABlocks.GEARBOX.waxed().oxidized(), TCABlocks.STICKY_GEARBOX.waxed().oxidized());

		// FAN
		insertBeforeInRedstoneBlocks(Items.RAIL, TCABlocks.COPPER_FAN.waxed().unaffected());

		insertAfterInBuildingBlocks(TCABlocks.STICKY_GEARBOX.weathering().unaffected(), TCABlocks.COPPER_FAN.weathering().unaffected());
		insertAfterInBuildingBlocks(TCABlocks.STICKY_GEARBOX.waxed().unaffected(), TCABlocks.COPPER_FAN.waxed().unaffected());
		insertAfterInBuildingBlocks(TCABlocks.STICKY_GEARBOX.weathering().exposed(), TCABlocks.COPPER_FAN.weathering().exposed());
		insertAfterInBuildingBlocks(TCABlocks.STICKY_GEARBOX.waxed().exposed(), TCABlocks.COPPER_FAN.waxed().exposed());
		insertAfterInBuildingBlocks(TCABlocks.STICKY_GEARBOX.weathering().weathered(), TCABlocks.COPPER_FAN.weathering().weathered());
		insertAfterInBuildingBlocks(TCABlocks.STICKY_GEARBOX.waxed().weathered(), TCABlocks.COPPER_FAN.waxed().weathered());
		insertAfterInBuildingBlocks(TCABlocks.STICKY_GEARBOX.weathering().oxidized(), TCABlocks.COPPER_FAN.weathering().oxidized());
		insertAfterInBuildingBlocks(TCABlocks.STICKY_GEARBOX.waxed().oxidized(), TCABlocks.COPPER_FAN.waxed().oxidized());

		// CHIME
		insertAfterInFunctionalBlocks(Items.LIGHTNING_ROD.weathering().unaffected(), TCABlocks.CHIME.weathering().unaffected());
		insertAfterInFunctionalBlocks(TCABlocks.CHIME.weathering().unaffected(), TCABlocks.CHIME.weathering().exposed());
		insertAfterInFunctionalBlocks(TCABlocks.CHIME.weathering().exposed(), TCABlocks.CHIME.weathering().weathered());
		insertAfterInFunctionalBlocks(TCABlocks.CHIME.weathering().weathered(), TCABlocks.CHIME.weathering().oxidized());
		insertAfterInFunctionalBlocks(TCABlocks.CHIME.weathering().oxidized(), TCABlocks.CHIME.waxed().unaffected());
		insertAfterInFunctionalBlocks(TCABlocks.CHIME.waxed().unaffected(), TCABlocks.CHIME.waxed().exposed());
		insertAfterInFunctionalBlocks(TCABlocks.CHIME.waxed().exposed(), TCABlocks.CHIME.waxed().weathered());
		insertAfterInFunctionalBlocks(TCABlocks.CHIME.waxed().weathered(), TCABlocks.CHIME.waxed().oxidized());

		insertAfterInBuildingBlocks(TCABlocks.COPPER_FAN.weathering().unaffected(), TCABlocks.CHIME.weathering().unaffected());
		insertAfterInBuildingBlocks(TCABlocks.COPPER_FAN.waxed().unaffected(), TCABlocks.CHIME.waxed().unaffected());
		insertAfterInBuildingBlocks(TCABlocks.COPPER_FAN.weathering().exposed(), TCABlocks.CHIME.weathering().exposed());
		insertAfterInBuildingBlocks(TCABlocks.COPPER_FAN.waxed().exposed(), TCABlocks.CHIME.waxed().exposed());
		insertAfterInBuildingBlocks(TCABlocks.COPPER_FAN.weathering().weathered(), TCABlocks.CHIME.weathering().weathered());
		insertAfterInBuildingBlocks(TCABlocks.COPPER_FAN.waxed().weathered(), TCABlocks.CHIME.waxed().weathered());
		insertAfterInBuildingBlocks(TCABlocks.COPPER_FAN.weathering().oxidized(), TCABlocks.CHIME.weathering().oxidized());
		insertAfterInBuildingBlocks(TCABlocks.COPPER_FAN.waxed().oxidized(), TCABlocks.CHIME.waxed().oxidized());

		// COPPER CRATE
		insertAfterInBuildingBlocks(Items.BARREL, TCABlocks.CRATE);

		insertAfterInRedstoneBlocks(Items.BARREL, TCABlocks.CRATE);

		// BUTTON
		insertAfterInRedstoneBlocks(Items.STONE_BUTTON, TCABlocks.COPPER_BUTTON.waxed().unaffected());
		insertAfterInRedstoneBlocks(TCABlocks.COPPER_BUTTON.waxed().unaffected(), TCABlocks.COPPER_BUTTON.waxed().exposed());
		insertAfterInRedstoneBlocks(TCABlocks.COPPER_BUTTON.waxed().exposed(), TCABlocks.COPPER_BUTTON.waxed().weathered());
		insertAfterInRedstoneBlocks(TCABlocks.COPPER_BUTTON.waxed().weathered(), TCABlocks.COPPER_BUTTON.waxed().oxidized());

		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.weathering().unaffected(), TCABlocks.COPPER_BUTTON.weathering().unaffected());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.waxed().unaffected(), TCABlocks.COPPER_BUTTON.waxed().unaffected());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.weathering().exposed(), TCABlocks.COPPER_BUTTON.weathering().exposed());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.waxed().exposed(), TCABlocks.COPPER_BUTTON.waxed().exposed());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.weathering().weathered(), TCABlocks.COPPER_BUTTON.weathering().weathered());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.waxed().weathered(), TCABlocks.COPPER_BUTTON.waxed().weathered());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.weathering().oxidized(), TCABlocks.COPPER_BUTTON.weathering().oxidized());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.waxed().oxidized(), TCABlocks.COPPER_BUTTON.waxed().oxidized());

		// PRESSURE PLATE
		insertAfterInRedstoneBlocks(Items.HEAVY_WEIGHTED_PRESSURE_PLATE, TCABlocks.WEIGHTED_PRESSURE_PLATE.waxed().unaffected());
		insertAfterInRedstoneBlocks(TCABlocks.WEIGHTED_PRESSURE_PLATE.waxed().unaffected(), TCABlocks.WEIGHTED_PRESSURE_PLATE.waxed().exposed());
		insertAfterInRedstoneBlocks(TCABlocks.WEIGHTED_PRESSURE_PLATE.waxed().exposed(), TCABlocks.WEIGHTED_PRESSURE_PLATE.waxed().weathered());
		insertAfterInRedstoneBlocks(TCABlocks.WEIGHTED_PRESSURE_PLATE.waxed().weathered(), TCABlocks.WEIGHTED_PRESSURE_PLATE.waxed().oxidized());

		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.weathering().unaffected(), TCABlocks.WEIGHTED_PRESSURE_PLATE.weathering().unaffected());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.waxed().unaffected(), TCABlocks.WEIGHTED_PRESSURE_PLATE.waxed().unaffected());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.weathering().exposed(), TCABlocks.WEIGHTED_PRESSURE_PLATE.weathering().exposed());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.waxed().exposed(), TCABlocks.WEIGHTED_PRESSURE_PLATE.waxed().exposed());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.weathering().exposed(), TCABlocks.WEIGHTED_PRESSURE_PLATE.weathering().weathered());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.waxed().weathered(), TCABlocks.WEIGHTED_PRESSURE_PLATE.waxed().weathered());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.weathering().oxidized(), TCABlocks.WEIGHTED_PRESSURE_PLATE.weathering().oxidized());
		insertAfterInBuildingBlocks(Items.COPPER_TRAPDOOR.waxed().oxidized(), TCABlocks.WEIGHTED_PRESSURE_PLATE.waxed().oxidized());

		insertAfterInFunctionalBlocks(Items.FURNACE, TCABlocks.KILN);

		// MINECARTS
		insertAfterInToolsAndUtilities(Items.CHEST_MINECART, TCAItems.CRATE_MINECART);
		insertAfterInToolsAndUtilities(Items.FURNACE_MINECART, TCAItems.JUKEBOX_MINECART);
		insertBeforeInToolsAndUtilities(Items.GOAT_HORN, TCAItems.MINECART_COUPLING);
		insertAfterInRedstoneBlocks(Items.CHEST_MINECART, TCAItems.CRATE_MINECART);
		insertAfterInRedstoneBlocks(Items.FURNACE_MINECART, TCAItems.JUKEBOX_MINECART);

		insertAfterInRedstoneBlocks(Items.ACTIVATOR_RAIL, TCABlocks.COPPER_RAIL.weathering().unaffected());
		insertAfterInRedstoneBlocks(TCABlocks.COPPER_RAIL.weathering().unaffected(), TCABlocks.COPPER_RAIL.weathering().exposed());
		insertAfterInRedstoneBlocks(TCABlocks.COPPER_RAIL.weathering().exposed(), TCABlocks.COPPER_RAIL.weathering().weathered());
		insertAfterInRedstoneBlocks(TCABlocks.COPPER_RAIL.weathering().weathered(), TCABlocks.COPPER_RAIL.weathering().oxidized());
		insertAfterInRedstoneBlocks(TCABlocks.COPPER_RAIL.weathering().oxidized(), TCABlocks.COPPER_RAIL.waxed().unaffected());
		insertAfterInRedstoneBlocks(TCABlocks.COPPER_RAIL.waxed().unaffected(), TCABlocks.COPPER_RAIL.waxed().exposed());
		insertAfterInRedstoneBlocks(TCABlocks.COPPER_RAIL.waxed().exposed(), TCABlocks.COPPER_RAIL.waxed().weathered());
		insertAfterInRedstoneBlocks(TCABlocks.COPPER_RAIL.waxed().weathered(), TCABlocks.COPPER_RAIL.waxed().oxidized());
		insertAfterInRedstoneBlocks(TCABlocks.COPPER_RAIL.waxed().oxidized(), TCABlocks.CROSS_RAIL);
		insertAfterInRedstoneBlocks(TCABlocks.CROSS_RAIL, TCABlocks.RELAYOR_RAIL);

		insertAfterInFunctionalBlocks(Items.SOUL_CAMPFIRE, TCABlocks.COPPER_CAMPFIRE);
		insertAfterInFunctionalBlocks(Items.SOUL_LANTERN, TCABlocks.CUPRIC_LANTERN);
		insertBeforeInToolsAndUtilities(Items.BRUSH, TCAItems.WRENCH);
		insertInstrumentBefore(Items.MUSIC_DISC_13, TCAItems.COPPER_HORN.get(), TCAInstrumentTags.COPPER_HORNS, CreativeModeTabs.TOOLS_AND_UTILITIES);
		insertAfterInNaturalBlocks(Blocks.JACK_O_LANTERN, TCABlocks.COPPER_JACK_O_LANTERN);
		insertAfterInNaturalBlocks(TCABlocks.COPPER_JACK_O_LANTERN, TCABlocks.REDSTONE_JACK_O_LANTERN);
		insertAfterInRedstoneBlocks(Blocks.REDSTONE_TORCH, TCABlocks.REDSTONE_JACK_O_LANTERN);
		insertAfterInRedstoneBlocks(Blocks.REDSTONE_BLOCK, TCABlocks.REDSTONE_GRIT);
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
}
