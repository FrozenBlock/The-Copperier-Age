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

import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.recipe.ItemWaxRecipe;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public final class TCARecipeTypes {
	public static final RecipeType<SmeltingRecipe> KILN = Registry.register(
		BuiltInRegistries.RECIPE_TYPE,
		TCAConstants.id("kiln"),
		new RecipeType<>() {
			@Override
			public String toString() {
				return "thecopperierage:kiln";
			}
		}
	);

	public static final RecipeSerializer<ItemWaxRecipe> ITEM_WAX_RECIPE = Registry.register(
		BuiltInRegistries.RECIPE_SERIALIZER,
		TCAConstants.id("crafting_item_wax"),
		ItemWaxRecipe.SERIALIZER
	);

	public static void init() {}
}
