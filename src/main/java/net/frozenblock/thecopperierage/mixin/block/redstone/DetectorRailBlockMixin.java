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

package net.frozenblock.thecopperierage.mixin.block.redstone;

import java.util.List;
import net.frozenblock.thecopperierage.entity.JukeboxMinecart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DetectorRailBlock.class)
public class DetectorRailBlockMixin {

	@Inject(
		method = "getAnalogOutputSignal(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)I",
		at = @At("HEAD"),
		cancellable = true
	)
	private void theCopperierAge$useJukeboxDiscComparatorValue(
		BlockState state,
		Level level,
		BlockPos pos,
		Direction direction,
		CallbackInfoReturnable<Integer> cir
	) {
		if (!state.getValue(DetectorRailBlock.POWERED)) {
			return;
		}

		final AABB searchBox = new AABB(
			pos.getX() + 0.2D,
			pos.getY(),
			pos.getZ() + 0.2D,
			pos.getX() + 0.8D,
			pos.getY() + 0.8D,
			pos.getZ() + 0.8D
		);

		final List<JukeboxMinecart> carts = level.getEntitiesOfClass(JukeboxMinecart.class, searchBox, entity -> true);
		if (carts.isEmpty()) {
			return;
		}

		cir.setReturnValue(carts.get(0).getComparatorOutput());
	}
}