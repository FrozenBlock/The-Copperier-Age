/*
 * Copyright 2026 FrozenBlock
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

package net.frozenblock.thecopperierage.entity.inventory;

import net.frozenblock.thecopperierage.registry.TCAMenuTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FurnaceMinecartMenu extends AbstractContainerMenu {
	public static final int SLOT_COUNT = 5;
	private static final int FIRST_CONTAINER_SLOT = 0;
	private static final int LAST_CONTAINER_SLOT_EXCLUSIVE = SLOT_COUNT;
	private static final int PLAYER_SLOTS_COUNT = 36;
	private static final int FIRST_PLAYER_SLOT = SLOT_COUNT;
	private static final int LAST_PLAYER_SLOT_EXCLUSIVE = FIRST_PLAYER_SLOT + PLAYER_SLOTS_COUNT;
	private static final int DEFAULT_FUEL_DURATION = 200;
	private static final int LIT_PROGRESS_HEIGHT = 13;
	public static final int CONTAINER_START_X = 44;
	public static final int CONTAINER_SLOT_Y = 49;
	public static final int PLAYER_INV_START_X = 8;
	public static final int PLAYER_INV_START_Y = 84;
	public static final int HOTBAR_Y = 142;
	private static final int DATA_COUNT = 2;
	private final Container container;
	private final ContainerData data;

	public FurnaceMinecartMenu(int containerId, Inventory playerInventory) {
		this(containerId, playerInventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(DATA_COUNT));
	}

	public FurnaceMinecartMenu(int containerId, Inventory playerInventory, Container container, ContainerData data) {
		super(TCAMenuTypes.FURNACE_MINECART, containerId);
		checkContainerSize(container, SLOT_COUNT);
		checkContainerDataCount(data, DATA_COUNT);
		this.container = container;
		this.data = data;
		container.startOpen(playerInventory.player);

		this.addContainerSlots(container);
		this.addPlayerInventorySlots(playerInventory);
		this.addPlayerHotbarSlots(playerInventory);
		this.addDataSlots(data);
	}

	private void addContainerSlots(Container container) {
		for (int slot = 0; slot < SLOT_COUNT; slot++) {
			this.addSlot(new Slot(container, slot, CONTAINER_START_X + slot * 18, CONTAINER_SLOT_Y));
		}
	}

	private void addPlayerInventorySlots(Inventory playerInventory) {
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				this.addSlot(new Slot(playerInventory, column + row * 9 + 9, PLAYER_INV_START_X + column * 18, PLAYER_INV_START_Y + row * 18));
			}
		}
	}

	private void addPlayerHotbarSlots(Inventory playerInventory) {
		for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
			this.addSlot(new Slot(playerInventory, hotbarSlot, PLAYER_INV_START_X + hotbarSlot * 18, HOTBAR_Y));
		}
	}

	public static FurnaceMinecartMenu create(int containerId, Inventory inventory) {
		return new FurnaceMinecartMenu(containerId, inventory);
	}

	public static FurnaceMinecartMenu create(int containerId, Inventory inventory, Container container, ContainerData data) {
		return new FurnaceMinecartMenu(containerId, inventory, container, data);
	}

	@Override
	public boolean stillValid(Player player) {
		return this.container.stillValid(player);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack moved = ItemStack.EMPTY;
		if (index < 0 || index >= this.slots.size()) return moved;

		final Slot clickedSlot = this.slots.get(index);
		if (!clickedSlot.hasItem()) return moved;

		final ItemStack clickedStack = clickedSlot.getItem();
		moved = clickedStack.copy();
		if (index < SLOT_COUNT) {
			if (!this.moveItemStackTo(clickedStack, FIRST_PLAYER_SLOT, LAST_PLAYER_SLOT_EXCLUSIVE, true)) return ItemStack.EMPTY;
		} else if (!this.moveItemStackTo(clickedStack, FIRST_CONTAINER_SLOT, LAST_CONTAINER_SLOT_EXCLUSIVE, false)) {
			return ItemStack.EMPTY;
		}

		if (clickedStack.isEmpty()) {
			clickedSlot.setByPlayer(ItemStack.EMPTY);
		} else {
			clickedSlot.setChanged();
		}

		return moved;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		this.container.stopOpen(player);
	}

	public boolean isLit() {
		return this.data.get(0) > 0;
	}

	public int getLitProgress() {
		int litTime = this.data.get(0);
		int litDuration = this.data.get(1);
		if (litDuration <= 0) litDuration = DEFAULT_FUEL_DURATION;
		litTime = Mth.clamp(litTime, 0, litDuration);
		return Mth.clamp(Mth.ceil((float) litTime * LIT_PROGRESS_HEIGHT / (float) litDuration), 0, LIT_PROGRESS_HEIGHT);
	}
}
