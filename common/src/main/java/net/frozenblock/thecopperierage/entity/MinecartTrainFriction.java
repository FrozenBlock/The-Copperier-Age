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

package net.frozenblock.thecopperierage.entity;

import net.frozenblock.thecopperierage.entity.impl.CouplingToEntityInterface;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.phys.Vec3;

public final class MinecartTrainFriction {
	public static final double FRICTION_GROWTH_PER_CART = 1.1D;
	private static final double EPSILON = 1.0E-9D;

	public static Vec3 apply(AbstractMinecart cart, Vec3 before, Vec3 after) {
		if (!(cart instanceof CouplingToEntityInterface access)) return after;

		final int trainSize = access.theCopperierAge$getTrainSize();
		if (trainSize <= 1) return after;

		final double beforeSpeed = before.horizontalDistance();
		final double afterSpeed = after.horizontalDistance();
		if (beforeSpeed < EPSILON || afterSpeed < EPSILON) return after;

		final double retention = Math.min(1D, afterSpeed / beforeSpeed);
		final double loss = 1D - retention;
		final double trainLoss = Math.min(1D, loss * Math.pow(FRICTION_GROWTH_PER_CART, trainSize - 1));
		final double scale = (1D - trainLoss) / retention;
		return new Vec3(after.x * scale, after.y, after.z * scale);
	}

	private MinecartTrainFriction() {}
}
