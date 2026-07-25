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

import net.frozenblock.thecopperierage.block.CrossRailBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.OldMinecartBehavior;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(OldMinecartBehavior.class)
public class OldMinecartBehaviorMixin {

	@ModifyVariable(method = "moveAlongTrack", at = @At("STORE"))
	private RailShape theCopperierAge$crossRailShape(RailShape shape) {
		final AbstractMinecart minecart = ((MinecartBehaviorAccessor) this).theCopperierAge$getMinecart();
		final BlockPos pos = minecart.getCurrentBlockPosOrRailBelow();
		if (!(minecart.level().getBlockState(pos).getBlock() instanceof CrossRailBlock)) return shape;
		return CrossRailBlock.travelShape(minecart.level(), pos, minecart);
	}
}
