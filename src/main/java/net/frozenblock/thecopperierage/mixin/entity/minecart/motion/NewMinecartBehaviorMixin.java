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

package net.frozenblock.thecopperierage.mixin.entity.minecart.motion;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.frozenblock.thecopperierage.block.RelayorRailBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(NewMinecartBehavior.class)
public class NewMinecartBehaviorMixin {

	@ModifyReturnValue(
		method = "calculateBoostTrackSpeed(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/phys/Vec3;",
		at = @At("RETURN")
	)
	private Vec3 theCopperierAge$boostAlongRelayor(Vec3 boosted, Vec3 deltaMovement, BlockPos pos, BlockState state) {
		if (!(state.getBlock() instanceof RelayorRailBlock rail) || !state.getValue(RelayorRailBlock.POWERED)) return boosted;

		final Direction direction = rail.getDirection(state);
		final double speed = deltaMovement.horizontalDistance();
		final double target = speed > RelayorRailBlock.MIN_MOVING_SPEED
			? speed + RelayorRailBlock.BOOST_PER_TICK
			: RelayorRailBlock.LAUNCH_FROM_REST;

		return new Vec3(direction.getStepX() * target, deltaMovement.y, direction.getStepZ() * target);
	}
}
