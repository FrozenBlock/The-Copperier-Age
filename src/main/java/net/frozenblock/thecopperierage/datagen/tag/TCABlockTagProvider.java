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
import net.frozenblock.thecopperierage.tag.TCABlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public final class TCABlockTagProvider extends FabricTagProvider.BlockTagProvider {
	public TCABlockTagProvider(@NotNull FabricDataOutput output, @NotNull CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void addTags(@NotNull HolderLookup.Provider registries) {
		this.valueLookupBuilder(BlockTags.COPPER);

		this.valueLookupBuilder(BlockTags.LANTERNS);

		this.valueLookupBuilder(BlockTags.BUTTONS);

		this.valueLookupBuilder(BlockTags.PRESSURE_PLATES);

		this.valueLookupBuilder(BlockTags.CAMPFIRES);

		this.valueLookupBuilder(BlockTags.FIRE)
			.add(TCABlocks.COPPER_FIRE);

		this.valueLookupBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
			.add(TCABlocks.GEARBOX);

		this.valueLookupBuilder(TCABlockTags.GEARBOXES)
			.add(TCABlocks.GEARBOX);

		TagAppender<Block, Block> tagAppender = this.valueLookupBuilder(TCABlockTags.COPPER_FIRE_BASE_BLOCKS);
		registries.lookupOrThrow(Registries.BLOCK)
			.listElements()
			.forEach(block -> {
				final ResourceLocation location = block.key().location();
				final String path = location.getPath();

				if (!path.contains("copper")) return;
				if (path.contains("bars")) return;
				if (path.contains("torch")) return;
				if (path.contains("golem")) return;
				if (path.contains("chain")) return;
				if (path.contains("lantern")) return;
				if (path.contains("fire")) return;
				if (path.contains("ore")) return;
				if (path.contains("door")) return;
				if (path.contains("button")) return;
				if (path.contains("pressure_plate")) return;
				if (path.contains("chest")) return;

				tagAppender.add(block.value());
			});
	}

	@NotNull
	private TagKey<Block> getTag(String id) {
		return TagKey.create(this.registryKey, ResourceLocation.parse(id));
	}

	@NotNull private ResourceKey<Block> getKey(String namespace, String path) {
		return ResourceKey.create(this.registryKey, ResourceLocation.fromNamespaceAndPath(namespace, path));
	}
}
