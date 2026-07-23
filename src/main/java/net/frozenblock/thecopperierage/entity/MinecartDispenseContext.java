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

package net.frozenblock.thecopperierage.entity;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * While a dispenser/dropper minecart is dispensing, this holds the offset from the notional
 * block cell to the minecart itself, so {@code DispenserBlock.getDispensePosition} can be
 * shifted to eject items from the minecart's position instead of the block grid cell.
 * Server dispensing is synchronous, so a single static slot is sufficient.
 */
public final class MinecartDispenseContext {
	@Nullable
	private static Vec3 offset;

	private MinecartDispenseContext() {
	}

	public static void begin(Vec3 dispenseOffset) {
		offset = dispenseOffset;
	}

	public static void end() {
		offset = null;
	}

	@Nullable
	public static Vec3 offset() {
		return offset;
	}
}
