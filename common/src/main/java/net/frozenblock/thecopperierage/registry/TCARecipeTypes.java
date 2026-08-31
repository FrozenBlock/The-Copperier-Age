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

import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.recipe.ItemWaxRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;

public final class TCARecipeTypes {
	private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, TCAConstants.MOD_ID);
	private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, TCAConstants.MOD_ID);

	public static final DeferredHolder<RecipeType<?>, RecipeType<SmeltingRecipe>> KILN = RECIPE_TYPES.register(
		"kiln",
		() -> new RecipeType<>() {
			@Override
			public String toString() {
				return "thecopperierage:kiln";
			}
		}
	);

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ItemWaxRecipe>> ITEM_WAX_RECIPE = RECIPE_SERIALIZERS.register(
		"crafting_item_wax",
		() -> ItemWaxRecipe.SERIALIZER
	);

	static {
		RECIPE_TYPES.register();
		RECIPE_SERIALIZERS.register();
	}

	public static void init() {}
}
