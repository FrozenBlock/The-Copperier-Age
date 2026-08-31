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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecartContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractMinecartDispenser extends AbstractMinecartContainer {
	private static final int CONTAINER_SIZE = 9;
	private static final int ACTIVATION_DELAY = 4;
	private int lastActivatedTick;

	protected AbstractMinecartDispenser(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public int getContainerSize() {
		return CONTAINER_SIZE;
	}

	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return new DispenserMenu(containerId, inventory, this);
	}

	@Override
	public int getDefaultDisplayOffset() {
		return 8;
	}

	@Override
	public void activateMinecart(ServerLevel level, int x, int y, int z, boolean powered) {
		if (!powered || this.tickCount - this.lastActivatedTick < ACTIVATION_DELAY) return;
		if (!(this.level() instanceof ServerLevel serverLevel)) return;
		this.lastActivatedTick = this.tickCount;

		final BlockPos blockPos = this.blockPosition();
		final Direction facing = this.dispenseDirection();
		final BlockState state = this.getDefaultDisplayBlockState().setValue(DispenserBlock.FACING, facing);
		final int slot = this.getRandomNonEmptySlot(serverLevel);
		if (slot < 0) {
			serverLevel.levelEvent(LevelEvent.SOUND_DISPENSER_FAIL, blockPos, 0);
			return;
		}

		final ItemStack stack = this.getItem(slot);
		if (stack.isEmpty()) return;

		final DispenserBlockEntity dispenser = this.createActivationBlockEntity(blockPos, state);
		final BlockSource blockSource = new BlockSource(serverLevel, blockPos, state, dispenser);
		final Vec3 dispenseOffset = this.getBoundingBox().getCenter().subtract(Vec3.atCenterOf(blockPos));
		MinecartDispenseContext.begin(dispenseOffset);
		try {
			final ItemStack result = this.trigger(serverLevel, blockPos, state, dispenser, blockSource, stack.copy());
			if (result != null) this.setItem(slot, result);
		} finally {
			MinecartDispenseContext.end();
		}
	}

	private Direction dispenseDirection() {
		final Vec3 motion = this.getDeltaMovement();
		if ((motion.x * motion.x) + (motion.z * motion.z) > 1.0E-4D) {
			return Direction.getApproximateNearest(motion.x, 0.0D, motion.z);
		}
		return this.getMotionDirection();
	}

	private int getRandomNonEmptySlot(ServerLevel serverLevel) {
		int selectedSlot = -1;
		int nonEmptyCount = 1;

		for (int slot = 0; slot < this.getContainerSize(); slot++) {
			if (this.getItem(slot).isEmpty()) continue;
			if (serverLevel.getRandom().nextInt(nonEmptyCount++) == 0) selectedSlot = slot;
		}

		return selectedSlot;
	}

	protected abstract DispenserBlockEntity createActivationBlockEntity(BlockPos blockPos, BlockState state);

	protected abstract ItemStack trigger(
		ServerLevel level,
		BlockPos pos,
		BlockState state,
		DispenserBlockEntity dispenser,
		BlockSource blockSource,
		ItemStack stack
	);
}
