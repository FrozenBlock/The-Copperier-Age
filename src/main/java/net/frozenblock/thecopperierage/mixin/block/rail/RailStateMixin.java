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

package net.frozenblock.thecopperierage.mixin.block.rail;

import java.util.List;
import net.frozenblock.thecopperierage.block.CrossRailBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.RailState;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RailState.class)
public class RailStateMixin {
	@Shadow
	@Final
	private BlockPos pos;
	@Shadow
	@Final
	private BaseRailBlock block;
	@Shadow
	@Final
	private List<BlockPos> connections;

	@Inject(method = "removeSoftConnections", at = @At("HEAD"), cancellable = true)
	private void theCopperierAge$crossRailKeepsAllSides(CallbackInfo info) {
		if (this.block instanceof CrossRailBlock) info.cancel();
	}

	@Inject(method = "canConnectTo", at = @At("HEAD"), cancellable = true)
	private void theCopperierAge$crossRailAcceptsAnyNeighbour(RailState rail, CallbackInfoReturnable<Boolean> info) {
		if (this.block instanceof CrossRailBlock) info.setReturnValue(true);
	}

	@Inject(method = "updateConnections", at = @At("TAIL"))
	private void theCopperierAge$crossRailConnectsOnAllSides(RailShape direction, CallbackInfo info) {
		if (!(this.block instanceof CrossRailBlock)) return;
		this.connections.clear();
		this.connections.add(this.pos.north());
		this.connections.add(this.pos.south());
		this.connections.add(this.pos.west());
		this.connections.add(this.pos.east());
	}
}
