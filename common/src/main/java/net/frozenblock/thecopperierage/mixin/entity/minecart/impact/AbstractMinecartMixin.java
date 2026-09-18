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

package net.frozenblock.thecopperierage.mixin.entity.minecart.impact;

import net.frozenblock.thecopperierage.block.RelayerRailBlock;
import net.frozenblock.thecopperierage.entity.vehicle.minecart.coupling.MinecartCouplingUtil;
import net.frozenblock.thecopperierage.entity.vehicle.minecart.api.MinecartImpacts;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin {
	@Unique
	private static final int THECOPPERIERAGE$SOUND_COOLDOWN_TICKS = 6;
	@Unique
	private static final double THECOPPERIERAGE$HALT_MIN_SPEED = 0.2D;
	@Unique
	private static final double THECOPPERIERAGE$HALT_SPEED_DROP = 0.15D;

	@Unique
	private int theCopperierAge$impactSoundCooldown;
	@Unique
	private double theCopperierAge$speedBeforeTick;

	@Inject(method = "tick", at = @At("HEAD"))
	private void theCopperierAge$recordSpeedBeforeTick(CallbackInfo info) {
		final AbstractMinecart cart = AbstractMinecart.class.cast(this);
		if (cart.level().isClientSide()) return;

		if (this.theCopperierAge$impactSoundCooldown > 0) this.theCopperierAge$impactSoundCooldown--;
		this.theCopperierAge$speedBeforeTick = cart.getDeltaMovement().horizontalDistance();
	}

	@Inject(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/vehicle/minecart/MinecartBehavior;tick()V",
			shift = At.Shift.AFTER
		)
	)
	private void theCopperierAge$runOverEntities(CallbackInfo info) {
		final AbstractMinecart cart = AbstractMinecart.class.cast(this);
		if (!(cart.level() instanceof ServerLevel level)) return;

		final double closing = MinecartImpacts.runOverEntities(level, cart);
		if (closing > 0D) this.theCopperierAge$playImpactSound(level, cart.position(), closing);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void theCopperierAge$soundAbruptHalt(CallbackInfo info) {
		final AbstractMinecart cart = AbstractMinecart.class.cast(this);
		if (!(cart.level() instanceof ServerLevel level)) return;

		final double speedBefore = this.theCopperierAge$speedBeforeTick;
		final double drop = speedBefore - cart.getDeltaMovement().horizontalDistance();
		if (speedBefore < THECOPPERIERAGE$HALT_MIN_SPEED || drop < THECOPPERIERAGE$HALT_SPEED_DROP) return;
		if (!cart.horizontalCollision && !RelayerRailBlock.isDockedAt(level, cart)) return;

		this.theCopperierAge$playImpactSound(level, cart.position(), drop);
	}

	@Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
	private void theCopperierAge$solveCartCollisionsElsewhere(Entity entity, CallbackInfo info) {
		if (!(entity instanceof AbstractMinecart other)) return;
		if (MinecartImpacts.isSolverHandled(AbstractMinecart.class.cast(this), other)) info.cancel();
	}

	@Inject(method = "canCollideWith", at = @At("HEAD"), cancellable = true)
	private void theCopperierAge$passThroughOnImpact(Entity entity, CallbackInfoReturnable<Boolean> info) {
		if (!MinecartImpacts.enabled()) return;

		final AbstractMinecart cart = AbstractMinecart.class.cast(this);
		if (entity instanceof AbstractMinecart other) {
			if (MinecartCouplingUtil.areCoupledTogether(cart, other) || MinecartImpacts.isSolverHandled(cart, other)) info.setReturnValue(false);
			return;
		}

		if (MinecartImpacts.shouldPassThrough(cart, entity)) info.setReturnValue(false);
	}

	@Unique
	private void theCopperierAge$playImpactSound(ServerLevel level, Vec3 pos, double speed) {
		if (this.theCopperierAge$impactSoundCooldown > 0) return;
		this.theCopperierAge$impactSoundCooldown = THECOPPERIERAGE$SOUND_COOLDOWN_TICKS;
		MinecartImpacts.playImpactSound(level, AbstractMinecart.class.cast(this), pos, speed);
	}
}
