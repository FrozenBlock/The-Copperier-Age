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

package net.frozenblock.thecopperierage.mixin.entity.minecart.camera;

import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.entity.impl.MinecartHeadingInterface;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Unique
	private static final float THECOPPERIERAGE$BODY_TURN_RATE = 0.3F;
	@Unique
	private static final float THECOPPERIERAGE$MAX_HEAD_ROTATION = 50F;
	@Unique
	private static final float THECOPPERIERAGE$FACING_BACKWARDS_DEGREES = 95F;

	@Inject(method = "positionRider(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity$MoveFunction;)V", at = @At("TAIL"))
	private void theCopperierAge$turnRiderWithMinecart(Entity passenger, Entity.MoveFunction moveFunction, CallbackInfo info) {
		if (!(Entity.class.cast(this) instanceof AbstractMinecart cart) || !(cart instanceof MinecartHeadingInterface heading)) return;
		if (!heading.theCopperierAge$hasRiderHeading() || !TCAConfig.MINECART_CAMERA_FOLLOWS_MOTION.get()) return;
		if (!(passenger instanceof LivingEntity living)) return;

		final boolean clientSide = cart.level().isClientSide();
		final boolean player = living instanceof Player;
		if (player && !clientSide) return;

		final float turn = heading.theCopperierAge$getRiderTurn();
		final boolean authoritative = player ? ((Player) living).isLocalPlayer() : !clientSide;
		if (authoritative && turn != 0F) {
			living.setYRot(living.getYRot() + turn);
			living.setYHeadRot(living.getYHeadRot() + turn);
		}

		theCopperierAge$turnBodyInHeadingFrame(living, heading.theCopperierAge$getRiderHeading(), turn);
	}

	@Unique
	private static void theCopperierAge$turnBodyInHeadingFrame(LivingEntity entity, float heading, float turn) {
		final float previousOffset = Mth.wrapDegrees(entity.yBodyRotO - (heading - turn));
		final float headOffset = Mth.wrapDegrees(entity.getYRot() - heading);

		float targetOffset = Math.abs(headOffset) > THECOPPERIERAGE$FACING_BACKWARDS_DEGREES ? 180F : 0F;
		if (entity.isSwinging()) targetOffset = headOffset;

		float offset = previousOffset + Mth.wrapDegrees(targetOffset - previousOffset) * THECOPPERIERAGE$BODY_TURN_RATE;
		final float headDifference = Mth.wrapDegrees(headOffset - offset);
		if (Math.abs(headDifference) > THECOPPERIERAGE$MAX_HEAD_ROTATION) {
			offset += headDifference - Mth.sign(headDifference) * THECOPPERIERAGE$MAX_HEAD_ROTATION;
		}

		entity.setYBodyRot(Mth.wrapDegrees(heading + offset));
	}
}
