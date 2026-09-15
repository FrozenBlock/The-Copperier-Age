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

import java.util.Optional;
import net.frozenblock.thecopperierage.block.CrateBlock;
import net.frozenblock.thecopperierage.block.entity.CrateBlockEntity;
import net.frozenblock.thecopperierage.block.entity.inventory.CrateMenu;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.registry.TCABlocks;
import net.frozenblock.thecopperierage.registry.TCAEntityTypes;
import net.frozenblock.thecopperierage.registry.TCAItems;
import net.frozenblock.thecopperierage.registry.TCASounds;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecartContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class MinecartCrate extends AbstractMinecartContainer {
	private NonNullList<ItemStack> invalidItems = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
	private int openCount;

	public MinecartCrate(EntityType<MinecartCrate> entityType, Level level) {
		super(entityType, level);
	}

	public MinecartCrate(Level level, double x, double y, double z) {
		this(TCAEntityTypes.CRATE_MINECART.get(), level);
		this.setPos(x, y, z);
	}

	@Override
	public int getContainerSize() {
		return CrateBlockEntity.CONTAINER_SIZE;
	}

	@Override
	protected Item getDropItem() {
		return TCAItems.CRATE_MINECART.get();
	}

	@Override
	public ItemStack getPickResult() {
		return new ItemStack(TCAItems.CRATE_MINECART);
	}

	@Override
	public void tick() {
		super.tick();
		this.tickCrate();
	}

	private void tickCrate() {
		if (this.level().isClientSide()) return;
		if (this.invalidItems.isEmpty()) return;

		final Direction facing = this.getDisplayBlockState().getValueOrElse(CrateBlock.FACING, Direction.UP);
		for (ItemStack stack : this.invalidItems) {
			CrateBlockEntity.dispense(
				this.level(),
				this.position().add(0D, 0.5D, 0D),
				facing,
				Optional.of(stack),
				this,
				true,
				sound -> {
					if (this.isSilent()) return;
					this.level().playSound(
						null,
						this.getX(), this.getY(), this.getZ(),
						sound,
						SoundSource.BLOCKS,
						0.2F, (this.getRandom().nextFloat() * 0.25F) + 0.8F
					);
				}
			);
		}
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
		if (!TCAConfig.CRATE_HAS_MENU.get()) return InteractionResult.PASS;
		return super.interact(player, hand, location);
	}

	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return CrateMenu.create(containerId, inventory, this);
	}

	@Override
	public void startOpen(ContainerUser user) {
		super.startOpen(user);
		if (this.openCount++ == 0) {
			this.updateOpenState(true);
			this.playCrateSound(true);
		}
	}

	@Override
	public void stopOpen(ContainerUser user) {
		super.stopOpen(user);
		if (this.openCount > 0 && --this.openCount <= 0) {
			this.updateOpenState(false);
			this.playCrateSound(false);
		}
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		final CrateBlock.SlotResult result = CrateBlock.verifyStackForPlacement(stack, this);
		if (!result.isSuccess() && !result.isEmptyItem()) {
			this.invalidItems.set(slot, stack);
			stack.limitSize(this.getMaxStackSize(stack));
			return;
		}

		super.setItem(slot, stack);
	}

	@Override
	public BlockState getDefaultDisplayBlockState() {
		return TCABlocks.CRATE.get()
			.defaultBlockState()
			.setValue(CrateBlock.FACING, Direction.UP)
			.setValue(CrateBlock.OPEN, false);
	}

	@Override
	public int getDefaultDisplayOffset() {
		return 6;
	}

	private void updateOpenState(boolean isOpen) {
		this.setCustomDisplayBlockState(Optional.of(this.getDefaultDisplayBlockState().setValue(CrateBlock.OPEN, isOpen)));
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		CrateBlockEntity.saveInvalidItems(output, this.invalidItems);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		this.invalidItems = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		CrateBlockEntity.loadInvalidItems(input, this.invalidItems);
	}

	private void playCrateSound(boolean opening) {
		if (this.level().isClientSide() || this.isSilent()) return;

		final SoundEvent sound = opening
			? TCASounds.BLOCK_CRATE_OPEN.get()
			: TCASounds.BLOCK_CRATE_CLOSE.get();

		this.level().playSound(
			null,
			this.getX(),
			this.getY(),
			this.getZ(),
			sound,
			SoundSource.BLOCKS,
			0.5F,
			this.level().getRandom().nextFloat() * 0.1F + 0.9F
		);
	}
}
