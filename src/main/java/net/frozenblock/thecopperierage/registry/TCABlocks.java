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

import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.frozenblock.thecopperierage.TCAConstants;
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
import net.frozenblock.thecopperierage.references.TCABlockIds;
import net.frozenblock.thecopperierage.references.TCABlockItemIds;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperCollection;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public final class TCABlocks {
	public static final Block COPPER_FIRE = Blocks.register(TCABlockIds.COPPER_FIRE,
		CopperFireBlock::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_LIGHT_GREEN)
			.lightLevel(state -> 15)
			.noCollision()
			.randomTicks()
	);

	public static final Block COPPER_CAMPFIRE = Blocks.register(TCABlockItemIds.COPPER_CAMPFIRE,
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

	public static final Block COPPER_JACK_O_LANTERN = Blocks.register(TCABlockItemIds.COPPER_JACK_O_LANTERN,
		CarvedPumpkinBlock::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_ORANGE)
			.strength(1F)
			.sound(SoundType.WOOD)
			.lightLevel(state -> 15)
			.isValidSpawn(Blocks::always)
			.pushReaction(PushReaction.DESTROY)
	);

	public static final Block REDSTONE_JACK_O_LANTERN = Blocks.register(TCABlockItemIds.REDSTONE_JACK_O_LANTERN,
		RedstonePumpkinBlock::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_ORANGE)
			.strength(1F)
			.sound(SoundType.WOOD)
			.lightLevel(state -> 7)
			.isValidSpawn(Blocks::always)
			.pushReaction(PushReaction.DESTROY)
			.isRedstoneConductor(Blocks::never)
	);

	public static final Block REDSTONE_GRIT = Blocks.register(TCABlockItemIds.REDSTONE_GRIT,
		properties -> new RedstoneGritBlock(new ColorRGBA(0xe3001a), properties),
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_RED)
			.strength(1F)
			.sound(SoundType.SAND)
			.isValidSpawn(Blocks::always)
			.pushReaction(PushReaction.NORMAL)
			.isRedstoneConductor(Blocks::never)
	);

	public static final WeatheringCopperCollection<Block> GEARBOX = WeatheringCopperCollection.registerBlocks(
		TCABlockItemIds.GEARBOX,
		Blocks::register,
		(weatherState, properties) -> new GearboxBlock(properties),
		WeatheringGearboxBlock::new,
		weatherState -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.STONE)
			.strength(1.5F)
			.isRedstoneConductor(Blocks::never)
	);

	public static final WeatheringCopperCollection<Block> STICKY_GEARBOX = WeatheringCopperCollection.registerBlocks(
		TCABlockItemIds.STICKY_GEARBOX,
		Blocks::register,
		(weatherState, properties) -> new StickyGearboxBlock(properties),
		WeatheringStickyGearboxBlock::new,
		weatherState -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.STONE)
			.strength(1.5F)
			.isRedstoneConductor(Blocks::never)
	);

	public static final WeatheringCopperCollection<Block> COPPER_FAN = WeatheringCopperCollection.registerBlocks(
		TCABlockItemIds.COPPER_FAN,
		Blocks::register,
		CopperFanBlock::new,
		WeatheringCopperFanBlock::new,
		weatherState -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.STONE)
			.strength(1.5F)
			.isValidSpawn(Blocks::never)
			.isRedstoneConductor(Blocks::never)
			.isSuffocating(Blocks::never)
	);

	public static final WeatheringCopperCollection<Block> CHIME = WeatheringCopperCollection.registerBlocks(
		TCABlockItemIds.CHIME,
		Blocks::register,
		(weatherState, properties) -> new ChimeBlock(properties),
		WeatheringChimeBlock::new,
		weatherState -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.METAL)
			.requiresCorrectToolForDrops()
			.strength(5F, 6F)
			.sound(TCASounds.CHIME)
			.noOcclusion()
	);

	public static final Block CRATE = Blocks.register(TCABlockItemIds.CRATE,
		CrateBlock::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.WOOD)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.5F)
			.sound(SoundType.WOOD)
			.ignitedByLava()
	);

	public static final WeatheringCopperCollection<Block> COPPER_BUTTON = WeatheringCopperCollection.registerBlocks(
		TCABlockItemIds.COPPER_BUTTON,
		Blocks::register,
		CopperButtonBlock::new,
		WeatheringCopperButtonBlock::new,
		weatherState -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.NONE)
			.strength(0.5F)
			.noCollision()
			.pushReaction(PushReaction.DESTROY)
	);

	public static final WeatheringCopperCollection<Block> WEIGHTED_PRESSURE_PLATE = WeatheringCopperCollection.registerBlocks(
		TCABlockItemIds.WEIGHTED_PRESSURE_PLATE,
		Blocks::register,
		CopperPressurePlateBlock::new,
		WeatheringCopperPressurePlateBlock::new,
		weatherState -> BlockBehaviour.Properties.of()
			.mapColor(getMapColorForWeatherState(weatherState))
			.strength(0.5F)
			.noCollision()
			.pushReaction(PushReaction.DESTROY)
	);

	public static void init() {
		TCAConstants.logWithModId("Registering Blocks for", TCAConstants.UNSTABLE_LOGGING);
	}

	public static void registerBlockProperties() {
		BlockEntityTypes.CAMPFIRE.addValidBlock(TCABlocks.COPPER_CAMPFIRE);

		OxidizableBlocksRegistry.registerWeatheringCopperBlocks(GEARBOX);
		OxidizableBlocksRegistry.registerWeatheringCopperBlocks(STICKY_GEARBOX);
		OxidizableBlocksRegistry.registerWeatheringCopperBlocks(COPPER_FAN);
		OxidizableBlocksRegistry.registerWeatheringCopperBlocks(CHIME);
		OxidizableBlocksRegistry.registerWeatheringCopperBlocks(COPPER_BUTTON);
		OxidizableBlocksRegistry.registerWeatheringCopperBlocks(WEIGHTED_PRESSURE_PLATE);
	}

	public static MapColor getMapColorForWeatherState(WeatheringCopper.WeatherState weatherState) {
		if (weatherState == WeatheringCopper.WeatherState.UNAFFECTED) return MapColor.COLOR_ORANGE;
		if (weatherState == WeatheringCopper.WeatherState.EXPOSED) return MapColor.TERRACOTTA_LIGHT_GRAY;
		if (weatherState == WeatheringCopper.WeatherState.WEATHERED) return MapColor.WARPED_STEM;
		if (weatherState == WeatheringCopper.WeatherState.OXIDIZED) return MapColor.WARPED_NYLIUM;
		return MapColor.NONE;
	}

}
