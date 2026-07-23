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

import net.minecraft.world.phys.Vec3;

/**
 * Implemented on {@code MinecartFurnace} to let the improved furnace minecart be aimed:
 * the supplied direction (horizontalised) becomes the direction it moves in once fueled.
 */
public interface FurnaceMinecartFacingInterface {
	void theCopperierAge$setFacing(Vec3 facing);
}
