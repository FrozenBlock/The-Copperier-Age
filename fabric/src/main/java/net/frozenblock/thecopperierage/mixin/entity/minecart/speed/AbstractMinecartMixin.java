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

package net.frozenblock.thecopperierage.mixin.entity.minecart.speed;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.frozenblock.thecopperierage.block.CopperRailBlock;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractMinecart.class)
public class AbstractMinecartMixin {
	@ModifyReturnValue(method = "getMaxSpeed(Lnet/minecraft/server/level/ServerLevel;)D", at = @At("RETURN"))
	private double theCopperierAge$copperRailMaxSpeed(double original, ServerLevel level) {
		return CopperRailBlock.adjustMaxSpeed(AbstractMinecart.class.cast(this), level, original);
	}

	@ModifyReturnValue(method = "applyNaturalSlowdown", at = @At("RETURN"))
	private Vec3 theCopperierAge$copperRailDeceleration(Vec3 result, Vec3 movement) {
		return CopperRailBlock.applyDeceleration(AbstractMinecart.class.cast(this), movement, result);
	}
}
