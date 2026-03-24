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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.Optional;
import net.frozenblock.thecopperierage.block.gearbox.GearboxEntityRotationHelper;
import net.frozenblock.thecopperierage.block.gearbox.GearboxRotationSessionInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class LocalPlayerGearboxRotationMixin {
	
	@Unique
	private static final float theCopperierAge$NANOS_TO_SECONDS = 1.0E-9F;

	@WrapOperation(
		method = "renderFrame",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;logFrameDuration(J)V"
		)
	)
	private void theCopperierAge$applyLocalPlayerGearboxRotation(
		DebugScreenOverlay instance, long deltaTime, Operation<Void> original,
		boolean advanceGameTime
	) {
		final Minecraft minecraft = Minecraft.class.cast(this);
		if (!advanceGameTime) return;

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
