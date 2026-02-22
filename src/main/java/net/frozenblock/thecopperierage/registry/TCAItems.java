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

import java.util.function.Function;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.item.CopperHornItem;
import net.frozenblock.thecopperierage.item.WrenchItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.component.InstrumentComponent;
import org.jetbrains.annotations.NotNull;

public final class TCAItems {
	public static final WrenchItem WRENCH = register("wrench",
		WrenchItem::new,
		new Item.Properties()
			.stacksTo(1)
			.durability(128)
	);

	public static final CopperHornItem COPPER_HORN = register("copper_horn",
		CopperHornItem::new,
		new Item.Properties()
			.stacksTo(1)
			.component(DataComponents.INSTRUMENT, new InstrumentComponent(TCAInstruments.SAX_COPPER_HORN))
	);

	public static final MinecartItem CRATE_MINECART = register("crate_minecart",
		properties -> new MinecartItem(TCAEntityTypes.CRATE_MINECART, properties),
		new Item.Properties().stacksTo(1)
	);

	public static final MinecartItem COPPER_GOLEM_STATUE_MINECART = register("copper_golem_statue_minecart",
		properties -> new MinecartItem(TCAEntityTypes.COPPER_GOLEM_STATUE_MINECART, properties),
		new Item.Properties().stacksTo(1)
	);

	public static final MinecartItem DISPENSER_MINECART = register("dispenser_minecart",
		properties -> new MinecartItem(TCAEntityTypes.DISPENSER_MINECART, properties),
		new Item.Properties().stacksTo(1)
	);

	public static final MinecartItem DROPPER_MINECART = register("dropper_minecart",
		properties -> new MinecartItem(TCAEntityTypes.DROPPER_MINECART, properties),
		new Item.Properties().stacksTo(1)
	);

	private TCAItems() {
		throw new UnsupportedOperationException("WWItems contains only static declarations.");
	}

	public static void init() {
	}

	private static @NotNull <T extends Item> T register(String name, @NotNull Function<Item.Properties, Item> function, Item.@NotNull Properties properties) {
		return (T) Items.registerItem(ResourceKey.create(Registries.ITEM, TCAConstants.id(name)), function, properties);
	}
}
