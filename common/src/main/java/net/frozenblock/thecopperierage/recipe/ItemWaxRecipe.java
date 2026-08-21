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

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.frozenblock.thecopperierage.item.api.OxidizableItemHelper;
import net.frozenblock.thecopperierage.registry.TCADataComponents;
import net.frozenblock.thecopperierage.registry.TCARecipeTypes;
import net.frozenblock.thecopperierage.tag.TCAItemTags;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ItemWaxRecipe extends CustomRecipe {
	public static final ItemWaxRecipe INSTANCE = new ItemWaxRecipe();
	public static final MapCodec<ItemWaxRecipe> MAP_CODEC = MapCodec.unit(INSTANCE);
	public static final StreamCodec<RegistryFriendlyByteBuf, ItemWaxRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);
	public static final RecipeSerializer<ItemWaxRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

	public ItemWaxRecipe() {
		super();
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		final List<ItemStack> items = input.items().stream().filter(stack -> !stack.isEmpty()).toList();
		if (items.size() != 2) return false;
		if (items.stream().noneMatch(stack -> stack.is(Items.HONEYCOMB))) return false;
		return !findWaxableItem(input).isEmpty();
	}

	@Override
	public ItemStack assemble(CraftingInput input) {
		final ItemStack stackToWax = findWaxableItem(input).copyWithCount(1);
		stackToWax.set(TCADataComponents.WAXED.get(), OxidizableItemHelper.getWeatherState(stackToWax));
		return stackToWax;
	}

	private static ItemStack findWaxableItem(CraftingInput input) {
		for (int i = 0; i < input.size(); i++) {
			final ItemStack stack = input.getItem(i);
			if (stack.is(TCAItemTags.OXIDIZABLE_EQUIPMENT) && !OxidizableItemHelper.isWaxed(stack)) return stack;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<ItemWaxRecipe> getSerializer() {
		return TCARecipeTypes.ITEM_WAX_RECIPE.get();
	}
}

