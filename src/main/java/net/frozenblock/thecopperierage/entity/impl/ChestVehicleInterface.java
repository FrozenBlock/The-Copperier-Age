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

package net.frozenblock.thecopperierage.entity.impl;

import net.frozenblock.thecopperierage.registry.TCAAttachments;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.ChestLidController;

public interface ChestVehicleInterface {
	float OPEN_CLOSE_SOUND_VOLUME = 0.5F;
	float OPEN_CLOSE_SOUND_PITCH_BASE = 0.9F;
	float OPEN_CLOSE_SOUND_PITCH_VARIANCE = 0.1F;

	default void theCopperierAge$tickLidController() {
		if (!(this instanceof Entity entity) || !entity.level().isClientSide()) return;

		final ChestLidController lidController = this.theCopperierAge$getLidController();
		lidController.shouldBeOpen(entity.getAttachedOrElse(TCAAttachments.CHEST_VEHICLE_OPENERS, 0) > 0);
		lidController.tickLid();
	}

	ChestLidController theCopperierAge$getLidController();

	float theCopperierAge$getLidOpenness(float partialTicks);

	default void theCopperierAge$onContainerOpen() {
		if (!(this instanceof Entity entity) || entity.level().isClientSide()) return;

		final int openers = entity.getAttachedOrElse(TCAAttachments.CHEST_VEHICLE_OPENERS, 0);
		entity.setAttached(TCAAttachments.CHEST_VEHICLE_OPENERS, openers + 1);
		if (openers == 0) this.theCopperierAge$playChestSound(true);
	}

	default void theCopperierAge$onContainerClose() {
		if (!(this instanceof Entity entity) || entity.level().isClientSide()) return;

		final int openers = entity.getAttachedOrElse(TCAAttachments.CHEST_VEHICLE_OPENERS, 0);
		if (openers <= 0) return;

		final int finalOpeners = openers - 1;
		entity.setAttached(TCAAttachments.CHEST_VEHICLE_OPENERS, finalOpeners);
		if (finalOpeners <= 0) {
			this.theCopperierAge$playChestSound(false);
			entity.removeAttached(TCAAttachments.CHEST_VEHICLE_OPENERS);
		}
	}

	default void theCopperierAge$playChestSound(boolean opening) {
		if (!(this instanceof Entity entity) || entity.level().isClientSide()) return;

		entity.playSound(
			opening ? SoundEvents.CHEST_OPEN : SoundEvents.CHEST_CLOSE,
			OPEN_CLOSE_SOUND_VOLUME,
			entity.getRandom().nextFloat() * OPEN_CLOSE_SOUND_PITCH_VARIANCE + OPEN_CLOSE_SOUND_PITCH_BASE
		);
	}
}
