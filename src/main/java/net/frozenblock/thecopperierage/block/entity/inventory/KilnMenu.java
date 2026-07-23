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

package net.frozenblock.thecopperierage.block.entity.inventory;

import net.frozenblock.thecopperierage.registry.TCAMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.crafting.RecipePropertySet;

public class KilnMenu extends AbstractFurnaceMenu {

	public KilnMenu(int i, Inventory inventory) {
		this(i, inventory, new SimpleContainer(3), new SimpleContainerData(4));
	}

	public KilnMenu(int i, Inventory inventory, Container container, ContainerData containerData) {
		super(
			TCAMenuTypes.KILN,
			RecipePropertySet.FURNACE_INPUT,
			RecipeBookType.FURNACE,
			i,
			inventory,
			container,
			containerData
		);
	}
}
