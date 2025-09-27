/*
 * Copyright 2025 FrozenBlock
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

package net.frozenblock.thecopperierage.mixin.block.copper_fan;

import net.frozenblock.thecopperierage.entity.impl.CopperFanQueuedMovementInterface;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements CopperFanQueuedMovementInterface {

	@Shadow
	public abstract void addDeltaMovement(Vec3 vec3);

	@Unique
	private Vec3 theCopperierAge$queuedCopperFanMovementMin = Vec3.ZERO;
	@Unique
	private Vec3 theCopperierAge$queuedCopperFanMovementMax = Vec3.ZERO;

	@Inject(
		method = "baseTick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Entity;handlePortal()V",
			shift = At.Shift.BEFORE
		)
	)
	public void theCopperierAge$applyAndClearQueuedCopperFanMovement(CallbackInfo info) {
		final Vec3 addedCopperFanMovement = this.theCopperierAge$queuedCopperFanMovementMin.add(this.theCopperierAge$queuedCopperFanMovementMax);
		this.addDeltaMovement(addedCopperFanMovement);
		this.theCopperierAge$queuedCopperFanMovementMin = Vec3.ZERO;
		this.theCopperierAge$queuedCopperFanMovementMax = Vec3.ZERO;
	}

	@Unique
	@Override
	public void theCopperierAge$queueCopperFanMovement(@NotNull Vec3 movement) {
		this.theCopperierAge$queuedCopperFanMovementMin = new Vec3(
			Math.min(this.theCopperierAge$queuedCopperFanMovementMin.x(), movement.x()),
			Math.min(this.theCopperierAge$queuedCopperFanMovementMin.y(), movement.y()),
			Math.min(this.theCopperierAge$queuedCopperFanMovementMin.z(), movement.z())
		);
		this.theCopperierAge$queuedCopperFanMovementMax = new Vec3(
			Math.max(this.theCopperierAge$queuedCopperFanMovementMax.x(), movement.x()),
			Math.max(this.theCopperierAge$queuedCopperFanMovementMax.y(), movement.y()),
			Math.max(this.theCopperierAge$queuedCopperFanMovementMax.z(), movement.z())
		);
	}
}
