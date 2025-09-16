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

package net.frozenblock.thecopperierage.datagen.recipe;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.frozenblock.lib.recipe.api.RecipeExportNamespaceFix;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.registry.TCABlocks;
import net.frozenblock.thecopperierage.registry.TCAItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class TCARecipeProvider extends FabricRecipeProvider {

	public TCARecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Contract("_, _ -> new")
	@Override
	protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput exporter) {
		return new RecipeProvider(registries, exporter) {
			@Override
			public void buildRecipes() {
				RecipeExportNamespaceFix.setCurrentGeneratingModId(TCAConstants.MOD_ID);

				CopperHornRecipeProvider.buildRecipes(this, registries, exporter);

				this.shaped(RecipeCategory.TOOLS, TCAItems.WRENCH)
					.group("wrench")
					.define('#', Ingredient.of(Items.COPPER_INGOT))
					.pattern("# #")
					.pattern(" # ")
					.pattern(" # ")
					.unlockedBy(RecipeProvider.getHasName(Items.COPPER_INGOT), this.has(Items.COPPER_INGOT))
					.save(exporter);

				this.shaped(RecipeCategory.DECORATIONS, TCABlocks.COPPER_CAMPFIRE)
					.define('L', ItemTags.LOGS)
					.define('S', Items.STICK)
					.define('C', Items.COPPER_NUGGET)
					.pattern(" S ")
					.pattern("SCS")
					.pattern("LLL")
					.unlockedBy("has_copper_nugget", this.has(Items.COPPER_NUGGET))
					.save(this.output);

				createGearboxRecipe(this, exporter, TCABlocks.GEARBOX.unaffected(), Blocks.COPPER_BLOCK);
				createGearboxRecipe(this, exporter, TCABlocks.GEARBOX.exposed(), Blocks.EXPOSED_COPPER);
				createGearboxRecipe(this, exporter, TCABlocks.GEARBOX.weathered(), Blocks.WEATHERED_COPPER);
				createGearboxRecipe(this, exporter, TCABlocks.GEARBOX.oxidized(), Blocks.OXIDIZED_COPPER);

				createGearboxRecipe(this, exporter, TCABlocks.GEARBOX.waxed(), Blocks.WAXED_COPPER_BLOCK);
				createGearboxRecipe(this, exporter, TCABlocks.GEARBOX.waxedExposed(), Blocks.WAXED_EXPOSED_COPPER);
				createGearboxRecipe(this, exporter, TCABlocks.GEARBOX.waxedWeathered(), Blocks.WAXED_WEATHERED_COPPER);
				createGearboxRecipe(this, exporter, TCABlocks.GEARBOX.waxedOxidized(), Blocks.WAXED_OXIDIZED_COPPER);

				createCopperFanRecipe(this, exporter, TCABlocks.COPPER_FAN.unaffected(), Blocks.COPPER_BLOCK);
				createCopperFanRecipe(this, exporter, TCABlocks.COPPER_FAN.exposed(), Blocks.EXPOSED_COPPER);
				createCopperFanRecipe(this, exporter, TCABlocks.COPPER_FAN.weathered(), Blocks.WEATHERED_COPPER);
				createCopperFanRecipe(this, exporter, TCABlocks.COPPER_FAN.oxidized(), Blocks.OXIDIZED_COPPER);

				createCopperFanRecipe(this, exporter, TCABlocks.COPPER_FAN.waxed(), Blocks.WAXED_COPPER_BLOCK);
				createCopperFanRecipe(this, exporter, TCABlocks.COPPER_FAN.waxedExposed(), Blocks.WAXED_EXPOSED_COPPER);
				createCopperFanRecipe(this, exporter, TCABlocks.COPPER_FAN.waxedWeathered(), Blocks.WAXED_WEATHERED_COPPER);
				createCopperFanRecipe(this, exporter, TCABlocks.COPPER_FAN.waxedOxidized(), Blocks.WAXED_OXIDIZED_COPPER);

				RecipeExportNamespaceFix.clearCurrentGeneratingModId();
			}
		};
	}

	private static void createGearboxRecipe(@NotNull RecipeProvider recipeProvider, RecipeOutput exporter, Block gearboxBlock, Block copperBlock) {
		recipeProvider.shaped(RecipeCategory.REDSTONE, gearboxBlock, 2)
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

	private static void createCopperFanRecipe(@NotNull RecipeProvider recipeProvider, RecipeOutput exporter, Block gearboxBlock, Block copperBlock) {
		recipeProvider.shaped(RecipeCategory.REDSTONE, gearboxBlock, 2)
			.define('X', Ingredient.of(copperBlock))
			.define('O', Ingredient.of(Items.WIND_CHARGE))
			.define('#', Ingredient.of(Items.COBBLESTONE))
			.define('R', Ingredient.of(Items.REDSTONE))
			.pattern("###")
			.pattern("XOX")
			.pattern("XRX")
			.unlockedBy(RecipeProvider.getHasName(copperBlock), recipeProvider.has(copperBlock))
			.save(exporter);
	}

	@Override
	@NotNull
	public String getName() {
		return "The Copperier Age Recipes";
	}
}
