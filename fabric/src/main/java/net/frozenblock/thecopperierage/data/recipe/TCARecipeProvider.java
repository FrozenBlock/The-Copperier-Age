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

package net.frozenblock.thecopperierage.data.recipe;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.recipe.v1.ingredient.DefaultCustomIngredients;
import net.frozenblock.lib.item.api.recipe.RecipeExportNamespaceFix;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.TCAFeatureFlags;
import net.frozenblock.thecopperierage.item.crafting.ItemWaxRecipe;
import net.frozenblock.thecopperierage.registry.TCABlocks;
import net.frozenblock.thecopperierage.registry.TCAInstruments;
import net.frozenblock.thecopperierage.registry.TCAItems;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Instruments;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.InstrumentComponent;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopperCollection;

public final class TCARecipeProvider extends FabricRecipeProvider {

	public TCARecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput exporter) {
		return new RecipeProvider(registries, exporter) {
			@Override
			public void buildRecipes() {
				RecipeExportNamespaceFix.setCurrentGeneratingModId(TCAConstants.MOD_ID);

				final HolderGetter<EntityType<?>> entityTypes = this.registries.lookupOrThrow(Registries.ENTITY_TYPE);
				final HolderGetter<Item> items = this.registries.lookupOrThrow(Registries.ITEM);

				SpecialRecipeBuilder.special(ItemWaxRecipe::new).save(this.output, "equipment_wax");
				this.waxRecipes(TCAFeatureFlags.THE_COPPERIER_AGE_FLAG_SET);

				this.shaped(RecipeCategory.TOOLS, TCAItems.WRENCH)
					.group("wrench")
					.define('#', Ingredient.of(Items.COPPER_INGOT))
					.pattern("# #")
					.pattern(" # ")
					.pattern(" # ")
					.unlockedBy(RecipeProvider.getHasName(Items.COPPER_INGOT), this.has(Items.COPPER_INGOT))
					.save(exporter);

				this.shaped(RecipeCategory.TOOLS, TCAItems.MINECART_COUPLING)
					.group("minecart_coupling")
					.define('X', Ingredient.of(Items.IRON_CHAIN))
					.define('C', Ingredient.of(Items.IRON_INGOT))
					.pattern("  C")
					.pattern(" X ")
					.pattern("C  ")
					.unlockedBy(RecipeProvider.getHasName(Items.MINECART), this.has(Items.MINECART))
					.save(exporter);

				this.shapeless(RecipeCategory.TRANSPORTATION, TCAItems.CRATE_MINECART)
					.requires(Items.MINECART)
					.requires(TCABlocks.CRATE)
					.unlockedBy(RecipeProvider.getHasName(Items.MINECART), this.has(Items.MINECART))
					.save(this.output);

				this.shapeless(RecipeCategory.TRANSPORTATION, TCAItems.JUKEBOX_MINECART)
					.requires(Items.MINECART)
					.requires(Items.JUKEBOX)
					.unlockedBy(RecipeProvider.getHasName(Items.MINECART), this.has(Items.MINECART))
					.save(this.output);

				this.shaped(RecipeCategory.DECORATIONS, TCABlocks.COPPER_CAMPFIRE)
					.define('L', ItemTags.LOGS)
					.define('S', Items.STICK)
					.define('C', Items.COPPER_NUGGET)
					.pattern(" S ")
					.pattern("SCS")
					.pattern("LLL")
					.unlockedBy("has_copper_nugget", this.has(Items.COPPER_NUGGET))
					.save(this.output);

				this.shaped(RecipeCategory.DECORATIONS, TCABlocks.KILN)
					.define('~', Ingredient.of(Items.BRICKS))
					.define('F', Ingredient.of(Items.FURNACE))
					.define('#', Ingredient.of(Items.SMOOTH_STONE))
					.pattern("#~#")
					.pattern("~F~")
					.pattern("#~#")
					.unlockedBy(RecipeProvider.getHasName(Items.FURNACE), this.has(Items.FURNACE))
					.save(this.output);

				this.shaped(RecipeCategory.DECORATIONS, TCABlocks.CUPRIC_LANTERN)
					.define('#', Items.COPPER_TORCH)
					.define('X', Items.IRON_NUGGET)
					.pattern("XXX")
					.pattern("X#X")
					.pattern("XXX")
					.unlockedBy(RecipeProvider.getHasName(Items.COPPER_TORCH), this.has(Items.COPPER_TORCH))
					.save(this.output);

				this.shaped(RecipeCategory.BUILDING_BLOCKS, TCABlocks.COPPER_JACK_O_LANTERN)
					.define('A', Items.CARVED_PUMPKIN)
					.define('B', Items.COPPER_TORCH)
					.pattern("A")
					.pattern("B")
					.unlockedBy("has_copper_nugget", this.has(Items.COPPER_NUGGET))
					.save(this.output);

				this.shaped(RecipeCategory.BUILDING_BLOCKS, TCABlocks.REDSTONE_JACK_O_LANTERN)
					.define('A', Items.CARVED_PUMPKIN)
					.define('B', Items.REDSTONE_TORCH)
					.pattern("A")
					.pattern("B")
					.unlockedBy("has_redstone", this.has(Items.REDSTONE))
					.save(this.output);

				this.shaped(RecipeCategory.REDSTONE, TCABlocks.REDSTONE_GRIT, 4)
					.define('^', Items.REDSTONE)
					.define('#', Items.GRAVEL)
					.pattern("^#^")
					.pattern("#^#")
					.pattern("^#^")
					.unlockedBy("has_redstone", this.has(Items.REDSTONE))
					.save(this.output);

				this.shaped(RecipeCategory.BUILDING_BLOCKS, TCABlocks.COPPER_BUTTON.weathering().unaffected())
					.define('#', Ingredient.of(Items.COPPER_NUGGET))
					.pattern("##")
					.pattern("##")
					.unlockedBy("has_copper_nugget", this.has(Items.COPPER_NUGGET))
					.save(this.output);

				createCopperPressurePlateRecipe(this, exporter, TCABlocks.WEIGHTED_PRESSURE_PLATE.weathering().unaffected().get(), Items.COPPER_INGOT);

				createGearboxRecipe(this, exporter, TCABlocks.GEARBOX.weathering().unaffected().get(), Blocks.COPPER_BLOCK.weathering().unaffected());
				createGearboxRecipe(this, exporter, TCABlocks.GEARBOX.weathering().exposed().get(), Blocks.COPPER_BLOCK.weathering().exposed());
				createGearboxRecipe(this, exporter, TCABlocks.GEARBOX.weathering().weathered().get(), Blocks.COPPER_BLOCK.weathering().weathered());
				createGearboxRecipe(this, exporter, TCABlocks.GEARBOX.weathering().oxidized().get(), Blocks.COPPER_BLOCK.weathering().oxidized());

				createGearboxRecipe(this, exporter, TCABlocks.GEARBOX.waxed().unaffected().get(), Blocks.COPPER_BLOCK.waxed().unaffected());
				createGearboxRecipe(this, exporter, TCABlocks.GEARBOX.waxed().exposed().get(), Blocks.COPPER_BLOCK.waxed().exposed());
				createGearboxRecipe(this, exporter, TCABlocks.GEARBOX.waxed().weathered().get(), Blocks.COPPER_BLOCK.waxed().weathered());
				createGearboxRecipe(this, exporter, TCABlocks.GEARBOX.waxed().oxidized().get(), Blocks.COPPER_BLOCK.waxed().oxidized());

				createStickyGearboxRecipe(this, exporter, TCABlocks.STICKY_GEARBOX.weathering().unaffected().get(), TCABlocks.GEARBOX.weathering().unaffected().get());
				createStickyGearboxRecipe(this, exporter, TCABlocks.STICKY_GEARBOX.weathering().exposed().get(), TCABlocks.GEARBOX.weathering().exposed().get());
				createStickyGearboxRecipe(this, exporter, TCABlocks.STICKY_GEARBOX.weathering().weathered().get(), TCABlocks.GEARBOX.weathering().weathered().get());
				createStickyGearboxRecipe(this, exporter, TCABlocks.STICKY_GEARBOX.weathering().oxidized().get(), TCABlocks.GEARBOX.weathering().oxidized().get());

				createStickyGearboxRecipe(this, exporter, TCABlocks.STICKY_GEARBOX.waxed().unaffected().get(), TCABlocks.GEARBOX.waxed().unaffected().get());
				createStickyGearboxRecipe(this, exporter, TCABlocks.STICKY_GEARBOX.waxed().exposed().get(), TCABlocks.GEARBOX.waxed().exposed().get());
				createStickyGearboxRecipe(this, exporter, TCABlocks.STICKY_GEARBOX.waxed().weathered().get(), TCABlocks.GEARBOX.waxed().weathered().get());
				createStickyGearboxRecipe(this, exporter, TCABlocks.STICKY_GEARBOX.waxed().oxidized().get(), TCABlocks.GEARBOX.waxed().oxidized().get());

				createCopperFanRecipe(this, exporter, TCABlocks.COPPER_FAN.weathering().unaffected().get(), Blocks.COPPER_BLOCK.weathering().unaffected());
				createCopperFanRecipe(this, exporter, TCABlocks.COPPER_FAN.weathering().exposed().get(), Blocks.COPPER_BLOCK.weathering().exposed());
				createCopperFanRecipe(this, exporter, TCABlocks.COPPER_FAN.weathering().weathered().get(), Blocks.COPPER_BLOCK.weathering().weathered());
				createCopperFanRecipe(this, exporter, TCABlocks.COPPER_FAN.weathering().oxidized().get(), Blocks.COPPER_BLOCK.weathering().oxidized());

				createCopperFanRecipe(this, exporter, TCABlocks.COPPER_FAN.waxed().unaffected().get(), Blocks.COPPER_BLOCK.waxed().unaffected());
				createCopperFanRecipe(this, exporter, TCABlocks.COPPER_FAN.waxed().exposed().get(), Blocks.COPPER_BLOCK.waxed().exposed());
				createCopperFanRecipe(this, exporter, TCABlocks.COPPER_FAN.waxed().weathered().get(), Blocks.COPPER_BLOCK.waxed().weathered());
				createCopperFanRecipe(this, exporter, TCABlocks.COPPER_FAN.waxed().oxidized().get(), Blocks.COPPER_BLOCK.waxed().oxidized());

				this.shaped(RecipeCategory.DECORATIONS, TCABlocks.CHIME.weathering().unaffected(), 1)
					.define('-', Ingredient.of(Items.COPPER_INGOT))
					.define('T', Ingredient.of(Items.IRON_CHAIN))
					.define('V', Ingredient.of(Items.AMETHYST_SHARD))
					.pattern(" T ")
					.pattern("---")
					.pattern("VVV")
					.unlockedBy(RecipeProvider.getHasName(Items.AMETHYST_SHARD), this.has(Items.AMETHYST_SHARD))
					.save(exporter);

				this.shaped(RecipeCategory.REDSTONE, TCABlocks.CRATE, 1)
					.define('#', Ingredient.of(items.getOrThrow(ItemTags.PLANKS)))
					.define('I', Ingredient.of(Items.IRON_INGOT))
					.pattern("I#I")
					.pattern("# #")
					.pattern("I#I")
					.unlockedBy(RecipeProvider.getHasName(Items.IRON_INGOT), this.has(Items.IRON_INGOT))
					.save(exporter);

				this.shaped(RecipeCategory.TRANSPORTATION, TCABlocks.COPPER_RAIL.weathering().unaffected(), 12)
					.define('X', Ingredient.of(Items.COPPER_INGOT))
					.define('#', Ingredient.of(Items.STICK))
					.pattern("X X")
					.pattern("X#X")
					.pattern("X X")
					.unlockedBy(RecipeProvider.getHasName(Items.COPPER_INGOT), this.has(Items.COPPER_INGOT))
					.save(exporter);

				this.shapeless(RecipeCategory.TRANSPORTATION, TCABlocks.CROSS_RAIL)
					.requires(Items.RAIL, 2)
					.unlockedBy(RecipeProvider.getHasName(Items.RAIL), this.has(Items.RAIL))
					.save(exporter);

				WeatheringCopperCollection.zipApply(
					TCABlocks.COPPER_RAIL.weathering(),
					TCABlocks.COPPER_CROSS_RAIL.weathering(),
					(rail, crossRail) -> this.shapeless(RecipeCategory.TRANSPORTATION, crossRail)
						.requires(rail.get(), 2)
						.unlockedBy(RecipeProvider.getHasName(rail.get()), this.has(rail.get()))
						.save(exporter)
				);

				this.shaped(RecipeCategory.TRANSPORTATION, TCABlocks.RELAYOR_RAIL, 6)
					.define('I', Ingredient.of(Items.IRON_INGOT))
					.define('G', Ingredient.of(Items.GOLD_INGOT))
					.define('R', Ingredient.of(Items.REDSTONE))
					.define('#', Ingredient.of(Items.STICK))
					.pattern("IRI")
					.pattern("G#G")
					.pattern("GRG")
					.unlockedBy(RecipeProvider.getHasName(Items.GOLD_INGOT), this.has(Items.GOLD_INGOT))
					.save(exporter);

				// COPPER HORN
				this.copperHorn("recorder", Instruments.YEARN_GOAT_HORN, TCAInstruments.RECORDER_COPPER_HORN);
				this.copperHorn("clarinet", Instruments.DREAM_GOAT_HORN, TCAInstruments.CLARINET_COPPER_HORN);
				this.copperHorn("flute", Instruments.CALL_GOAT_HORN, TCAInstruments.FLUTE_COPPER_HORN);
				this.copperHorn("oboe", Instruments.SING_GOAT_HORN, TCAInstruments.OBOE_COPPER_HORN);
				this.copperHorn("sax", Instruments.PONDER_GOAT_HORN, TCAInstruments.SAX_COPPER_HORN);
				this.copperHorn("trombone", Instruments.SEEK_GOAT_HORN, TCAInstruments.TROMBONE_COPPER_HORN);
				this.copperHorn("trumpet", Instruments.ADMIRE_GOAT_HORN, TCAInstruments.TRUMPET_COPPER_HORN);
				this.copperHorn("tuba", Instruments.FEEL_GOAT_HORN, TCAInstruments.TUBA_COPPER_HORN);

				RecipeExportNamespaceFix.clearCurrentGeneratingModId();
			}

			private void copperHorn(
				String name,
				ResourceKey<Instrument> goatHornInstrument,
				ResourceKey<Instrument> copperHornInstrument
			) {
				copperHornBuilder(copperHornInstrument)
					.group("wilderwild_copper_horn")
					.define('C', Ingredient.of(Items.COPPER_INGOT))
					.define('G', DefaultCustomIngredients.components(
						Ingredient.of(Items.GOAT_HORN),
						DataComponentPatch.builder()
							.set(DataComponents.INSTRUMENT, new InstrumentComponent(this.registries.lookupOrThrow(Registries.INSTRUMENT).getOrThrow(goatHornInstrument)))
							.build()
					))
					.pattern("CGC")
					.pattern(" C ")
					.unlockedBy(getHasName(Items.GOAT_HORN), this.has(Items.GOAT_HORN))
					.save(exporter, TCAConstants.string(name + "_copper_horn"));
			}

			private ShapedRecipeBuilder copperHornBuilder(ResourceKey<Instrument> instrument) {
				return new ShapedRecipeBuilder(
					this.registries.lookupOrThrow(Registries.ITEM),
					RecipeCategory.TOOLS,
					new ItemStackTemplate(
						TCAItems.COPPER_HORN.get(),
						DataComponentPatch.builder().set(
							DataComponents.INSTRUMENT,
							new InstrumentComponent(this.registries.lookupOrThrow(Registries.INSTRUMENT).getOrThrow(instrument))
						).build())
				);
			}
		};
	}

	private static void createCopperPressurePlateRecipe(RecipeProvider recipeProvider, RecipeOutput exporter, Block pressurePlateBlock, ItemLike ingredient) {
		recipeProvider.shaped(RecipeCategory.BUILDING_BLOCKS, pressurePlateBlock)
			.define('#', Ingredient.of(ingredient))
			.pattern("##")
			.unlockedBy(RecipeProvider.getHasName(ingredient), recipeProvider.has(ingredient))
			.save(exporter);
	}

	private static void createGearboxRecipe(RecipeProvider recipeProvider, RecipeOutput exporter, Block gearboxBlock, Block copperBlock) {
		recipeProvider.shaped(RecipeCategory.REDSTONE, gearboxBlock, 4)
			.define('X', Ingredient.of(copperBlock))
			.define('-', Ingredient.of(Items.COPPER_INGOT))
			.define('#', Ingredient.of(Items.COBBLESTONE))
			.define('R', Ingredient.of(Items.REDSTONE))
			.pattern("XXX")
			.pattern("#-#")
			.pattern("#R#")
			.unlockedBy(RecipeProvider.getHasName(copperBlock), recipeProvider.has(copperBlock))
			.save(exporter);
	}

	private static void createStickyGearboxRecipe(RecipeProvider recipeProvider, RecipeOutput exporter, Block stickyGearboxBlock, Block gearboxBlock) {
		recipeProvider.shaped(RecipeCategory.REDSTONE, stickyGearboxBlock, 1)
			.define('G', Ingredient.of(gearboxBlock))
			.define('S', Ingredient.of(Items.SLIME_BALL))
			.pattern("S")
			.pattern("G")
			.unlockedBy(RecipeProvider.getHasName(Items.SLIME_BALL), recipeProvider.has(Items.SLIME_BALL))
			.save(exporter);
	}

	private static void createCopperFanRecipe(RecipeProvider recipeProvider, RecipeOutput exporter, Block fanBlock, Block copperBlock) {
		recipeProvider.shaped(RecipeCategory.REDSTONE, fanBlock, 4)
			.define('X', Ingredient.of(copperBlock))
			.define('/', Ingredient.of(Items.BREEZE_ROD))
			.define('#', Ingredient.of(Items.COBBLESTONE))
			.define('R', Ingredient.of(Items.REDSTONE))
			.pattern("###")
			.pattern("X/X")
			.pattern("XRX")
			.unlockedBy(RecipeProvider.getHasName(copperBlock), recipeProvider.has(copperBlock))
			.save(exporter);
	}

	@Override
	public String getName() {
		return "The Copperier Age Recipes";
	}
}
