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

package net.frozenblock.thecopperierage.datagen.model;

import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.registry.TCABlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import static net.minecraft.client.data.models.BlockModelGenerators.*;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public final class TCAPackModelProvider extends FabricModelProvider {
	private static final ModelTemplate COPPER_CHAIN_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_copper_chain")),
		Optional.empty(),
		TextureSlot.TEXTURE
	);
	public static final TexturedModel.Provider COPPER_CHAIN_PROVIDER = TexturedModel.createDefault(TextureMapping::defaultTexture, COPPER_CHAIN_MODEL);
	private static final ModelTemplate COPPER_LANTERN_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_copper_lantern")),
		Optional.empty(),
		TextureSlot.LANTERN, TextureSlot.BARS
	);
	private static final ModelTemplate HANGING_COPPER_LANTERN_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_hanging_copper_lantern")),
		Optional.of("_hanging"),
		TextureSlot.LANTERN, TextureSlot.BARS
	);
	private static final ModelTemplate COPPER_BARS_CAP_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_copper_bars_cap")),
		Optional.of("_cap"),
		TextureSlot.BARS, TextureSlot.EDGE
	);
	private static final ModelTemplate COPPER_BARS_CAP_ALT_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_copper_bars_cap_alt")),
		Optional.of("_cap_alt"),
		TextureSlot.BARS, TextureSlot.EDGE
	);
	private static final ModelTemplate COPPER_BARS_POST_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_copper_bars_post")),
		Optional.of("_post"),
		TextureSlot.BARS, TextureSlot.EDGE
	);
	private static final ModelTemplate COPPER_BARS_POST_ENDS_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_copper_bars_post_ends")),
		Optional.of("_post_ends"),
		TextureSlot.BARS, TextureSlot.EDGE
	);
	private static final ModelTemplate COPPER_BARS_SIDE_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_copper_bars_side")),
		Optional.of("_side"),
		TextureSlot.BARS, TextureSlot.EDGE
	);
	private static final ModelTemplate COPPER_BARS_SIDE_ALT_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_copper_bars_side_alt")),
		Optional.of("_side_alt"),
		TextureSlot.BARS, TextureSlot.EDGE
	);
	private static final ModelTemplate COPPER_BUTTON_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_copper_button")),
		Optional.empty(),
		TextureSlot.TEXTURE
	);
	private static final ModelTemplate COPPER_BUTTON_PRESSED_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_copper_button_pressed")),
		Optional.of("_pressed"),
		TextureSlot.TEXTURE
	);
	private static final ModelTemplate COPPER_BUTTON_INVENTORY_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_copper_button_inventory")),
		Optional.of("_inventory"),
		TextureSlot.TEXTURE
	);
	public static boolean GENERATING_COPPER_BUTTON = false;

	public TCAPackModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(@NotNull BlockModelGenerators generator) {
		generateCopperChain(generator, Blocks.COPPER_CHAIN.unaffected());
		generateCopperChain(generator, Blocks.COPPER_CHAIN.exposed());
		generateCopperChain(generator, Blocks.COPPER_CHAIN.weathered());
		generateCopperChain(generator, Blocks.COPPER_CHAIN.oxidized());
		generateCopperLantern(generator, Blocks.COPPER_LANTERN.unaffected(), Blocks.COPPER_CHAIN.unaffected());
		generateCopperLantern(generator, Blocks.COPPER_LANTERN.exposed(), Blocks.COPPER_CHAIN.exposed());
		generateCopperLantern(generator, Blocks.COPPER_LANTERN.weathered(), Blocks.COPPER_CHAIN.weathered());
		generateCopperLantern(generator, Blocks.COPPER_LANTERN.oxidized(), Blocks.COPPER_CHAIN.oxidized());
		Blocks.COPPER_BARS.waxedMapping().forEach((block, waxedBlock) -> generateCopperBars(generator, block, waxedBlock));
		TCABlocks.COPPER_BUTTON.waxedMapping().forEach((block, waxedBlock) -> createCopperButtonOverrides(generator, block, waxedBlock));
	}

	@Override
	public void generateItemModels(@NotNull ItemModelGenerators generator) {
	}

	private static void generateCopperChain(@NotNull BlockModelGenerators generator, Block chain) {
		COPPER_CHAIN_PROVIDER.create(chain, generator.modelOutput);
	}

	private static void generateCopperLantern(@NotNull BlockModelGenerators generator, Block lantern, Block chain) {
		final TextureMapping textureMapping = new TextureMapping()
			.put(TextureSlot.LANTERN, TextureMapping.getBlockTexture(lantern))
			.put(TextureSlot.BARS, TextureMapping.getBlockTexture(chain));

		COPPER_LANTERN_MODEL.create(lantern, textureMapping, generator.modelOutput);
		HANGING_COPPER_LANTERN_MODEL.create(lantern, textureMapping, generator.modelOutput);
	}

	public static void generateCopperBars(@NotNull BlockModelGenerators generator, Block block, Block waxedBlock) {
		final TextureMapping textureMapping = new TextureMapping()
			.put(TextureSlot.BARS, TextureMapping.getBlockTexture(block))
			.put(TextureSlot.EDGE, TextureMapping.getBlockTexture(block, "_edge"));

		final MultiVariant postEnds = plainVariant(COPPER_BARS_POST_ENDS_MODEL.create(block, textureMapping, generator.modelOutput));
		final MultiVariant post = plainVariant(COPPER_BARS_POST_MODEL.create(block, textureMapping, generator.modelOutput));
		final MultiVariant cap = plainVariant(COPPER_BARS_CAP_MODEL.create(block, textureMapping, generator.modelOutput));
		final MultiVariant capAlt = plainVariant(COPPER_BARS_CAP_ALT_MODEL.create(block, textureMapping, generator.modelOutput));
		final MultiVariant side = plainVariant(COPPER_BARS_SIDE_MODEL.create(block, textureMapping, generator.modelOutput));
		final MultiVariant sideAlt = plainVariant(COPPER_BARS_SIDE_ALT_MODEL.create(block, textureMapping, generator.modelOutput));

		Function<MultiPartGenerator, MultiPartGenerator> barsDispatcher = multiPartGenerator -> {
			return multiPartGenerator.with(postEnds)
				.with(
					condition()
						.term(BlockStateProperties.NORTH, false)
						.term(BlockStateProperties.EAST, false)
						.term(BlockStateProperties.SOUTH, false)
						.term(BlockStateProperties.WEST, false),
					post
				).with(
					condition()
						.term(BlockStateProperties.NORTH, true)
						.term(BlockStateProperties.EAST, false)
						.term(BlockStateProperties.SOUTH, false)
						.term(BlockStateProperties.WEST, false),
					cap
				).with(
					condition()
						.term(BlockStateProperties.NORTH, false)
						.term(BlockStateProperties.EAST, true)
						.term(BlockStateProperties.SOUTH, false)
						.term(BlockStateProperties.WEST, false),
					cap.with(Y_ROT_90)
				).with(
					condition()
						.term(BlockStateProperties.NORTH, false)
						.term(BlockStateProperties.EAST, false)
						.term(BlockStateProperties.SOUTH, true)
						.term(BlockStateProperties.WEST, false),
					capAlt
				).with(
					condition()
						.term(BlockStateProperties.NORTH, false)
						.term(BlockStateProperties.EAST, false)
						.term(BlockStateProperties.SOUTH, false)
						.term(BlockStateProperties.WEST, true),
					capAlt.with(Y_ROT_90)
				)
				.with(condition().term(BlockStateProperties.NORTH, true), side)
				.with(condition().term(BlockStateProperties.EAST, true), side.with(Y_ROT_90))
				.with(condition().term(BlockStateProperties.SOUTH, true), sideAlt)
				.with(condition().term(BlockStateProperties.WEST, true), sideAlt.with(Y_ROT_90));
		};

		generator.blockStateOutput.accept(barsDispatcher.apply(MultiPartGenerator.multiPart(block)));
		generator.blockStateOutput.accept(barsDispatcher.apply(MultiPartGenerator.multiPart(waxedBlock)));
	}

	private static void createCopperButtonOverrides(@NotNull BlockModelGenerators generator, @NotNull Block block, @NotNull Block waxedBlock) {
		GENERATING_COPPER_BUTTON = true;
		TCAModelProvider.createCopperButton(generator, block, waxedBlock, block, COPPER_BUTTON_MODEL, COPPER_BUTTON_PRESSED_MODEL, COPPER_BUTTON_INVENTORY_MODEL);
		GENERATING_COPPER_BUTTON = false;
	}

}
