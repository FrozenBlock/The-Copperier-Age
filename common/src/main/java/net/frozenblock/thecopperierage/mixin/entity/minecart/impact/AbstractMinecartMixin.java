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

import net.frozenblock.lib.platform.ModLoader;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.block.RelayerRailBlock;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.entity.MinecartTrackHelper;
import net.frozenblock.thecopperierage.entity.coupling.MinecartCouplingUtil;
import net.frozenblock.thecopperierage.entity.impl.MinecartImpacts;
import net.frozenblock.thecopperierage.registry.TCAAttachmentTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.block.state.properties.RailShape;
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
	private static final double THECOPPERIERAGE$HALT_MIN_SPEED = 0.2D;
	@Unique
	private static final double THECOPPERIERAGE$HALT_SPEED_DROP = 0.15D;

	@Unique
	private double theCopperierAge$previousStepLength;

	@Inject(method = "tick", at = @At("HEAD"))
	private void theCopperierAge$recordSpeedBeforeTick(CallbackInfo info) {
		final AbstractMinecart cart = AbstractMinecart.class.cast(this);
		if (cart.level().isClientSide()) return;

		final int impactSoundCooldown = TCAAttachmentTypes.MINECART_IMPACT_SOUND_COOLDOWN.getAttachedOrElse(cart, 0);
		if (impactSoundCooldown > 0) TCAAttachmentTypes.MINECART_IMPACT_SOUND_COOLDOWN.set(cart, impactSoundCooldown - 1);
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

		final double previousStep = this.theCopperierAge$previousStepLength;
		this.theCopperierAge$previousStepLength = MinecartImpacts.getStepDelta(cart).horizontalDistance();
		final double stepDifference = previousStep - theCopperierAge$previousStepLength;
		if (previousStep < THECOPPERIERAGE$HALT_MIN_SPEED || stepDifference < THECOPPERIERAGE$HALT_SPEED_DROP) return;
		if (!cart.horizontalCollision && !RelayerRailBlock.isDockedAt(level, cart)) return;

		//this.theCopperierAge$playImpactSound(level, cart.position(), stepDifference);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void theCopperierAge$debugSlopeMotion(CallbackInfo info) {
		if (!TCAConfig.DEBUG_MINECART_MOTION.get() || !ModLoader.isDevelopmentEnvironment()) return;

		final AbstractMinecart cart = AbstractMinecart.class.cast(this);
		if (!(cart.level() instanceof ServerLevel level)) return;

		final RailShape shape = MinecartTrackHelper.railShapeUnder(cart);
		if (shape == null || !shape.isSlope()) return;

		AbstractMinecart nearest = null;
		double nearestDistance = Double.MAX_VALUE;
		for (AbstractMinecart other : level.getEntitiesOfClass(AbstractMinecart.class, cart.getBoundingBox().inflate(2D), candidate -> candidate != cart)) {
			final double distance = other.position().distanceTo(cart.position());
			if (distance < nearestDistance) {
				nearestDistance = distance;
				nearest = other;
			}
		}
		if (nearest == null) return;

		final Vec3 pos = cart.position();
		final Vec3 velocity = cart.getDeltaMovement();
		TCAConstants.LOGGER.info(String.format(
			"[TCA slope] t=%d id=%d shape=%s rails=%b pos=(%.4f,%.4f,%.4f) v=(%.4f,%.4f,%.4f) hCol=%b vCol=%b near=%d dist=%.4f solver=%b",
			level.getGameTime(), cart.getId(), shape.getSerializedName(), cart.isOnRails(),
			pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z,
			cart.horizontalCollision, cart.verticalCollision,
			nearest.getId(), nearestDistance, MinecartImpacts.isSolverHandled(cart, nearest)
		));
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
		final AbstractMinecart cart = AbstractMinecart.class.cast(this);
		if (TCAAttachmentTypes.MINECART_IMPACT_SOUND_COOLDOWN.getAttachedOrElse(cart, 0) > 0) return;

		TCAAttachmentTypes.MINECART_IMPACT_SOUND_COOLDOWN.set(cart, MinecartImpacts.IMPACT_SOUND_COOLDOWN_TICKS);
		MinecartImpacts.playImpactSound(level, cart, pos, speed);
	}
}
