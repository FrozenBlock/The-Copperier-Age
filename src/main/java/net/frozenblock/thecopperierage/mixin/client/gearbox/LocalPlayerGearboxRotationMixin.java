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
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class LocalPlayerGearboxRotationMixin {
	@Unique
	private static final float theCopperierAge$NANOS_TO_SECONDS = 1.0E-9F;
	@Unique
	private static final float theCopperierAge$MAX_FRAME_SECONDS = 0.05F;
	@Unique
	private long theCopperierAge$lastFrameNanos = -1L;
	@Unique
	private int theCopperierAge$lastPlayerTickCount = Integer.MIN_VALUE;
	@Unique
	private boolean theCopperierAge$activeRotationSession = false;
	@Unique
	private float theCopperierAge$cachedYawDeltaPerTick = 0F;

	@Inject(
		method = "runTick",
		at = @At("TAIL")
	)
	private void theCopperierAge$applyLocalPlayerGearboxRotation(CallbackInfo info) {
		final Minecraft minecraft = (Minecraft) (Object) this;
		final LocalPlayer player = minecraft.player;
		if (player == null) {
			this.theCopperierAge$lastFrameNanos = -1L;
			this.theCopperierAge$lastPlayerTickCount = Integer.MIN_VALUE;
			this.theCopperierAge$activeRotationSession = false;
			this.theCopperierAge$cachedYawDeltaPerTick = 0F;
			return;
		}

		if (minecraft.isPaused()) {
			this.theCopperierAge$lastFrameNanos = System.nanoTime();
			return;
		}

		if (player.tickCount != this.theCopperierAge$lastPlayerTickCount) {
			this.theCopperierAge$lastPlayerTickCount = player.tickCount;
			this.theCopperierAge$cachedYawDeltaPerTick = GearboxEntityRotationHelper.getGearboxYawDelta(player);
			final boolean activeNow = this.theCopperierAge$cachedYawDeltaPerTick != 0F;
			if (activeNow && !this.theCopperierAge$activeRotationSession) {
				this.theCopperierAge$lastFrameNanos = -1L;
			}
			this.theCopperierAge$activeRotationSession = activeNow;
		}

		if (!this.theCopperierAge$activeRotationSession) {
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
		deltaSeconds = Math.min(deltaSeconds, theCopperierAge$MAX_FRAME_SECONDS);
		if (deltaSeconds <= 0F) return;

		final float yawDeltaPerTick = this.theCopperierAge$cachedYawDeltaPerTick;
		if (yawDeltaPerTick == 0F) return;

		final float yawDeltaThisFrame = yawDeltaPerTick * 20F * deltaSeconds;
		GearboxEntityRotationHelper.applyRotation(player, yawDeltaThisFrame, false);
		GearboxEntityRotationHelper.debug(player, yawDeltaThisFrame);
	}
}
