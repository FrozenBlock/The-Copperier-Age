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

package net.frozenblock.thecopperierage.mixin.entity.chest_boat;

import net.frozenblock.thecopperierage.entity.impl.ChestLidAnimating;
import net.frozenblock.thecopperierage.entity.impl.ChestVehicleAnimationConstants;
import net.frozenblock.thecopperierage.registry.TCAAttachments;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.vehicle.AbstractChestBoat;
import net.minecraft.world.level.block.entity.ChestLidController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractChestBoat.class)
public abstract class AbstractChestBoatMixin implements ChestLidAnimating {
	@Unique
	private final ChestLidController theCopperierAge$lidController = new ChestLidController();
	@Unique
	private long theCopperierAge$lastLidTickGameTime = Long.MIN_VALUE;

	@Override
	@Unique
	public void theCopperierAge$onContainerOpen() {
		final int openers = this.theCopperierAge$getOpeners();
		this.theCopperierAge$setOpeners(openers + 1);

		if (openers == 0) {
			this.theCopperierAge$setLidShouldBeOpen(true);
			if (!this.theCopperierAge$level().isClientSide()) {
				this.theCopperierAge$level().broadcastEntityEvent((AbstractChestBoat) (Object) this, ChestVehicleAnimationConstants.OPEN_EVENT);
				this.theCopperierAge$playChestSound(true);
			}
		}
	}

	@Override
	@Unique
	public void theCopperierAge$onContainerClose() {
		final int openers = this.theCopperierAge$getOpeners();
		if (openers <= 0) return;

		final int nextOpeners = openers - 1;
		this.theCopperierAge$setOpeners(nextOpeners);

		if (nextOpeners == 0) {
			this.theCopperierAge$setLidShouldBeOpen(false);
			if (!this.theCopperierAge$level().isClientSide()) {
				this.theCopperierAge$level().broadcastEntityEvent((AbstractChestBoat) (Object) this, ChestVehicleAnimationConstants.CLOSE_EVENT);
				this.theCopperierAge$playChestSound(false);
			}
		}
	}

	@Override
	@Unique
	public void theCopperierAge$tickLidController() {
		final long gameTime = this.theCopperierAge$level().getGameTime();
		if (gameTime != this.theCopperierAge$lastLidTickGameTime) {
			this.theCopperierAge$lastLidTickGameTime = gameTime;
			this.theCopperierAge$lidController.tickLid();
		}
	}

	@Override
	@Unique
	public float theCopperierAge$getLidOpenness(float partialTicks) {
		return this.theCopperierAge$lidController.getOpenness(partialTicks);
	}

	@Override
	@Unique
	public void theCopperierAge$setLidShouldBeOpen(boolean open) {
		this.theCopperierAge$lidController.shouldBeOpen(open);
	}

	@Unique
	private net.minecraft.world.level.Level theCopperierAge$level() {
		return ((AbstractChestBoat) (Object) this).level();
	}

	@Unique
	private int theCopperierAge$getOpeners() {
		final AbstractChestBoat boat = (AbstractChestBoat) (Object) this;
		return boat.getAttachedOrCreate(TCAAttachments.CHEST_VEHICLE_OPENERS);
	}

	@Unique
	private void theCopperierAge$setOpeners(int openers) {
		final AbstractChestBoat boat = (AbstractChestBoat) (Object) this;
		boat.setAttached(TCAAttachments.CHEST_VEHICLE_OPENERS, Math.max(0, openers));
	}

	@Unique
	private void theCopperierAge$playChestSound(boolean opening) {
		final AbstractChestBoat boat = (AbstractChestBoat) (Object) this;
		boat.level().playSound(
			null,
			boat.getX(),
			boat.getY(),
			boat.getZ(),
			opening ? SoundEvents.CHEST_OPEN : SoundEvents.CHEST_CLOSE,
			SoundSource.BLOCKS,
			ChestVehicleAnimationConstants.OPEN_CLOSE_SOUND_VOLUME,
			boat.level().random.nextFloat() * ChestVehicleAnimationConstants.OPEN_CLOSE_SOUND_PITCH_VARIANCE + ChestVehicleAnimationConstants.OPEN_CLOSE_SOUND_PITCH_BASE
		);
	}
}
