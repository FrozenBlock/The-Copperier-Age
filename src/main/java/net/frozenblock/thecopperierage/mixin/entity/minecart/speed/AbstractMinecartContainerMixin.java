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
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecartContainer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractMinecartContainer.class)
public class AbstractMinecartContainerMixin {
	@ModifyReturnValue(method = "applyNaturalSlowdown", at = @At("RETURN"))
	private Vec3 theCopperierAge$copperRailDeceleration(Vec3 result, Vec3 movement) {
		return CopperRailBlock.applyDeceleration(AbstractMinecart.class.cast(this), movement, result);
	}
}
