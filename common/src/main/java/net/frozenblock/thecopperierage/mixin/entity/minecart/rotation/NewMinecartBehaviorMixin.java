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

package net.frozenblock.thecopperierage.mixin.entity.minecart.rotation;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(NewMinecartBehavior.class)
public abstract class NewMinecartBehaviorMixin extends MinecartBehavior {
	@Unique
	private static final float THECOPPERIERAGE$MAX_OFF_RAIL_TURN = 30F;
	@Unique
	private static final float THECOPPERIERAGE$MAX_EASED_TURN = 90F;

	protected NewMinecartBehaviorMixin(AbstractMinecart minecart) {
		super(minecart);
	}

	@ModifyVariable(method = "setRotation", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private float theCopperierAge$easeOffRailTurn(float yRot) {
		if (this.minecart.isOnRails() || this.minecart.isFirstTick()) return yRot;

		final float current = this.getYRot();
		final float delta = Mth.wrapDegrees(yRot - current);
		final float magnitude = Math.abs(delta);
		if (magnitude <= THECOPPERIERAGE$MAX_OFF_RAIL_TURN || magnitude > THECOPPERIERAGE$MAX_EASED_TURN) return yRot;

		return current + Math.signum(delta) * THECOPPERIERAGE$MAX_OFF_RAIL_TURN;
	}
}
