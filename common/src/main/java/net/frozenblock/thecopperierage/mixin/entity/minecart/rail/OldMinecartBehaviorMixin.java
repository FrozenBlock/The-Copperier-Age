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

import net.frozenblock.thecopperierage.block.CopperRail;
import net.frozenblock.thecopperierage.block.CrossRailBlock;
import net.frozenblock.thecopperierage.block.RelayerRailBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartBehavior;
import net.minecraft.world.entity.vehicle.minecart.OldMinecartBehavior;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OldMinecartBehavior.class)
public abstract class OldMinecartBehaviorMixin extends MinecartBehavior {

	protected OldMinecartBehaviorMixin(AbstractMinecart minecart) {
		super(minecart);
	}

	@ModifyVariable(method = "moveAlongTrack", at = @At("STORE"))
	private RailShape theCopperierAge$crossRailShape(RailShape shape) {
		final AbstractMinecart minecart = this.minecart;
		final BlockPos pos = minecart.getCurrentBlockPosOrRailBelow();
		if (!(minecart.level().getBlockState(pos).getBlock() instanceof CrossRailBlock)) return shape;
		return CrossRailBlock.railShapeFromMotion(minecart.level(), pos, minecart);
	}

	@ModifyVariable(method = "getPos", at = @At("STORE"))
	private RailShape theCopperierAge$crossRailPosShape(RailShape shape, double x, double y, double z) {
		return this.theCopperierAge$crossRailShapeAt(shape, x, y, z);
	}

	@ModifyVariable(method = "getPosOffs", at = @At("STORE"))
	private RailShape theCopperierAge$crossRailPosOffsShape(RailShape shape, double x, double y, double z, double offs) {
		return this.theCopperierAge$crossRailShapeAt(shape, x, y, z);
	}

	@Unique
	private RailShape theCopperierAge$crossRailShapeAt(RailShape shape, double x, double y, double z) {
		final Level level = this.level();
		final int xt = Mth.floor(x);
		final int zt = Mth.floor(z);
		int yt = Mth.floor(y);
		if (level.getBlockState(new BlockPos(xt, yt - 1, zt)).is(BlockTags.RAILS)) yt--;

		final BlockPos pos = new BlockPos(xt, yt, zt);
		if (!(level.getBlockState(pos).getBlock() instanceof CrossRailBlock)) return shape;
		return CrossRailBlock.railShapeFromMotion(level, pos, this.minecart);
	}

	@Inject(method = "moveAlongTrack", at = @At("RETURN"))
	private void theCopperierAge$applyCustomRailSpeeds(ServerLevel level, CallbackInfo info) {
		final AbstractMinecart minecart = this.minecart;
		CopperRail.clampStoredSpeed(minecart, level);

		final BlockPos pos = minecart.getCurrentBlockPosOrRailBelow();
		final BlockState state = level.getBlockState(pos);
		if (!RelayerRailBlock.isPowered(state)) return;
		this.setDeltaMovement(RelayerRailBlock.boost(state, this.getDeltaMovement()));
	}
}
