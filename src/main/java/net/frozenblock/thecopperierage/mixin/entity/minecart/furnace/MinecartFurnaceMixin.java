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

import java.util.stream.IntStream;
import net.frozenblock.thecopperierage.entity.inventory.FurnaceMinecartMenu;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecartFurnace.class)
public abstract class MinecartFurnaceMixin extends AbstractMinecart implements Container, MenuProvider, WorldlyContainer {
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
    private static final int[] THECOPPERIERAGE$SLOTS_FOR_ALL_SIDES = IntStream.range(0, THECOPPERIERAGE$CONTAINER_SIZE).toArray();

    @Shadow
    private int fuel;

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

    protected MinecartFurnaceMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void theCopperierAge$pullFuelFromInventory(CallbackInfo info) {
        if (this.level().isClientSide()) return;
        if (!TCAConfig.IMPROVED_FURNACE_MINECARTS) return;

        final MinecartFurnace minecartFurnace = MinecartFurnace.class.cast(this);
        if (!this.theCopperierAge$isFiniteHorizontal(minecartFurnace.push)) {
            minecartFurnace.push = Vec3.ZERO;
        }

        if (this.fuel > 0) {
            this.theCopperierAge$ensurePushDirection();
            return;
        }

        for (int slot = 0; slot < this.theCopperierAge$inventory.size(); slot++) {
            if (this.theCopperierAge$tryConsumeFuel(slot)) break;
        }
    }

    @Unique
    private boolean theCopperierAge$tryConsumeFuel(int slot) {
        final ItemStack stack = this.theCopperierAge$inventory.get(slot);
        final int burnDuration = this.level().fuelValues().burnDuration(stack);
        if (burnDuration <= 0) return false;

        final ItemStack remainder = stack.getItem().getCraftingRemainder();
        stack.shrink(1);
        if (stack.isEmpty()) {
            this.theCopperierAge$inventory.set(slot, remainder.isEmpty() ? ItemStack.EMPTY : remainder.copy());
        } else if (!remainder.isEmpty()) {
            this.theCopperierAge$placeRemainder(slot, remainder.copy());
        }

        this.fuel = Math.min(this.fuel + burnDuration, THECOPPERIERAGE$MAX_FUEL);
        this.theCopperierAge$fuelDuration = Math.max(THECOPPERIERAGE$MIN_BURN_DURATION, burnDuration);
        this.theCopperierAge$ensurePushDirection();
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
    private void theCopperierAge$ensurePushDirection() {
        final MinecartFurnace minecartFurnace = MinecartFurnace.class.cast(this);
        if (!this.theCopperierAge$isFiniteHorizontal(minecartFurnace.push)) minecartFurnace.push = Vec3.ZERO;
        if (!Mth.equal((float) minecartFurnace.push.x, 0F) || !Mth.equal((float) minecartFurnace.push.z, 0F)) return;

        final Vec3 horizontalVelocity = this.getDeltaMovement().multiply(1D, 0D, 1D);
        if (horizontalVelocity.lengthSqr() > 1.0E-4D) {
            minecartFurnace.push = horizontalVelocity.normalize();
            return;
        }

        final Vec3 facingDirection = Vec3.directionFromRotation(0F, this.getYRot()).multiply(1D, 0D, 1D);
        if (facingDirection.lengthSqr() > 1.0E-4D) {
            minecartFurnace.push = facingDirection.normalize();
            return;
        }

        minecartFurnace.push = new Vec3(1D, 0D, 0D);
    }

    @Unique
    private boolean theCopperierAge$isFiniteHorizontal(Vec3 vec) {
        return Double.isFinite(vec.x()) && Double.isFinite(vec.z());
    }

    @Unique
    private boolean theCopperierAge$isValidSlot(int slot) {
        return slot >= 0 && slot < this.theCopperierAge$inventory.size();
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void theCopperierAge$openInventory(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
        if (!TCAConfig.IMPROVED_FURNACE_MINECARTS) return;
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
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return this.isAlive() && player.canInteractWithEntity(this, 4D);
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
