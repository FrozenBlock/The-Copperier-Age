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

package net.frozenblock.thecopperierage.mixin.recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.recipe.KilnSmeltingRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
	@Shadow
	private RecipeMap recipes;

	@Inject(method = "finalizeRecipeLoading", at = @At("HEAD"))
	private void theCopperierAge$generateKilnRecipes(FeatureFlagSet featureFlagSet, CallbackInfo ci) {
		Collection<RecipeHolder<SmeltingRecipe>> smeltingRecipes = this.recipes.byType(RecipeType.SMELTING);
		if (smeltingRecipes.isEmpty()) return;

		Set<net.minecraft.world.item.Item> smokingItems = collectItems(this.recipes.byType(RecipeType.SMOKING));
		Set<net.minecraft.world.item.Item> blastingItems = collectItems(this.recipes.byType(RecipeType.BLASTING));

		List<RecipeHolder<?>> allRecipes = new ArrayList<>(this.recipes.values());
		for (RecipeHolder<SmeltingRecipe> smeltingRecipeHolder : smeltingRecipes) {
			SmeltingRecipe smeltingRecipe = smeltingRecipeHolder.value();
			Ingredient input = smeltingRecipe.input();
			if (overlapsAny(input, smokingItems) || overlapsAny(input, blastingItems)) continue;

			ResourceLocation sourceId = smeltingRecipeHolder.id().location();
			ResourceLocation kilnId = TCAConstants.id("kiln/" + sourceId.getNamespace() + "/" + sourceId.getPath());
			RecipeHolder<KilnSmeltingRecipe> kilnRecipeHolder = new RecipeHolder<>(
				ResourceKey.create(Registries.RECIPE, kilnId),
				KilnSmeltingRecipe.from(smeltingRecipe)
			);
			allRecipes.add(kilnRecipeHolder);
		}

		this.recipes = RecipeMap.create(allRecipes);
	}

	@SuppressWarnings("deprecation")
	private static <T extends AbstractCookingRecipe> Set<net.minecraft.world.item.Item> collectItems(Collection<RecipeHolder<T>> recipes) {
		Set<net.minecraft.world.item.Item> items = new HashSet<>();
		for (RecipeHolder<T> recipeHolder : recipes) {
			Ingredient ingredient = recipeHolder.value().input();
			for (Holder<net.minecraft.world.item.Item> itemHolder : (Iterable<Holder<net.minecraft.world.item.Item>>) ingredient.items()::iterator) {
				items.add(itemHolder.value());
			}
		}
		return items;
	}

	@SuppressWarnings("deprecation")
	private static boolean overlapsAny(Ingredient ingredient, Set<net.minecraft.world.item.Item> blockedItems) {
		if (blockedItems.isEmpty()) return false;

		for (Holder<net.minecraft.world.item.Item> itemHolder : (Iterable<Holder<net.minecraft.world.item.Item>>) ingredient.items()::iterator) {
			if (blockedItems.contains(itemHolder.value())) return true;
		}
		return false;
	}
}
