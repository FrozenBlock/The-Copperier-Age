package net.frozenblock.thecopperierage.references;

import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public final class TCAItemIds {
	public static final ResourceKey<Item> WRENCH = create("wrench");
	public static final ResourceKey<Item> COPPER_HORN = create("copper_horn");

	private TCAItemIds() {
		throw new UnsupportedOperationException("TCAItemIds contains only static declarations.");
	}

	private static ResourceKey<Item> create(String name) {
		return ResourceKey.create(Registries.ITEM, TCAConstants.id(name));
	}
}
