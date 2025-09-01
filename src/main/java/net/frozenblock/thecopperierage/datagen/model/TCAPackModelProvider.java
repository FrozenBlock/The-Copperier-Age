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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import static net.minecraft.client.data.models.BlockModelGenerators.createBooleanModelDispatch;
import static net.minecraft.client.data.models.model.TextureMapping.getBlockTexture;

@Environment(EnvType.CLIENT)
public final class TCAPackModelProvider extends FabricModelProvider {
	private static final ModelTemplate COPPER_CHAIN_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_copper_chain")),
		Optional.empty(),
		TextureSlot.TEXTURE
	);
	private static final ModelTemplate COPPER_LANTERN_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_copper_lantern")),
		Optional.empty(),
		TextureSlot.LANTERN
	);
	private static final ModelTemplate HANGING_COPPER_LANTERN_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_hanging_copper_lantern")),
		Optional.of("_hanging"),
		TextureSlot.LANTERN
	);
	public static final TexturedModel.Provider COPPER_CHAIN_PROVIDER = TexturedModel.createDefault(TextureMapping::defaultTexture, COPPER_CHAIN_MODEL);

	public TCAPackModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(@NotNull BlockModelGenerators generator) {
		generateCopperChain(generator, Blocks.COPPER_CHAIN.unaffected());
		generateCopperChain(generator, Blocks.COPPER_CHAIN.exposed());
		generateCopperChain(generator, Blocks.COPPER_CHAIN.weathered());
		generateCopperChain(generator, Blocks.COPPER_CHAIN.oxidized());
		generateCopperLantern(generator, Blocks.COPPER_LANTERN.unaffected());
		generateCopperLantern(generator, Blocks.COPPER_LANTERN.exposed());
		generateCopperLantern(generator, Blocks.COPPER_LANTERN.weathered());
		generateCopperLantern(generator, Blocks.COPPER_LANTERN.oxidized());
	}

	@Override
	public void generateItemModels(@NotNull ItemModelGenerators generator) {
	}

	private static void generateCopperChain(@NotNull BlockModelGenerators generator, Block chain) {
		COPPER_CHAIN_PROVIDER.create(chain, generator.modelOutput);
	}

	public static void generateCopperLantern(@NotNull BlockModelGenerators generator, Block block) {
		TextureMapping textureMapping = new TextureMapping().put(TextureSlot.LANTERN, getBlockTexture(block));
		ResourceLocation lanternModel = COPPER_LANTERN_MODEL.create(block, textureMapping, generator.modelOutput);
		ResourceLocation hangingLanternModel = HANGING_COPPER_LANTERN_MODEL.create(block, textureMapping, generator.modelOutput);

		generator.blockStateOutput.accept(
			MultiVariantGenerator.dispatch(block).with(createBooleanModelDispatch(BlockStateProperties.HANGING, BlockModelGenerators.plainVariant(hangingLanternModel), BlockModelGenerators.plainVariant(lanternModel)))
		);
	}
}
