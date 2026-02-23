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

package net.frozenblock.thecopperierage.mixin.entity.minecart.coupling;

import net.frozenblock.thecopperierage.entity.coupling.MinecartCouplingUtil;
import net.frozenblock.thecopperierage.entity.impl.CouplingToEntityInterface;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecart.class)
public class AbstractMinecartMixin implements CouplingToEntityInterface {

	@Unique
	@Nullable
	private Entity theCopperierAge$coupledTo = null;


	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/vehicle/MinecartBehavior;tick()V",
			shift = At.Shift.AFTER
		)
	)
	private void theCopperierAge$tickCoupling(CallbackInfo info) {
		final AbstractMinecart minecart = AbstractMinecart.class.cast(this);
		MinecartCouplingUtil.tickCoupling(minecart);
	}

	@Unique
	@Override
	public void theCopperierAge$setCoupledTo(@Nullable Entity entity) {
		this.theCopperierAge$coupledTo = entity;
	}

	@Unique
	@Nullable
	@Override
	public Entity theCopperierAge$getCoupledTo() {
		return this.theCopperierAge$coupledTo;
	}
}
