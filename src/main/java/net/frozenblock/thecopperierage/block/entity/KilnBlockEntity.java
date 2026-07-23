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

package net.frozenblock.thecopperierage.block.entity;

import net.frozenblock.thecopperierage.block.entity.inventory.KilnMenu;
import net.frozenblock.thecopperierage.registry.TCABlockEntityTypes;
import net.frozenblock.thecopperierage.registry.TCARecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.state.BlockState;

public class KilnBlockEntity extends AbstractFurnaceBlockEntity {

	public KilnBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(TCABlockEntityTypes.KILN, blockPos, blockState, TCARecipeTypes.KILN);
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.thecopperierage.kiln");
	}

	@Override
	protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
		return new KilnMenu(i, inventory, this, this.dataAccess);
	}

	@Override
	protected int getBurnDuration(FuelValues fuelValues, ItemStack fuel) {
		return super.getBurnDuration(fuelValues, fuel) / 2;
	}
}
