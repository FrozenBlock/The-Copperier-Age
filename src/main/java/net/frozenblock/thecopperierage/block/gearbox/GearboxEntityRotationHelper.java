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

package net.frozenblock.thecopperierage.block.gearbox;

import net.frozenblock.thecopperierage.block.GearboxBlock;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class GearboxEntityRotationHelper {

	public static float getYawDeltaFromPower(int power) {
		if (power <= 0) return 0F;
		return (power & 1) == 0 ? TCAConfig.GEARBOX_ENTITY_ROTATION.get() : -TCAConfig.GEARBOX_ENTITY_ROTATION.get();
	}

	public static boolean isStandingOnBlock(Entity entity, BlockPos pos) {
		if (entity.isPassenger()) return false;

		final BlockPos onPos = entity.getOnPos();
		if (onPos.getX() == pos.getX() && onPos.getY() == pos.getY() && onPos.getZ() == pos.getZ()) return true;

		final int y = Mth.floor(entity.getBoundingBox().minY - 0.05D);
		if (y != pos.getY()) return false;

		final int minX = Mth.floor(entity.getBoundingBox().minX + 0.0001D);
		final int maxX = Mth.floor(entity.getBoundingBox().maxX - 0.0001D);
		if (pos.getX() < minX || pos.getX() > maxX) return false;

		final int minZ = Mth.floor(entity.getBoundingBox().minZ + 0.0001D);
		final int maxZ = Mth.floor(entity.getBoundingBox().maxZ - 0.0001D);
		return pos.getZ() >= minZ && pos.getZ() <= maxZ;
	}

	public static float getGearboxYawDelta(Entity entity) {
		if (!entity.onGround() || entity.isPassenger()) return 0F;
		final Level level = entity.level();

		final BlockPos onPos = entity.getOnPos();
		final int onX = onPos.getX();
		final int onY = onPos.getY();
		final int onZ = onPos.getZ();
		final BlockState onState = level.getBlockState(onPos);
		if (onState.getBlock() instanceof GearboxBlock && onState.getValue(GearboxBlock.FACING) == Direction.UP) {
			return getYawDeltaFromPower(onState.getValue(GearboxBlock.POWER));
		}

		final int y = Mth.floor(entity.getBoundingBox().minY - 0.05D);
		final int minX = Mth.floor(entity.getBoundingBox().minX + 0.0001D);
		final int maxX = Mth.floor(entity.getBoundingBox().maxX - 0.0001D);
		final int minZ = Mth.floor(entity.getBoundingBox().minZ + 0.0001D);
		final int maxZ = Mth.floor(entity.getBoundingBox().maxZ - 0.0001D);
		if (minX == maxX && minZ == maxZ && onX == minX && onY == y && onZ == minZ) return 0F;

		int selectedPower = 0;
		final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				final BlockState state;
				if (x == onX && y == onY && z == onZ) {
					state = onState;
				} else {
					mutablePos.set(x, y, z);
					state = level.getBlockState(mutablePos);
				}
				if (!(state.getBlock() instanceof GearboxBlock)) continue;
				if (state.getValue(GearboxBlock.FACING) != Direction.UP) continue;

				final int power = state.getValue(GearboxBlock.POWER);
				if (power > selectedPower) selectedPower = power;
				if (selectedPower >= 15) return getYawDeltaFromPower(selectedPower);
			}
		}

		return getYawDeltaFromPower(selectedPower);
	}

	public static void applyRotation(Entity entity, float yawDelta, boolean invertVisualRot, boolean skipRotation) {
		if (yawDelta == 0F || !(entity instanceof GearboxRotationSessionInterface rotationSession) || !entity.level().tickRateManager().runsNormally()) return;

		final BlockPos gearboxPos = rotationSession.theCopperierAge$getGearboxPosition();
		final Vec3 gearboxCenter = gearboxPos.above().getBottomCenter();

		final Vec3 relativePos = entity.position().subtract(gearboxCenter);
		final Vec3 newRelativePos = relativePos.yRot(-yawDelta * Mth.DEG_TO_RAD);
		final Vec3 difference = newRelativePos.subtract(relativePos);
		final boolean wasOnGround = entity.onGround();
		entity.move(MoverType.SHULKER_BOX, difference);
		entity.setOnGround(wasOnGround);

		if (skipRotation) return;
		yawDelta *= invertVisualRot ? -1F : 1F;
		final float oldYaw = entity.getYRot();
		entity.yRotO = oldYaw;
		final float newYaw = Mth.wrapDegrees(oldYaw + yawDelta);
		entity.setYRot(newYaw);

		if (!(entity instanceof LivingEntity livingEntity) || livingEntity instanceof ArmorStand) return;
		final float oldBodyYaw = livingEntity.yBodyRot;
		livingEntity.yBodyRotO = oldBodyYaw;
		livingEntity.setYBodyRot(oldBodyYaw + yawDelta);

		final float oldHeadYaw = livingEntity.getYHeadRot();
		livingEntity.yHeadRotO = oldHeadYaw;
		livingEntity.setYHeadRot(oldHeadYaw + yawDelta);
	}

	public static void applyLocalRotation(Entity entity, float yawDelta) {
		if (yawDelta == 0F) return;

		entity.yRotO += yawDelta;
		entity.setYRot(entity.getYRot() + yawDelta);

		if (!(entity instanceof LivingEntity livingEntity) || livingEntity instanceof ArmorStand) return;
		livingEntity.yBodyRotO += yawDelta;
		livingEntity.setYBodyRot(livingEntity.yBodyRot + yawDelta);

		livingEntity.yHeadRotO += yawDelta;
		livingEntity.setYHeadRot(livingEntity.yHeadRot + yawDelta);
	}
}
