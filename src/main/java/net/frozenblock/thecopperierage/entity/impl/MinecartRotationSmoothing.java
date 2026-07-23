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

/**
 * Implemented on {@code AbstractMinecart} (client-side) to provide an eased render rotation that
 * follows the cart's authoritative yaw/pitch without the jitter and on/off-rail snapping of vanilla's
 * step-lerp. The vanilla "flipped" (keep-facing-on-reversal) behavior is preserved because the
 * smoothing target is the cart's own rotation.
 */
public interface MinecartRotationSmoothing {
	/** True only while smoothing is active (experimental movement + config on + initialised). */
	boolean theCopperierAge$hasSmoothedRotation();

	float theCopperierAge$getSmoothYRot(float partialTick);

	float theCopperierAge$getSmoothXRot(float partialTick);
}
