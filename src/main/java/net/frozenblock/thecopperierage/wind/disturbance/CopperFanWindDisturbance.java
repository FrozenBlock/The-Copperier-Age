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

package net.frozenblock.thecopperierage.wind.disturbance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.lib.wind.disturbance.WindDisturbance;
import net.frozenblock.lib.wind.disturbance.WindDisturbanceResult;
import net.frozenblock.lib.wind.disturbance.WindDisturbanceType;
import net.frozenblock.thecopperierage.block.CopperFanBlock;
import net.frozenblock.thecopperierage.registry.TCAWindDisturbances;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public record CopperFanWindDisturbance(
	Vec3 origin,
	Vec3 areaMin,
	Vec3 areaMax,
	Direction direction,
	double fanDistance,
	boolean reverse,
	long creationGameTime
) implements WindDisturbance<LevelChunk> {
	public static final MapCodec<CopperFanWindDisturbance> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Vec3.CODEC.fieldOf("origin").forGetter(CopperFanWindDisturbance::origin),
		Vec3.CODEC.fieldOf("area_min").forGetter(CopperFanWindDisturbance::areaMin),
		Vec3.CODEC.fieldOf("area_max").forGetter(CopperFanWindDisturbance::areaMax),
		Direction.CODEC.fieldOf("direction").forGetter(CopperFanWindDisturbance::direction),
		Codec.DOUBLE.fieldOf("fan_distance").forGetter(CopperFanWindDisturbance::fanDistance),
		Codec.BOOL.fieldOf("reverse").forGetter(CopperFanWindDisturbance::reverse),
		Codec.LONG.fieldOf("creation_game_time").forGetter(CopperFanWindDisturbance::creationGameTime)
	).apply(instance, CopperFanWindDisturbance::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, CopperFanWindDisturbance> STREAM_CODEC = StreamCodec.composite(
		Vec3.STREAM_CODEC, CopperFanWindDisturbance::origin,
		Vec3.STREAM_CODEC, CopperFanWindDisturbance::areaMin,
		Vec3.STREAM_CODEC, CopperFanWindDisturbance::areaMax,
		Direction.STREAM_CODEC, CopperFanWindDisturbance::direction,
		ByteBufCodecs.DOUBLE, CopperFanWindDisturbance::fanDistance,
		ByteBufCodecs.BOOL, CopperFanWindDisturbance::reverse,
		ByteBufCodecs.VAR_LONG, CopperFanWindDisturbance::creationGameTime,
		CopperFanWindDisturbance::new
	);

	@Override
	public Vec3 origin(LevelChunk source, Level level) {
		return this.origin;
	}

	@Override
	public AABB area(LevelChunk source, Level level, Vec3 origin, Vec3 target, double scale) {
		return new AABB(this.areaMin, this.areaMax);
	}

	@Override
	public WindDisturbanceResult get(LevelChunk source, Level level, Vec3 origin, AABB area, Vec3 target, double scale) {
		final Vec3 movement = Vec3.atLowerCornerOf(this.direction.getUnitVec3i());
		final double windIntensityScale = this.reverse ? CopperFanBlock.WIND_INTENSITY_SUCK_SCALE : 1D;
		final double strength = this.fanDistance - Math.min(target.distanceTo(origin), this.fanDistance);
		final double fixedStrength = this.reverse ? ((this.fanDistance + strength + strength) / 3D) : strength;
		final double intensity = fixedStrength / this.fanDistance;
		return WindDisturbanceResult.success(
			Mth.clamp(intensity * 1.5D, 0D, 1D) * windIntensityScale,
			fixedStrength * 1.5D * windIntensityScale,
			movement.scale(intensity * CopperFanBlock.WIND_INTENSITY).scale(20D * windIntensityScale)
		);
	}

	@Override
	public boolean expired(LevelChunk source, Level level) {
		return level.getGameTime() > this.creationGameTime;
	}

	@Override
	public WindDisturbanceType<?> type() {
		return this.reverse ? TCAWindDisturbances.COPPER_FAN_WIND_DISTURBANCE_REVERSE : TCAWindDisturbances.COPPER_FAN_WIND_DISTURBANCE;
	}
}
