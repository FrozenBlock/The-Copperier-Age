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
import java.util.Optional;
import java.util.stream.IntStream;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.frozenblock.thecopperierage.block.CrateBlock;
import net.frozenblock.thecopperierage.block.entity.inventory.CrateMenu;
import net.frozenblock.thecopperierage.registry.TCABlockEntityTypes;
import net.frozenblock.thecopperierage.tag.TCABlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CrateBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {
	public static int ROW_COUNT = 4;
	public static final int CONTAINER_SIZE = ROW_COUNT * 9;
	private static final int[] SLOTS = IntStream.range(0, CONTAINER_SIZE).toArray();
	private static final Component DEFAULT_NAME = Component.translatable("container.thecopperierage.crate");
	private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
	private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
		@Override
		protected void onOpen(Level level, BlockPos pos, BlockState state) {
			if (state.getBlock() instanceof CrateBlock copperCrate) CrateBlockEntity.this.playSound(state, copperCrate.getOpenSound());
			CrateBlockEntity.this.updateBlockState(state, true);
		}

		@Override
		protected void onClose(Level level, BlockPos pos, BlockState state) {
			if (state.getBlock() instanceof CrateBlock copperCrate) CrateBlockEntity.this.playSound(state, copperCrate.getCloseSound());
			CrateBlockEntity.this.updateBlockState(state, false);
		}

		@Override
		protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int prevOpenCount, int openCount) {
		}

		@Override
		public boolean isOwnContainer(Player player) {
			if (!(player.containerMenu instanceof ChestMenu chestMenu)) return false;

			final net.minecraft.world.Container container = chestMenu.getContainer();
			return container == CrateBlockEntity.this;
		}
	};

	public CrateBlockEntity(BlockPos pos, BlockState state) {
		super(TCABlockEntityTypes.CRATE, pos, state);
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
	public void setItem(int slot, ItemStack stack) {
		this.unpackLootTable(null);
		if (!CrateBlock.verifyStackForPlacement(stack, this).isSuccess()) {
			final Level level = this.getLevel();
			final BlockPos pos = this.getBlockPos();
			final BlockState state = this.getBlockState();
			this.moveOut(level, pos, state, stack);
			this.dispense(level, pos, state, Optional.of(stack), true);
			return;
		}

		super.setItem(slot, stack);
	}

	private boolean moveOut(Level level, BlockPos pos, BlockState state, ItemStack stack) {
		if (stack.isEmpty()) return false;

		final Direction facing = state.getValue(CrateBlock.FACING);
		final BlockPos facingPos = pos.relative(facing);
		final BlockState outputState = level.getBlockState(facingPos);
		if (outputState.is(TCABlockTags.CRATES)) return false;

		final Storage<ItemVariant> outputInventory = ItemStorage.SIDED.find(level, facingPos, outputState, level.getBlockEntity(facingPos), facing.getOpposite());
		if (outputInventory == null) return false;
		if (!outputInventory.supportsInsertion()) return false;

		final Transaction transaction = Transaction.openOuter();
		final ItemVariant item = ItemVariant.of(stack);
		final long inserted = outputInventory.insert(item, stack.getCount(), transaction);
		if (inserted > 0) { // successfully inserted item
			transaction.commit(); // applies the changes
			stack.shrink((int) inserted);
			return true;
		}

		transaction.close(); // if it can't commit, close it.
		return false;
	}

	private boolean dispense(Level level, BlockPos pos, BlockState state, Optional<ItemStack> selectedStack, boolean dispenseWholeStack) {
		final int slot = selectedStack.isPresent() ? 0 : this.chooseNonEmptySlot(level.random);
		if (slot < 0) return false;

		final ItemStack stack = selectedStack.orElse(this.getItem(slot));
		if (stack.isEmpty()) return false;

		final ItemStack dispensedItem = this.dispenseItem(level, pos, state, stack, dispenseWholeStack);
		if (selectedStack.isEmpty()) this.setItem(slot, dispensedItem);

		return true;
	}

	private ItemStack dispenseItem(Level level, BlockPos pos, BlockState state, ItemStack stack, boolean dispenseWholeStack) {
		final Direction facing = state.getValue(CrateBlock.FACING);
		final Vec3 dispensePos = pos.getCenter().relative(facing, 0.7D);
		final ItemStack dispenseStack = dispenseWholeStack ? stack.copyAndClear() : stack.split(1);
		//level.levelEvent(LevelEvent.PARTICLES_SHOOT_SMOKE, pos, facing.get3DDataValue());
		DefaultDispenseItemBehavior.spawnItem(level, dispenseStack, 2, facing, dispensePos);
		// TODO: sound
		level.playSound(null, pos, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 0.2F, (level.random.nextFloat() * 0.25F) + 0.8F);

		return stack;
	}

	public int chooseNonEmptySlot(RandomSource random) {
		this.unpackLootTable(null);
		int slot = -1;
		int selectionChance = 1;
		for (int k = 0; k < this.items.size(); ++k) {
			if (!this.items.get(k).isEmpty() && random.nextInt(selectionChance++) == 0) slot = k;
		}
		return slot;
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
		this.level.setBlock(this.getBlockPos(), state.setValue(CrateBlock.OPEN, open), Block.UPDATE_ALL);
	}

	void playSound(BlockState state, SoundEvent sound) {
		final Vec3i directionOffset = (state.getValue(BarrelBlock.FACING)).getUnitVec3i();
		final double x = this.worldPosition.getX() + 0.5D + directionOffset.getX() / 2D;
		final double y = this.worldPosition.getY() + 0.5D + directionOffset.getY() / 2D;
		final double z = this.worldPosition.getZ() + 0.5D + directionOffset.getZ() / 2D;
		this.level.playSound(null, x, y, z, sound, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
	}

	@Override
	public int[] getSlotsForFace(Direction direction) {
		return SLOTS;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction) {
		return direction == null || direction != this.getBlockState().getValue(CrateBlock.FACING);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
		return true;
	}
}
