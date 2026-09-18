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

package net.frozenblock.thecopperierage.entity.vehicle.minecart.api;

import com.mojang.datafixers.util.Pair;
import net.frozenblock.thecopperierage.block.CrossRailBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class MinecartTrackHelper {
	private static final double EPSILON = 1.0E-6D;

	@Nullable
	public static RailShape railShapeAt(Level level, BlockPos pos, AbstractMinecart minecart) {
		final BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof BaseRailBlock rail)) return null;
		if (rail instanceof CrossRailBlock) return CrossRailBlock.railShapeFromMotion(level, pos, minecart);
		return state.getValue(rail.getShapeProperty());
	}

	@Nullable
	public static RailShape railShapeUnder(AbstractMinecart minecart) {
		return railShapeAt(minecart.level(), minecart.getCurrentBlockPosOrRailBelow(), minecart);
	}

	@Nullable
	public static TrackTangent tangentUnder(AbstractMinecart minecart) {
		final RailShape shape = railShapeUnder(minecart);
		return shape == null ? null : tangentOf(shape);
	}

	@Nullable
	public static TrackTangent tangentOf(RailShape shape) {
		final Pair<Vec3i, Vec3i> exits = AbstractMinecart.exits(shape);
		final Vec3 horizontal = new Vec3(exits.getSecond().subtract(exits.getFirst())).horizontal();
		if (horizontal.lengthSqr() < EPSILON) return null;

		final Vec3 direction = horizontal.normalize();
		final double rise = switch (shape) {
			case ASCENDING_EAST -> Math.signum(direction.x);
			case ASCENDING_WEST -> -Math.signum(direction.x);
			case ASCENDING_NORTH -> -Math.signum(direction.z);
			case ASCENDING_SOUTH -> Math.signum(direction.z);
			default -> 0D;
		};
		return new TrackTangent(direction, new Vec3(direction.x, rise, direction.z));
	}

	public record TrackTangent(Vec3 horizontal, Vec3 slope) {
		public double along(Vec3 vec) {
			return vec.x * this.horizontal.x + vec.z * this.horizontal.z;
		}

		public TrackTangent facing(Vec3 direction) {
			return this.along(direction) < 0D ? this.reversed() : this;
		}

		public TrackTangent reversed() {
			return new TrackTangent(this.horizontal.scale(-1D), this.slope.scale(-1D));
		}
	}

	private MinecartTrackHelper() {}
}
