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
import net.frozenblock.thecopperierage.tag.TCAEntityTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public final class TCAEntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {

	public TCAEntityTypeTagProvider(@NotNull FabricDataOutput output, @NotNull CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void addTags(@NotNull HolderLookup.Provider arg) {
		this.valueLookupBuilder(TCAEntityTypeTags.COPPER_FAN_WEAKER_PUSH)
			.add(EntityType.ALLAY)
			.add(EntityType.HORSE)
			.add(EntityType.ZOMBIE_HORSE)
			.add(EntityType.SKELETON_HORSE)
			.add(EntityType.DONKEY)
			.add(EntityType.MULE)
			.add(EntityType.LLAMA)
			.add(EntityType.TRADER_LLAMA)
			.add(EntityType.SNIFFER)
			.add(EntityType.POLAR_BEAR)
			.add(EntityType.HOGLIN)
			.add(EntityType.ZOGLIN)
			.add(EntityType.CAMEL)
			.add(EntityType.GUARDIAN)
			.add(EntityType.CREAKING)
			.add(EntityType.GHAST)
			.add(EntityType.HAPPY_GHAST);

		this.builder(TCAEntityTypeTags.COPPER_FAN_WEAKER_PUSH)
			.addOptional(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("wilderwild", "ostrich")));

		this.valueLookupBuilder(TCAEntityTypeTags.COPPER_FAN_CANNOT_PUSH)
			.add(EntityType.WITHER)
			.add(EntityType.ENDER_DRAGON)
			.add(EntityType.EYE_OF_ENDER)
			.add(EntityType.BLOCK_DISPLAY)
			.add(EntityType.MARKER)
			.add(EntityType.BREEZE)
			.add(EntityType.GIANT)
			.add(EntityType.ELDER_GUARDIAN)
			.add(EntityType.IRON_GOLEM)
			.add(EntityType.WARDEN)
			.add(EntityType.VEX)
			.add(EntityType.SHULKER)
			.add(EntityType.RAVAGER)
			.add(EntityType.WIND_CHARGE);

		this.builder(TCAEntityTypeTags.COPPER_FAN_CANNOT_PUSH)
			.addOptional(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("trailiertales", "apparition")));
	}

	@NotNull
	private TagKey<EntityType<?>> getTag(String id) {
		return TagKey.create(this.registryKey, ResourceLocation.parse(id));
	}

	@NotNull private ResourceKey<EntityType<?>> getKey(String namespace, String path) {
		return ResourceKey.create(this.registryKey, ResourceLocation.fromNamespaceAndPath(namespace, path));
	}
}
