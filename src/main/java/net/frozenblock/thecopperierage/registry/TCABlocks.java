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
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.TCAFeatureFlags;
import net.frozenblock.thecopperierage.block.CopperButtonBlock;
import net.frozenblock.thecopperierage.block.CopperFanBlock;
import net.frozenblock.thecopperierage.block.CopperFireBlock;
import net.frozenblock.thecopperierage.block.CopperPressurePlateBlock;
import net.frozenblock.thecopperierage.block.GearboxBlock;
import net.frozenblock.thecopperierage.block.RedstonePumpkinBlock;
import net.frozenblock.thecopperierage.block.WeatheringCopperButtonBlock;
import net.frozenblock.thecopperierage.block.WeatheringCopperPressurePlateBlock;
import net.frozenblock.thecopperierage.block.WeatheringCopperFanBlock;
import net.frozenblock.thecopperierage.block.WeatheringGearboxBlock;
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
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import java.util.function.Function;

public final class TCABlocks {

	public static final CopperFireBlock COPPER_FIRE = register("copper_fire",
		CopperFireBlock::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_LIGHT_GREEN)
			.lightLevel(state -> 15)
			.noCollision()
	);

	public static final CampfireBlock COPPER_CAMPFIRE = register("copper_campfire",
		properties -> new CampfireBlock(true, 1, properties),
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.PODZOL)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2F)
			.sound(SoundType.WOOD)
			.lightLevel(Blocks.litBlockEmission(15))
			.noOcclusion()
			.ignitedByLava()
	);

	public static final CarvedPumpkinBlock COPPER_JACK_O_LANTERN = register("copper_jack_o_lantern",
		CarvedPumpkinBlock::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_ORANGE)
			.strength(1.0F)
			.sound(SoundType.WOOD)
			.lightLevel(blockStatex -> 15)
			.isValidSpawn(Blocks::always)
			.pushReaction(PushReaction.DESTROY)
	);

	public static final RedstonePumpkinBlock REDSTONE_JACK_O_LANTERN = register("redstone_jack_o_lantern",
		RedstonePumpkinBlock::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_ORANGE)
			.strength(1.0F)
			.sound(SoundType.WOOD)
			.lightLevel(blockStatex -> 7)
			.isValidSpawn(Blocks::always)
			.pushReaction(PushReaction.DESTROY)
	);

	public static final WeatheringCopperBlocks GEARBOX = WeatheringCopperBlocks.create(
		"gearbox",
		TCABlocks::register,
		GearboxBlock::new,
		WeatheringGearboxBlock::new,
		(weatherState) -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.STONE)
			.strength(1.5F)
			.isRedstoneConductor(Blocks::never)
	);

	public static final WeatheringCopperBlocks COPPER_FAN = WeatheringCopperBlocks.create(
		"copper_fan",
		TCABlocks::register,
		CopperFanBlock::new,
		WeatheringCopperFanBlock::new,
		(weatherState) -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.STONE)
			.strength(1.5F)
			.isRedstoneConductor(Blocks::never)
	);

	public static final WeatheringCopperBlocks COPPER_BUTTON = TCABlocksHelper.create(
		"copper_button",
		TCABlocks::register,
		CopperButtonBlock.Waxed::new,
		WeatheringCopperButtonBlock::new,
		(weatherState) -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.NONE)
			.strength(0.5F)
			.noCollision()
			.pushReaction(PushReaction.DESTROY)
	);

	public static final WeatheringCopperBlocks COPPER_PRESSURE_PLATE = TCABlocksHelper.create(
		"copper_pressure_plate",
		TCABlocks::register,
		CopperPressurePlateBlock.Waxed::new,
		WeatheringCopperPressurePlateBlock::new,
		(weatherState) -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.NONE)
			.strength(0.5F)
			.noCollision()
			.pushReaction(PushReaction.DESTROY)
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

		BlockEntityType.CAMPFIRE.addSupportedBlock(TCABlocks.COPPER_CAMPFIRE);

		OxidizableBlocksRegistry.registerCopperBlockSet(GEARBOX);
		OxidizableBlocksRegistry.registerCopperBlockSet(COPPER_FAN);
		OxidizableBlocksRegistry.registerCopperBlockSet(COPPER_BUTTON);

		registerFlammability();
		registerFuels();
		registerBonemeal();
		registerAxe();
	}

	private static void registerDispenses() {
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
