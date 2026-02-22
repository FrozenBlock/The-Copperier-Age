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

package net.frozenblock.thecopperierage.mixin.client.gearbox;

import net.frozenblock.thecopperierage.block.gearbox.GearboxEntityRotationHelper;
import net.frozenblock.thecopperierage.block.gearbox.GearboxRotationSessionInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Optional;

@Mixin(Minecraft.class)
public class LocalPlayerGearboxRotationMixin {
	@Unique
	private static final float theCopperierAge$NANOS_TO_SECONDS = 1.0E-9F;
	@Unique
	private long theCopperierAge$lastFrameNanos = -1L;

	@Inject(method = "runTick", at = @At("TAIL"))
	private void theCopperierAge$applyLocalPlayerGearboxRotation(CallbackInfo info) {
		final Minecraft minecraft = Minecraft.class.cast(this);
		final LocalPlayer player = minecraft.player;
		final Entity entity = player != null ? Optional.ofNullable(player.getControlledVehicle()).orElse(player) : null;
		if (!(entity instanceof GearboxRotationSessionInterface rotationSession)) {
			this.theCopperierAge$lastFrameNanos = -1L;
			return;
		}

		if (minecraft.isPaused()) {
			this.theCopperierAge$lastFrameNanos = System.nanoTime();
			return;
		}

		final float gearboxYawDelta = rotationSession.theCopperierAge$getGearboxYawDelta();
		if (gearboxYawDelta == 0F) {
			this.theCopperierAge$lastFrameNanos = -1L;
			return;
		}

		final long now = System.nanoTime();
		if (this.theCopperierAge$lastFrameNanos < 0L) {
			this.theCopperierAge$lastFrameNanos = now;
			return;
		}

		float deltaSeconds = (now - this.theCopperierAge$lastFrameNanos) * theCopperierAge$NANOS_TO_SECONDS;
		this.theCopperierAge$lastFrameNanos = now;
		if (deltaSeconds <= 0F) return;

		final float yawDeltaThisFrame = gearboxYawDelta * 20F * deltaSeconds;
		GearboxEntityRotationHelper.applyRotation(player, yawDeltaThisFrame, false);
		GearboxEntityRotationHelper.debug(player, yawDeltaThisFrame);
	}
}
