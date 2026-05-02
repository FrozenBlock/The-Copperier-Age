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

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.frozenblock.lib.tag.api.FrozenLibBlockTags;
import net.frozenblock.thecopperierage.references.TCABlockIds;
import net.frozenblock.thecopperierage.references.TCABlockItemIds;
import net.frozenblock.thecopperierage.tag.TCABlockItemTags;
import net.frozenblock.thecopperierage.tag.TCABlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.BlockItemTagsProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.references.BlockIds;
import net.minecraft.references.BlockItemIds;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class TCABlockTagsProvider extends FabricTagsProvider.BlockTagsProvider {

	public TCABlockTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		new TCABlockItemTagsProvider(tagId -> BlockItemTagsProvider.wrapForBlocks(this.tag(tagId.block()))).run();

		this.builder(BlockTags.PRESSURE_PLATES)
			.addOptionalTag(TCABlockItemTags.COPPER_PRESSURE_PLATES.block());

		this.builder(BlockTags.CAMPFIRES)
			.add(TCABlockItemIds.COPPER_CAMPFIRE);

		this.builder(BlockTags.FIRE)
			.add(TCABlockIds.COPPER_FIRE);

		this.builder(BlockTags.MINEABLE_WITH_AXE)
			.add(TCABlockItemIds.COPPER_CAMPFIRE)
			.add(TCABlockItemIds.COPPER_JACK_O_LANTERN, TCABlockItemIds.REDSTONE_JACK_O_LANTERN)
			.add(TCABlockItemIds.CRATE);

		this.builder(BlockTags.SWORD_EFFICIENT)
			.add(TCABlockItemIds.COPPER_JACK_O_LANTERN, TCABlockItemIds.REDSTONE_JACK_O_LANTERN);

		this.builder(BlockTags.MINEABLE_WITH_PICKAXE)
			.addOptionalTag(TCABlockItemTags.GEARBOXES.block())
			.addOptionalTag(TCABlockItemTags.COPPER_FANS.block())
			.addOptionalTag(TCABlockItemTags.CHIMES.block())
			.addOptionalTag(TCABlockItemTags.COPPER_BUTTONS.block())
			.addOptionalTag(TCABlockItemTags.COPPER_PRESSURE_PLATES.block());

		this.builder(BlockTags.MINEABLE_WITH_SHOVEL)
			.add(TCABlockItemIds.REDSTONE_GRIT);

		this.builder(TCABlockTags.CANNOT_ROTATE)
			.add(BlockItemIds.VAULT)
			.add(BlockIds.PISTON_HEAD, BlockIds.MOVING_PISTON)
			.add(BlockItemIds.END_PORTAL_FRAME)
			.add(BlockIds.NETHER_PORTAL)
			.add(BlockItemIds.BIG_DRIPLEAF, BlockItemIds.SMALL_DRIPLEAF)
			.add(BlockIds.BIG_DRIPLEAF_STEM)
			.addOptionalTag(BlockTags.SHULKER_BOXES)
			.addOptionalTag(ConventionalBlockTags.RELOCATION_NOT_SUPPORTED)
			.addOptionalTag(BlockTags.BEDS)
			.addOptionalTag(BlockTags.MAINTAINS_FARMLAND);

		this.builder(TCABlockTags.CANNOT_ROTATE)
			.addOptional(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("trailiertales", "coffin")));

		this.builder(FrozenLibBlockTags.HAS_PUSHABLE_BLOCK_ENTITY)
			.addOptionalTag(TCABlockItemTags.STICKY_GEARBOXES.block())
			.add(TCABlockItemIds.CRATE);

		this.builder(FrozenLibBlockTags.BLOWING_CAN_PASS_THROUGH)
			.addOptionalTag(TCABlockItemTags.COPPER_FANS.block())
			.addOptionalTag(TCABlockItemTags.CHIMES.block());

		this.builder(FrozenLibBlockTags.STRUCTURE_PLACE_SCHEDULES_TICK)
			.addOptionalTag(TCABlockItemTags.COPPER_FANS.block());

		final TagAppender<Block> copperFireBaseBlocksTag = this.builder(TCABlockTags.COPPER_FIRE_BASE_BLOCKS);
		registries.lookupOrThrow(Registries.BLOCK)
			.listElements()
			.forEach(block -> {
				final Identifier id = block.key().identifier();
				final String path = id.getPath();

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
				if (path.contains("campfire")) return;
				if (path.contains("jack_o_lantern")) return;
				if (path.contains("fan")) return;

				copperFireBaseBlocksTag.add(block.key());
			});

		// WILDER WILD
		this.builder(getTag("wilderwild:sound/melon"))
			.add(TCABlockItemIds.COPPER_JACK_O_LANTERN, TCABlockItemIds.REDSTONE_JACK_O_LANTERN);
	}

	private TagKey<Block> getTag(String name) {
		return TagKey.create(this.registryKey, Identifier.parse(name));
	}

	private ResourceKey<Block> getKey(String namespace, String path) {
		return ResourceKey.create(this.registryKey, Identifier.fromNamespaceAndPath(namespace, path));
	}
}
