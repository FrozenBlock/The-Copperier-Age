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
import net.frozenblock.lib.wind.disturbance.BlockStateWindDisturbance;
import net.frozenblock.lib.wind.disturbance.WindDisturbanceResult;
import net.frozenblock.lib.wind.disturbance.WindDisturbanceType;
import net.frozenblock.thecopperierage.block.CopperFanBlock;
import net.frozenblock.thecopperierage.registry.TCAWindDisturbances;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CopperFanWindDisturbance extends BlockStateWindDisturbance {
	private static final AABB NEVER_TO_BE_SEEN_AREA = AABB.ofSize(new Vec3(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE), 0.05D, 0.05D, 0.05D);
	public static final MapCodec<CopperFanWindDisturbance> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockState.CODEC.fieldOf("block_state").forGetter(disturbance -> disturbance.blockState),
		BlockPos.CODEC.fieldOf("position").forGetter(disturbance -> disturbance.position),
		Codec.DOUBLE.fieldOf("distance").forGetter(disturbance -> disturbance.distance),
		Codec.BOOL.fieldOf("reverse").forGetter(disturbance -> disturbance.reverse)
	).apply(instance, CopperFanWindDisturbance::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, CopperFanWindDisturbance> STREAM_CODEC = StreamCodec.composite(
		BLOCK_STATE_CODEC, disturbance -> disturbance.blockState,
		BlockPos.STREAM_CODEC, disturbance -> disturbance.position,
		ByteBufCodecs.DOUBLE, disturbance -> disturbance.distance,
		ByteBufCodecs.BOOL, disturbance -> disturbance.reverse,
		CopperFanWindDisturbance::new
	);
	public final double distance;
	public final boolean reverse;
	private final Direction facing;
	private final Direction searchDirection;

	public CopperFanWindDisturbance(BlockState blockState, BlockPos position, double distance, boolean reverse) {
		super(blockState, position);
		this.distance = distance;
		this.reverse = reverse;
		this.facing = blockState.getValue(CopperFanBlock.FACING);
		this.searchDirection = !reverse ? this.facing : this.facing.getOpposite();
	}

	@Override
	public AABB area(ChunkAccess source, Level level, Vec3 origin, Vec3 target, double scale) {
		return CopperFanBlock.computeBlowingArea(level, this.blockState, this.position, this.searchDirection, this.reverse)
			.orElse(NEVER_TO_BE_SEEN_AREA);
	}

	@Override
	public WindDisturbanceResult get(ChunkAccess source, Level level, Vec3 origin, AABB area, Vec3 target, double scale) {
		final Vec3 movement = Vec3.atLowerCornerOf(this.facing.getUnitVec3i());
		final double windIntensityScale = this.reverse ? CopperFanBlock.WIND_INTENSITY_SUCK_SCALE : 1D;
		final double strength = this.distance - Math.min(target.distanceTo(origin), this.distance);
		final double fixedStrength = this.reverse ? ((this.distance + strength + strength) / 3D) : strength;
		final double intensity = fixedStrength / this.distance;
		return WindDisturbanceResult.success(
			Mth.clamp(intensity * 1.5D, 0D, 1D) * windIntensityScale,
			fixedStrength * 1.5D * windIntensityScale,
			movement.scale(intensity * CopperFanBlock.WIND_INTENSITY).scale(20D * windIntensityScale)
		);
	}

	@Override
	public WindDisturbanceType<?> type() {
		return this.reverse ? TCAWindDisturbances.COPPER_FAN_WIND_DISTURBANCE_REVERSE : TCAWindDisturbances.COPPER_FAN_WIND_DISTURBANCE;
	}
}
