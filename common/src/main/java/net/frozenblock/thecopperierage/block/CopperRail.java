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

package net.frozenblock.thecopperierage.block;

import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface CopperRail {
	double DECELERATION_BASE = 1.5D;
	double DECELERATION_AT_MAX_OXIDATION = 0.1D;

	WeatheringCopper.WeatherState getWeatherState();

	static double speedMultiplier(WeatheringCopper.WeatherState weatherState) {
		return switch (weatherState) {
			case UNAFFECTED -> 1.0D;
			case EXPOSED -> 0.8D;
			case WEATHERED -> 0.6D;
			case OXIDIZED -> 0.4D;
		};
	}

	static int oxidationLevel(WeatheringCopper.WeatherState weatherState) {
		return switch (weatherState) {
			case UNAFFECTED -> 1;
			case EXPOSED -> 2;
			case WEATHERED -> 3;
			case OXIDIZED -> 4;
		};
	}

	static double decelerationMultiplier(WeatheringCopper.WeatherState weatherState) {
		return Math.pow(DECELERATION_BASE, oxidationLevel(weatherState) - 1);
	}

	static double adjustMaxSpeed(AbstractMinecart minecart, ServerLevel level, double original) {
		final CopperRail rail = railUnder(minecart, level);
		if (rail == null) return original;

		final double capped = original * speedMultiplier(rail.getWeatherState());
		if (TCAConfig.DEBUG_MINECART_MOTION.get()) {
			TCAConstants.LOGGER.info(String.format(
				"[TCA copper cap] %-22s vehicle=%-5b %-10s maxSpeed %.4f -> %.4f",
				minecart.getType().toString(), minecart.isVehicle(), rail.getWeatherState(), original, capped
			));
		}
		return capped;
	}

	static double decelerationFactor(WeatheringCopper.WeatherState weatherState, double ironFactor) {
		final int maxLevel = oxidationLevel(WeatheringCopper.WeatherState.OXIDIZED);
		final double oxidationProgress = (oxidationLevel(weatherState) - 1) / (double) (maxLevel - 1);
		final double ironDeceleration = 1.0D - ironFactor;
		final double deceleration = Mth.lerp(oxidationProgress, ironDeceleration, DECELERATION_AT_MAX_OXIDATION)
			* decelerationMultiplier(weatherState);
		return Math.clamp(1.0D - deceleration, 0.0D, 1.0D);
	}

	static Vec3 applyDeceleration(AbstractMinecart minecart, Vec3 before, Vec3 after) {
		final CopperRail rail = railUnder(minecart, minecart.level());
		if (rail == null) return after;

		final double beforeSpeed = before.horizontalDistance();
		final double afterSpeed = after.horizontalDistance();
		if (beforeSpeed < 1.0E-9D || afterSpeed < 1.0E-9D) return after;

		final double ironFactor = Math.min(1.0D, afterSpeed / beforeSpeed);
		final double adjusted = decelerationFactor(rail.getWeatherState(), ironFactor);
		final double scale = adjusted / ironFactor;

		if (TCAConfig.DEBUG_MINECART_MOTION.get()) {
			TCAConstants.LOGGER.info(String.format(
				"[TCA copper] %-24s %-10s ironFactor=%.4f -> %.4f  hSpeed %.4f -> %.4f",
				minecart.getType().toString(), rail.getWeatherState(), ironFactor, adjusted, afterSpeed, afterSpeed * scale
			));
		}
		return new Vec3(after.x * scale, after.y, after.z * scale);
	}

	static void clampStoredSpeed(AbstractMinecart minecart, ServerLevel level) {
		final CopperRail rail = railUnder(minecart, level);
		if (rail == null) return;

		final Vec3 movement = minecart.getDeltaMovement();
		final double speed = movement.horizontalDistance();
		final double maxSpeed = minecart.getBehavior().getMaxSpeed(level) * speedMultiplier(rail.getWeatherState());
		if (speed <= maxSpeed || speed < 1.0E-9D) return;

		final double scale = maxSpeed / speed;
		minecart.setDeltaMovement(movement.x * scale, movement.y, movement.z * scale);
	}

	@Nullable
	static CopperRail railUnder(AbstractMinecart minecart, Level level) {
		final BlockPos cartPos = minecart.blockPosition();
		final BlockPos[] candidates = {
			minecart.getCurrentBlockPosOrRailBelow(),
			cartPos,
			cartPos.below(),
			cartPos.above()
		};
		for (BlockPos candidate : candidates) {
			if (level.getBlockState(candidate).getBlock() instanceof CopperRail rail) return rail;
		}
		return null;
	}
}
