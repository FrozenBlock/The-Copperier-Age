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

package net.frozenblock.thecopperierage.entity.vehicle.minecart.coupling;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface CouplingToEntityInterface {
	void theCopperierAge$setCoupledTo(@Nullable Entity entity);

	@Nullable
	Entity theCopperierAge$getCoupledTo();

	double theCopperierAge$getMaxSpeed(ServerLevel level);

	@Nullable
	Vec3 theCopperierAge$getTickStartPosition();

	@Nullable
	Vec3 theCopperierAge$getBlockedDirection();

	long theCopperierAge$getBlockedTick();

	void theCopperierAge$setBlocked(Vec3 direction, long gameTime);

	boolean theCopperierAge$isTerrainJammed();

	int theCopperierAge$getTrainSize();

	void theCopperierAge$setTrainSize(int size);

	int theCopperierAge$incrementMissingCoupledTo();

	void theCopperierAge$resetMissingCoupledTo();

	int theCopperierAge$incrementMissingCoupledFrom();

	void theCopperierAge$resetMissingCoupledFrom();
}
