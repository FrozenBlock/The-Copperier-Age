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
import net.frozenblock.thecopperierage.block.RelayerRailBlock;
import net.frozenblock.thecopperierage.entity.vehicle.minecart.impl.CrossRailAxisInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecart.class)
public class AbstractMinecartMixin implements CrossRailAxisInterface {

	@Unique
	@Nullable
	private BlockPos theCopperierAge$crossRailPos = null;

	@Unique
	@Nullable
	private Direction.Axis theCopperierAge$crossRailAxis = null;

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/vehicle/minecart/MinecartBehavior;tick()V"
		)
	)
	private void theCopperierAge$applyCustomRails(CallbackInfo info) {
		final AbstractMinecart minecart = AbstractMinecart.class.cast(this);
		if (!(minecart.level() instanceof ServerLevel level)) return;

		final BlockPos pos = minecart.getCurrentBlockPosOrRailBelow();
		final BlockState state = level.getBlockState(pos);

		if (state.getBlock() instanceof RelayerRailBlock) {
			RelayerRailBlock.handleCart(level, pos, state, minecart);
		}
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void theCopperierAge$settleDockedCart(CallbackInfo info) {
		final AbstractMinecart minecart = AbstractMinecart.class.cast(this);
		final Level level = minecart.level();
		final BlockPos pos = minecart.getCurrentBlockPosOrRailBelow();
		final BlockState state = level.getBlockState(pos);

		if (!(state.getBlock() instanceof CrossRailBlock) || !pos.equals(this.theCopperierAge$crossRailPos)) {
			this.theCopperierAge$setCrossRailAxis(null, null);
		}

		RelayerRailBlock.holdDocked(level, pos, state, minecart);
	}

	@Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
	private void theCopperierAge$keepDockedCartUnpushable(Entity entity, CallbackInfo info) {
		final AbstractMinecart minecart = AbstractMinecart.class.cast(this);
		if (RelayerRailBlock.isDockedAt(minecart.level(), minecart)) info.cancel();
	}

	@Unique
	@Nullable
	@Override
	public BlockPos theCopperierAge$getCrossRailPos() {
		return this.theCopperierAge$crossRailPos;
	}

	@Unique
	@Nullable
	@Override
	public Direction.Axis theCopperierAge$getCrossRailAxis() {
		return this.theCopperierAge$crossRailAxis;
	}

	@Unique
	@Override
	public void theCopperierAge$setCrossRailAxis(@Nullable BlockPos pos, @Nullable Direction.Axis axis) {
		this.theCopperierAge$crossRailPos = pos;
		this.theCopperierAge$crossRailAxis = axis;
	}

	@ModifyVariable(method = "makeStepAlongTrack", at = @At("HEAD"), argsOnly = true)
	private RailShape theCopperierAge$crossRailStepShape(RailShape shape, BlockPos pos, RailShape unusedShape, double movementLeft) {
		final AbstractMinecart minecart = AbstractMinecart.class.cast(this);
		final BlockState state = minecart.level().getBlockState(pos);
		if (!(state.getBlock() instanceof CrossRailBlock)) return shape;

		return CrossRailBlock.railShapeFromMotion(minecart.level(), pos, minecart);
	}

	@Inject(method = "move", at = @At("HEAD"), cancellable = true)
	private void theCopperierAge$holdDockedCart(MoverType moverType, Vec3 delta, CallbackInfo info) {
		final AbstractMinecart minecart = AbstractMinecart.class.cast(this);
		if (!(minecart.level() instanceof ServerLevel level)) return;

		final BlockPos pos = minecart.getCurrentBlockPosOrRailBelow();
		if (RelayerRailBlock.isDocked(level, pos, level.getBlockState(pos), minecart)) info.cancel();
	}
}
