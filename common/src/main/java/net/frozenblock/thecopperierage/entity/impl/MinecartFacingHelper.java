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
	private static final double ORIENTATION_HYSTERESIS = 0.35D;
	private static final double HAS_FACING_THRESHOLD_SQR = 1.0E-4D;
	private static final double MOVING_THRESHOLD_SQR = 1.0E-6D;
	private static final double PARALLEL_ALIGNMENT = 0.9D;
	private static final double AXIS_TRANSFER_THRESHOLD = 0.01D;
	private static final double TURN_EPSILON = 1.0E-4D;
	private static final double REVERSAL_TURN_COS = -0.98D;

	public static boolean isFurnaceMinecart(AbstractMinecart minecart) {
		return minecart instanceof MinecartFurnace;
	}

	public static Vec3 getFacing(AbstractMinecart minecart) {
		return TCAAttachmentTypes.MINECART_FACING.getAttachedOrCreate(minecart);
	}

	public static boolean hasFacing(Vec3 facing) {
		return facing != null && facing.lengthSqr() > HAS_FACING_THRESHOLD_SQR;
	}

	public static boolean hasFacing(AbstractMinecart minecart) {
		return hasFacing(getFacing(minecart));
	}

	public static void setFacing(AbstractMinecart minecart, Vec3 facing, Consumer<Vec3> callback) {
		final Vec3 horizontal = facing.horizontal();
		if (horizontal.lengthSqr() < HAS_FACING_THRESHOLD_SQR) return;

		applyFacing(minecart, trackAlignedDirection(minecart, horizontal.normalize()), callback);
	}

	private static void applyFacing(AbstractMinecart minecart, Vec3 facing, Consumer<Vec3> callback) {
		if (!getFacing(minecart).equals(facing)) TCAAttachmentTypes.MINECART_FACING.set(minecart, facing);
		callback.accept(facing);
	}

	public static void alignFacingToTrack(AbstractMinecart minecart, Consumer<Vec3> callback) {
		final Vec3 travel = new Vec3(minecart.getX() - minecart.xo, 0D, minecart.getZ() - minecart.zo);
		final boolean moving = travel.lengthSqr() > MOVING_THRESHOLD_SQR;
		final Vec3 facing = getFacing(minecart);
		final boolean hasFacing = hasFacing(facing);

		final MinecartTrackHelper.TrackTangent track = MinecartTrackHelper.tangentUnder(minecart);
		if (track == null) {
			if (!hasFacing) {
				if (moving) setFacing(minecart, travel.normalize(), callback);
				return;
			}

			final Vec3 turned = turnWithBody(minecart, facing);
			if (turned != null) applyFacing(minecart, turned, callback);
			return;
		}

		final Vec3 tangent = track.horizontal();
		if (!hasFacing) {
			applyFacing(minecart, moving && track.along(travel) < 0D ? tangent.scale(-1D) : tangent, callback);
			return;
		}

		final double alignment = facing.dot(tangent);
		if (Math.abs(alignment) > PARALLEL_ALIGNMENT) {
			final Vec3 aligned = alignment >= 0D ? tangent : tangent.scale(-1D);
			applyFacing(minecart, aligned, callback);
			if (moving) setEngineReversed(minecart, aligned.dot(travel) < 0D);
			return;
		}

		final double along = track.along(travel);
		if (Math.abs(along) <= AXIS_TRANSFER_THRESHOLD) return;

		final Vec3 heading = along >= 0D ? tangent : tangent.scale(-1D);
		applyFacing(minecart, isEngineReversed(minecart) ? heading.scale(-1D) : heading, callback);
	}

	@Nullable
	private static Vec3 turnWithBody(AbstractMinecart minecart, Vec3 facing) {
		final Vec3 previous = bodyForward(minecart, minecart.yRotO);
		final Vec3 current = bodyForward(minecart, minecart.getYRot());
		final double cos = (previous.x * current.x) + (previous.z * current.z);
		final double sin = (previous.x * current.z) - (previous.z * current.x);
		if (cos < REVERSAL_TURN_COS || Math.abs(sin) < TURN_EPSILON) return null;

		return new Vec3(
			(facing.x * cos) - (facing.z * sin),
			0D,
			(facing.x * sin) + (facing.z * cos)
		).normalize();
	}

	public static boolean isEngineReversed(AbstractMinecart minecart) {
		return TCAAttachmentTypes.MINECART_ENGINE_REVERSED.getAttachedOrCreate(minecart);
	}

	public static void setEngineReversed(AbstractMinecart minecart, boolean reversed) {
		if (isEngineReversed(minecart) != reversed) TCAAttachmentTypes.MINECART_ENGINE_REVERSED.set(minecart, reversed);
	}

	public static Vec3 trackAlignedDirection(AbstractMinecart minecart, Vec3 look) {
		final MinecartTrackHelper.TrackTangent track = MinecartTrackHelper.tangentUnder(minecart);
		if (track != null) return track.facing(look).horizontal();

		final Direction cardinal = Direction.getApproximateNearest(look.x, 0D, look.z);
		return new Vec3(cardinal.getStepX(), 0D, cardinal.getStepZ());
	}

	@Nullable
	public static Direction getDisplayFacing(AbstractMinecart minecart) {
		return TCAAttachmentTypes.MINECART_FACING_DISPLAY.get(minecart);
	}

	public static void setDisplayFacing(AbstractMinecart minecart, Direction facing) {
		if (getDisplayFacing(minecart) != facing) TCAAttachmentTypes.MINECART_FACING_DISPLAY.set(minecart, facing);
	}

	public static Vec3 bodyForward(AbstractMinecart minecart, float yRot) {
		final float renderYaw = AbstractMinecart.useExperimentalMovement(minecart.level()) ? yRot : 180F - yRot;
		return bodyForward(renderYaw * Mth.DEG_TO_RAD);
	}

	public static Vec3 bodyForward(float radians) {
		return new Vec3(Mth.cos(radians), 0D, -Mth.sin(radians));
	}

	public static Direction displayFacingFor(float radians, Vec3 facing, @Nullable Direction previous) {
		final Vec3 bodyForward = bodyForward(radians);
		final double alignment = -((bodyForward.x * facing.x) + (bodyForward.z * facing.z));
		if (Math.abs(alignment) < ORIENTATION_HYSTERESIS && previous != null) return previous;

		return alignment >= 0D ? Direction.SOUTH : Direction.NORTH;
	}

	private MinecartFacingHelper() {}
}
