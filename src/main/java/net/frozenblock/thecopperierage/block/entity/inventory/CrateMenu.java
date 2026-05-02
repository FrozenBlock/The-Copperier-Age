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

import net.frozenblock.thecopperierage.block.entity.CrateBlockEntity;
import net.frozenblock.thecopperierage.registry.TCAMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;

public class CrateMenu extends ChestMenu {

	public CrateMenu(MenuType<?> menuType, int containerId, Inventory inventory, Container container) {
		super(menuType, containerId, inventory, container, CrateBlockEntity.ROW_COUNT);
	}

	public static CrateMenu create(int containerId, Inventory inventory) {
		return new CrateMenu(TCAMenuTypes.CRATE, containerId, inventory, new SimpleContainer(9 * CrateBlockEntity.ROW_COUNT));
	}

	public static CrateMenu create(int containerId, Inventory inventory, Container container) {
		return new CrateMenu(TCAMenuTypes.CRATE, containerId, inventory, container);
	}

	@Override
	public void addChestGrid(Container container, int left, int top) {
		for (int row = 0; row < this.getRowCount(); row++) {
			for (int column = 0; column < 9; column++) {
				this.addSlot(new CrateSlot(container, column + row * 9, left + column * 18, top + row * 18));
			}
		}
	}

}
