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

package net.frozenblock.thecopperierage.datagen.tag;

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
import net.minecraft.references.BlockItemId;
import net.minecraft.references.ItemIds;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ColorCollection;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperCollection;

public final class TCAItemTagProvider extends FabricTagsProvider.ItemTagsProvider {

	public TCAItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void addTags(HolderLookup.Provider arg) {
		// TODO 26.2 blockitemtag provider? might need fabric change
		this.builder(BlockItemTags.BUTTONS.item())
			.addOptionalTag(TCAItemTags.COPPER_BUTTONS);

		this.builder(TCAItemTags.GEARBOXES)
			.addAll(toIds(TCABlockItemIds.GEARBOX))
			.addTag(TCAItemTags.STICKY_GEARBOXES);

		this.builder(TCAItemTags.STICKY_GEARBOXES)
			.addAll(toIds(TCABlockItemIds.STICKY_GEARBOX));

		this.builder(TCAItemTags.COPPER_FANS)
			.addAll(toIds(TCABlockItemIds.COPPER_FAN));

		this.builder(TCAItemTags.CHIMES)
			.addAll(toIds(TCABlockItemIds.CHIME));

		this.builder(TCAItemTags.COPPER_BUTTONS)
			.addAll(toIds(TCABlockItemIds.COPPER_BUTTON));

		this.builder(TCAItemTags.COPPER_PRESSURE_PLATES)
			.addAll(toIds(TCABlockItemIds.WEIGHTED_PRESSURE_PLATE));

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
		arg.lookupOrThrow(Registries.BLOCK)
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

	private TagKey<Item> getTag(String id) {
		return TagKey.create(this.registryKey, Identifier.parse(id));
	}

	private ResourceKey<Item> getKey(String namespace, String path) {
		return ResourceKey.create(this.registryKey, Identifier.fromNamespaceAndPath(namespace, path));
	}

	private static ColorCollection<ResourceKey<Item>> toIds(final ColorCollection<BlockItemId> ids) {
		return ids.map(BlockItemId::item);
	}

	private static WeatheringCopperCollection<ResourceKey<Item>> toIds(final WeatheringCopperCollection<BlockItemId> ids) {
		return ids.map(BlockItemId::item);
	}
}
