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

package net.frozenblock.thecopperierage.block.gearbox;

import net.minecraft.core.BlockPos;

public interface GearboxRotationSessionInterface {
	void theCopperierAge$activateGearboxRotationSession(int currentTick, BlockPos supportPos);
	float theCopperierAge$getGearboxYawDelta();
	boolean theCopperierAge$automaticallyRotatesWithGearbox();
}
