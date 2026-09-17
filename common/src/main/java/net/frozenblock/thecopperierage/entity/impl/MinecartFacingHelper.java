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

package net.frozenblock.thecopperierage.entity.impl;

import java.util.function.Consumer;
import net.frozenblock.thecopperierage.entity.MinecartTrackHelper;
import net.frozenblock.thecopperierage.registry.TCAAttachmentTypes;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartFurnace;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class MinecartFacingHelper {
	// TODO: better field name
	private static final double AMBIGUOUS_ALIGNMENT = 0.2D;
	private static final double HAS_FACING_THRESHOLD_SQR = 1.0E-4D;
	private static final double MOVING_THRESHOLD_SQR = 1.0E-6D;
	private static final double ALIGNMENT_EPSILON = 1.0E-3D;

	public static boolean isFurnaceMinecart(AbstractMinecart minecart) {
		return minecart instanceof MinecartFurnace;
	}

	public static Vec3 getFacing(AbstractMinecart minecart) {
		return TCAAttachmentTypes.MINECART_FACING.getAttachedOrCreate(minecart);
	}

	public static void setFacing(AbstractMinecart minecart, Vec3 facing, Consumer<Vec3> callback) {
		final Vec3 horizontal = facing.horizontal();
		if (horizontal.lengthSqr() < HAS_FACING_THRESHOLD_SQR) return;

		TCAAttachmentTypes.MINECART_FACING.set(minecart, trackAlignedDirection(minecart, horizontal.normalize()));
		callback.accept(getFacing(minecart));

		if (!minecart.level().isClientSide()) syncFacing(minecart);
	}

	@Nullable
	public static Direction getSyncedFacing(AbstractMinecart minecart) {
		return TCAAttachmentTypes.MINECART_FACING_SYNCED.get(minecart);
	}

	public static void setSyncedFacing(AbstractMinecart minecart, @Nullable Direction facing) {
		if (facing == null) {
			TCAAttachmentTypes.MINECART_FACING_SYNCED.remove(minecart);
			return;
		}

		TCAAttachmentTypes.MINECART_FACING_SYNCED.set(minecart, facing);
	}

	@Nullable
	public static Direction getDisplayFacing(AbstractMinecart minecart) {
		return TCAAttachmentTypes.MINECART_FACING_DISPLAY.get(minecart);
	}

	public static void setDisplayFacing(AbstractMinecart minecart, Direction facing) {
		TCAAttachmentTypes.MINECART_FACING_DISPLAY.set(minecart, facing);
	}

	public static void alignFacingToTrack(AbstractMinecart minecart, Consumer<Vec3> callback) {
		final Vec3 velocity = minecart.getDeltaMovement().horizontal();
		final boolean moving = velocity.lengthSqr() > MOVING_THRESHOLD_SQR;
		Vec3 facing = getFacing(minecart);
		final boolean hasFacing = hasFacing(facing);

		final MinecartTrackHelper.TrackTangent track = MinecartTrackHelper.tangentUnder(minecart);
		if (track == null) {
			if (!hasFacing && moving) setFacing(minecart, velocity.normalize(), callback);
			return;
		}

		final Vec3 tangent = track.horizontal();
		if (hasFacing) {
			final double alignment = facing.dot(tangent);
			if (Math.abs(alignment) > ALIGNMENT_EPSILON) {
				setFacing(minecart, alignment >= 0D ? tangent : tangent.scale(-1D), callback);
				return;
			}
		}

		if (moving) {
			setFacing(minecart, track.along(velocity) >= 0D ? tangent : tangent.scale(-1D), callback);
		} else if (!hasFacing) {
			setFacing(minecart, tangent, callback);
		}
	}

	public static void syncFacing(AbstractMinecart minecart) {
		Direction value = null;

		final Vec3 facing = getFacing(minecart);
		if (hasFacing(facing)) {
			final Direction direction = Direction.getApproximateNearest(facing.x, 0D, facing.z);
			if (direction.getAxis().isHorizontal()) value = direction;
		}

		if (getSyncedFacing(minecart) != value) setSyncedFacing(minecart, value);
	}

	public static Vec3 trackAlignedDirection(AbstractMinecart minecart, Vec3 look) {
		final MinecartTrackHelper.TrackTangent track = MinecartTrackHelper.tangentUnder(minecart);
		if (track != null) return track.facing(look).horizontal();

		final Direction cardinal = Direction.getApproximateNearest(look.x, 0D, look.z);
		return new Vec3(cardinal.getStepX(), 0D, cardinal.getStepZ());
	}

	public static boolean hasFacing(Vec3 facing) {
		return facing != null && facing.lengthSqr() > HAS_FACING_THRESHOLD_SQR;
	}

	public static boolean hasFacing(AbstractMinecart minecart) {
		return hasFacing(getFacing(minecart));
	}

	public static Direction displayFacingFor(float radians, Direction facing, @Nullable Direction previous) {
		final Vec3 bodyForward = new Vec3(Mth.cos(radians), 0D, -Mth.sin(radians));
		final double alignment = -(bodyForward.x * facing.getStepX() + bodyForward.z * facing.getStepZ());
		if (Math.abs(alignment) < AMBIGUOUS_ALIGNMENT && previous != null) return previous;

		return alignment >= 0D ? Direction.SOUTH : Direction.NORTH;
	}

	private MinecartFacingHelper() {}
}
