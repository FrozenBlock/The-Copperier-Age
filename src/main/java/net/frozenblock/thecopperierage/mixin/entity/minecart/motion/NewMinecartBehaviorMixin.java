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

package net.frozenblock.thecopperierage.mixin.entity.minecart.motion;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Stops experimental minecarts getting stuck -- most visibly on corners and ramps.
 *
 * <p>When a single track step is fully obstructed, {@code stepAlongTrack} hard-zeroes the cart's
 * velocity. That velocity is not the raw input velocity though: it has already been redirected to
 * point along the rail's exit ({@code movement} in the vanilla method). Throwing it away is what
 * makes carts die on a momentary obstruction and, worse, lose the turn they were mid-way through on
 * a corner or a slope, since the cart forgets the along-track direction it had just been given.
 *
 * <p>Instead of discarding it, we keep that track-aligned momentum, scaled by a retention factor.
 * A momentary obstruction is coasted straight through (retention close to 1 loses almost nothing),
 * while a genuine wall or buffer still brings the cart to rest: every blocked tick multiplies the
 * remaining speed by the factor (on top of the usual friction), so it decays to a stop within about
 * a second rather than continuing forever. {@code Entity#move} still performs the real collision, so
 * the cart never actually passes through solid blocks -- the retained velocity only resumes once the
 * way ahead is clear.
 */
@Mixin(NewMinecartBehavior.class)
public class NewMinecartBehaviorMixin {

	@WrapOperation(
		method = "stepAlongTrack",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/vehicle/minecart/NewMinecartBehavior;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V",
			ordinal = 1
		)
	)
	private void theCopperierAge$keepMomentumWhenBlocked(
		NewMinecartBehavior behavior,
		Vec3 zeroed,
		Operation<Void> original,
		@Local(ordinal = 1) Vec3 trackAlignedMovement
	) {
		if (!TCAConfig.SMOOTH_MINECART_MOTION.get()) {
			original.call(behavior, zeroed);
			return;
		}

		final float retention = Mth.clamp(TCAConfig.MINECART_MOMENTUM_RETENTION.get(), 0F, 1F);
		original.call(behavior, trackAlignedMovement.scale(retention));
	}
}
