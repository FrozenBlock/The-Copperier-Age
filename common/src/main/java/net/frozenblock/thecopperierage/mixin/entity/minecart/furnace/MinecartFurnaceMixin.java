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

package net.frozenblock.thecopperierage.mixin.entity.minecart.furnace;

import java.util.function.Consumer;
import java.util.stream.IntStream;
import net.frozenblock.thecopperierage.block.RelayerRailBlock;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.entity.impl.MinecartFacingHelper;
import net.frozenblock.thecopperierage.entity.inventory.FurnaceMinecartMenu;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartFurnace;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecartFurnace.class)
public abstract class MinecartFurnaceMixin extends AbstractMinecart implements ContainerEntity, WorldlyContainer {
	@Unique
	private static final int THECOPPERIERAGE$CONTAINER_SIZE = FurnaceMinecartMenu.SLOT_COUNT;
	@Unique
	private static final int THECOPPERIERAGE$MAX_FUEL = 32000;
	@Unique
	private static final int THECOPPERIERAGE$MIN_BURN_DURATION = 1;
	@Unique
	private static final int THECOPPERIERAGE$DEFAULT_FUEL_DURATION = 200;
	@Unique
	private static final int THECOPPERIERAGE$MENU_DATA_COUNT = 2;
	@Unique
	private static final String THECOPPERIERAGE$FUEL_DURATION_TAG_ID = "FuelDuration";
	@Unique
	private static final double THECOPPERIERAGE$TARGET_SPEED = 0.2D;
	@Unique
	private static final double THECOPPERIERAGE$WATER_TARGET_SCALE = 0.5D;
	@Unique
	private static final double THECOPPERIERAGE$MAX_THRUST = 0.1D;
	@Unique
	private static final int[] THECOPPERIERAGE$SLOTS_FOR_ALL_SIDES = IntStream.range(0, THECOPPERIERAGE$CONTAINER_SIZE).toArray();

	@Shadow
	private int fuel;
	@Shadow
	public Vec3 push;

	@Unique
	private NonNullList<ItemStack> theCopperierAge$inventory = NonNullList.withSize(THECOPPERIERAGE$CONTAINER_SIZE, ItemStack.EMPTY);
	@Unique
	private int theCopperierAge$fuelDuration = THECOPPERIERAGE$DEFAULT_FUEL_DURATION;
	@Unique
	private final ContainerData theCopperierAge$menuData = new ContainerData() {
		@Override
		public int get(int index) {
			return switch (index) {
				case 0 -> MinecartFurnaceMixin.this.fuel;
				case 1 -> MinecartFurnaceMixin.this.theCopperierAge$fuelDuration;
				default -> 0;
			};
		}

		@Override
		public void set(int index, int value) {
			if (index == 0) MinecartFurnaceMixin.this.fuel = value;
		}

		@Override
		public int getCount() {
			return THECOPPERIERAGE$MENU_DATA_COUNT;
		}
	};
	@Unique
	private final Consumer<Vec3> theCopperierAge$onFacingSet = facing -> {
		if (this.fuel > 0) this.push = facing.scale(THECOPPERIERAGE$TARGET_SPEED);
	};

	protected MinecartFurnaceMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	protected void destroy(ServerLevel level, DamageSource source) {
		super.destroy(level, source);
		this.chestVehicleDestroyed(source, level, this);
	}

	@Override
	public void remove(Entity.RemovalReason reason) {
		if (!this.level().isClientSide() && reason.shouldDestroy()) Containers.dropContents(this.level(), this, this);
		super.remove(reason);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void theCopperierAge$tickPropulsion(CallbackInfo info) {
		if (this.level().isClientSide()) return;
		if (!TCAConfig.IMPROVED_FURNACE_MINECARTS.get()) return;

		final MinecartFurnace minecartFurnace = MinecartFurnace.class.cast(this);
		if (!this.theCopperierAge$isFiniteHorizontal(minecartFurnace.push)) minecartFurnace.push = Vec3.ZERO;

		if (this.fuel <= 0 && !RelayerRailBlock.isCaptured(this.level(), minecartFurnace)) {
			for (int slot = 0; slot < this.theCopperierAge$inventory.size(); slot++) {
				if (this.theCopperierAge$tryConsumeFuel(slot)) break;
			}
		}

		minecartFurnace.push = this.fuel > 0 && MinecartFacingHelper.hasFacing(this)
			? MinecartFacingHelper.getFacing(this).scale(THECOPPERIERAGE$TARGET_SPEED)
			: Vec3.ZERO;
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void theCopperierAge$alignFacing(CallbackInfo info) {
		if (this.level().isClientSide()) return;
		if (!TCAConfig.IMPROVED_FURNACE_MINECARTS.get()) return;

		final MinecartFurnace minecartFurnace = MinecartFurnace.class.cast(this);
		MinecartFacingHelper.alignFacingToTrack(minecartFurnace, this.theCopperierAge$onFacingSet);
	}

	@Unique
	private boolean theCopperierAge$tryConsumeFuel(int slot) {
		final ItemStack stack = this.theCopperierAge$inventory.get(slot);
		final int burnDuration = this.level().fuelValues().burnDuration(stack);
		if (burnDuration <= 0) return false;

		final ItemStackTemplate remainderTemplate = stack.getItem().getCraftingRemainder();
		final ItemStack remainder = remainderTemplate == null ? ItemStack.EMPTY : remainderTemplate.create();
		stack.shrink(1);
		if (stack.isEmpty()) {
			this.theCopperierAge$inventory.set(slot, remainder.isEmpty() ? ItemStack.EMPTY : remainder.copy());
		} else if (!remainder.isEmpty()) {
			this.theCopperierAge$placeRemainder(slot, remainder.copy());
		}

		this.fuel = Math.min(this.fuel + burnDuration, THECOPPERIERAGE$MAX_FUEL);
		this.theCopperierAge$fuelDuration = Math.max(THECOPPERIERAGE$MIN_BURN_DURATION, burnDuration);
		this.setChanged();
		return true;
	}

	@Unique
	private void theCopperierAge$placeRemainder(int sourceSlot, ItemStack remainder) {
		for (int slot = 0; slot < this.theCopperierAge$inventory.size(); slot++) {
			if (slot == sourceSlot) continue;

			final ItemStack existing = this.theCopperierAge$inventory.get(slot);
			if (existing.isEmpty()) {
				this.theCopperierAge$inventory.set(slot, remainder);
				return;
			}

			if (ItemStack.isSameItemSameComponents(existing, remainder) && existing.getCount() < existing.getMaxStackSize()) {
				existing.grow(1);
				return;
			}
		}

		if (this.level() instanceof ServerLevel serverLevel) this.spawnAtLocation(serverLevel, remainder);
	}

	@Unique
	private boolean theCopperierAge$isFiniteHorizontal(Vec3 vec) {
		return Double.isFinite(vec.x()) && Double.isFinite(vec.z());
	}

	@Inject(method = "applyNaturalSlowdown", at = @At("HEAD"), cancellable = true)
	private void theCopperierAge$applyPropulsion(Vec3 deltaMovement, CallbackInfoReturnable<Vec3> info) {
		if (!TCAConfig.IMPROVED_FURNACE_MINECARTS.get()) return;

		Vec3 result = super.applyNaturalSlowdown(deltaMovement);
		if (this.fuel > 0 && MinecartFacingHelper.hasFacing(this) && !RelayerRailBlock.isCaptured(this.level(), this)) {
			final Vec3 facing = MinecartFacingHelper.getFacing(this);
			final double targetSpeed = this.isInWater() ? THECOPPERIERAGE$TARGET_SPEED * THECOPPERIERAGE$WATER_TARGET_SCALE : THECOPPERIERAGE$TARGET_SPEED;
			final double forwardSpeed = result.x * facing.x + result.z * facing.z;
			final double thrust = Mth.clamp(targetSpeed - forwardSpeed, 0D, THECOPPERIERAGE$MAX_THRUST);
			if (thrust > 0D) result = result.add(facing.x * thrust, 0D, facing.z * thrust);
		}

		info.setReturnValue(result);
	}

	@Inject(method = "getMaxSpeed(Lnet/minecraft/server/level/ServerLevel;)D", at = @At("HEAD"), cancellable = true)
	private void theCopperierAge$useRegularMaxSpeed(ServerLevel level, CallbackInfoReturnable<Double> info) {
		if (!TCAConfig.IMPROVED_FURNACE_MINECARTS.get()) return;
		info.setReturnValue(super.getMaxSpeed(level));
	}

	@Unique
	private boolean theCopperierAge$isValidSlot(int slot) {
		return slot >= 0 && slot < this.theCopperierAge$inventory.size();
	}

	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	private void theCopperierAge$openInventory(Player player, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> info) {
		if (!TCAConfig.IMPROVED_FURNACE_MINECARTS.get()) return;

		if (player.isShiftKeyDown()) {
			if (!this.level().isClientSide()) {
				MinecartFacingHelper.setFacing(this, player.getLookAngle(), this.theCopperierAge$onFacingSet);
				this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ITEM_FRAME_ROTATE_ITEM, this.getSoundSource(), 0.8F, 1.0F);
			}

			info.setReturnValue(InteractionResult.SUCCESS);
			return;
		}

		if (this.level().isClientSide()) {
			info.setReturnValue(InteractionResult.SUCCESS);
			return;
		}

		player.openMenu(this);
		info.setReturnValue(InteractionResult.CONSUME);
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void theCopperierAge$saveInventory(ValueOutput output, CallbackInfo info) {
		ContainerHelper.saveAllItems(output, this.theCopperierAge$inventory);
		output.putInt(THECOPPERIERAGE$FUEL_DURATION_TAG_ID, this.theCopperierAge$fuelDuration);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void theCopperierAge$loadInventory(ValueInput input, CallbackInfo info) {
		this.theCopperierAge$inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(input, this.theCopperierAge$inventory);
		this.theCopperierAge$fuelDuration = input.getInt(THECOPPERIERAGE$FUEL_DURATION_TAG_ID).orElse(THECOPPERIERAGE$DEFAULT_FUEL_DURATION);
	}

	@Override
	public Component getDisplayName() {
		return this.getName();
	}

	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
		return FurnaceMinecartMenu.create(containerId, inventory, this, this.theCopperierAge$menuData);
	}

	@Override
	public int getContainerSize() {
		return THECOPPERIERAGE$CONTAINER_SIZE;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack stack : this.theCopperierAge$inventory) {
			if (!stack.isEmpty()) return false;
		}
		return true;
	}

	@Override
	public ItemStack getItem(int slot) {
		if (!this.theCopperierAge$isValidSlot(slot)) return ItemStack.EMPTY;
		return this.theCopperierAge$inventory.get(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		if (!this.theCopperierAge$isValidSlot(slot) || amount <= 0) return ItemStack.EMPTY;
		final ItemStack stack = ContainerHelper.removeItem(this.theCopperierAge$inventory, slot, amount);
		if (!stack.isEmpty()) this.setChanged();
		return stack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		if (!this.theCopperierAge$isValidSlot(slot)) return ItemStack.EMPTY;
		return ContainerHelper.takeItem(this.theCopperierAge$inventory, slot);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		if (!this.theCopperierAge$isValidSlot(slot)) return;
		this.theCopperierAge$inventory.set(slot, stack);
		stack.limitSize(this.getMaxStackSize(stack));
		this.setChanged();
	}

	@Override
	public void setChanged() {}

	@Override
	public boolean stillValid(Player player) {
		return this.isAlive() && player.isWithinEntityInteractionRange(this, 4.0D);
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return this.theCopperierAge$isValidSlot(slot);
	}

	@Override
	public void clearContent() {
		this.theCopperierAge$inventory.clear();
		this.setChanged();
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return THECOPPERIERAGE$SLOTS_FOR_ALL_SIDES;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
		return this.canPlaceItem(slot, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
		return true;
	}
}
