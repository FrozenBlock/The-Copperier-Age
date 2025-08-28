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

package net.frozenblock.thecopperierage.registry;

import java.util.function.Function;
import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public final class TCAItems {

	// ITEMS
	public static final Item TEST = register("test", Item::new, new Properties());

	private TCAItems() {
		throw new UnsupportedOperationException("TCAItems contains only static declarations.");
	}

	public static void registerItems() {
		TCAConstants.logWithModId("Registering Items for", TCAConstants.UNSTABLE_LOGGING);
	}

	private static @NotNull <T extends Item> T register(String name, @NotNull Function<Properties, Item> function, @NotNull Properties properties) {
		return (T) Items.registerItem(ResourceKey.create(Registries.ITEM, TCAConstants.id(name)), function, properties);
	}
}
