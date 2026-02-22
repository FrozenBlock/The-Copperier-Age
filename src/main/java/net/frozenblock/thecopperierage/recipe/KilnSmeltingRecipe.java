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

package net.frozenblock.thecopperierage.recipe;

import net.frozenblock.thecopperierage.mixin.recipe.SingleItemRecipeAccessor;
import net.frozenblock.thecopperierage.registry.TCABlocks;
import net.frozenblock.thecopperierage.registry.TCARecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import org.jetbrains.annotations.NotNull;

public final class KilnSmeltingRecipe extends SmeltingRecipe {

	public KilnSmeltingRecipe(
		String group,
		CookingBookCategory category,
		Ingredient ingredient,
		ItemStack result,
		float experience,
		int cookingTime
	) {
		super(group, category, ingredient, result, experience, cookingTime);
	}

	public static @NotNull KilnSmeltingRecipe from(@NotNull SmeltingRecipe smeltingRecipe) {
		ItemStack result = ((SingleItemRecipeAccessor) smeltingRecipe).theCopperierAge$getResult().copy();
		int cookingTime = Math.max(1, smeltingRecipe.cookingTime() / 2);
		return new KilnSmeltingRecipe(
			smeltingRecipe.group(),
			smeltingRecipe.category(),
			smeltingRecipe.input(),
			result,
			smeltingRecipe.experience(),
			cookingTime
		);
	}

	@Override
	public @NotNull RecipeType<SmeltingRecipe> getType() {
		return TCARecipeTypes.KILN;
	}

	@Override
	@SuppressWarnings("unchecked")
	public @NotNull RecipeSerializer<SmeltingRecipe> getSerializer() {
		return (RecipeSerializer<SmeltingRecipe>) RecipeSerializer.SMELTING_RECIPE;
	}

	@Override
	protected @NotNull Item furnaceIcon() {
		return TCABlocks.KILN.asItem();
	}
}
