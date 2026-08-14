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

package net.frozenblock.thecopperierage.mixin.entity.chestvehicle;

import net.frozenblock.thecopperierage.entity.ChestVehicleOpeners;
import net.frozenblock.thecopperierage.entity.impl.ChestVehicleLidInterface;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractChestBoat;
import net.minecraft.world.level.block.entity.ChestLidController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractChestBoat.class)
public abstract class AbstractChestBoatMixin implements ChestVehicleLidInterface {
	@Unique
	private final ChestLidController theCopperierAge$lidController = new ChestLidController();

	public void startOpen(ContainerUser user) {
		if (user.getLivingEntity() instanceof Player player && player.isSpectator()) return;
		ChestVehicleOpeners.add((AbstractChestBoat) (Object) this, 1);
	}

	@Inject(method = "stopOpen", at = @At("TAIL"))
	public void theCopperierAge$stopOpen(ContainerUser user, CallbackInfo info) {
		if (user.getLivingEntity() instanceof Player player && player.isSpectator()) return;
		ChestVehicleOpeners.add((AbstractChestBoat) (Object) this, -1);
	}

	@Unique
	@Override
	public ChestLidController theCopperierAge$getLidController() {
		return this.theCopperierAge$lidController;
	}
}
