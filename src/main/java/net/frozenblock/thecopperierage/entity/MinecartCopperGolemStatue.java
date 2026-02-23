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

package net.frozenblock.thecopperierage.entity;

import net.frozenblock.thecopperierage.registry.TCAEntityTypes;
import net.frozenblock.thecopperierage.registry.TCAItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class MinecartCopperGolemStatue extends AbstractMinecart {

	public MinecartCopperGolemStatue(EntityType<MinecartCopperGolemStatue> entityType, Level level) {
		super(entityType, level);
	}

	public MinecartCopperGolemStatue(Level level, double x, double y, double z) {
		this(TCAEntityTypes.COPPER_GOLEM_STATUE_MINECART, level);
		this.setPos(x, y, z);
	}

	@Override
	protected Item getDropItem() {
		return TCAItems.COPPER_GOLEM_STATUE_MINECART;
	}

	@Override
	public ItemStack getPickResult() {
		return new ItemStack(TCAItems.COPPER_GOLEM_STATUE_MINECART);
	}

	@Override
	public BlockState getDefaultDisplayBlockState() {
		return Blocks.COPPER_GOLEM_STATUE.defaultBlockState();
	}

	@Override
	public int getDefaultDisplayOffset() {
		return 8;
	}
}
