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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.server.level.ServerLevel;

public class MinecartDispenser extends AbstractMinecartDispenser {

	public MinecartDispenser(EntityType<? extends MinecartDispenser> entityType, Level level) {
		super(entityType, level);
	}

	public MinecartDispenser(Level level, double x, double y, double z) {
		this(TCAEntityTypes.DISPENSER_MINECART.get(), level);
		this.setPos(x, y, z);
	}

	@Override
	protected Item getDropItem() {
		return TCAItems.DISPENSER_MINECART.get();
	}

	@Override
	public ItemStack getPickResult() {
		return new ItemStack(TCAItems.DISPENSER_MINECART);
	}

	@Override
	public BlockState getDefaultDisplayBlockState() {
		return Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, Direction.UP);
	}

	@Override
	protected DispenserBlockEntity createActivationBlockEntity(BlockPos blockPos, BlockState state) {
		return new DispenserBlockEntity(blockPos, state);
	}

	@Override
	protected ItemStack trigger(
		ServerLevel level,
		BlockPos pos,
		BlockState state,
		DispenserBlockEntity dispenser,
		BlockSource blockSource,
		ItemStack stack
	) {
		final DispenseItemBehavior behavior = ((DispenserBlock) Blocks.DISPENSER).getDispenseMethod(level, stack);
		if (behavior == DispenseItemBehavior.NOOP) return null;
		return behavior.dispense(blockSource, stack);
	}
}
