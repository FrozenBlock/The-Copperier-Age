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

import net.frozenblock.lib.block.api.registry.OxidizableBlocksRegistry;
import net.frozenblock.lib.platform.api.registry.DeferredBlock;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.TCAFeatureFlags;
import net.frozenblock.thecopperierage.block.ChimeBlock;
import net.frozenblock.thecopperierage.block.CopperButtonBlock;
import net.frozenblock.thecopperierage.block.CopperCrossRailBlock;
import net.frozenblock.thecopperierage.block.CopperFanBlock;
import net.frozenblock.thecopperierage.block.CopperFireBlock;
import net.frozenblock.thecopperierage.block.CopperPressurePlateBlock;
import net.frozenblock.thecopperierage.block.CopperRailBlock;
import net.frozenblock.thecopperierage.block.CrateBlock;
import net.frozenblock.thecopperierage.block.CrossRailBlock;
import net.frozenblock.thecopperierage.block.GearboxBlock;
import net.frozenblock.thecopperierage.block.RedstoneGritBlock;
import net.frozenblock.thecopperierage.block.RedstonePumpkinBlock;
import net.frozenblock.thecopperierage.block.RelayerRailBlock;
import net.frozenblock.thecopperierage.block.StickyGearboxBlock;
import net.frozenblock.thecopperierage.block.WeatheringChimeBlock;
import net.frozenblock.thecopperierage.block.WeatheringCopperButtonBlock;
import net.frozenblock.thecopperierage.block.WeatheringCopperCrossRailBlock;
import net.frozenblock.thecopperierage.block.WeatheringCopperFanBlock;
import net.frozenblock.thecopperierage.block.WeatheringCopperPressurePlateBlock;
import net.frozenblock.thecopperierage.block.WeatheringCopperRailBlock;
import net.frozenblock.thecopperierage.block.WeatheringGearboxBlock;
import net.frozenblock.thecopperierage.block.WeatheringStickyGearboxBlock;
import net.frozenblock.thecopperierage.block.rotation.WrenchDispenseItemBehavior;
import net.frozenblock.thecopperierage.references.TCABlockIds;
import net.frozenblock.thecopperierage.references.TCABlockItemIds;
import net.minecraft.core.dispenser.MinecartDispenseItemBehavior;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperCollection;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public final class TCABlocks {
	private static final DeferredRegister.Blocks REGISTER = DeferredRegister.createBlocks(TCAConstants.MOD_ID).requiredFeatures(TCAFeatureFlags.FEATURE_FLAG);

	public static final DeferredBlock<Block> COPPER_FIRE = REGISTER.registerBlock(TCABlockIds.COPPER_FIRE,
		CopperFireBlock::new,
		() -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_LIGHT_GREEN)
			.lightLevel(state -> 15)
			.noCollision()
			.randomTicks()
	);

	public static final DeferredBlock<Block> COPPER_CAMPFIRE = REGISTER.registerBlock(TCABlockItemIds.COPPER_CAMPFIRE.block(),
		properties -> new CampfireBlock(true, 1, properties),
		() -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.PODZOL)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2F)
			.sound(SoundType.WOOD)
			.lightLevel(Blocks.litBlockEmission(15))
			.noOcclusion()
			.ignitedByLava()
	);

	public static final DeferredBlock<Block> CUPRIC_LANTERN = REGISTER.registerBlock(TCABlockItemIds.CUPRIC_LANTERN.block(),
		LanternBlock::new,
		() -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.METAL)
			.forceSolidOn()
			.requiresCorrectToolForDrops()
			.strength(3.5F)
			.sound(SoundType.LANTERN)
			.lightLevel(state -> 15)
			.noOcclusion()
			.pushReaction(PushReaction.POPPED)
	);

	public static final DeferredBlock<Block> COPPER_JACK_O_LANTERN = REGISTER.registerBlock(TCABlockItemIds.COPPER_JACK_O_LANTERN.block(),
		CarvedPumpkinBlock::new,
		() -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_ORANGE)
			.strength(1F)
			.sound(SoundType.WOOD)
			.lightLevel(state -> 15)
			.isValidSpawn(Blocks::always)
			.pushReaction(PushReaction.POPPED)
	);

	public static final DeferredBlock<Block> REDSTONE_JACK_O_LANTERN = REGISTER.registerBlock(TCABlockItemIds.REDSTONE_JACK_O_LANTERN.block(),
		RedstonePumpkinBlock::new,
		() -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_ORANGE)
			.strength(1F)
			.sound(SoundType.WOOD)
			.lightLevel(state -> 7)
			.isValidSpawn(Blocks::always)
			.pushReaction(PushReaction.POPPED)
			.isRedstoneConductor(Blocks::never)
	);

	public static final DeferredBlock<Block> REDSTONE_GRIT = REGISTER.registerBlock(TCABlockItemIds.REDSTONE_GRIT.block(),
		properties -> new RedstoneGritBlock(new ColorRGBA(0xe3001a), properties),
		() -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_RED)
			.strength(1F)
			.sound(SoundType.SAND)
			.isValidSpawn(Blocks::always)
			.instrument(NoteBlockInstrument.SNARE)
			.isRedstoneConductor(Blocks::never)
	);

	public static final WeatheringCopperCollection<DeferredBlock<? extends Block>> GEARBOX = REGISTER.registerWeatheringCopperCollection(
		TCABlockItemIds.GEARBOX,
		(blocks, id, factory, props) -> blocks.registerBlock(id.block(), factory, props),
		(weatherState, properties) -> new GearboxBlock(properties),
		WeatheringGearboxBlock::new,
		weatherState -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.STONE)
			.strength(1.5F)
			.isRedstoneConductor(Blocks::never)
	);

	public static final WeatheringCopperCollection<DeferredBlock<? extends Block>> STICKY_GEARBOX = REGISTER.registerWeatheringCopperCollection(
		TCABlockItemIds.STICKY_GEARBOX,
		(blocks, id, factory, props) -> blocks.registerBlock(id.block(), factory, props),
		(weatherState, properties) -> new StickyGearboxBlock(properties),
		WeatheringStickyGearboxBlock::new,
		weatherState -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.STONE)
			.strength(1.5F)
			.isRedstoneConductor(Blocks::never)
	);

	public static final WeatheringCopperCollection<DeferredBlock<? extends Block>> COPPER_FAN = REGISTER.registerWeatheringCopperCollection(
		TCABlockItemIds.COPPER_FAN,
		(blocks, id, factory, props) -> blocks.registerBlock(id.block(), factory, props),
		CopperFanBlock::new,
		WeatheringCopperFanBlock::new,
		weatherState -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.STONE)
			.strength(1.5F)
			.isValidSpawn(Blocks::never)
			.isRedstoneConductor(Blocks::never)
			.isSuffocating(Blocks::never)
	);

	public static final WeatheringCopperCollection<DeferredBlock<? extends Block>> CHIME = REGISTER.registerWeatheringCopperCollection(
		TCABlockItemIds.CHIME,
		(blocks, id, factory, props) -> blocks.registerBlock(id.block(), factory, props),
		(weatherState, properties) -> new ChimeBlock(properties),
		WeatheringChimeBlock::new,
		weatherState -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.METAL)
			.requiresCorrectToolForDrops()
			.strength(5F, 6F)
			.sound(TCASounds.chimeSoundType())
			.noOcclusion()
	);

	public static final DeferredBlock<Block> CRATE = REGISTER.registerBlock(TCABlockItemIds.CRATE.block(),
		CrateBlock::new,
		() -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.WOOD)
			.instrument(NoteBlockInstrument.BASS)
			.strength(2.5F)
			.sound(SoundType.WOOD)
			.ignitedByLava()
	);

	public static final WeatheringCopperCollection<DeferredBlock<? extends Block>> COPPER_BUTTON = REGISTER.registerWeatheringCopperCollection(
		TCABlockItemIds.COPPER_BUTTON,
		(blocks, id, factory, props) -> blocks.registerBlock(id.block(), factory, props),
		CopperButtonBlock::new,
		WeatheringCopperButtonBlock::new,
		weatherState -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.NONE)
			.strength(0.5F)
			.noCollision()
			.pushReaction(PushReaction.POPPED)
	);

	public static final WeatheringCopperCollection<DeferredBlock<? extends Block>> WEIGHTED_PRESSURE_PLATE = REGISTER.registerWeatheringCopperCollection(
		TCABlockItemIds.WEIGHTED_PRESSURE_PLATE,
		(blocks, id, factory, props) -> blocks.registerBlock(id.block(), factory, props),
		CopperPressurePlateBlock::new,
		WeatheringCopperPressurePlateBlock::new,
		weatherState -> BlockBehaviour.Properties.of()
			.mapColor(getMapColorForWeatherState(weatherState))
			.strength(0.5F)
			.noCollision()
			.pushReaction(PushReaction.POPPED)
	);

	public static final WeatheringCopperCollection<DeferredBlock<? extends Block>> COPPER_RAIL = REGISTER.registerWeatheringCopperCollection(
		TCABlockItemIds.COPPER_RAIL,
		(blocks, id, factory, props) -> blocks.registerBlock(id.block(), factory, props),
		CopperRailBlock::new,
		WeatheringCopperRailBlock::new,
		weatherState -> BlockBehaviour.Properties.of()
			.mapColor(getMapColorForWeatherState(weatherState))
			.noCollision()
			.strength(0.7F)
			.sound(SoundType.METAL)
	);

	public static final DeferredBlock<Block> CROSS_RAIL = REGISTER.registerBlock(TCABlockItemIds.CROSS_RAIL.block(),
		CrossRailBlock::new,
		() -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.METAL)
			.noCollision()
			.strength(0.7F)
			.sound(SoundType.METAL)
	);

	public static final WeatheringCopperCollection<DeferredBlock<? extends Block>> COPPER_CROSS_RAIL = REGISTER.registerWeatheringCopperCollection(
		TCABlockItemIds.COPPER_CROSS_RAIL,
		(blocks, id, factory, props) -> blocks.registerBlock(id.block(), factory, props),
		CopperCrossRailBlock::new,
		WeatheringCopperCrossRailBlock::new,
		weatherState -> BlockBehaviour.Properties.of()
			.mapColor(getMapColorForWeatherState(weatherState))
			.noCollision()
			.strength(0.7F)
			.sound(SoundType.METAL)
	);

	public static final DeferredBlock<Block> RELAYER_RAIL = REGISTER.registerBlock(TCABlockItemIds.RELAYER_RAIL.block(),
		RelayerRailBlock::new,
		() -> BlockBehaviour.Properties.of()
			.mapColor(MapColor.METAL)
			.noCollision()
			.strength(0.7F)
			.sound(SoundType.METAL)
	);

	static {
		// Fabric: register all the blocks
		// NeoForge: add the RegisterEvent listener
		REGISTER.register();
	}

	public static void init() {}

	public static void setup() {
		BlockEntityTypes.CAMPFIRE.frozenLib$addValidBlock(TCABlocks.COPPER_CAMPFIRE.get());

		OxidizableBlocksRegistry.registerWeatheringCopperBlocks(asBlocks(GEARBOX));
		OxidizableBlocksRegistry.registerWeatheringCopperBlocks(asBlocks(STICKY_GEARBOX));
		OxidizableBlocksRegistry.registerWeatheringCopperBlocks(asBlocks(COPPER_FAN));
		OxidizableBlocksRegistry.registerWeatheringCopperBlocks(asBlocks(CHIME));
		OxidizableBlocksRegistry.registerWeatheringCopperBlocks(asBlocks(COPPER_BUTTON));
		OxidizableBlocksRegistry.registerWeatheringCopperBlocks(asBlocks(WEIGHTED_PRESSURE_PLATE));
		OxidizableBlocksRegistry.registerWeatheringCopperBlocks(asBlocks(COPPER_RAIL));
		OxidizableBlocksRegistry.registerWeatheringCopperBlocks(asBlocks(COPPER_CROSS_RAIL));

		DispenserBlock.registerBehavior(TCAItems.CRATE_MINECART.get(), new MinecartDispenseItemBehavior(TCAEntityTypes.CRATE_MINECART.get()));
		DispenserBlock.registerBehavior(TCAItems.DISPENSER_MINECART.get(), new MinecartDispenseItemBehavior(TCAEntityTypes.DISPENSER_MINECART.get()));
		DispenserBlock.registerBehavior(TCAItems.DROPPER_MINECART.get(), new MinecartDispenseItemBehavior(TCAEntityTypes.DROPPER_MINECART.get()));
		DispenserBlock.registerBehavior(TCAItems.JUKEBOX_MINECART.get(), new MinecartDispenseItemBehavior(TCAEntityTypes.JUKEBOX_MINECART.get()));

		DispenserBlock.registerBehavior(TCAItems.WRENCH.get(), new WrenchDispenseItemBehavior());
	}

	public static MapColor getMapColorForWeatherState(WeatheringCopper.WeatherState weatherState) {
		if (weatherState == WeatheringCopper.WeatherState.UNAFFECTED) return MapColor.COLOR_ORANGE;
		if (weatherState == WeatheringCopper.WeatherState.EXPOSED) return MapColor.TERRACOTTA_LIGHT_GRAY;
		if (weatherState == WeatheringCopper.WeatherState.WEATHERED) return MapColor.WARPED_STEM;
		if (weatherState == WeatheringCopper.WeatherState.OXIDIZED) return MapColor.WARPED_NYLIUM;
		return MapColor.NONE;
	}

	public static WeatheringCopperCollection<Block> asBlocks(WeatheringCopperCollection<DeferredBlock<? extends Block>> collection) {
		return collection.map(DeferredBlock::get);
	}
}
