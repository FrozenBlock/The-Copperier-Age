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

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.datafixers.util.Pair;
import java.util.stream.IntStream;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.entity.impl.FurnaceMinecartFacingInterface;
import net.frozenblock.thecopperierage.entity.inventory.FurnaceMinecartMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
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
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecartFurnace.class)
public abstract class MinecartFurnaceMixin extends AbstractMinecart implements ContainerEntity, WorldlyContainer, FurnaceMinecartFacingInterface {
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
    private static final String THECOPPERIERAGE$FACING_X_TAG_ID = "FacingX";
    @Unique
    private static final String THECOPPERIERAGE$FACING_Z_TAG_ID = "FacingZ";
    @Unique
    private static final double THECOPPERIERAGE$FURNACE_SPEED_CAP_UNDO = 2.0D;
    @Unique
    private static final double THECOPPERIERAGE$PUSH_MAGNITUDE = 0.0101D;
    @Unique
    private static final double THECOPPERIERAGE$FUELLED_COASTING = 0.975D;
    @Unique
    private static final int[] THECOPPERIERAGE$SLOTS_FOR_ALL_SIDES = IntStream.range(0, THECOPPERIERAGE$CONTAINER_SIZE).toArray();

    @Shadow
    private int fuel;

    @Unique
    private NonNullList<ItemStack> theCopperierAge$inventory = NonNullList.withSize(THECOPPERIERAGE$CONTAINER_SIZE, ItemStack.EMPTY);
    @Unique
    private int theCopperierAge$fuelDuration = THECOPPERIERAGE$DEFAULT_FUEL_DURATION;
    @Unique
    private Vec3 theCopperierAge$facing = Vec3.ZERO;
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
    private void theCopperierAge$pullFuelFromInventory(CallbackInfo info) {
        if (this.level().isClientSide()) return;
        if (!TCAConfig.IMPROVED_FURNACE_MINECARTS.get()) return;

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

        if (this.theCopperierAge$facing.lengthSqr() > 1.0E-4D) {
            minecartFurnace.push = this.theCopperierAge$facing.scale(THECOPPERIERAGE$PUSH_MAGNITUDE);
            return;
        }

        final Vec3 horizontalVelocity = this.getDeltaMovement().multiply(1D, 0D, 1D);
        if (horizontalVelocity.lengthSqr() > 1.0E-4D) {
            minecartFurnace.push = horizontalVelocity.normalize().scale(THECOPPERIERAGE$PUSH_MAGNITUDE);
            return;
        }

        final Vec3 facingDirection = Vec3.directionFromRotation(0F, this.getYRot()).multiply(1D, 0D, 1D);
        if (facingDirection.lengthSqr() > 1.0E-4D) {
            minecartFurnace.push = facingDirection.normalize().scale(THECOPPERIERAGE$PUSH_MAGNITUDE);
            return;
        }

        minecartFurnace.push = new Vec3(THECOPPERIERAGE$PUSH_MAGNITUDE, 0D, 0D);
    }

    @Unique
    private boolean theCopperierAge$isFiniteHorizontal(Vec3 vec) {
        return Double.isFinite(vec.x()) && Double.isFinite(vec.z());
    }

    @Override
    public void theCopperierAge$setFacing(Vec3 facing) {
        final Vec3 horizontal = facing.multiply(1D, 0D, 1D);
        if (horizontal.lengthSqr() < 1.0E-4D) return;

        // Snap the aimed direction to the rail the cart sits on, keeping whichever rail end the
        // player was looking toward. This leaves the furnace rail-aligned (never askew on the
        // track) while still letting the placer pick forward vs backward along the rail.
        this.theCopperierAge$facing = this.theCopperierAge$railAlignedDirection(horizontal.normalize());

        // Only redirect a live push (fuelled + moving). Writing push onto a stationary,
        // unfuelled cart would accelerate it, since applyNaturalSlowdown adds push to
        // velocity every tick regardless of fuel.
        if (this.fuel > 0) {
            MinecartFurnace.class.cast(this).push = this.theCopperierAge$facing.scale(THECOPPERIERAGE$PUSH_MAGNITUDE);
        }

        // Turn the cart (and therefore the rendered furnace) to face the aimed direction.
        // Minecarts use their own yaw convention -- 180 - atan2(z, x) -- which is what
        // NewMinecartBehavior#adjustToRails, #moveAlongTrack and the renderer all use. Using
        // Direction#toYRot here put the yaw 90 degrees out, so adjustToRails' 180-degree
        // flip-detection never matched and the cart just snapped to the rail's canonical
        // direction, ignoring the chosen forward/backward facing.
        final float yRot = 180F - (float) (Math.atan2(this.theCopperierAge$facing.z, this.theCopperierAge$facing.x) * 180D / Math.PI);
        this.setYRot(yRot);
        this.yRotO = yRot;
    }

    @Unique
    private Vec3 theCopperierAge$railAlignedDirection(Vec3 look) {
        final BlockPos pos = this.getCurrentBlockPosOrRailBelow();
        final BlockState state = this.level().getBlockState(pos);
        if (BaseRailBlock.isRail(state)) {
            final RailShape shape = state.getValue(((BaseRailBlock) state.getBlock()).getShapeProperty());
            final Pair<Vec3i, Vec3i> exits = AbstractMinecart.exits(shape);
            // A rail's orientation is the line between its two exits. This matches the resting yaw
            // NewMinecartBehavior#adjustToRails gives every shape -- straights, slopes and corners
            // alike (a corner rests along the chord between its two arms, not along a single arm).
            // Pick whichever way along that line the player was looking.
            final Vec3 axis = new Vec3(exits.getSecond()).subtract(new Vec3(exits.getFirst())).horizontal();
            if (axis.lengthSqr() > 1.0E-4D) {
                final Vec3 dir = axis.normalize();
                return look.dot(dir) >= 0.0D ? dir : dir.scale(-1.0D);
            }
        }

        // Off-rail: fall back to the nearest cardinal so the furnace still faces cleanly.
        final Direction cardinal = Direction.getApproximateNearest(look.x, 0D, look.z);
        return new Vec3(cardinal.getStepX(), 0D, cardinal.getStepZ());
    }

    @ModifyConstant(method = "applyNaturalSlowdown", constant = @Constant(doubleValue = 0.8D))
    private double theCopperierAge$fuelledCartsCoastLikeNormalCarts(double vanillaDrag) {
        if (!TCAConfig.IMPROVED_FURNACE_MINECARTS.get()) return vanillaDrag;
        return THECOPPERIERAGE$FUELLED_COASTING;
    }

    @ModifyReturnValue(method = "getMaxSpeed(Lnet/minecraft/server/level/ServerLevel;)D", at = @At("RETURN"))
    private double theCopperierAge$allowBoostAboveSelfPropelledSpeed(double original) {
        if (!TCAConfig.IMPROVED_FURNACE_MINECARTS.get()) return original;
        return original * THECOPPERIERAGE$FURNACE_SPEED_CAP_UNDO;
    }

    @ModifyReturnValue(method = "getDefaultDisplayBlockState", at = @At("RETURN"))
    private BlockState theCopperierAge$faceFurnaceForward(BlockState original) {
        if (!TCAConfig.IMPROVED_FURNACE_MINECARTS.get()) return original;
        // Vanilla bakes the display furnace at FACING=NORTH, which the minecart renderer then
        // rotates by the cart yaw -- leaving the furnace's front pointing opposite the travel
        // direction. Flip it to SOUTH so the lit front leads the way the cart is heading.
        return original.setValue(FurnaceBlock.FACING, Direction.SOUTH);
    }

    @Unique
    private boolean theCopperierAge$isValidSlot(int slot) {
        return slot >= 0 && slot < this.theCopperierAge$inventory.size();
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void theCopperierAge$openInventory(Player player, InteractionHand hand, Vec3 vec3, CallbackInfoReturnable<InteractionResult> info) {
        if (!TCAConfig.IMPROVED_FURNACE_MINECARTS.get()) return;
        if (player.isShiftKeyDown()) {
            if (!this.level().isClientSide()) {
                this.theCopperierAge$setFacing(player.getLookAngle());
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
        output.putDouble(THECOPPERIERAGE$FACING_X_TAG_ID, this.theCopperierAge$facing.x);
        output.putDouble(THECOPPERIERAGE$FACING_Z_TAG_ID, this.theCopperierAge$facing.z);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void theCopperierAge$loadInventory(ValueInput input, CallbackInfo info) {
        this.theCopperierAge$inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.theCopperierAge$inventory);
        this.theCopperierAge$fuelDuration = input.getInt(THECOPPERIERAGE$FUEL_DURATION_TAG_ID).orElse(THECOPPERIERAGE$DEFAULT_FUEL_DURATION);
        this.theCopperierAge$facing = new Vec3(
            input.getDoubleOr(THECOPPERIERAGE$FACING_X_TAG_ID, 0D),
            0D,
            input.getDoubleOr(THECOPPERIERAGE$FACING_Z_TAG_ID, 0D)
        );
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
