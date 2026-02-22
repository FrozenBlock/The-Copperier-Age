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
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.EquipmentDispenseItemBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.server.level.ServerLevel;

public class DispenserMinecart extends AbstractDispenserMinecart {
	private static final DefaultDispenseItemBehavior DEFAULT_BEHAVIOR = new DefaultDispenseItemBehavior();

	public DispenserMinecart(EntityType<? extends DispenserMinecart> entityType, Level level) {
		super(entityType, level);
	}

	public DispenserMinecart(Level level, double x, double y, double z) {
		this(TCAEntityTypes.DISPENSER_MINECART, level);
		this.setPos(x, y, z);
	}

	@Override
	protected Item getDropItem() {
		return TCAItems.DISPENSER_MINECART;
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
	public int getDefaultDisplayOffset() {
		return 6;
	}

	@Override
	protected DispenserBlockEntity createActivationBlockEntity(BlockPos blockPos, BlockState state) {
		return new DispenserBlockEntity(blockPos, state);
	}

	@Override
	protected ItemStack trigger(
		ServerLevel serverLevel,
		BlockPos blockPos,
		BlockState state,
		DispenserBlockEntity dispenser,
		BlockSource blockSource,
		ItemStack stack
	) {
		final DispenseItemBehavior behavior = this.getDispenseMethod(serverLevel, stack);
		if (behavior == DispenseItemBehavior.NOOP) return null;
		return behavior.dispense(blockSource, stack);
	}

	private DispenseItemBehavior getDispenseMethod(Level level, ItemStack stack) {
		if (!stack.isItemEnabled(level.enabledFeatures())) {
			return DEFAULT_BEHAVIOR;
		}

		final DispenseItemBehavior behavior = DispenserBlock.DISPENSER_REGISTRY.get(stack.getItem());
		if (behavior != null) {
			return behavior;
		}

		if (stack.has(DataComponents.EQUIPPABLE)) {
			return EquipmentDispenseItemBehavior.INSTANCE;
		}

		return DEFAULT_BEHAVIOR;
	}
}
