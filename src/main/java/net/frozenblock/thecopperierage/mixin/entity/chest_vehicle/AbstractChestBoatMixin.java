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
import net.minecraft.world.entity.vehicle.AbstractChestBoat;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractChestBoat.class)
public class AbstractChestBoatMixin implements ChestVehicleInterface, ChestVehicleBubbleInterface {
	@Unique
	private final ChestLidController theCopperierAge$lidController = new ChestLidController();

	@Unique
	@Override
	public ChestLidController theCopperierAge$getLidController() {
		return this.theCopperierAge$lidController;
	}

	@Unique
	@Override
	public float theCopperierAge$getLidOpenness(float partialTicks) {
		if (!TCAConfig.IMPROVED_VEHICLE_CHESTS) return 0F;
		return this.theCopperierAge$lidController.getOpenness(partialTicks);
	}

	@Unique
	@Override
	public Vec3 theCopperierAge$bubbleEmitPosition() {
		final AbstractChestBoat boat = AbstractChestBoat.class.cast(this);
		return boat.getEyePosition()
			.add(0D, 2.5D / 16D, 0D)
			.add(boat.getLookAngle().multiply(-0.56D, 0D, -0.56D));
	}
}
