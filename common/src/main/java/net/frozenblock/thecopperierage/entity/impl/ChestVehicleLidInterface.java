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

package net.frozenblock.thecopperierage.entity.impl;

import net.frozenblock.thecopperierage.entity.ChestVehicleOpeners;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.ChestLidController;

public interface ChestVehicleLidInterface {
	ChestLidController theCopperierAge$getLidController();

	default void theCopperierAge$tickLidController() {
		if (!(this instanceof Entity entity) || !entity.level().isClientSide()) return;

		final ChestLidController lidController = this.theCopperierAge$getLidController();
		lidController.shouldBeOpen(ChestVehicleOpeners.isOpen(entity));
		lidController.tickLid();
	}

	default float theCopperierAge$getLidOpenness(float partialTicks) {
		return this.theCopperierAge$getLidController().getOpenness(partialTicks);
	}
}
