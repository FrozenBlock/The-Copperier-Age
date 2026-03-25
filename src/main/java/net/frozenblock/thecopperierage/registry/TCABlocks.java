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

package net.frozenblock.thecopperierage.registry;

import java.util.function.BiFunction;
import java.util.function.Function;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.TCAFeatureFlags;
import net.frozenblock.thecopperierage.block.ChimeBlock;
import net.frozenblock.thecopperierage.block.CopperButtonBlock;
import net.frozenblock.thecopperierage.block.CopperFanBlock;
import net.frozenblock.thecopperierage.block.CopperFireBlock;
import net.frozenblock.thecopperierage.block.CopperPressurePlateBlock;
import net.frozenblock.thecopperierage.block.CrateBlock;
import net.frozenblock.thecopperierage.block.GearboxBlock;
import net.frozenblock.thecopperierage.block.RedstoneGritBlock;
import net.frozenblock.thecopperierage.block.RedstonePumpkinBlock;
import net.frozenblock.thecopperierage.block.StickyGearboxBlock;
import net.frozenblock.thecopperierage.block.WeatheringChimeBlock;
import net.frozenblock.thecopperierage.block.WeatheringCopperButtonBlock;
import net.frozenblock.thecopperierage.block.WeatheringCopperFanBlock;
import net.frozenblock.thecopperierage.block.WeatheringCopperPressurePlateBlock;
import net.frozenblock.thecopperierage.block.WeatheringGearboxBlock;
import net.frozenblock.thecopperierage.block.WeatheringStickyGearboxBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.apache.commons.lang3.function.TriFunction;

public final class TCABlocks {
	public static final CopperFireBlock COPPER_FIRE = registerWithoutItem("copper_fire",
		CopperFireBlock::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_LIGHT_GREEN)
			.lightLevel(state -> 15)
			.noCollision()
			.randomTicks()
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
			.strength(1F)
			.sound(SoundType.WOOD)
			.lightLevel(blockStatex -> 15)
			.isValidSpawn(Blocks::always)
			.pushReaction(PushReaction.DESTROY)
	);

	public static final RedstonePumpkinBlock REDSTONE_JACK_O_LANTERN = register("redstone_jack_o_lantern",
		RedstonePumpkinBlock::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_ORANGE)
			.strength(1F)
			.sound(SoundType.WOOD)
			.lightLevel(blockStatex -> 7)
			.isValidSpawn(Blocks::always)
			.pushReaction(PushReaction.DESTROY)
			.isRedstoneConductor(Blocks::never)
	);

	public static final RedstoneGritBlock REDSTONE_GRIT = register("redstone_grit",
		properties -> new RedstoneGritBlock(new ColorRGBA(0xe3001a), properties),
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_RED)
			.strength(1F)
			.sound(SoundType.SAND)
			.isValidSpawn(Blocks::always)
			.pushReaction(PushReaction.NORMAL)
			.isRedstoneConductor(Blocks::never)
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

	public static final WeatheringCopperBlocks STICKY_GEARBOX = WeatheringCopperBlocks.create(
		"sticky_gearbox",
		TCABlocks::register,
		StickyGearboxBlock::new,
		WeatheringStickyGearboxBlock::new,
		(weatherState) -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.STONE)
			.strength(1.5F)
			.isRedstoneConductor(Blocks::never)
	);

	public static final WeatheringCopperBlocks COPPER_FAN = createWeatheringCopperSet(
		"copper_fan",
		TCABlocks::register,
		CopperFanBlock::new,
		WeatheringCopperFanBlock::new,
		(weatherState) -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.STONE)
			.strength(1.5F)
			.isValidSpawn(Blocks::never)
			.isRedstoneConductor(Blocks::never)
			.isSuffocating(Blocks::never)
	);

	public static final WeatheringCopperBlocks CHIME = WeatheringCopperBlocks.create(
		"chime",
		TCABlocks::register,
		ChimeBlock::new,
		WeatheringChimeBlock::new,
		(weatherState) -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.METAL)
			.requiresCorrectToolForDrops()
			.strength(5F, 6F)
			.sound(TCASounds.CHIME)
			.noOcclusion()
	);

	public static final CrateBlock CRATE = registerWithContainerComponentItem(
		"crate",
		CrateBlock::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.WOOD)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.5F)
			.sound(SoundType.WOOD)
			.ignitedByLava()
	);

	public static final WeatheringCopperBlocks COPPER_BUTTON = createWeatheringCopperSet(
		"copper_button",
		TCABlocks::register,
		CopperButtonBlock::new,
		WeatheringCopperButtonBlock::new,
		(weatherState) -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.NONE)
			.strength(0.5F)
			.noCollision()
			.pushReaction(PushReaction.DESTROY)
	);

	public static final WeatheringCopperBlocks WEIGHTED_PRESSURE_PLATE = createWeatheringCopperSet(
		"weighted_pressure_plate",
		TCABlocks::register,
		CopperPressurePlateBlock::new,
		WeatheringCopperPressurePlateBlock::new,
		(weatherState) -> BlockBehaviour.Properties.of()
			.mapColor(getMapColorForWeatherState(weatherState))
			.strength(0.5F)
			.noCollision()
			.pushReaction(PushReaction.DESTROY)
	);

	private TCABlocks() {
		throw new UnsupportedOperationException("TCABlocks contains only static declarations.");
	}

	public static void init() {
		TCAConstants.logWithModId("Registering Blocks for", TCAConstants.UNSTABLE_LOGGING);
	}

	private static <T extends Block> T registerWithoutItem(String path, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties properties) {
		Identifier id = TCAConstants.id(path);
		properties = properties.requiredFeatures(TCAFeatureFlags.FEATURE_FLAG);
		return doRegister(id, makeBlock(block, properties, id));
	}

	private static <T extends Block> T register(String path, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties properties) {
		T registered = registerWithoutItem(path, block, properties);
		Items.registerBlock(registered);
		return registered;
	}

	private static <T extends Block> T registerWithContainerComponentItem(String path, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties properties) {
		T registered = registerWithoutItem(path, block, properties);
		Items.registerBlock(registered, new Item.Properties().component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
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

	private static <T extends Block> T doRegister(Identifier id, T block) {
		if (BuiltInRegistries.BLOCK.getOptional(id).isEmpty()) {
			return Registry.register(BuiltInRegistries.BLOCK, id, block);
		}
		throw new IllegalArgumentException("Block with id " + id + " is already in the block registry.");
	}

	private static <T extends Block> T makeBlock(Function<BlockBehaviour.Properties, T> function, BlockBehaviour.Properties properties, Identifier id) {
		return function.apply(properties.setId(ResourceKey.create(Registries.BLOCK, id)));
	}

	public static void registerBlockProperties() {
		registerDispenses();

		BlockEntityType.CAMPFIRE.addValidBlock(TCABlocks.COPPER_CAMPFIRE);

		OxidizableBlocksRegistry.registerWeatheringCopperBlocks(GEARBOX);
		OxidizableBlocksRegistry.registerWeatheringCopperBlocks(STICKY_GEARBOX);
		OxidizableBlocksRegistry.registerWeatheringCopperBlocks(COPPER_FAN);
		OxidizableBlocksRegistry.registerWeatheringCopperBlocks(CHIME);
		OxidizableBlocksRegistry.registerWeatheringCopperBlocks(COPPER_BUTTON);
		OxidizableBlocksRegistry.registerWeatheringCopperBlocks(WEIGHTED_PRESSURE_PLATE);
	}

	private static void registerDispenses() {
	}

	public static MapColor getMapColorForWeatherState(WeatheringCopper.WeatherState weatherState) {
		if (weatherState == WeatheringCopper.WeatherState.UNAFFECTED) return MapColor.COLOR_ORANGE;
		if (weatherState == WeatheringCopper.WeatherState.EXPOSED) return MapColor.TERRACOTTA_LIGHT_GRAY;
		if (weatherState == WeatheringCopper.WeatherState.WEATHERED) return MapColor.WARPED_STEM;
		if (weatherState == WeatheringCopper.WeatherState.OXIDIZED) return MapColor.WARPED_NYLIUM;
		return MapColor.NONE;
	}

	public static <W extends Block> WeatheringCopperBlocks createWeatheringCopperSet(
		String id,
		TriFunction<String, Function<BlockBehaviour.Properties, Block>, BlockBehaviour.Properties, Block> blockCreator,
		BiFunction<WeatheringCopper.WeatherState, BlockBehaviour.Properties, W> waxedBlockCreator,
		BiFunction<WeatheringCopper.WeatherState, BlockBehaviour.Properties, ? extends Block> weatheringBlockCreator,
		Function<WeatheringCopper.WeatherState, BlockBehaviour.Properties> propertiesCreator
	) {
		final Block unaffected = blockCreator.apply(
			id,
			properties -> weatheringBlockCreator.apply(WeatheringCopper.WeatherState.UNAFFECTED, properties),
			propertiesCreator.apply(WeatheringCopper.WeatherState.UNAFFECTED)
		);
		final Block exposed = blockCreator.apply(
			"exposed_" + id,
			properties -> weatheringBlockCreator.apply(WeatheringCopper.WeatherState.EXPOSED, properties),
			propertiesCreator.apply(WeatheringCopper.WeatherState.EXPOSED)
		);
		final Block weathered = blockCreator.apply(
			"weathered_" + id,
			properties -> weatheringBlockCreator.apply(WeatheringCopper.WeatherState.WEATHERED, properties),
			propertiesCreator.apply(WeatheringCopper.WeatherState.WEATHERED)
		);
		final Block oxidized = blockCreator.apply(
			"oxidized_" + id,
			properties -> weatheringBlockCreator.apply(WeatheringCopper.WeatherState.OXIDIZED, properties),
			propertiesCreator.apply(WeatheringCopper.WeatherState.OXIDIZED)
		);

		final Block waxed = blockCreator.apply(
			"waxed_" + id,
			properties -> waxedBlockCreator.apply(WeatheringCopper.WeatherState.UNAFFECTED, properties),
			propertiesCreator.apply(WeatheringCopper.WeatherState.UNAFFECTED)
		);
		final Block waxedExposed = blockCreator.apply(
			"waxed_exposed_" + id,
			properties -> waxedBlockCreator.apply(WeatheringCopper.WeatherState.EXPOSED, properties),
			propertiesCreator.apply(WeatheringCopper.WeatherState.EXPOSED)
		);
		final Block waxedWeathered = blockCreator.apply(
			"waxed_weathered_" + id,
			properties -> waxedBlockCreator.apply(WeatheringCopper.WeatherState.WEATHERED, properties),
			propertiesCreator.apply(WeatheringCopper.WeatherState.WEATHERED)
		);
		final Block waxedOxidized = blockCreator.apply(
			"waxed_oxidized_" + id,
			properties -> waxedBlockCreator.apply(WeatheringCopper.WeatherState.OXIDIZED, properties),
			propertiesCreator.apply(WeatheringCopper.WeatherState.OXIDIZED)
		);

		return new WeatheringCopperBlocks(unaffected, exposed, weathered, oxidized, waxed, waxedExposed, waxedWeathered, waxedOxidized);
	}
}
