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
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.frozenblock.lib.tag.api.FrozenBlockTags;
import net.frozenblock.thecopperierage.registry.TCABlocks;
import net.frozenblock.thecopperierage.tag.TCABlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class TCABlockTagProvider extends FabricTagsProvider.BlockTagsProvider {

	public TCABlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void addTags(HolderLookup.Provider registries) {
		this.valueLookupBuilder(BlockTags.COPPER);

		this.valueLookupBuilder(BlockTags.LANTERNS);

		this.builder(BlockTags.BUTTONS)
			.addOptionalTag(TCABlockTags.COPPER_BUTTONS);

		this.builder(BlockTags.PRESSURE_PLATES)
			.addOptionalTag(TCABlockTags.COPPER_PRESSURE_PLATES);

		this.valueLookupBuilder(BlockTags.CAMPFIRES)
			.add(TCABlocks.COPPER_CAMPFIRE);

		this.valueLookupBuilder(BlockTags.FIRE)
			.add(TCABlocks.COPPER_FIRE);

		TagAppender<Block, Block> gearboxesTag = this.valueLookupBuilder(TCABlockTags.GEARBOXES);
		TCABlocks.GEARBOX.forEach(gearboxesTag::add);

		TagAppender<Block, Block> copperFansTag = this.valueLookupBuilder(TCABlockTags.COPPER_FANS);
		TCABlocks.COPPER_FAN.forEach(copperFansTag::add);

		TagAppender<Block, Block> chimesTag = this.valueLookupBuilder(TCABlockTags.CHIMES);
		TCABlocks.CHIME.forEach(chimesTag::add);

		TagAppender<Block, Block> copperButtonsTag = this.valueLookupBuilder(TCABlockTags.COPPER_BUTTONS);
		TCABlocks.COPPER_BUTTON.forEach(copperButtonsTag::add);

		TagAppender<Block, Block> copperPressurePlatesTag = this.valueLookupBuilder(TCABlockTags.COPPER_PRESSURE_PLATES);
		TCABlocks.WEIGHTED_PRESSURE_PLATE.forEach(copperPressurePlatesTag::add);

		this.valueLookupBuilder(BlockTags.MINEABLE_WITH_AXE)
			.add(TCABlocks.COPPER_CAMPFIRE)
			.add(TCABlocks.COPPER_JACK_O_LANTERN, TCABlocks.REDSTONE_JACK_O_LANTERN);

		this.valueLookupBuilder(BlockTags.SWORD_EFFICIENT)
			.add(TCABlocks.COPPER_JACK_O_LANTERN, TCABlocks.REDSTONE_JACK_O_LANTERN);

		this.builder(BlockTags.MINEABLE_WITH_PICKAXE)
			.addOptionalTag(TCABlockTags.GEARBOXES)
			.addOptionalTag(TCABlockTags.COPPER_FANS)
			.addOptionalTag(TCABlockTags.CHIMES)
			.addOptionalTag(TCABlockTags.COPPER_BUTTONS)
			.addOptionalTag(TCABlockTags.COPPER_PRESSURE_PLATES);

		this.valueLookupBuilder(TCABlockTags.WRENCH_CANNOT_ROTATE)
			.add(Blocks.VAULT)
			.add(Blocks.PISTON_HEAD, Blocks.MOVING_PISTON)
			.add(Blocks.END_PORTAL_FRAME, Blocks.NETHER_PORTAL)
			.add(Blocks.BIG_DRIPLEAF, Blocks.BIG_DRIPLEAF_STEM, Blocks.SMALL_DRIPLEAF)
			.addOptionalTag(BlockTags.SHULKER_BOXES)
			.addOptionalTag(ConventionalBlockTags.RELOCATION_NOT_SUPPORTED)
			.addOptionalTag(BlockTags.BEDS)
			.addOptionalTag(BlockTags.MAINTAINS_FARMLAND);

		this.builder(TCABlockTags.WRENCH_CANNOT_ROTATE)
			.addOptional(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("trailiertales", "coffin")));

		this.builder(FrozenBlockTags.BLOWING_CAN_PASS_THROUGH)
			.addOptionalTag(TCABlockTags.COPPER_FANS)
			.addOptionalTag(TCABlockTags.CHIMES);

		this.builder(FrozenBlockTags.STRUCTURE_PLACE_SCHEDULES_TICK)
			.addOptionalTag(TCABlockTags.COPPER_FANS);

		TagAppender<Block, Block> copperFireBaseBlocksTag = this.valueLookupBuilder(TCABlockTags.COPPER_FIRE_BASE_BLOCKS);
		registries.lookupOrThrow(Registries.BLOCK)
			.listElements()
			.forEach(block -> {
				final Identifier location = block.key().identifier();
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
				if (path.contains("campfire")) return;
				if (path.contains("jack_o_lantern")) return;
				if (path.contains("fan")) return;

				copperFireBaseBlocksTag.add(block.value());
			});

		// WILDER WILD
		this.valueLookupBuilder(getTag("wilderwild:sound/melon"))
			.add(TCABlocks.COPPER_JACK_O_LANTERN, TCABlocks.REDSTONE_JACK_O_LANTERN);
	}

	private TagKey<Block> getTag(String id) {
		return TagKey.create(this.registryKey, Identifier.parse(id));
	}

	private ResourceKey<Block> getKey(String namespace, String path) {
		return ResourceKey.create(this.registryKey, Identifier.fromNamespaceAndPath(namespace, path));
	}
}
