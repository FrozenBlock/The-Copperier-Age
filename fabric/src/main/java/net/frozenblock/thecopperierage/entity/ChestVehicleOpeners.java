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

import net.frozenblock.thecopperierage.registry.TCAAttachments;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.boat.AbstractChestBoat;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.block.Blocks;

public final class ChestVehicleOpeners {
	private static final float LID_SOUND_VOLUME = 0.5F;
	private static final float LID_SOUND_PITCH_BASE = 0.9F;
	private static final float LID_SOUND_PITCH_VARIANCE = 0.1F;

	private ChestVehicleOpeners() {
	}

	public static void add(Entity vehicle, int delta) {
		if (vehicle.level().isClientSide()) return;

		final int current = vehicle.getAttachedOrCreate(TCAAttachments.CHEST_VEHICLE_OPENERS);
		final int updated = Math.max(0, current + delta);
		if (updated == current) return;
		vehicle.setAttached(TCAAttachments.CHEST_VEHICLE_OPENERS, updated);

		if (current == 0) {
			playLidSound(vehicle, SoundEvents.CHEST_OPEN);
		} else if (updated == 0) {
			playLidSound(vehicle, SoundEvents.CHEST_CLOSE);
		}
	}

	public static boolean isOpen(Entity vehicle) {
		final Integer openers = vehicle.getAttached(TCAAttachments.CHEST_VEHICLE_OPENERS);
		return openers != null && openers > 0;
	}

	public static boolean hasChest(Entity vehicle) {
		if (vehicle instanceof AbstractChestBoat) return true;
		return vehicle instanceof AbstractMinecart minecart && minecart.getDisplayBlockState().is(Blocks.CHEST);
	}

	private static void playLidSound(Entity vehicle, SoundEvent sound) {
		if (!hasChest(vehicle)) return;

		vehicle.level().playSound(
			null,
			vehicle.getX(), vehicle.getY(), vehicle.getZ(),
			sound,
			SoundSource.BLOCKS,
			LID_SOUND_VOLUME,
			vehicle.getRandom().nextFloat() * LID_SOUND_PITCH_VARIANCE + LID_SOUND_PITCH_BASE
		);
	}
}
