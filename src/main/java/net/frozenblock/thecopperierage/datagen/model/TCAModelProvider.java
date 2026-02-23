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

package net.frozenblock.thecopperierage.datagen.model;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.block.CopperFanBlock;
import net.frozenblock.thecopperierage.block.GearboxBlock;
import net.frozenblock.thecopperierage.client.renderer.item.properties.select.OxidizedItemsEnabled;
import net.frozenblock.thecopperierage.client.renderer.item.properties.select.WeatherState;
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
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.block.model.VariantMutator;
import static net.minecraft.client.renderer.item.ItemModel.Unbaked;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public final class TCAModelProvider extends FabricModelProvider {
	// GEARBOX
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
	// COPPER FAN
	private static final ModelTemplate COPPER_FAN_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_copper_fan")),
		Optional.empty(),
		TextureSlot.SIDE, TextureSlot.BOTTOM
	);
	private static final ModelTemplate COPPER_FAN_POWERED_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_copper_fan")),
		Optional.of("_powered"),
		TextureSlot.FRONT, TextureSlot.SIDE, TextureSlot.BOTTOM
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
		TCABlocks.CHIME.waxedMapping().forEach((block, waxedBlock) -> createChime(generator, block, waxedBlock));
		TCABlocks.CRATE.waxedMapping().forEach((block, waxedBlock) -> createCopperCrate(generator, block, waxedBlock));

		createCopperButton(generator, TCABlocks.COPPER_BUTTON.unaffected(), TCABlocks.COPPER_BUTTON.waxed(), Blocks.COPPER_BLOCK);
		createCopperButton(generator, TCABlocks.COPPER_BUTTON.exposed(), TCABlocks.COPPER_BUTTON.waxedExposed(), Blocks.EXPOSED_COPPER);
		createCopperButton(generator, TCABlocks.COPPER_BUTTON.weathered(), TCABlocks.COPPER_BUTTON.waxedWeathered(), Blocks.WEATHERED_COPPER);
		createCopperButton(generator, TCABlocks.COPPER_BUTTON.oxidized(), TCABlocks.COPPER_BUTTON.waxedOxidized(), Blocks.OXIDIZED_COPPER);
	}

	@Override
	public void generateItemModels(@NotNull ItemModelGenerators generator) {
		generateCopperHorn(generator, TCAItems.COPPER_HORN);
		generator.generateFlatItem(TCAItems.WRENCH, ModelTemplates.FLAT_HANDHELD_ITEM);
		generator.generateFlatItem(TCAItems.MINECART_COUPLING, ModelTemplates.FLAT_ITEM);
		generator.generateFlatItem(TCAItems.CRATE_MINECART, ModelTemplates.FLAT_ITEM);
		generator.generateFlatItem(TCAItems.COPPER_GOLEM_STATUE_MINECART, ModelTemplates.FLAT_ITEM);
		generator.generateFlatItem(TCAItems.JUKEBOX_MINECART, ModelTemplates.FLAT_ITEM);
		generator.generateFlatItem(TCAItems.DISPENSER_MINECART, ModelTemplates.FLAT_ITEM);
		generator.generateFlatItem(TCAItems.DROPPER_MINECART, ModelTemplates.FLAT_ITEM);
	}

	private static void createCopperFire(@NotNull BlockModelGenerators generator) {
		final MultiVariant floorModels = generator.createFloorFireModels(TCABlocks.COPPER_FIRE);
		final MultiVariant sideModels = generator.createSideFireModels(TCABlocks.COPPER_FIRE);
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
		final TextureMapping mapping = new TextureMapping()
			.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"))
			.put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_top"));
		final MultiVariant model = BlockModelGenerators.plainVariant(GEARBOX_MODEL.create(block, mapping, generator.modelOutput));

		final TextureMapping counterMapping = new TextureMapping()
			.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side_counter_clockwise"))
			.put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_top_counter_clockwise"));
		final MultiVariant counterModel = BlockModelGenerators.plainVariant(GEARBOX_COUNTER_CLOCKWISE_MODEL.create(block, counterMapping, generator.modelOutput));

		final TextureMapping clockwiseMapping = new TextureMapping()
			.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side_clockwise"))
			.put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_top_clockwise"));
		final MultiVariant clockwiseModel = BlockModelGenerators.plainVariant(GEARBOX_CLOCKWISE_MODEL.create(block, clockwiseMapping, generator.modelOutput));

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
		final TextureMapping mapping = new TextureMapping()
			.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"))
			.put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, "_bottom"));
		final TextureMapping poweredMapping = new TextureMapping()
			.put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, "_top_powered"))
			.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"))
			.put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, "_bottom"));

		final MultiVariant model = BlockModelGenerators.plainVariant(COPPER_FAN_MODEL.create(block, mapping, generator.modelOutput));
		final MultiVariant poweredModel = BlockModelGenerators.plainVariant(COPPER_FAN_POWERED_MODEL.create(block, poweredMapping, generator.modelOutput));

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

	public static void createChime(@NotNull BlockModelGenerators generator, Block block, Block waxed) {
		final MultiVariant model = generator.createParticleOnlyBlockModel(block, block);
		generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, model));
		generator.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(waxed, model));

		generator.itemModelOutput.copy(block.asItem(), waxed.asItem());
		generator.registerSimpleFlatItemModel(block);
	}

	private static void createCopperCrate(BlockModelGenerators generator, Block block, Block waxed) {
		final ResourceLocation topOpenTexture = TextureMapping.getBlockTexture(block, "_top_open");
		final MultiVariant model = BlockModelGenerators.plainVariant(TexturedModel.CUBE_TOP_BOTTOM.create(block, generator.modelOutput));
		final MultiVariant openModel = BlockModelGenerators.plainVariant(
			TexturedModel.CUBE_TOP_BOTTOM
				.get(block)
				.updateTextures(textureMapping -> textureMapping.put(TextureSlot.TOP, topOpenTexture))
				.createWithSuffix(block, "_open", generator.modelOutput)
		);

		dispatchCopperCrate(generator, block, model, openModel);
		dispatchCopperCrate(generator, waxed, model, openModel);
		generator.itemModelOutput.copy(block.asItem(), waxed.asItem());
	}

	private static void dispatchCopperCrate(BlockModelGenerators generator, Block block, MultiVariant model, MultiVariant openModel) {
		generator.blockStateOutput.accept(
			MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(BlockStateProperties.OPEN).select(false, model).select(true, openModel))
				.with(BlockModelGenerators.ROTATIONS_COLUMN_WITH_FACING)
		);
	}

	private static void createCopperButton(@NotNull BlockModelGenerators generator, @NotNull Block block, @NotNull Block waxedBlock, Block originalBlock) {
		createCopperButton(generator, block, waxedBlock, originalBlock, ModelTemplates.BUTTON, ModelTemplates.BUTTON_PRESSED, ModelTemplates.BUTTON_INVENTORY);
	}

	static void createCopperButton(
		@NotNull BlockModelGenerators generator,
		@NotNull Block block,
		@NotNull Block waxedBlock,
		Block originalBlock,
		@NotNull ModelTemplate modelTemplate,
		@NotNull ModelTemplate pressedTemplate,
		@NotNull ModelTemplate inventoryTemplate
	) {
		final TextureMapping mapping = TextureMapping.defaultTexture(originalBlock);
		final MultiVariant model = BlockModelGenerators.plainVariant(modelTemplate.create(block, mapping, generator.modelOutput));
		final MultiVariant pressedModel = BlockModelGenerators.plainVariant(pressedTemplate.create(block, mapping, generator.modelOutput));

		generator.blockStateOutput.accept(BlockModelGenerators.createButton(block, model, pressedModel));
		generator.blockStateOutput.accept(BlockModelGenerators.createButton(waxedBlock, model, pressedModel));

		generator.itemModelOutput.copy(block.asItem(), waxedBlock.asItem());
		generator.registerSimpleItemModel(block, inventoryTemplate.create(block, mapping, generator.modelOutput));
	}

	private static void generateCopperHorn(@NotNull ItemModelGenerators generator, Item item) {
		final Unbaked model = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item));
		final Unbaked tooting = ItemModelUtils.plainModel(TCAConstants.id("item/copper_horn_tooting"));
		generator.generateBooleanDispatch(item, ItemModelUtils.isUsingItem(), tooting, model);
	}

	@Contract("_, _, _, _ -> new")
	public static Unbaked createOxidizableDispatch(Unbaked unaffected, Unbaked exposed, Unbaked weathered, Unbaked oxidized) {
		return ItemModelUtils.select(
			new OxidizedItemsEnabled(),
			unaffected,
			ItemModelUtils.when(
				true,
				ItemModelUtils.select(
					new WeatherState(),
					unaffected,
					ItemModelUtils.when(WeatheringCopper.WeatherState.EXPOSED, exposed),
					ItemModelUtils.when(WeatheringCopper.WeatherState.WEATHERED, weathered),
					ItemModelUtils.when(WeatheringCopper.WeatherState.OXIDIZED, oxidized)
				)
			)
		);
	}
}
