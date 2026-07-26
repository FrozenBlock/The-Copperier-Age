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

package net.frozenblock.thecopperierage.mixin.entity.minecart.motion;

import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin {
	@Unique
	private static final double THECOPPERIERAGE$MIN_SPEED_SQR = 1.0E-4D;
	@Unique
	private static final double THECOPPERIERAGE$REVERSAL_DOT = -0.5D;
	@Unique
	private static final double THECOPPERIERAGE$STALL_REPORT_SPEED = 0.02D;

	@Unique
	private Vec3 theCopperierAge$lastTravelDirection = Vec3.ZERO;
	@Unique
	private double theCopperierAge$lastSpeed;

	@Inject(method = "tick", at = @At("TAIL"))
	private void theCopperierAge$reportMotionEvents(CallbackInfo info) {
		if (!TCAConfig.DEBUG_MINECART_MOTION.get()) return;

		final AbstractMinecart minecart = AbstractMinecart.class.cast(this);
		if (!(minecart.level() instanceof ServerLevel level)) return;

		final Vec3 velocity = minecart.getDeltaMovement();
		final double speedSqr = (velocity.x * velocity.x) + (velocity.z * velocity.z);
		if (speedSqr < THECOPPERIERAGE$MIN_SPEED_SQR) {
			if (this.theCopperierAge$lastSpeed > THECOPPERIERAGE$STALL_REPORT_SPEED) {
				theCopperierAge$log(minecart, level, "STALLED", this.theCopperierAge$lastTravelDirection,
					Vec3.ZERO, this.theCopperierAge$lastSpeed, 0.0D);
			}
			this.theCopperierAge$lastSpeed = 0.0D;
			return;
		}

		final double speed = Math.sqrt(speedSqr);
		final Vec3 direction = new Vec3(velocity.x / speed, 0.0D, velocity.z / speed);
		final Vec3 last = this.theCopperierAge$lastTravelDirection;

		if (last.lengthSqr() > 1.0E-6D && direction.dot(last) < THECOPPERIERAGE$REVERSAL_DOT) {
			theCopperierAge$log(minecart, level, "REVERSE", last, direction, this.theCopperierAge$lastSpeed, speed);
		}

		this.theCopperierAge$lastTravelDirection = direction;
		this.theCopperierAge$lastSpeed = speed;
	}

	@Unique
	private static void theCopperierAge$log(
		AbstractMinecart minecart, ServerLevel level, String event, Vec3 last, Vec3 now, double previousSpeed, double speed
	) {
		final BlockPos pos = minecart.getCurrentBlockPosOrRailBelow();
		final BlockState state = level.getBlockState(pos);
		final RailShape shape = BaseRailBlock.isRail(state)
			? state.getValue(((BaseRailBlock) state.getBlock()).getShapeProperty())
			: null;

		TCAConstants.LOGGER.info(String.format(
			"[TCA %s] t=%d id=%d %s dot=%.3f speed %.4f -> %.4f last=(%.3f,%.3f) new=(%.3f,%.3f) "
				+ "pos=%s block=%s shape=%s hCol=%b vCol=%b onRails=%b onGround=%b passengers=%d",
			event, level.getGameTime(), minecart.getId(), minecart.getType().toString(),
			now.dot(last), previousSpeed, speed,
			last.x, last.z, now.x, now.z,
			pos.toShortString(),
			state.getBlock().builtInRegistryHolder().key().identifier().toString(),
			shape == null ? "none" : shape.getSerializedName(),
			minecart.horizontalCollision, minecart.verticalCollision,
			minecart.isOnRails(), minecart.onGround(), minecart.getPassengers().size()
		));
	}
}
