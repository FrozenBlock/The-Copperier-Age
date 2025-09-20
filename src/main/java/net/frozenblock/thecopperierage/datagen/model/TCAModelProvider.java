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
import net.frozenblock.thecopperierage.block.CopperFanBlock;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
	private static final ModelTemplate COPPER_FAN_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_copper_fan")),
		Optional.empty(),
		TextureSlot.SIDE, TextureSlot.BOTTOM
	);
	private static final ModelTemplate COPPER_FAN_POWERED_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_copper_fan_powered")),
		Optional.of("_powered"),
		TextureSlot.SIDE, TextureSlot.BOTTOM
	);

	public TCAModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(@NotNull BlockModelGenerators generator) {
		createCopperFire(generator);
		generator.createPumpkinVariant(TCABlocks.COPPER_JACK_O_LANTERN, TextureMapping.column(Blocks.PUMPKIN));
		generator.createPumpkinVariant(TCABlocks.REDSTONE_JACK_O_LANTERN, TextureMapping.column(Blocks.PUMPKIN));
		generator.createCampfires(TCABlocks.COPPER_CAMPFIRE);

		generator.createWeightedPressurePlate(TCABlocks.WEIGHTED_PRESSURE_PLATE.unaffected(), Blocks.COPPER_BLOCK);
		generator.createWeightedPressurePlate(TCABlocks.WEIGHTED_PRESSURE_PLATE.waxed(), Blocks.COPPER_BLOCK);
		generator.createWeightedPressurePlate(TCABlocks.WEIGHTED_PRESSURE_PLATE.exposed(), Blocks.EXPOSED_COPPER);
		generator.createWeightedPressurePlate(TCABlocks.WEIGHTED_PRESSURE_PLATE.waxedExposed(), Blocks.EXPOSED_COPPER);
		generator.createWeightedPressurePlate(TCABlocks.WEIGHTED_PRESSURE_PLATE.weathered(), Blocks.WEATHERED_COPPER);
		generator.createWeightedPressurePlate(TCABlocks.WEIGHTED_PRESSURE_PLATE.waxedWeathered(), Blocks.WEATHERED_COPPER);
		generator.createWeightedPressurePlate(TCABlocks.WEIGHTED_PRESSURE_PLATE.oxidized(), Blocks.OXIDIZED_COPPER);
		generator.createWeightedPressurePlate(TCABlocks.WEIGHTED_PRESSURE_PLATE.waxedOxidized(), Blocks.OXIDIZED_COPPER);

		TCABlocks.GEARBOX.waxedMapping().forEach((block, waxedBlock) -> createGearbox(generator, block, waxedBlock));
		TCABlocks.COPPER_FAN.waxedMapping().forEach((block, waxedBlock) -> createCopperFan(generator, block, waxedBlock));

		createCopperButton(generator, TCABlocks.COPPER_BUTTON.unaffected(), TCABlocks.COPPER_BUTTON.waxed(), Blocks.COPPER_BLOCK);
		createCopperButton(generator, TCABlocks.COPPER_BUTTON.exposed(), TCABlocks.COPPER_BUTTON.waxedExposed(), Blocks.EXPOSED_COPPER);
		createCopperButton(generator, TCABlocks.COPPER_BUTTON.weathered(), TCABlocks.COPPER_BUTTON.waxedWeathered(), Blocks.WEATHERED_COPPER);
		createCopperButton(generator, TCABlocks.COPPER_BUTTON.oxidized(), TCABlocks.COPPER_BUTTON.waxedOxidized(), Blocks.OXIDIZED_COPPER);
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

	private static void createCopperFan(@NotNull BlockModelGenerators generator, @NotNull Block block, @NotNull Block waxedBlock) {
		TextureMapping mapping = new TextureMapping()
			.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"))
			.put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, "_bottom"));

		MultiVariant model = BlockModelGenerators.plainVariant(COPPER_FAN_MODEL.create(block, mapping, generator.modelOutput));
		MultiVariant poweredModel = BlockModelGenerators.plainVariant(COPPER_FAN_POWERED_MODEL.create(block, mapping, generator.modelOutput));

		generator.itemModelOutput.copy(block.asItem(), waxedBlock.asItem());

		dispatchCopperFanStates(generator, block, model, poweredModel);
		dispatchCopperFanStates(generator, waxedBlock, model, poweredModel);
	}

	private static void dispatchCopperFanStates(@NotNull BlockModelGenerators generator, Block block, MultiVariant model, MultiVariant poweredModel) {
		generator.blockStateOutput.accept(
			MultiVariantGenerator.dispatch(block)
				.with(
					PropertyDispatch.initial(CopperFanBlock.POWERED)
						.select(false, model)
						.select(true, poweredModel)
				)
				.with(BlockModelGenerators.ROTATION_FACING)
		);
	}


	private static void generateCopperHorn(@NotNull ItemModelGenerators generator, Item item) {
		ItemModel.Unbaked unbaked = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item));
		ItemModel.Unbaked unbaked2 = ItemModelUtils.plainModel(TCAConstants.id("item/copper_horn_tooting"));
		generator.generateBooleanDispatch(item, ItemModelUtils.isUsingItem(), unbaked2, unbaked);
	}

	private static void createCopperButton(@NotNull BlockModelGenerators generator, @NotNull Block block, @NotNull Block waxedBlock, Block originalBlock) {
		final TextureMapping mapping = TextureMapping.defaultTexture(originalBlock);
		MultiVariant model = BlockModelGenerators.plainVariant(ModelTemplates.BUTTON.create(block, mapping, generator.modelOutput));
		MultiVariant pressedModel = BlockModelGenerators.plainVariant(ModelTemplates.BUTTON_PRESSED.create(block, mapping, generator.modelOutput));

		generator.blockStateOutput.accept(BlockModelGenerators.createButton(block, model, pressedModel));
		generator.blockStateOutput.accept(BlockModelGenerators.createButton(waxedBlock, model, pressedModel));

		generator.itemModelOutput.copy(block.asItem(), waxedBlock.asItem());
		final ResourceLocation itemModel = ModelTemplates.BUTTON_INVENTORY.create(block, mapping, generator.modelOutput);
		generator.registerSimpleItemModel(block, itemModel);
	}
}
