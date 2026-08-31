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
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MinecartDispenserDropper extends AbstractMinecartDispenser {
	private static final DefaultDispenseItemBehavior DISPENSE_BEHAVIOR = new DefaultDispenseItemBehavior();

	public MinecartDispenserDropper(EntityType<? extends MinecartDispenserDropper> entityType, Level level) {
		super(entityType, level);
	}

	public MinecartDispenserDropper(Level level, double x, double y, double z) {
		this(TCAEntityTypes.DROPPER_MINECART.get(), level);
		this.setPos(x, y, z);
	}

	@Override
	protected Item getDropItem() {
		return TCAItems.DROPPER_MINECART.get();
	}

	@Override
	public ItemStack getPickResult() {
		return new ItemStack(TCAItems.DROPPER_MINECART);
	}

	@Override
	public BlockState getDefaultDisplayBlockState() {
		return Blocks.DROPPER.defaultBlockState().setValue(DispenserBlock.FACING, Direction.UP);
	}

	@Override
	protected DispenserBlockEntity createActivationBlockEntity(BlockPos blockPos, BlockState state) {
		return new DropperBlockEntity(blockPos, state);
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
		final Direction direction = state.getValue(DispenserBlock.FACING);
		final Container container = HopperBlockEntity.getContainerAt(level, pos.relative(direction));
		if (container == null) return DISPENSE_BEHAVIOR.dispense(blockSource, stack);

		final ItemStack transferred = HopperBlockEntity.addItem(this, container, stack.copyWithCount(1), direction.getOpposite());
		if (transferred.isEmpty()) {
			final ItemStack remaining = stack.copy();
			remaining.shrink(1);
			return remaining;
		}

		return stack.copy();
	}
}
