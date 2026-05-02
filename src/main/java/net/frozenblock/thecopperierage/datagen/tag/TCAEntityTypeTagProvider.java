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

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.frozenblock.thecopperierage.tag.TCAEntityTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypeIds;

public final class TCAEntityTypeTagProvider extends FabricTagsProvider.EntityTypeTagsProvider {

	public TCAEntityTypeTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void addTags(HolderLookup.Provider arg) {
		this.builder(TCAEntityTypeTags.COPPER_FAN_WEAKER_PUSH)
			.add(EntityTypeIds.ALLAY)
			.add(EntityTypeIds.HORSE)
			.add(EntityTypeIds.ZOMBIE_HORSE)
			.add(EntityTypeIds.SKELETON_HORSE)
			.add(EntityTypeIds.DONKEY)
			.add(EntityTypeIds.MULE)
			.add(EntityTypeIds.LLAMA)
			.add(EntityTypeIds.TRADER_LLAMA)
			.add(EntityTypeIds.SNIFFER)
			.add(EntityTypeIds.POLAR_BEAR)
			.add(EntityTypeIds.HOGLIN)
			.add(EntityTypeIds.ZOGLIN)
			.add(EntityTypeIds.CAMEL)
			.add(EntityTypeIds.GUARDIAN)
			.add(EntityTypeIds.CREAKING)
			.add(EntityTypeIds.GHAST)
			.add(EntityTypeIds.HAPPY_GHAST);

		this.builder(TCAEntityTypeTags.COPPER_FAN_WEAKER_PUSH)
			.addOptional(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("wilderwild", "ostrich")));

		this.builder(TCAEntityTypeTags.COPPER_FAN_CANNOT_PUSH)
			.add(EntityTypeIds.WITHER)
			.add(EntityTypeIds.ENDER_DRAGON)
			.add(EntityTypeIds.EYE_OF_ENDER)
			.add(EntityTypeIds.BLOCK_DISPLAY)
			.add(EntityTypeIds.MARKER)
			.add(EntityTypeIds.BREEZE)
			.add(EntityTypeIds.GIANT)
			.add(EntityTypeIds.ELDER_GUARDIAN)
			.add(EntityTypeIds.IRON_GOLEM)
			.add(EntityTypeIds.WARDEN)
			.add(EntityTypeIds.VEX)
			.add(EntityTypeIds.SHULKER)
			.add(EntityTypeIds.RAVAGER)
			.add(EntityTypeIds.WIND_CHARGE);

		this.builder(TCAEntityTypeTags.COPPER_FAN_CANNOT_PUSH)
			.addOptional(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("trailiertales", "apparition"))); ;

		this.builder(TCAEntityTypeTags.GEARBOX_CANNOT_ROTATE)
			.add(EntityTypeIds.WITHER)
			.add(EntityTypeIds.ENDER_DRAGON)
			.add(EntityTypeIds.EYE_OF_ENDER)
			.add(EntityTypeIds.BLOCK_DISPLAY)
			.add(EntityTypeIds.BREEZE)
			.add(EntityTypeIds.BLAZE)
			.add(EntityTypeIds.GIANT)
			.add(EntityTypeIds.ELDER_GUARDIAN)
			.add(EntityTypeIds.IRON_GOLEM)
			.add(EntityTypeIds.VEX)
			.add(EntityTypeIds.SHULKER)
			.add(EntityTypeIds.RAVAGER)
			.add(EntityTypeIds.GHAST)
			.add(EntityTypeIds.HAPPY_GHAST)
			.add(EntityTypeIds.ALLAY)
			.add(EntityTypeIds.BAT);

		this.builder(TCAEntityTypeTags.GEARBOX_CANNOT_ROTATE)
			.addOptionalTag(getTag("netheriernether:blazes"))
			.addOptional(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("trailiertales", "apparition")));
	}

	private TagKey<EntityType<?>> getTag(String id) {
		return TagKey.create(this.registryKey, Identifier.parse(id));
	}

	private ResourceKey<EntityType<?>> getKey(String namespace, String path) {
		return ResourceKey.create(this.registryKey, Identifier.fromNamespaceAndPath(namespace, path));
	}
}
