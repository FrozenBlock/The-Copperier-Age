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
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public final class TCAModelProvider extends FabricModelProvider {

	public TCAModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(@NotNull BlockModelGenerators generator) {
		createCopperFire(generator);
		createGearbox(generator);
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

	private static void createGearbox(@NotNull BlockModelGenerators generator) {
		MultiVariant model = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(TCABlocks.GEARBOX));
		MultiVariant counter = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(TCABlocks.GEARBOX, "_counter_clockwise"));
		MultiVariant clockwise = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(TCABlocks.GEARBOX, "_clockwise"));

		generator.blockStateOutput.accept(
			MultiVariantGenerator.dispatch(TCABlocks.GEARBOX)
				.with(
					PropertyDispatch.initial(GearboxBlock.POWER)
						.select(15, counter)
						.select(14, clockwise)
						.select(13, counter)
						.select(12, clockwise)
						.select(11, counter)
						.select(10, clockwise)
						.select(9, counter)
						.select(8, clockwise)
						.select(7, counter)
						.select(6, clockwise)
						.select(5, counter)
						.select(4, clockwise)
						.select(3, counter)
						.select(2, clockwise)
						.select(1, counter)
						.select(0, model)
				)
				.with(BlockModelGenerators.ROTATION_FACING)
		);
	}

	private static void generateCopperHorn(@NotNull ItemModelGenerators generator, Item item) {
		ItemModel.Unbaked unbaked = ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item));
		ItemModel.Unbaked unbaked2 = ItemModelUtils.plainModel(TCAConstants.id("item/copper_horn_tooting"));
		generator.generateBooleanDispatch(item, ItemModelUtils.isUsingItem(), unbaked2, unbaked);
	}
}
