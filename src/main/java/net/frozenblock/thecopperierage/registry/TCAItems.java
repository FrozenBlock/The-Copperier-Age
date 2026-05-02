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

import net.frozenblock.thecopperierage.item.CopperHornItem;
import net.frozenblock.thecopperierage.item.WrenchItem;
import net.frozenblock.thecopperierage.references.TCABlockItemIds;
import net.frozenblock.thecopperierage.references.TCAItemIds;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.InstrumentComponent;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.WeatheringCopperCollection;

public final class TCAItems {
	// BLOCK ITEMS
	public static final Item COPPER_CAMPFIRE = Items.registerBlock(TCABlockItemIds.COPPER_CAMPFIRE, TCABlocks.COPPER_CAMPFIRE);
	public static final Item COPPER_JACK_O_LANTERN = Items.registerBlock(TCABlockItemIds.COPPER_JACK_O_LANTERN, TCABlocks.COPPER_JACK_O_LANTERN);
	public static final Item REDSTONE_JACK_O_LANTERN = Items.registerBlock(TCABlockItemIds.REDSTONE_JACK_O_LANTERN, TCABlocks.REDSTONE_JACK_O_LANTERN);
	public static final Item REDSTONE_GRIT = Items.registerBlock(TCABlockItemIds.REDSTONE_GRIT, TCABlocks.REDSTONE_GRIT);

	public static final WeatheringCopperCollection<Item> GEARBOX = WeatheringCopperCollection.registerItems(
		TCABlockItemIds.GEARBOX, TCABlocks.GEARBOX, Items::registerBlock
	);
	public static final WeatheringCopperCollection<Item> STICKY_GEARBOX = WeatheringCopperCollection.registerItems(
		TCABlockItemIds.STICKY_GEARBOX, TCABlocks.STICKY_GEARBOX, Items::registerBlock
	);
	public static final WeatheringCopperCollection<Item> COPPER_FAN = WeatheringCopperCollection.registerItems(
		TCABlockItemIds.COPPER_FAN, TCABlocks.COPPER_FAN, Items::registerBlock
	);
	public static final WeatheringCopperCollection<Item> CHIME = WeatheringCopperCollection.registerItems(
		TCABlockItemIds.CHIME, TCABlocks.CHIME, Items::registerBlock
	);

	public static final Item CRATE = Items.registerBlock(TCABlockItemIds.CRATE, TCABlocks.CRATE,
		new Item.Properties().component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
	);

	public static final WeatheringCopperCollection<Item> COPPER_BUTTON = WeatheringCopperCollection.registerItems(
		TCABlockItemIds.COPPER_BUTTON, TCABlocks.COPPER_BUTTON, Items::registerBlock
	);
	public static final WeatheringCopperCollection<Item> WEIGHTED_PRESSURE_PLATE = WeatheringCopperCollection.registerItems(
		TCABlockItemIds.WEIGHTED_PRESSURE_PLATE, TCABlocks.WEIGHTED_PRESSURE_PLATE, Items::registerBlock
	);

	// ITEMS
	public static final Item WRENCH = Items.registerItem(TCAItemIds.WRENCH,
		WrenchItem::new,
		new Item.Properties()
			.stacksTo(1)
			.durability(128)
	);

	public static final Item COPPER_HORN = Items.registerItem(TCAItemIds.COPPER_HORN,
		CopperHornItem::new,
		new Item.Properties()
			.stacksTo(1)
			.delayedComponent(DataComponents.INSTRUMENT, context -> new InstrumentComponent(context.getOrThrow(TCAInstruments.SAX_COPPER_HORN)))
	);

	private TCAItems() {
		throw new UnsupportedOperationException("TCAItems contains only static declarations.");
	}

	public static void init() {
	}
}
