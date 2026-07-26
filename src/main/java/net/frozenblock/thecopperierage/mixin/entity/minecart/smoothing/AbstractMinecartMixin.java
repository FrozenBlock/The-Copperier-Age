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

package net.frozenblock.thecopperierage.mixin.entity.minecart.smoothing;

import net.frozenblock.thecopperierage.entity.coupling.CouplingData;
import net.frozenblock.thecopperierage.entity.impl.MinecartRotationSmoothing;
import net.frozenblock.thecopperierage.registry.TCAAttachments;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecart.class)
public class AbstractMinecartMixin implements MinecartRotationSmoothing {
	/** Cart speed (blocks/tick) at which the configured turn rate applies unscaled. */
	@Unique
	private static final double THECOPPERIERAGE$ROTATION_REFERENCE_SPEED = 0.2D;
	/** Floor on the turn-rate scale, so stationary re-aims and slow nudges still turn. */
	@Unique
	private static final float THECOPPERIERAGE$MIN_ROTATION_FACTOR = 0.2F;
	/** Ceiling on the turn-rate scale, so very fast carts do not spin instantly. */
	@Unique
	private static final float THECOPPERIERAGE$MAX_ROTATION_FACTOR = 2.5F;
	@Unique
	private static final float THECOPPERIERAGE$SNAP_DEGREES = 150.0F;
	/** Disabled: rotation smoothing is retained for reference but never applied. */
	@Unique
	private static final boolean THECOPPERIERAGE$SMOOTHING_ENABLED = false;
	@Unique
	private static final float THECOPPERIERAGE$TURN_DEGREES_PER_TICK = 45.0F;

	@Unique
	private boolean theCopperierAge$rotationInitialized;
	@Unique
	private float theCopperierAge$smoothYRot;
	@Unique
	private float theCopperierAge$smoothYRotO;
	@Unique
	private float theCopperierAge$smoothXRot;
	@Unique
	private float theCopperierAge$smoothXRotO;

	@Inject(method = "tick", at = @At("TAIL"))
	private void theCopperierAge$smoothRotation(CallbackInfo info) {
		final AbstractMinecart minecart = AbstractMinecart.class.cast(this);
		if (!minecart.level().isClientSide()
			|| !THECOPPERIERAGE$SMOOTHING_ENABLED
			|| !AbstractMinecart.useExperimentalMovement(minecart.level())
			|| !(minecart.getBehavior() instanceof NewMinecartBehavior behavior)
		) {
			this.theCopperierAge$rotationInitialized = false;
			return;
		}

		final float targetYRot;
		final float targetXRot;
		if (behavior.cartHasPosRotLerp()) {
			targetYRot = behavior.getCartLerpYRot(1.0F);
			targetXRot = behavior.getCartLerpXRot(1.0F);
		} else {
			targetYRot = minecart.getYRot();
			targetXRot = minecart.getXRot();
		}

		if (!this.theCopperierAge$rotationInitialized) {
			this.theCopperierAge$smoothYRot = targetYRot;
			this.theCopperierAge$smoothYRotO = targetYRot;
			this.theCopperierAge$smoothXRot = targetXRot;
			this.theCopperierAge$smoothXRotO = targetXRot;
			this.theCopperierAge$rotationInitialized = true;
			return;
		}

		// Cap how far the rendered rotation may turn in one tick.
		final float configured = THECOPPERIERAGE$TURN_DEGREES_PER_TICK;
		final float maxStep;
		if (theCopperierAge$isCoupled(minecart)) {
			// Coupled carts: the coupling solver nudges velocity both ways while a train settles.
			// Speed-scaling made the cart ease slowly through those low-speed nudges, which reads as
			// a strange drift. Track tightly at the fixed rate (as before speed-scaling) so a coupled
			// cart holds its rail heading instead of wandering.
			maxStep = configured;
		} else {
			// Uncoupled: scale the cap by how fast the cart is moving. A single fixed cap turns slow
			// carts too fast (snappy) and fast carts too slow (laggy); scaling keeps the turn matched
			// to the motion -- gentle when crawling or being nudged onto a rail, quick through corners.
			final double speed = minecart.getDeltaMovement().horizontalDistance();
			final float speedFactor = (float) Mth.clamp(
				speed / THECOPPERIERAGE$ROTATION_REFERENCE_SPEED,
				THECOPPERIERAGE$MIN_ROTATION_FACTOR,
				THECOPPERIERAGE$MAX_ROTATION_FACTOR
			);
			maxStep = configured * speedFactor;
		}
		this.theCopperierAge$smoothYRotO = this.theCopperierAge$smoothYRot;
		this.theCopperierAge$smoothXRotO = this.theCopperierAge$smoothXRot;
		this.theCopperierAge$smoothYRot = theCopperierAge$approachAngle(this.theCopperierAge$smoothYRot, targetYRot, maxStep);
		this.theCopperierAge$smoothXRot = theCopperierAge$approachAngle(this.theCopperierAge$smoothXRot, targetXRot, maxStep);
	}

	@Unique
	private static boolean theCopperierAge$isCoupled(AbstractMinecart minecart) {
		final CouplingData coupling = minecart.getAttached(TCAAttachments.MINECART_COUPLING);
		return coupling != null && (coupling.isCoupledTo() || coupling.isCoupledFrom());
	}

	@Unique
	private static float theCopperierAge$approachAngle(float current, float target, float maxStep) {
		final float delta = Mth.wrapDegrees(target - current);
		if (Math.abs(delta) >= THECOPPERIERAGE$SNAP_DEGREES) return target;
		return current + Mth.clamp(delta, -maxStep, maxStep);
	}

	@Override
	public boolean theCopperierAge$hasSmoothedRotation() {
		return this.theCopperierAge$rotationInitialized;
	}

	@Override
	public float theCopperierAge$getSmoothYRot(float partialTick) {
		return Mth.rotLerp(partialTick, this.theCopperierAge$smoothYRotO, this.theCopperierAge$smoothYRot);
	}

	@Override
	public float theCopperierAge$getSmoothXRot(float partialTick) {
		return Mth.rotLerp(partialTick, this.theCopperierAge$smoothXRotO, this.theCopperierAge$smoothXRot);
	}
}
