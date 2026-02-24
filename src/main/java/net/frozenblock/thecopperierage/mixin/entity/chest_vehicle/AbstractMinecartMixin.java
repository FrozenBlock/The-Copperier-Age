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

package net.frozenblock.thecopperierage.mixin.entity.chest_vehicle;

import net.frozenblock.thecopperierage.entity.impl.ChestVehicleBubbleInterface;
import net.frozenblock.thecopperierage.entity.impl.ChestVehicleInterface;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecart.class)
public class AbstractMinecartMixin {

	@Unique
	private boolean theCopperierAge$actualFirstTick = true;

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;updateInWaterStateAndDoFluidPushing()Z",
			shift = At.Shift.AFTER
		)
	)
	public void theCopperierAge$tick(CallbackInfo info) {
		final AbstractMinecart minecart = AbstractMinecart.class.cast(this);
		minecart.updateFluidOnEyes();
		if (minecart instanceof ChestVehicleInterface chestLidAnimating) chestLidAnimating.theCopperierAge$tickLidController();
		if (!this.theCopperierAge$actualFirstTick && minecart instanceof ChestVehicleBubbleInterface bubbleInterface) bubbleInterface.theCopperierAge$tickBubble();
		this.theCopperierAge$actualFirstTick = true;
	}
}
