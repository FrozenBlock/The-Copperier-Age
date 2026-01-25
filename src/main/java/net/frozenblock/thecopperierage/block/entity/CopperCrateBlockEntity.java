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

import java.util.List;
import net.frozenblock.thecopperierage.block.CopperCrateBlock;
import net.frozenblock.thecopperierage.block.entity.inventory.CrateMenu;
import net.frozenblock.thecopperierage.registry.TCABlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class CopperCrateBlockEntity extends RandomizableContainerBlockEntity {
	public static int ROW_COUNT = 4;
	public static final int CONTAINER_SIZE = ROW_COUNT * 9;
	private static final Component DEFAULT_NAME = Component.translatable("container.thecopperierage.copper_crate");
	private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
	private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
		@Override
		protected void onOpen(Level level, BlockPos pos, BlockState state) {
			if (state.getBlock() instanceof CopperCrateBlock copperCrate) CopperCrateBlockEntity.this.playSound(state, copperCrate.getOpenSound());
			CopperCrateBlockEntity.this.updateBlockState(state, true);
		}

		@Override
		protected void onClose(Level level, BlockPos pos, BlockState state) {
			if (state.getBlock() instanceof CopperCrateBlock copperCrate) CopperCrateBlockEntity.this.playSound(state, copperCrate.getCloseSound());
			CopperCrateBlockEntity.this.updateBlockState(state, false);
		}

		@Override
		protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int prevOpenCount, int openCount) {
		}

		@Override
		public boolean isOwnContainer(Player player) {
			if (!(player.containerMenu instanceof ChestMenu chestMenu)) return false;

			final net.minecraft.world.Container container = chestMenu.getContainer();
			return container == CopperCrateBlockEntity.this;
		}
	};

	public CopperCrateBlockEntity(BlockPos pos, BlockState state) {
		super(TCABlockEntityTypes.COPPER_CRATE, pos, state);
	}

	// TODO: Config
	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		if (!true) return;
		super.preRemoveSideEffects(pos, state);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		if (!this.trySaveLootTable(output)) ContainerHelper.saveAllItems(output, this.items);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		if (!this.tryLoadLootTable(input)) ContainerHelper.loadAllItems(input, this.items);
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder builder) {
		super.collectImplicitComponents(builder);

		final List<ItemStack> items = this.getItems();
		if (!items.isEmpty() && items.stream().anyMatch(stack -> !stack.isEmpty())) {
			builder.set(DataComponents.MAX_STACK_SIZE, 1);
		} else {
			builder.set(DataComponents.MAX_STACK_SIZE, this.getBlockState().getBlock().asItem().getDefaultMaxStackSize());
		}
	}

	@Override
	public int getContainerSize() {
		return CONTAINER_SIZE;
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> nonNullList) {
		this.items = nonNullList;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return CopperCrateBlock.verifyStackForPlacement(stack, this).isSuccess();
	}

	@Override
	protected Component getDefaultName() {
		return DEFAULT_NAME;
	}

	@Override
	protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
		return CrateMenu.create(i, inventory, this);
	}

	@Override
	public void startOpen(ContainerUser user) {
		if (this.remove || user.getLivingEntity().isSpectator()) return;
		this.openersCounter.incrementOpeners(
			user.getLivingEntity(),
			this.getLevel(),
			this.getBlockPos(),
			this.getBlockState(),
			user.getContainerInteractionRange()
		);
	}

	@Override
	public void stopOpen(ContainerUser user) {
		if (this.remove || user.getLivingEntity().isSpectator()) return;
		this.openersCounter.decrementOpeners(user.getLivingEntity(), this.getLevel(), this.getBlockPos(), this.getBlockState());
	}

	@Override
	public List<ContainerUser> getEntitiesWithContainerOpen() {
		return this.openersCounter.getEntitiesWithContainerOpen(this.getLevel(), this.getBlockPos());
	}

	public void recheckOpen() {
		if (this.remove) return;
		this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
	}

	void updateBlockState(BlockState state, boolean open) {
		this.level.setBlock(this.getBlockPos(), state.setValue(CopperCrateBlock.OPEN, open), Block.UPDATE_ALL);
	}

	void playSound(BlockState state, SoundEvent sound) {
		final Vec3i directionOffset = (state.getValue(BarrelBlock.FACING)).getUnitVec3i();
		final double x = this.worldPosition.getX() + 0.5D + directionOffset.getX() / 2D;
		final double y = this.worldPosition.getY() + 0.5D + directionOffset.getY() / 2D;
		final double z = this.worldPosition.getZ() + 0.5D + directionOffset.getZ() / 2D;
		this.level.playSound(null, x, y, z, sound, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
	}
}
