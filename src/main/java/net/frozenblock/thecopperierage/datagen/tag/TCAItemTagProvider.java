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

package net.frozenblock.thecopperierage.datagen.tag;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.frozenblock.thecopperierage.registry.TCABlocks;
import net.frozenblock.thecopperierage.registry.TCAItems;
import net.frozenblock.thecopperierage.tag.TCAItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public final class TCAItemTagProvider extends FabricTagProvider.ItemTagProvider {

	public TCAItemTagProvider(@NotNull FabricDataOutput output, @NotNull CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void addTags(@NotNull HolderLookup.Provider arg) {
		this.builder(ItemTags.BUTTONS)
			.addOptionalTag(TCAItemTags.COPPER_BUTTONS);

		TagAppender<Item, Item> gearboxesTag = this.valueLookupBuilder(TCAItemTags.GEARBOXES);
		TCABlocks.GEARBOX.forEach(block -> gearboxesTag.add(block.asItem()));

		TagAppender<Item, Item> copperFansTag = this.valueLookupBuilder(TCAItemTags.COPPER_FANS);
		TCABlocks.COPPER_FAN.forEach(block -> copperFansTag.add(block.asItem()));

		TagAppender<Item, Item> chimesTag = this.valueLookupBuilder(TCAItemTags.CHIMES);
		TCABlocks.CHIME.forEach(block -> chimesTag.add(block.asItem()));

		TagAppender<Item, Item> copperButtonsTag = this.valueLookupBuilder(TCAItemTags.COPPER_BUTTONS);
		TCABlocks.COPPER_BUTTON.forEach(block -> copperButtonsTag.add(block.asItem()));

		TagAppender<Item, Item> copperPressurePlatesTag = this.valueLookupBuilder(TCAItemTags.COPPER_PRESSURE_PLATES);
		TCABlocks.WEIGHTED_PRESSURE_PLATE.forEach(block -> copperPressurePlatesTag.add(block.asItem()));

		this.valueLookupBuilder(ItemTags.BREAKS_DECORATED_POTS)
			.add(TCAItems.WRENCH);

		this.valueLookupBuilder(ItemTags.DURABILITY_ENCHANTABLE)
			.add(TCAItems.WRENCH);

		this.valueLookupBuilder(TCAItemTags.OXIDIZABLE_EQUIPMENT)
			.add(Items.COPPER_SWORD)
			.add(Items.COPPER_AXE)
			.add(Items.COPPER_HOE)
			.add(Items.COPPER_PICKAXE)
			.add(Items.COPPER_SHOVEL)
			.add(Items.COPPER_SPEAR)
			.add(Items.COPPER_HELMET)
			.add(Items.COPPER_CHESTPLATE)
			.add(Items.COPPER_LEGGINGS)
			.add(Items.COPPER_BOOTS)
			.add(Items.BRUSH)
			.add(TCAItems.WRENCH);

		this.valueLookupBuilder(TCAItemTags.OXIDIZING_DOES_NOT_SCALE_ATTACK_SPEED)
			.add(Items.COPPER_SPEAR);
	}

	@NotNull
	private TagKey<Item> getTag(String id) {
		return TagKey.create(this.registryKey, ResourceLocation.parse(id));
	}

	@NotNull private ResourceKey<Item> getKey(String namespace, String path) {
		return ResourceKey.create(this.registryKey, ResourceLocation.fromNamespaceAndPath(namespace, path));
	}
}
