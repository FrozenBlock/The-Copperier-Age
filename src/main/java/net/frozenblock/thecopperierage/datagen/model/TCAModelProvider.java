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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.block.CopperFanBlock;
import net.frozenblock.thecopperierage.block.GearboxBlock;
import net.frozenblock.thecopperierage.client.renderer.item.properties.select.OxidizedItemsEnabled;
import net.frozenblock.thecopperierage.item.api.OxidizableItemHelper;
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
import static net.minecraft.client.renderer.item.ItemModel.Unbaked;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.renderer.item.properties.numeric.Damage;
import net.minecraft.client.renderer.item.properties.numeric.UseCycle;
import net.minecraft.client.renderer.item.properties.select.TrimMaterialProperty;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
	// BRUSH
	public static final ModelTemplate BRUSH = ModelTemplates.createItem("brush", TextureSlot.LAYER0);
	public static final ModelTemplate BRUSH_BRUSHING_0 = ModelTemplates.createItem("brush_brushing_0", TextureSlot.LAYER0);
	public static final ModelTemplate BRUSH_BRUSHING_1 = ModelTemplates.createItem("brush_brushing_1", TextureSlot.LAYER0);
	public static final ModelTemplate BRUSH_BRUSHING_2 = ModelTemplates.createItem("brush_brushing_2", TextureSlot.LAYER0);

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

		createCopperButton(generator, TCABlocks.COPPER_BUTTON.unaffected(), TCABlocks.COPPER_BUTTON.waxed(), Blocks.COPPER_BLOCK);
		createCopperButton(generator, TCABlocks.COPPER_BUTTON.exposed(), TCABlocks.COPPER_BUTTON.waxedExposed(), Blocks.EXPOSED_COPPER);
		createCopperButton(generator, TCABlocks.COPPER_BUTTON.weathered(), TCABlocks.COPPER_BUTTON.waxedWeathered(), Blocks.WEATHERED_COPPER);
		createCopperButton(generator, TCABlocks.COPPER_BUTTON.oxidized(), TCABlocks.COPPER_BUTTON.waxedOxidized(), Blocks.OXIDIZED_COPPER);
	}

	@Override
	public void generateItemModels(@NotNull ItemModelGenerators generator) {
		generateCopperHorn(generator, TCAItems.COPPER_HORN);

		generateOxidizableItem(generator, TCAItems.WRENCH, ModelTemplates.FLAT_HANDHELD_ITEM, true);
		generateOxidizableBrush(generator);

		generateOxidizableItem(generator, Items.COPPER_AXE, ModelTemplates.FLAT_HANDHELD_ITEM, false);
		generateOxidizableItem(generator, Items.COPPER_HOE, ModelTemplates.FLAT_HANDHELD_ITEM, false);
		generateOxidizableItem(generator, Items.COPPER_PICKAXE, ModelTemplates.FLAT_HANDHELD_ITEM, false);
		generateOxidizableItem(generator, Items.COPPER_SHOVEL, ModelTemplates.FLAT_HANDHELD_ITEM, false);
		generateOxidizableItem(generator, Items.COPPER_SWORD, ModelTemplates.FLAT_HANDHELD_ITEM, false);

		generateOxidizableTrimmableItem(generator, Items.COPPER_HELMET, EquipmentAssets.COPPER, ItemModelGenerators.TRIM_PREFIX_HELMET);
		generateOxidizableTrimmableItem(generator, Items.COPPER_CHESTPLATE, EquipmentAssets.COPPER, ItemModelGenerators.TRIM_PREFIX_CHESTPLATE);
		generateOxidizableTrimmableItem(generator, Items.COPPER_LEGGINGS, EquipmentAssets.COPPER, ItemModelGenerators.TRIM_PREFIX_LEGGINGS);
		generateOxidizableTrimmableItem(generator, Items.COPPER_BOOTS, EquipmentAssets.COPPER, ItemModelGenerators.TRIM_PREFIX_BOOTS);
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

	private static void generateOxidizableItem(@NotNull ItemModelGenerators generator, Item item, ModelTemplate template, boolean generateFirstModel) {
		final Unbaked unaffected = generateFirstModel
			? ItemModelUtils.plainModel(generator.createFlatItemModel(item, template))
			: ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item));
		final Unbaked exposed = ItemModelUtils.plainModel(createFlatItemModelWithTCANamespace(generator, item, "_exposed", template));
		final Unbaked weathered = ItemModelUtils.plainModel(createFlatItemModelWithTCANamespace(generator, item, "_weathered", template));
		final Unbaked oxidized = ItemModelUtils.plainModel(createFlatItemModelWithTCANamespace(generator, item, "_oxidized", template));

		generator.itemModelOutput.accept(item, createOxidizableDispatch(unaffected, exposed, weathered, oxidized));
	}

	private static void generateOxidizableBrush(@NotNull ItemModelGenerators generator) {
		generator.itemModelOutput
			.accept(
				Items.BRUSH,
				ItemModelUtils.rangeSelect(
					new UseCycle(10F),
					0.1F,
					createOxidizableBrushDispatch(generator, BRUSH, ""),
					ItemModelUtils.override(createOxidizableBrushDispatch(generator, BRUSH_BRUSHING_0, "_brushing_0"), 0.25F),
					ItemModelUtils.override(createOxidizableBrushDispatch(generator, BRUSH_BRUSHING_1, "_brushing_1"), 0.5F),
					ItemModelUtils.override(createOxidizableBrushDispatch(generator, BRUSH_BRUSHING_2, "_brushing_2"), 0.75F)
				)
			);
	}

	@Contract("_, _, _ -> new")
	private static @NotNull Unbaked createOxidizableBrushDispatch(@NotNull ItemModelGenerators generator, @NotNull ModelTemplate modelTemplate, String suffix) {
		return createOxidizableDispatch(
			ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(Items.BRUSH, suffix)),
			ItemModelUtils.plainModel(
				modelTemplate.create(
					getItemModelWithTCANamespace(Items.BRUSH, "_exposed" + suffix),
					TextureMapping.layer0(getItemTextureWithTCANamespace(Items.BRUSH, "_exposed")),
					generator.modelOutput
				)
			),
			ItemModelUtils.plainModel(
				modelTemplate.create(
					getItemModelWithTCANamespace(Items.BRUSH, "_weathered" + suffix),
					TextureMapping.layer0(getItemTextureWithTCANamespace(Items.BRUSH, "_weathered")),
					generator.modelOutput
				)
			),
			ItemModelUtils.plainModel(
				modelTemplate.create(
					getItemModelWithTCANamespace(Items.BRUSH, "_oxidized" + suffix),
					TextureMapping.layer0(getItemTextureWithTCANamespace(Items.BRUSH, "_oxidized")),
					generator.modelOutput
				)
			)
		);
	}

	private static void generateOxidizableTrimmableItem(
		@NotNull ItemModelGenerators generator,
		Item item,
		ResourceKey<EquipmentAsset> equipmentAsset,
		ResourceLocation resourceLocation
	) {
		final ResourceLocation vanillaModel = ModelLocationUtils.getModelLocation(item);
		final ResourceLocation tcaModel = getItemModelWithTCANamespace(item, "");
		final ResourceLocation exposedTexture = getItemTextureWithTCANamespace(item, "_exposed");
		final ResourceLocation weatheredTexture = getItemTextureWithTCANamespace(item, "_weathered");
		final ResourceLocation oxidizedTexture = getItemTextureWithTCANamespace(item, "_oxidized");

		final int trimMaterialListSize = ItemModelGenerators.TRIM_MATERIAL_MODELS.size();
		final List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> unaffectedList = new ArrayList<>(trimMaterialListSize);
		final List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> exposedList = new ArrayList<>(trimMaterialListSize);
		final List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> weatheredList = new ArrayList<>(trimMaterialListSize);
		final List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> oxidizedList = new ArrayList<>(trimMaterialListSize);
		for (ItemModelGenerators.TrimMaterialData trimMaterialData : ItemModelGenerators.TRIM_MATERIAL_MODELS) {
			final ResourceKey<TrimMaterial> materialKey = trimMaterialData.materialKey();
			final ResourceLocation trimmedTexture = resourceLocation.withSuffix("_" + trimMaterialData.assets().assetId(equipmentAsset).suffix());

			final ResourceLocation unaffectedModel = vanillaModel.withSuffix("_" + trimMaterialData.assets().base().suffix() + "_trim");
			final Unbaked unaffected = ItemModelUtils.plainModel(unaffectedModel);
			unaffectedList.add(ItemModelUtils.when(materialKey, unaffected));

			final ResourceLocation exposedModel = tcaModel.withSuffix("_exposed_" + trimMaterialData.assets().base().suffix() + "_trim");
			final Unbaked exposed = ItemModelUtils.plainModel(exposedModel);
			generator.generateLayeredItem(exposedModel, exposedTexture, trimmedTexture);
			exposedList.add(ItemModelUtils.when(materialKey, exposed));

			final ResourceLocation weatheredModel = tcaModel.withSuffix("_weathered_" + trimMaterialData.assets().base().suffix() + "_trim");
			final Unbaked weathered = ItemModelUtils.plainModel(weatheredModel);
			generator.generateLayeredItem(weatheredModel, weatheredTexture, trimmedTexture);
			weatheredList.add(ItemModelUtils.when(materialKey, weathered));

			final ResourceLocation oxidizedModel = tcaModel.withSuffix("_oxidized_" + trimMaterialData.assets().base().suffix() + "_trim");
			final Unbaked oxidized = ItemModelUtils.plainModel(oxidizedModel);
			generator.generateLayeredItem(oxidizedModel, oxidizedTexture, trimmedTexture);
			oxidizedList.add(ItemModelUtils.when(materialKey, oxidized));
		}

		final Unbaked unaffected = ItemModelUtils.plainModel(vanillaModel);
		final Unbaked exposed = ItemModelUtils.plainModel(createFlatItemModelWithTCANamespace(generator, item, "_exposed", ModelTemplates.FLAT_ITEM));
		final Unbaked weathered = ItemModelUtils.plainModel(createFlatItemModelWithTCANamespace(generator, item, "_weathered", ModelTemplates.FLAT_ITEM));
		final Unbaked oxidized = ItemModelUtils.plainModel(createFlatItemModelWithTCANamespace(generator, item, "_oxidized", ModelTemplates.FLAT_ITEM));

		generator.itemModelOutput.accept(
			item,
			createOxidizableDispatch(
				ItemModelUtils.select(new TrimMaterialProperty(), unaffected, unaffectedList),
				ItemModelUtils.select(new TrimMaterialProperty(), exposed, exposedList),
				ItemModelUtils.select(new TrimMaterialProperty(), weathered, weatheredList),
				ItemModelUtils.select(new TrimMaterialProperty(), oxidized, oxidizedList)
			)
		);
	}

	private static @NotNull ResourceLocation createFlatItemModelWithTCANamespace(
		@NotNull ItemModelGenerators generator,
		Item item,
		String suffix,
		@NotNull ModelTemplate modelTemplate
	) {
		return modelTemplate.create(getItemModelWithTCANamespace(item, suffix), TextureMapping.layer0(getItemTextureWithTCANamespace(item, suffix)), generator.modelOutput);
	}

	private static @NotNull ResourceLocation getItemModelWithTCANamespace(Item item, String suffix) {
		return TCAConstants.id(ModelLocationUtils.getModelLocation(item, suffix).getPath());
	}

	private static @NotNull ResourceLocation getItemTextureWithTCANamespace(Item item, String suffix) {
		return TCAConstants.id(TextureMapping.getItemTexture(item, suffix).getPath());
	}

	@Contract("_, _, _, _ -> new")
	private static @NotNull Unbaked createOxidizableDispatch(Unbaked unaffected, Unbaked exposed, Unbaked weathered, Unbaked oxidized) {
		return ItemModelUtils.select(
			new OxidizedItemsEnabled(),
			unaffected,
			ItemModelUtils.when(
				true,
				ItemModelUtils.rangeSelect(
					new Damage(true),
					unaffected,
					ItemModelUtils.override(exposed, OxidizableItemHelper.EXPOSED_THRESHOLD),
					ItemModelUtils.override(weathered, OxidizableItemHelper.WEATHERED_THRESHOLD),
					ItemModelUtils.override(oxidized, OxidizableItemHelper.OXIDIZED_THRESHOLD)
				)
			)
		);
	}
}
