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

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CopperRailBlock extends BaseRailBlock {
	public static final MapCodec<CopperRailBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
			WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(block -> block.weatherState),
			propertiesCodec()
		).apply(instance, CopperRailBlock::new)
	);
	public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE;

	private static final double DECELERATION_BASE = 1.5D;
	private static final double DECELERATION_AT_MAX_OXIDATION = 0.1D;

	public final WeatheringCopper.WeatherState weatherState;

	public CopperRailBlock(WeatheringCopper.WeatherState weatherState, Properties properties) {
		super(false, properties);
		this.weatherState = weatherState;
		this.registerDefaultState(this.stateDefinition.any().setValue(SHAPE, RailShape.NORTH_SOUTH).setValue(WATERLOGGED, false));
	}

	@Override
	public MapCodec<? extends CopperRailBlock> codec() {
		return CODEC;
	}

	@Override
	public Property<RailShape> getShapeProperty() {
		return SHAPE;
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(SHAPE, this.rotate(state.getValue(SHAPE), rotation));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.setValue(SHAPE, this.mirror(state.getValue(SHAPE), mirror));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(SHAPE, WATERLOGGED);
	}

	public static double speedMultiplier(WeatheringCopper.WeatherState weatherState) {
		return switch (weatherState) {
			case UNAFFECTED -> 1.0D;
			case EXPOSED -> 0.8D;
			case WEATHERED -> 0.6D;
			case OXIDIZED -> 0.4D;
		};
	}

	public static int oxidationLevel(WeatheringCopper.WeatherState weatherState) {
		return switch (weatherState) {
			case UNAFFECTED -> 1;
			case EXPOSED -> 2;
			case WEATHERED -> 3;
			case OXIDIZED -> 4;
		};
	}

	public static double decelerationMultiplier(WeatheringCopper.WeatherState weatherState) {
		return Math.pow(DECELERATION_BASE, oxidationLevel(weatherState) - 1);
	}

	public static double adjustMaxSpeed(AbstractMinecart minecart, ServerLevel level, double original) {
		final CopperRailBlock rail = railUnder(minecart, level);
		if (rail == null) return original;

		final double capped = original * speedMultiplier(rail.weatherState);
		if (TCAConfig.DEBUG_MINECART_MOTION.get()) {
			TCAConstants.LOGGER.info(String.format(
				"[TCA copper cap] %-22s vehicle=%-5b %-10s maxSpeed %.4f -> %.4f",
				minecart.getType().toString(), minecart.isVehicle(), rail.weatherState, original, capped
			));
		}
		return capped;
	}

	public static double decelerationFactor(WeatheringCopper.WeatherState weatherState, double ironFactor) {
		final int maxLevel = oxidationLevel(WeatheringCopper.WeatherState.OXIDIZED);
		final double oxidationProgress = (oxidationLevel(weatherState) - 1) / (double) (maxLevel - 1);
		final double ironDeceleration = 1.0D - ironFactor;
		final double deceleration = Mth.lerp(oxidationProgress, ironDeceleration, DECELERATION_AT_MAX_OXIDATION)
			* decelerationMultiplier(weatherState);
		return Math.clamp(1.0D - deceleration, 0.0D, 1.0D);
	}

	public static Vec3 applyDeceleration(AbstractMinecart minecart, Vec3 before, Vec3 after) {
		final CopperRailBlock rail = railUnder(minecart, minecart.level());
		if (rail == null) return after;

		final double beforeSpeed = before.horizontalDistance();
		final double afterSpeed = after.horizontalDistance();
		if (beforeSpeed < 1.0E-9D || afterSpeed < 1.0E-9D) return after;

		final double ironFactor = Math.min(1.0D, afterSpeed / beforeSpeed);
		final double adjusted = decelerationFactor(rail.weatherState, ironFactor);
		final double scale = adjusted / ironFactor;

		if (TCAConfig.DEBUG_MINECART_MOTION.get()) {
			TCAConstants.LOGGER.info(String.format(
				"[TCA copper] %-24s %-10s ironFactor=%.4f -> %.4f  hSpeed %.4f -> %.4f",
				minecart.getType().toString(), rail.weatherState, ironFactor, adjusted, afterSpeed, afterSpeed * scale
			));
		}
		return new Vec3(after.x * scale, after.y, after.z * scale);
	}

	@Nullable
	private static CopperRailBlock railUnder(AbstractMinecart minecart, Level level) {
		final BlockPos cartPos = minecart.blockPosition();
		final BlockPos[] candidates = {
			minecart.getCurrentBlockPosOrRailBelow(),
			cartPos,
			cartPos.below(),
			cartPos.above()
		};
		for (BlockPos candidate : candidates) {
			if (level.getBlockState(candidate).getBlock() instanceof CopperRailBlock rail) return rail;
		}
		return null;
	}
}
