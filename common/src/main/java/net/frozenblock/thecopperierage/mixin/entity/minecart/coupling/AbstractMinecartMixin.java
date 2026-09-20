/*
 * Copyright 2026 FrozenBlock
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

package net.frozenblock.thecopperierage.mixin.entity.minecart.coupling;

import net.frozenblock.thecopperierage.entity.vehicle.minecart.coupling.MinecartCouplingUtil;
import net.frozenblock.thecopperierage.entity.vehicle.minecart.coupling.CouplingToEntityInterface;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin implements CouplingToEntityInterface {

	@Unique
	private static final double THECOPPERIERAGE$JAM_DISPLACEMENT_SQR = 1.0E-6D;

	@Shadow
	protected abstract double getMaxSpeed(ServerLevel level);

	@Unique
	@Nullable
	private Entity theCopperierAge$coupledTo = null;
	@Unique
	@Nullable
	private Vec3 theCopperierAge$tickStartPosition = null;
	@Unique
	@Nullable
	private Vec3 theCopperierAge$blockedDirection;
	@Unique
	private long theCopperierAge$blockedTick = Long.MIN_VALUE;
	@Unique
	private boolean theCopperierAge$terrainJammed;
	@Unique
	private int theCopperierAge$trainSize = 1;
	@Unique
	private int theCopperierAge$missingCoupledToTicks;
	@Unique
	private int theCopperierAge$missingCoupledFromTicks;

	@Inject(method = "tick", at = @At("HEAD"))
	private void theCopperierAge$recordTickStart(CallbackInfo info) {
		final AbstractMinecart minecart = AbstractMinecart.class.cast(this);
		if (!minecart.level().isClientSide()) this.theCopperierAge$tickStartPosition = minecart.position();
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/vehicle/minecart/MinecartBehavior;tick()V",
			shift = At.Shift.AFTER
		)
	)
	private void theCopperierAge$tickCoupling(CallbackInfo info) {
		MinecartCouplingUtil.tickCoupling(AbstractMinecart.class.cast(this));
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void theCopperierAge$trackTerrainJam(CallbackInfo info) {
		final AbstractMinecart minecart = AbstractMinecart.class.cast(this);
		if (minecart.level().isClientSide()) return;

		if (minecart.horizontalCollision) {
			this.theCopperierAge$terrainJammed = true;
			return;
		}

		final Vec3 tickStart = this.theCopperierAge$tickStartPosition;
		if (tickStart != null && minecart.position().subtract(tickStart).horizontal().lengthSqr() > THECOPPERIERAGE$JAM_DISPLACEMENT_SQR) {
			this.theCopperierAge$terrainJammed = false;
		}
	}

	@Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
	private void theCopperierAge$ignoreCoupledPartnerCollision(Entity entity, CallbackInfo info) {
		if (MinecartCouplingUtil.areCoupledTogether(AbstractMinecart.class.cast(this), entity)) info.cancel();
	}

	@Unique
	@Override
	public void theCopperierAge$setCoupledTo(@Nullable Entity entity) {
		this.theCopperierAge$coupledTo = entity;
	}

	@Unique
	@Nullable
	@Override
	public Entity theCopperierAge$getCoupledTo() {
		return this.theCopperierAge$coupledTo;
	}

	@Unique
	@Override
	public double theCopperierAge$getMaxSpeed(ServerLevel level) {
		return this.getMaxSpeed(level);
	}

	@Unique
	@Nullable
	@Override
	public Vec3 theCopperierAge$getTickStartPosition() {
		return this.theCopperierAge$tickStartPosition;
	}

	@Unique
	@Nullable
	@Override
	public Vec3 theCopperierAge$getBlockedDirection() {
		return this.theCopperierAge$blockedDirection;
	}

	@Unique
	@Override
	public long theCopperierAge$getBlockedTick() {
		return this.theCopperierAge$blockedTick;
	}

	@Unique
	@Override
	public void theCopperierAge$setBlocked(Vec3 direction, long gameTime) {
		this.theCopperierAge$blockedDirection = direction;
		this.theCopperierAge$blockedTick = gameTime;
	}

	@Unique
	@Override
	public boolean theCopperierAge$isTerrainJammed() {
		return this.theCopperierAge$terrainJammed;
	}

	@Unique
	@Override
	public int theCopperierAge$getTrainSize() {
		return this.theCopperierAge$trainSize;
	}

	@Unique
	@Override
	public void theCopperierAge$setTrainSize(int size) {
		this.theCopperierAge$trainSize = Math.max(1, size);
	}

	@Unique
	@Override
	public int theCopperierAge$incrementMissingCoupledTo() {
		return ++this.theCopperierAge$missingCoupledToTicks;
	}

	@Unique
	@Override
	public void theCopperierAge$resetMissingCoupledTo() {
		this.theCopperierAge$missingCoupledToTicks = 0;
	}

	@Unique
	@Override
	public int theCopperierAge$incrementMissingCoupledFrom() {
		return ++this.theCopperierAge$missingCoupledFromTicks;
	}

	@Unique
	@Override
	public void theCopperierAge$resetMissingCoupledFrom() {
		this.theCopperierAge$missingCoupledFromTicks = 0;
	}
}
