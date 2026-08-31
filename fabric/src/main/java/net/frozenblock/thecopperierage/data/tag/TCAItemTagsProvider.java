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

package net.frozenblock.thecopperierage.data.tag;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.frozenblock.thecopperierage.item.api.OxidizableItemHelper;
import net.frozenblock.thecopperierage.references.TCABlockItemIds;
import net.frozenblock.thecopperierage.references.TCAItemIds;
import net.frozenblock.thecopperierage.tag.TCAItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.BlockItemTagAppender;
import net.minecraft.data.tags.BlockItemTagsProvider;
import net.minecraft.references.ItemIds;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;

public final class TCAItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {

	public TCAItemTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		new TCABlockItemTagsProvider(tagId -> BlockItemTagsProvider.wrapForItems(this.tag(tagId.item()))).run();

		this.builder(chaosHypercubedTag("sulfur_cube_functional"))
			.add(TCABlockItemIds.KILN.item());

		this.builder(ItemTags.BREAKS_DECORATED_POTS)
			.add(TCAItemIds.WRENCH);

		this.builder(ItemTags.DURABILITY_ENCHANTABLE)
			.add(TCAItemIds.WRENCH);

		this.builder(TCAItemTags.OXIDIZABLE_EQUIPMENT)
			.add(ItemIds.COPPER_SWORD)
			.add(ItemIds.COPPER_AXE)
			.add(ItemIds.COPPER_HOE)
			.add(ItemIds.COPPER_PICKAXE)
			.add(ItemIds.COPPER_SHOVEL)
			.add(ItemIds.COPPER_SPEAR)
			.add(ItemIds.COPPER_HELMET)
			.add(ItemIds.COPPER_CHESTPLATE)
			.add(ItemIds.COPPER_LEGGINGS)
			.add(ItemIds.COPPER_BOOTS)
			.add(ItemIds.BRUSH)
			.add(TCAItemIds.WRENCH);

		this.builder(TCAItemTags.OXIDIZING_DOES_NOT_SCALE_ATTACK_SPEED)
			.add(ItemIds.COPPER_SPEAR);

		final BlockItemTagAppender<Item> unaffectedTag = this.builder(TCAItemTags.WEATHERING_UNAFFECTED);
		final BlockItemTagAppender<Item> exposedTag = this.builder(TCAItemTags.WEATHERING_EXPOSED);
		final BlockItemTagAppender<Item> weatheredTag = this.builder(TCAItemTags.WEATHERING_WEATHERED);
		final BlockItemTagAppender<Item> oxidizedTag = this.builder(TCAItemTags.WEATHERING_OXIDIZED);
		final BlockItemTagAppender<Item> waxedTag = this.builder(TCAItemTags.WEATHERING_WAXED);
		registries.lookupOrThrow(Registries.BLOCK)
			.listElements()
			.forEach(block -> {
				final Item item = block.value().asItem();
				final ResourceKey<Item> itemId = item.builtInRegistryHolder().key();

				final Optional<Block> nonWaxedBlock = OxidizableItemHelper.getNonWaxedEquivalent(block.value());
				if (nonWaxedBlock.orElse(block.value()) instanceof WeatheringCopper weatheringCopper) {
					final WeatheringCopper.WeatherState weatherState = weatheringCopper.getAge();
					if (weatherState == WeatheringCopper.WeatherState.UNAFFECTED) unaffectedTag.add(itemId);
					if (weatherState == WeatheringCopper.WeatherState.EXPOSED) exposedTag.add(itemId);
					if (weatherState == WeatheringCopper.WeatherState.WEATHERED) weatheredTag.add(itemId);
					if (weatherState == WeatheringCopper.WeatherState.OXIDIZED) oxidizedTag.add(itemId);
				}
				if (nonWaxedBlock.isPresent()) waxedTag.add(itemId);
			});
	}

	private TagKey<Item> getTag(String name) {
		return TagKey.create(this.registryKey, Identifier.parse(name));
	}

	private TagKey<Item> chaosHypercubedTag(String name) {
		return TagKey.create(this.registryKey, Identifier.fromNamespaceAndPath("chaoshypercubed", name));
	}

	private ResourceKey<Item> getKey(String namespace, String path) {
		return ResourceKey.create(this.registryKey, Identifier.fromNamespaceAndPath(namespace, path));
	}
}
