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

package net.frozenblock.thecopperierage.client.gui.screens.recipebook;

import java.util.List;
import net.frozenblock.thecopperierage.registry.TCABlocks;
import net.minecraft.client.gui.screens.recipebook.FurnaceRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public class KilnRecipeBookComponent extends FurnaceRecipeBookComponent {

	public KilnRecipeBookComponent(AbstractFurnaceMenu menu, Component recipeFilterName, List<RecipeBookComponent.TabInfo> tabs) {
		super(menu, recipeFilterName, tabs);
	}

	@Override
	protected void selectMatchingRecipes(RecipeCollection recipeCollection, StackedItemContents stackedItemContents) {
		recipeCollection.selectRecipes(stackedItemContents, recipeDisplay -> {
			if (!(recipeDisplay instanceof FurnaceRecipeDisplay furnaceRecipeDisplay)) return false;
			if (!(furnaceRecipeDisplay.craftingStation() instanceof SlotDisplay.ItemSlotDisplay itemSlotDisplay)) return false;
			return itemSlotDisplay.item().value() == TCABlocks.KILN.asItem();
		});
	}
}
