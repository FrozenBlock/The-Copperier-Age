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

package net.frozenblock.thecopperierage.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityType;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.TCAFeatureFlags;
import net.frozenblock.thecopperierage.block.CopperFireBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import java.util.function.Function;

public final class TCABlocks {

	public static final CopperFireBlock COPPER_FIRE = register("copper_fire",
		CopperFireBlock::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_LIGHT_GREEN)
			.lightLevel(blockStatex -> 15)
			.noCollision()
	);

	private TCABlocks() {
		throw new UnsupportedOperationException("TCABlocks contains only static declarations.");
	}

	public static void registerBlocks() {
		TCAConstants.logWithModId("Registering Blocks for", TCAConstants.UNSTABLE_LOGGING);
	}

	private static <T extends Block> T registerWithoutItem(String path, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties properties) {
		ResourceLocation id = TCAConstants.id(path);
		properties = properties.requiredFeatures(TCAFeatureFlags.FEATURE_FLAG);
		return doRegister(id, makeBlock(block, properties, id));
	}

	private static <T extends Block> T register(String path, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties properties) {
		T registered = registerWithoutItem(path, block, properties);
		Items.registerBlock(registered);
		return registered;
	}

	private static <T extends Block> T registerWithDoubleHighItem(String path, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties properties) {
		T registered = registerWithoutItem(path, block, properties);
		Items.registerBlock(registered, DoubleHighBlockItem::new);
		return registered;
	}

	private static <T extends Block> T registerWithFireResistantItem(String path, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties properties) {
		T registered = registerWithoutItem(path, block, properties);
		Items.registerBlock(registered, new Item.Properties().fireResistant());
		return registered;
	}

	private static <T extends Block> T doRegister(ResourceLocation id, T block) {
		if (BuiltInRegistries.BLOCK.getOptional(id).isEmpty()) {
			return Registry.register(BuiltInRegistries.BLOCK, id, block);
		}
		throw new IllegalArgumentException("Block with id " + id + " is already in the block registry.");
	}

	private static <T extends Block> T makeBlock(Function<BlockBehaviour.Properties, T> function, BlockBehaviour.Properties properties, ResourceLocation id) {
		return function.apply(properties.setId(ResourceKey.create(Registries.BLOCK, id)));
	}

	public static void registerBlockProperties() {
		registerDispenses();

		var sign = (FabricBlockEntityType) BlockEntityType.SIGN;
		var hangingSign = (FabricBlockEntityType) BlockEntityType.HANGING_SIGN;

		/*
		sign.addSupportedBlock(WILTED_SIGN);
		sign.addSupportedBlock(WILTED_WALL_SIGN);

		hangingSign.addSupportedBlock(WILTED_HANGING_SIGN);
		hangingSign.addSupportedBlock(WILTED_WALL_HANGING_SIGN);
		 */

		registerComposting();
		registerFlammability();
		registerFuels();
		registerBonemeal();
		registerAxe();
	}

	private static void registerDispenses() {
	}


	private static void registerComposting() {

	}

	private static void registerFlammability() {
		TCAConstants.logWithModId("Registering Flammability for", TCAConstants.UNSTABLE_LOGGING);
		var flammableBlockRegistry = FlammableBlockRegistry.getDefaultInstance();

	}

	private static void registerFuels() {
		TCAConstants.logWithModId("Registering Fuels for", TCAConstants.UNSTABLE_LOGGING);
		FuelRegistryEvents.BUILD.register((builder, context) -> {

		});
	}

	private static void registerBonemeal() {

	}

	private static void registerAxe() {

	}
}
