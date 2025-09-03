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
import net.frozenblock.thecopperierage.block.GearboxBlock;
import net.frozenblock.thecopperierage.registry.TCABlocks;
import net.frozenblock.thecopperierage.registry.TCAItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public final class TCAModelProvider extends FabricModelProvider {
	private static final PropertyDispatch<VariantMutator> GEARBOX_ROTATION = PropertyDispatch.modify(GearboxBlock.FACING)
		.select(Direction.DOWN, BlockModelGenerators.X_ROT_90)
		.select(Direction.UP, BlockModelGenerators.X_ROT_270.then(BlockModelGenerators.Y_ROT_180))
		.select(Direction.NORTH, BlockModelGenerators.NOP)
		.select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
		.select(Direction.WEST, BlockModelGenerators.Y_ROT_270)
		.select(Direction.EAST, BlockModelGenerators.Y_ROT_90);

	private static final ModelTemplate GEARBOX_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_gearbox")),
		Optional.empty(),
		TextureSlot.SIDE, TextureSlot.FRONT
	);
	private static final ModelTemplate GEARBOX_COUNTER_CLOCKWISE_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_gearbox_on")),
		Optional.of("_counter_clockwise"),
		TextureSlot.SIDE, TextureSlot.FRONT
	);
	private static final ModelTemplate GEARBOX_CLOCKWISE_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_gearbox_on")),
		Optional.of("_clockwise"),
		TextureSlot.SIDE, TextureSlot.FRONT
	);

	public TCAModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(@NotNull BlockModelGenerators generator) {
		createCopperFire(generator);
		TCABlocks.GEARBOX.waxedMapping().forEach((block, waxedBlock) -> createGearbox(generator, block, waxedBlock));
	}

	@Override
	public void generateItemModels(@NotNull ItemModelGenerators generator) {
		generateCopperHorn(generator, TCAItems.COPPER_HORN);
		generator.generateFlatItem(TCAItems.WRENCH, ModelTemplates.FLAT_HANDHELD_ITEM);
	}

	private static void createCopperFire(@NotNull BlockModelGenerators generator) {
		MultiVariant floorModels = generator.createFloorFireModels(TCABlocks.COPPER_FIRE);
		MultiVariant sideModels = generator.createSideFireModels(TCABlocks.COPPER_FIRE);
		generator.blockStateOutput.accept(
			MultiPartGenerator.multiPart(TCABlocks.COPPER_FIRE)
				.with(floorModels)
				.with(sideModels)
				.with(sideModels.with(BlockModelGenerators.Y_ROT_90))
				.with(sideModels.with(BlockModelGenerators.Y_ROT_180))
				.with(sideModels.with(BlockModelGenerators.Y_ROT_270))
		);
	}

	private static void createGearbox(@NotNull BlockModelGenerators generator, @NotNull Block block, @NotNull Block waxedBlock) {
		TextureMapping mapping = new TextureMapping()
			.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"))
			.put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_top"));
		MultiVariant model = BlockModelGenerators.plainVariant(GEARBOX_MODEL.create(block, mapping, generator.modelOutput));

		TextureMapping counterMapping = new TextureMapping()
			.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side_counter_clockwise"))
			.put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_top_counter_clockwise"));
		MultiVariant counterModel = BlockModelGenerators.plainVariant(GEARBOX_COUNTER_CLOCKWISE_MODEL.create(block, counterMapping, generator.modelOutput));

		TextureMapping clockwiseMapping = new TextureMapping()
			.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side_clockwise"))
			.put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_top_clockwise"));
		MultiVariant clockwiseModel = BlockModelGenerators.plainVariant(GEARBOX_CLOCKWISE_MODEL.create(block, clockwiseMapping, generator.modelOutput));

		generator.itemModelOutput.copy(block.asItem(), waxedBlock.asItem());

		dispatchGearboxStates(generator, block, model, counterModel, clockwiseModel);
		dispatchGearboxStates(generator, waxedBlock, model, counterModel, clockwiseModel);
	}

	private static void dispatchGearboxStates(@NotNull BlockModelGenerators generator, Block block, MultiVariant model, MultiVariant counterModel, MultiVariant clockwiseModel) {
		generator.blockStateOutput.accept(
			MultiVariantGenerator.dispatch(block)
				.with(
					PropertyDispatch.initial(GearboxBlock.POWER)
						.select(15, counterModel)
						.select(14, clockwiseModel)
						.select(13, counterModel)
						.select(12, clockwiseModel)
						.select(11, counterModel)
						.select(10, clockwiseModel)
						.select(9, counterModel)
						.select(8, clockwiseModel)
						.select(7, counterModel)
						.select(6, clockwiseModel)
						.select(5, counterModel)
						.select(4, clockwiseModel)
						.select(3, counterModel)
						.select(2, clockwiseModel)
						.select(1, counterModel)
						.select(0, model)
				)
				.with(GEARBOX_ROTATION)
		);
	}

	private static void generateCopperHorn(@NotNull ItemModelGenerators generator, Item item) {
		ItemModel.Unbaked unbaked = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item));
		ItemModel.Unbaked unbaked2 = ItemModelUtils.plainModel(TCAConstants.id("item/copper_horn_tooting"));
		generator.generateBooleanDispatch(item, ItemModelUtils.isUsingItem(), unbaked2, unbaked);
	}
}
