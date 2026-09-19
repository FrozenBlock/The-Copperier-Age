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

package net.frozenblock.thecopperierage.mixin.entity.minecart.rail;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.frozenblock.thecopperierage.block.RelayerRailBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NewMinecartBehavior.class)
public abstract class NewMinecartBehaviorMixin extends MinecartBehavior {
	@Unique
	private static final double THECOPPERIERAGE$SLOPE_PIN_TOLERANCE = 0.02D;
	@Unique
	private static final double THECOPPERIERAGE$MAX_SLOPE_PIN_PER_STEP = 0.25D;

	protected NewMinecartBehaviorMixin(AbstractMinecart minecart) {
		super(minecart);
	}

	@ModifyReturnValue(
		method = "calculateBoostTrackSpeed(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/phys/Vec3;",
		at = @At("RETURN")
	)
	private Vec3 theCopperierAge$boostAlongRelayor(Vec3 boosted, Vec3 deltaMovement, BlockPos pos, BlockState state) {
		if (!RelayerRailBlock.isPowered(state)) return boosted;
		return RelayerRailBlock.boost(state, deltaMovement);
	}

	@Inject(method = "stepAlongTrack", at = @At("RETURN"))
	private void theCopperierAge$pinToSlope(BlockPos pos, RailShape shape, double movementLeft, CallbackInfoReturnable<Double> info) {
		if (!shape.isSlope()) return;

		final Vec3 position = this.position();
		final double surface = theCopperierAge$slopeSurfaceY(pos, shape, position);
		final double excess = position.y - surface;
		if (excess <= THECOPPERIERAGE$SLOPE_PIN_TOLERANCE) return;

		final double corrected = Math.max(surface, position.y - Math.min(excess, THECOPPERIERAGE$MAX_SLOPE_PIN_PER_STEP));
		this.setPos(position.x, corrected, position.z);
	}

	@Unique
	private static double theCopperierAge$slopeSurfaceY(BlockPos pos, RailShape shape, Vec3 position) {
		final double alongAscent = switch (shape) {
			case ASCENDING_EAST -> position.x - pos.getX();
			case ASCENDING_WEST -> 1D - (position.x - pos.getX());
			case ASCENDING_NORTH -> 1D - (position.z - pos.getZ());
			case ASCENDING_SOUTH -> position.z - pos.getZ();
			default -> 0D;
		};
		return pos.getY() + NewMinecartBehavior.ON_RAIL_Y_OFFSET + Mth.clamp(alongAscent, 0D, 1D);
	}
}
