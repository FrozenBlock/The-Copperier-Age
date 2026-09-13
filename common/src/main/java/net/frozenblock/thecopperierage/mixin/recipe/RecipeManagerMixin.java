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
import net.frozenblock.thecopperierage.item.crafting.KilnSmeltingRecipe;
import net.frozenblock.thecopperierage.item.crafting.TCAKilnRecipeProvider;
import net.frozenblock.thecopperierage.registry.TCARecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin implements TCAKilnRecipeProvider {
	@Shadow
	private RecipeMap recipes;

	@Unique
	@Override
	public List<RecipeHolder<?>> theCopperierAge$getKilnRecipes() {
		return new ArrayList<>(this.recipes.byType(TCARecipeTypes.KILN.get()));
	}

	@Inject(method = "finalizeRecipeLoading", at = @At("HEAD"))
	private void theCopperierAge$generateKilnRecipes(FeatureFlagSet enabledFlags, CallbackInfo info) {
		final Collection<RecipeHolder<SmeltingRecipe>> smeltingRecipes = this.recipes.byType(RecipeType.SMELTING);
		if (smeltingRecipes.isEmpty()) return;

		final Set<Item> smokingItems = theCopperierAge$collectItems(this.recipes.byType(RecipeType.SMOKING));
		final Set<Item> blastingItems = theCopperierAge$collectItems(this.recipes.byType(RecipeType.BLASTING));

		final List<RecipeHolder<?>> allRecipes = new ArrayList<>(this.recipes.values());
		for (RecipeHolder<SmeltingRecipe> smeltingRecipeHolder : smeltingRecipes) {
			final SmeltingRecipe smeltingRecipe = smeltingRecipeHolder.value();
			final Ingredient input = smeltingRecipe.input();
			if (theCopperierAge$overlapsAny(input, smokingItems) || theCopperierAge$overlapsAny(input, blastingItems)) continue;

			final Identifier sourceId = smeltingRecipeHolder.id().identifier();
			final Identifier kilnId = TCAConstants.id("kiln/" + sourceId.getNamespace() + "/" + sourceId.getPath());
			final RecipeHolder<KilnSmeltingRecipe> kilnRecipeHolder = new RecipeHolder<>(
				ResourceKey.create(Registries.RECIPE, kilnId),
				KilnSmeltingRecipe.from(smeltingRecipe)
			);
			allRecipes.add(kilnRecipeHolder);
		}

		this.recipes = RecipeMap.create(allRecipes);
	}

	@Unique
	private static <T extends AbstractCookingRecipe> Set<Item> theCopperierAge$collectItems(Collection<RecipeHolder<T>> recipes) {
		Set<Item> items = new HashSet<>();
		for (RecipeHolder<T> recipeHolder : recipes) {
			Ingredient ingredient = recipeHolder.value().input();
			for (Holder<Item> itemHolder : (Iterable<Holder<Item>>) ingredient.items()::iterator) {
				items.add(itemHolder.value());
			}
		}
		return items;
	}

	@Unique
	private static boolean theCopperierAge$overlapsAny(Ingredient ingredient, Set<Item> blockedItems) {
		if (blockedItems.isEmpty()) return false;

		for (Holder<Item> itemHolder : (Iterable<Holder<Item>>) ingredient.items()::iterator) {
			if (blockedItems.contains(itemHolder.value())) return true;
		}
		return false;
	}
}
