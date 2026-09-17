/*
 * Copyright 2026 FrozenBlock
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

package net.frozenblock.thecopperierage.mixin.entity.minecart.camera;

import net.frozenblock.thecopperierage.entity.impl.MinecartHeadingInterface;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin implements MinecartHeadingInterface {
	@Unique
	private static final double THECOPPERIERAGE$MIN_HEADING_SPEED_SQR = 1.0E-4D;
	@Unique
	private static final float THECOPPERIERAGE$REVERSAL_DEGREES = 120F;
	@Unique
	private static final float THECOPPERIERAGE$HEADING_SMOOTHING = 0.35F;
	@Unique
	private static final float THECOPPERIERAGE$MIN_TURN = 1.0E-3F;

	@Unique
	private float theCopperierAge$riderHeading;
	@Unique
	private float theCopperierAge$riderTurn;
	@Unique
	private boolean theCopperierAge$hasRiderHeading;

	@Inject(method = "tick", at = @At("TAIL"))
	private void theCopperierAge$updateRiderHeading(CallbackInfo info) {
		final AbstractMinecart cart = AbstractMinecart.class.cast(this);
		this.theCopperierAge$riderTurn = 0F;

		final double dx = cart.getX() - cart.xo;
		final double dz = cart.getZ() - cart.zo;
		if (dx * dx + dz * dz < THECOPPERIERAGE$MIN_HEADING_SPEED_SQR) return;

		float heading = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90F;
		if (!this.theCopperierAge$hasRiderHeading) {
			this.theCopperierAge$riderHeading = heading;
			this.theCopperierAge$hasRiderHeading = true;
			return;
		}

		if (Math.abs(Mth.wrapDegrees(heading - this.theCopperierAge$riderHeading)) > THECOPPERIERAGE$REVERSAL_DEGREES) heading += 180F;
		final float turn = Mth.wrapDegrees(heading - this.theCopperierAge$riderHeading) * THECOPPERIERAGE$HEADING_SMOOTHING;
		if (Math.abs(turn) < THECOPPERIERAGE$MIN_TURN) return;

		this.theCopperierAge$riderHeading = Mth.wrapDegrees(this.theCopperierAge$riderHeading + turn);
		this.theCopperierAge$riderTurn = turn;
	}

	@Unique
	@Override
	public boolean theCopperierAge$hasRiderHeading() {
		return this.theCopperierAge$hasRiderHeading;
	}

	@Unique
	@Override
	public float theCopperierAge$getRiderHeading() {
		return this.theCopperierAge$riderHeading;
	}

	@Unique
	@Override
	public float theCopperierAge$getRiderTurn() {
		return this.theCopperierAge$riderTurn;
	}
}
