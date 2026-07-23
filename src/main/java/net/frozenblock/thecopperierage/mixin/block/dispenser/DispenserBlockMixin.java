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

package net.frozenblock.thecopperierage.mixin.block.dispenser;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.frozenblock.thecopperierage.entity.MinecartDispenseContext;
import net.minecraft.core.Position;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {

	@ModifyReturnValue(
		method = "getDispensePosition(Lnet/minecraft/core/dispenser/BlockSource;DLnet/minecraft/world/phys/Vec3;)Lnet/minecraft/core/Position;",
		at = @At("RETURN")
	)
	private static Position theCopperierAge$offsetDispenseForMinecart(Position original) {
		final Vec3 offset = MinecartDispenseContext.offset();
		if (offset == null) return original;
		return new Vec3(original.x() + offset.x, original.y() + offset.y, original.z() + offset.z);
	}
}
