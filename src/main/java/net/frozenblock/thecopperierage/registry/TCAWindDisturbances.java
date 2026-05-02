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

package net.frozenblock.thecopperierage.registry;

import java.util.Optional;
import net.frozenblock.lib.wind.api.WindDisturbance;
import net.frozenblock.lib.wind.api.WindDisturbanceLogic;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.block.CopperFanBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class TCAWindDisturbances {
	public static final Identifier COPPER_FAN_WIND_DISTURBANCE = TCAConstants.id("copper_fan");
	public static final Identifier COPPER_FAN_WIND_DISTURBANCE_REVERSE = TCAConstants.id("copper_fan_reverse");

	public static void init() {
		WindDisturbanceLogic.register(
			COPPER_FAN_WIND_DISTURBANCE,
			(WindDisturbanceLogic.DisturbanceLogic<CopperFanBlock>) (source, level, windOrigin, affectedArea, windTarget) -> {
				return getCopperFanDisturbanceResult(source, level, windOrigin, windTarget, false, 1D);
			}
		);

		WindDisturbanceLogic.register(
			COPPER_FAN_WIND_DISTURBANCE_REVERSE,
			(WindDisturbanceLogic.DisturbanceLogic<CopperFanBlock>) (source, level, windOrigin, affectedArea, windTarget) -> {
				return getCopperFanDisturbanceResult(source, level, windOrigin, windTarget, true, CopperFanBlock.WIND_INTENSITY_SUCK_SCALE);
			}
		);
	}

	@Nullable
	private static WindDisturbance.DisturbanceResult getCopperFanDisturbanceResult(
		Optional<CopperFanBlock> source,
		Level level,
		Vec3 windOrigin,
		Vec3 windTarget,
		boolean reverse,
		double windIntensityScale
	) {
		if (source.isEmpty()) return null;

		final BlockPos pos = BlockPos.containing(windOrigin);
		final BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof CopperFanBlock copperFanBlock)) return null;

		final double fanDistance = (!reverse ? copperFanBlock.pushBlocks : copperFanBlock.suckBlocks) + 1D;
		final Direction direction = state.getValue(CopperFanBlock.FACING);
		final Vec3 movement = Vec3.atLowerCornerOf(direction.getUnitVec3i());
		double strength = fanDistance - Math.min(windTarget.distanceTo(windOrigin), fanDistance);
		double fixedStrength = reverse ? ((fanDistance + strength + strength) / 3D) : strength;
		double intensity = fixedStrength / fanDistance;
		return new WindDisturbance.DisturbanceResult(
			Mth.clamp(intensity * 1.5D, 0D, 1D) * windIntensityScale,
			fixedStrength * 1.5D * windIntensityScale,
			movement.scale(intensity * CopperFanBlock.WIND_INTENSITY).scale(20D * windIntensityScale)
		);
	}
}
