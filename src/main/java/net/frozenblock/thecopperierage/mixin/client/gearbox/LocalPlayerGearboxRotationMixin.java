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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import java.util.Optional;
import net.frozenblock.thecopperierage.block.gearbox.GearboxEntityRotationHelper;
import net.frozenblock.thecopperierage.block.gearbox.GearboxRotationSessionInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class LocalPlayerGearboxRotationMixin {

	@Shadow
	private volatile boolean pause;

	@Unique
	private static final float theCopperierAge$NANOS_TO_SECONDS = 1.0E-9F;

	@ModifyExpressionValue(
		method = "runTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/Minecraft;isLevelRunningNormally()Z"
		)
	)
	private boolean theCopperierAge$captureIsLevelRunningNormally(
		boolean original,
		@Share("theCopperierAge$isLevelRunningNormally") LocalBooleanRef isLevelRunningNormally
	) {
		isLevelRunningNormally.set(original);
		return original;
	}

	@WrapOperation(
		method = "runTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;logFrameDuration(J)V"
		)
	)
	private void theCopperierAge$applyLocalPlayerGearboxRotation(
		DebugScreenOverlay instance, long deltaTime, Operation<Void> original,
		@Share("theCopperierAge$isLevelRunningNormally") LocalBooleanRef isLevelRunningNormally
	) {
		final Minecraft minecraft = Minecraft.class.cast(this);
		original.call(instance, deltaTime);
		if (this.pause || !isLevelRunningNormally.get()) return;

		final LocalPlayer player = minecraft.player;
		final Entity entity = player != null ? Optional.ofNullable(player.getControlledVehicle()).orElse(player) : null;
		if (!(entity instanceof GearboxRotationSessionInterface rotationSession)) return;

		final float gearboxYawDelta = rotationSession.theCopperierAge$getGearboxYawDelta();
		float deltaSeconds = deltaTime * theCopperierAge$NANOS_TO_SECONDS;
		if (deltaSeconds <= 0F) return;

		final float yawDeltaThisFrame = gearboxYawDelta * 20F * deltaSeconds;
		GearboxEntityRotationHelper.applyLocalRotation(player, yawDeltaThisFrame);
	}
}
