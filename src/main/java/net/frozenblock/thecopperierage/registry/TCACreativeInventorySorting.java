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
import org.jetbrains.annotations.NotNull;

public final class TCACreativeInventorySorting {

	public static void init() {
		// GEARBOX
		addBeforeInRedstoneBlocks(Items.REPEATER, TCABlocks.GEARBOX.waxed());

		addAfterInBuildingBlocks(Items.COPPER_BULB, TCABlocks.GEARBOX.unaffected());
		addAfterInBuildingBlocks(Items.WAXED_COPPER_BULB, TCABlocks.GEARBOX.waxed());
		addAfterInBuildingBlocks(Items.EXPOSED_COPPER_BULB, TCABlocks.GEARBOX.exposed());
		addAfterInBuildingBlocks(Items.WAXED_EXPOSED_COPPER_BULB, TCABlocks.GEARBOX.waxedExposed());
		addAfterInBuildingBlocks(Items.WEATHERED_COPPER_BULB, TCABlocks.GEARBOX.weathered());
		addAfterInBuildingBlocks(Items.WAXED_WEATHERED_COPPER_BULB, TCABlocks.GEARBOX.waxedWeathered());
		addAfterInBuildingBlocks(Items.OXIDIZED_COPPER_BULB, TCABlocks.GEARBOX.oxidized());
		addAfterInBuildingBlocks(Items.WAXED_OXIDIZED_COPPER_BULB, TCABlocks.GEARBOX.waxedOxidized());

		// FAN
		addBeforeInRedstoneBlocks(Items.RAIL, TCABlocks.COPPER_FAN.waxed());

		addAfterInBuildingBlocks(TCABlocks.GEARBOX.unaffected(), TCABlocks.COPPER_FAN.unaffected());
		addAfterInBuildingBlocks(TCABlocks.GEARBOX.waxed(), TCABlocks.COPPER_FAN.waxed());
		addAfterInBuildingBlocks(TCABlocks.GEARBOX.exposed(), TCABlocks.COPPER_FAN.exposed());
		addAfterInBuildingBlocks(TCABlocks.GEARBOX.waxedExposed(), TCABlocks.COPPER_FAN.waxedExposed());
		addAfterInBuildingBlocks(TCABlocks.GEARBOX.weathered(), TCABlocks.COPPER_FAN.weathered());
		addAfterInBuildingBlocks(TCABlocks.GEARBOX.waxedWeathered(), TCABlocks.COPPER_FAN.waxedWeathered());
		addAfterInBuildingBlocks(TCABlocks.GEARBOX.oxidized(), TCABlocks.COPPER_FAN.oxidized());
		addAfterInBuildingBlocks(TCABlocks.GEARBOX.waxedOxidized(), TCABlocks.COPPER_FAN.waxedOxidized());

		// CHIME
		addAfterInFunctionalBlocks(Items.WAXED_OXIDIZED_LIGHTNING_ROD, TCABlocks.CHIME.unaffected());
		addAfterInFunctionalBlocks(TCABlocks.CHIME.unaffected(), TCABlocks.CHIME.exposed());
		addAfterInFunctionalBlocks(TCABlocks.CHIME.exposed(), TCABlocks.CHIME.weathered());
		addAfterInFunctionalBlocks(TCABlocks.CHIME.weathered(), TCABlocks.CHIME.oxidized());
		addAfterInFunctionalBlocks(TCABlocks.CHIME.oxidized(), TCABlocks.CHIME.waxed());
		addAfterInFunctionalBlocks(TCABlocks.CHIME.waxed(), TCABlocks.CHIME.waxedExposed());
		addAfterInFunctionalBlocks(TCABlocks.CHIME.waxedExposed(), TCABlocks.CHIME.waxedWeathered());
		addAfterInFunctionalBlocks(TCABlocks.CHIME.waxedWeathered(), TCABlocks.CHIME.waxedOxidized());

		addAfterInBuildingBlocks(TCABlocks.COPPER_FAN.unaffected(), TCABlocks.CHIME.unaffected());
		addAfterInBuildingBlocks(TCABlocks.COPPER_FAN.waxed(), TCABlocks.CHIME.waxed());
		addAfterInBuildingBlocks(TCABlocks.COPPER_FAN.exposed(), TCABlocks.CHIME.exposed());
		addAfterInBuildingBlocks(TCABlocks.COPPER_FAN.waxedExposed(), TCABlocks.CHIME.waxedExposed());
		addAfterInBuildingBlocks(TCABlocks.COPPER_FAN.weathered(), TCABlocks.CHIME.weathered());
		addAfterInBuildingBlocks(TCABlocks.COPPER_FAN.waxedWeathered(), TCABlocks.CHIME.waxedWeathered());
		addAfterInBuildingBlocks(TCABlocks.COPPER_FAN.oxidized(), TCABlocks.CHIME.oxidized());
		addAfterInBuildingBlocks(TCABlocks.COPPER_FAN.waxedOxidized(), TCABlocks.CHIME.waxedOxidized());

		// BUTTON
		addAfterInRedstoneBlocks(Items.STONE_BUTTON, TCABlocks.COPPER_BUTTON.waxed());
		addAfterInRedstoneBlocks(TCABlocks.COPPER_BUTTON.waxed(), TCABlocks.COPPER_BUTTON.waxedExposed());
		addAfterInRedstoneBlocks(TCABlocks.COPPER_BUTTON.waxedExposed(), TCABlocks.COPPER_BUTTON.waxedWeathered());
		addAfterInRedstoneBlocks(TCABlocks.COPPER_BUTTON.waxedWeathered(), TCABlocks.COPPER_BUTTON.waxedOxidized());

		addAfterInBuildingBlocks(Items.COPPER_TRAPDOOR, TCABlocks.COPPER_BUTTON.unaffected());
		addAfterInBuildingBlocks(Items.WAXED_COPPER_TRAPDOOR, TCABlocks.COPPER_BUTTON.waxed());
		addAfterInBuildingBlocks(Items.EXPOSED_COPPER_TRAPDOOR, TCABlocks.COPPER_BUTTON.exposed());
		addAfterInBuildingBlocks(Items.WAXED_EXPOSED_COPPER_TRAPDOOR, TCABlocks.COPPER_BUTTON.waxedExposed());
		addAfterInBuildingBlocks(Items.WEATHERED_COPPER_TRAPDOOR, TCABlocks.COPPER_BUTTON.weathered());
		addAfterInBuildingBlocks(Items.WAXED_WEATHERED_COPPER_TRAPDOOR, TCABlocks.COPPER_BUTTON.waxedWeathered());
		addAfterInBuildingBlocks(Items.OXIDIZED_COPPER_TRAPDOOR, TCABlocks.COPPER_BUTTON.oxidized());
		addAfterInBuildingBlocks(Items.WAXED_OXIDIZED_COPPER_TRAPDOOR, TCABlocks.COPPER_BUTTON.waxedOxidized());

		// PRESSURE PLATE
		addAfterInRedstoneBlocks(Items.HEAVY_WEIGHTED_PRESSURE_PLATE, TCABlocks.WEIGHTED_PRESSURE_PLATE.waxed());
		addAfterInRedstoneBlocks(TCABlocks.WEIGHTED_PRESSURE_PLATE.waxed(), TCABlocks.WEIGHTED_PRESSURE_PLATE.waxedExposed());
		addAfterInRedstoneBlocks(TCABlocks.WEIGHTED_PRESSURE_PLATE.waxedExposed(), TCABlocks.WEIGHTED_PRESSURE_PLATE.waxedWeathered());
		addAfterInRedstoneBlocks(TCABlocks.WEIGHTED_PRESSURE_PLATE.waxedWeathered(), TCABlocks.WEIGHTED_PRESSURE_PLATE.waxedOxidized());

		addAfterInBuildingBlocks(Items.COPPER_TRAPDOOR, TCABlocks.WEIGHTED_PRESSURE_PLATE.unaffected());
		addAfterInBuildingBlocks(Items.WAXED_COPPER_TRAPDOOR, TCABlocks.WEIGHTED_PRESSURE_PLATE.waxed());
		addAfterInBuildingBlocks(Items.EXPOSED_COPPER_TRAPDOOR, TCABlocks.WEIGHTED_PRESSURE_PLATE.exposed());
		addAfterInBuildingBlocks(Items.WAXED_EXPOSED_COPPER_TRAPDOOR, TCABlocks.WEIGHTED_PRESSURE_PLATE.waxedExposed());
		addAfterInBuildingBlocks(Items.WEATHERED_COPPER_TRAPDOOR, TCABlocks.WEIGHTED_PRESSURE_PLATE.weathered());
		addAfterInBuildingBlocks(Items.WAXED_WEATHERED_COPPER_TRAPDOOR, TCABlocks.WEIGHTED_PRESSURE_PLATE.waxedWeathered());
		addAfterInBuildingBlocks(Items.OXIDIZED_COPPER_TRAPDOOR, TCABlocks.WEIGHTED_PRESSURE_PLATE.oxidized());
		addAfterInBuildingBlocks(Items.WAXED_OXIDIZED_COPPER_TRAPDOOR, TCABlocks.WEIGHTED_PRESSURE_PLATE.waxedOxidized());

		addAfterInFunctionalBlocks(Items.SOUL_CAMPFIRE, TCABlocks.COPPER_CAMPFIRE);
		addBeforeInToolsAndUtilities(Items.BRUSH, TCAItems.WRENCH);
		addInstrumentBefore(Items.MUSIC_DISC_13, TCAItems.COPPER_HORN, TCAInstrumentTags.COPPER_HORNS, CreativeModeTabs.TOOLS_AND_UTILITIES);
		addAfterInNaturalBlocks(Blocks.JACK_O_LANTERN, TCABlocks.COPPER_JACK_O_LANTERN);
		addAfterInNaturalBlocks(TCABlocks.COPPER_JACK_O_LANTERN, TCABlocks.REDSTONE_JACK_O_LANTERN);
		addAfterInRedstoneBlocks(Blocks.REDSTONE_TORCH, TCABlocks.REDSTONE_JACK_O_LANTERN);
	}

	private static void addBeforeInBuildingBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.addBefore(comparedItem, item, CreativeModeTabs.BUILDING_BLOCKS);
	}

	private static void addAfterInBuildingBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.addAfter(comparedItem, item, CreativeModeTabs.BUILDING_BLOCKS);
	}

	private static void addBeforeInColoredBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.addBefore(comparedItem, item, CreativeModeTabs.COLORED_BLOCKS);
	}

	private static void addAfterInColoredBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.addAfter(comparedItem, item, CreativeModeTabs.COLORED_BLOCKS);
	}


	private static void addBeforeInNaturalBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.addBefore(comparedItem, item, CreativeModeTabs.NATURAL_BLOCKS);
	}

	private static void addAfterInNaturalBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.addAfter(comparedItem, item, CreativeModeTabs.NATURAL_BLOCKS);
	}

	private static void addAfterInBuildingAndNaturalBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.addAfter(comparedItem, item, CreativeModeTabs.BUILDING_BLOCKS, CreativeModeTabs.NATURAL_BLOCKS);
	}

	private static void addAfterInNaturalAndFunctionalBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.addAfter(comparedItem, item, CreativeModeTabs.NATURAL_BLOCKS, CreativeModeTabs.FUNCTIONAL_BLOCKS);
	}

	private static void addBeforeInFunctionalBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.addBefore(comparedItem, item, CreativeModeTabs.FUNCTIONAL_BLOCKS);
	}

	private static void addAfterInFunctionalBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.addAfter(comparedItem, item, CreativeModeTabs.FUNCTIONAL_BLOCKS);
	}

	private static void addBeforeInRedstoneBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.addBefore(comparedItem, item, CreativeModeTabs.REDSTONE_BLOCKS);
	}

	private static void addAfterInRedstoneBlocks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.addAfter(comparedItem, item, CreativeModeTabs.REDSTONE_BLOCKS);
	}

	private static void addInToolsAndUtilities(ItemLike item) {
		FrozenCreativeTabs.add(item, CreativeModeTabs.TOOLS_AND_UTILITIES);
	}

	private static void addAfterInToolsAndUtilities(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.addAfter(comparedItem, item, CreativeModeTabs.TOOLS_AND_UTILITIES);
	}

	private static void addBeforeInToolsAndUtilities(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.addBefore(comparedItem, item, CreativeModeTabs.TOOLS_AND_UTILITIES);
	}

	private static void addBeforeInIngredients(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.addBefore(comparedItem, item, CreativeModeTabs.INGREDIENTS);
	}

	private static void addAfterInIngredients(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.addAfter(comparedItem, item, CreativeModeTabs.INGREDIENTS);
	}

	private static void addBeforeInFoodAndDrinks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.addBefore(comparedItem, item, CreativeModeTabs.FOOD_AND_DRINKS);
	}

	private static void addAfterInFoodAndDrinks(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.addAfter(comparedItem, item, CreativeModeTabs.FOOD_AND_DRINKS);
	}

	private static void addAfterInCombat(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.addAfter(comparedItem, item, CreativeModeTabs.COMBAT);
	}

	private static void addBeforeInSpawnEggs(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.addBefore(comparedItem, item, CreativeModeTabs.SPAWN_EGGS);
	}

	private static void addAfterInSpawnEggs(ItemLike comparedItem, ItemLike item) {
		FrozenCreativeTabs.addAfter(comparedItem, item, CreativeModeTabs.SPAWN_EGGS);
	}

	@SafeVarargs
	private static void addInstrumentBefore(
		@NotNull Item comparedItem,
		@NotNull Item instrument,
		@NotNull TagKey<Instrument> tagKey,
		@NotNull ResourceKey<CreativeModeTab>... tabs
	) {
		FrozenCreativeTabs.addInstrumentBefore(comparedItem, instrument, tagKey, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, tabs);
	}
}
