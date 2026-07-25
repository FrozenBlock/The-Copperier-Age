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

package net.frozenblock.thecopperierage.mixin.entity.minecart.motion;

import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.entity.coupling.CouplingData;
import net.frozenblock.thecopperierage.entity.coupling.MinecartCouplingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Keeps the experimental minecart movement from spontaneously reversing a cart's travel direction
 * (the "miraculous reverse"). This also stops furnace carts from suddenly powering backwards, since
 * their push follows the cart's velocity ({@code MinecartFurnace#calculateNewPushAlong}). Legitimate
 * reversals -- gravity roll-back on slopes, powered rails, block collisions, and rider steering --
 * are left untouched. Only near-180-degree flips within a single tick are corrected, so genuine
 * curves and corners (which turn at most ~90 degrees per tick) are unaffected.
 *
 * <p>Coupled carts are skipped entirely: the coupling solver ({@code MinecartCouplingUtil}) already
 * pushes carts both ways to hold a train at length, and correcting those as "reversals" fought the
 * solver, making couplings oscillate and snap apart.
 *
 */
@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin {
	@Unique
	private static final double THECOPPERIERAGE$MIN_SPEED_SQR = 1.0E-4D;
	@Unique
	private static final double THECOPPERIERAGE$REVERSAL_DOT = -0.5D;
	@Unique
	private Vec3 theCopperierAge$lastTravelDirection = Vec3.ZERO;

	@Inject(method = "moveAlongTrack", at = @At("TAIL"))
	private void theCopperierAge$preventSpuriousReversal(ServerLevel level, CallbackInfo info) {
		final AbstractMinecart minecart = AbstractMinecart.class.cast(this);
		if (!TCAConfig.SMOOTH_MINECART_MOTION.get()
			|| !AbstractMinecart.useExperimentalMovement(level)
			|| !(minecart.getBehavior() instanceof NewMinecartBehavior)
			|| !minecart.isOnRails()
			|| theCopperierAge$isCoupled(minecart)) {
			this.theCopperierAge$lastTravelDirection = Vec3.ZERO;
			return;
		}

		final Vec3 velocity = minecart.getDeltaMovement();
		final double speedSqr = (velocity.x * velocity.x) + (velocity.z * velocity.z);
		if (speedSqr < THECOPPERIERAGE$MIN_SPEED_SQR) return;

		final double speed = Math.sqrt(speedSqr);
		final Vec3 direction = new Vec3(velocity.x / speed, 0.0D, velocity.z / speed);
		final Vec3 last = this.theCopperierAge$lastTravelDirection;

		if (last.lengthSqr() > 1.0E-6D && direction.dot(last) < THECOPPERIERAGE$REVERSAL_DOT) {
			final boolean allowed = theCopperierAge$reversalAllowed(minecart, level);
			if (TCAConfig.DEBUG_MINECART_MOTION.get()) {
				theCopperierAge$logReversal(minecart, level, last, direction, speed, allowed);
			}
			if (!allowed) {
				minecart.setDeltaMovement(last.x * speed, velocity.y, last.z * speed);
				return;
			}
		}

		this.theCopperierAge$lastTravelDirection = direction;
	}

	@Unique
	private static boolean theCopperierAge$isCoupled(AbstractMinecart minecart) {
		final CouplingData coupling = MinecartCouplingUtil.getCoupling(minecart);
		return coupling.isCoupledTo() || coupling.isCoupledFrom();
	}

	@Unique
	private static void theCopperierAge$logReversal(
		AbstractMinecart minecart, ServerLevel level, Vec3 last, Vec3 now, double speed, boolean allowed
	) {
		final BlockPos pos = minecart.getCurrentBlockPosOrRailBelow();
		final BlockState state = level.getBlockState(pos);
		final boolean isRail = BaseRailBlock.isRail(state);
		final RailShape shape = isRail ? state.getValue(((BaseRailBlock) state.getBlock()).getShapeProperty()) : null;

		final StringBuilder reason = new StringBuilder();
		if (minecart.horizontalCollision) reason.append("collision ");
		if (minecart.getFirstPassenger() instanceof Player) reason.append("rider ");
		if (!isRail) reason.append("offRail ");
		if (shape != null && shape.isSlope()) reason.append("slope ");
		if (state.is(Blocks.POWERED_RAIL)) reason.append("poweredRail ");
		if (reason.isEmpty()) reason.append("<none>");

		TCAConstants.LOGGER.info(String.format(
			"[TCA minecart] t=%d id=%d %s REVERSAL %s dot=%.3f speed=%.4f last=(%.2f,%.2f) new=(%.2f,%.2f) "
				+ "pos=%s rail=%s hCol=%b vCol=%b onRails=%b allowReason=[%s]",
			level.getGameTime(),
			minecart.getId(),
			minecart.getClass().getSimpleName(),
			allowed ? "ALLOWED" : "corrected",
			now.dot(last),
			speed,
			last.x, last.z,
			now.x, now.z,
			pos.toShortString(),
			shape == null ? "none" : shape.getSerializedName(),
			minecart.horizontalCollision,
			minecart.verticalCollision,
			minecart.isOnRails(),
			reason.toString().trim()
		));
	}

	@Unique
	private static boolean theCopperierAge$reversalAllowed(AbstractMinecart minecart, ServerLevel level) {
		if (minecart.horizontalCollision) return true;
		if (minecart.getFirstPassenger() instanceof Player) return true;
		final BlockPos pos = minecart.getCurrentBlockPosOrRailBelow();
		final BlockState state = level.getBlockState(pos);
		if (!BaseRailBlock.isRail(state)) return true;
		final RailShape shape = state.getValue(((BaseRailBlock) state.getBlock()).getShapeProperty());
		return shape.isSlope() || state.is(Blocks.POWERED_RAIL);
	}
}
