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

package net.frozenblock.thecopperierage.mixin.block.gearbox;

import net.frozenblock.thecopperierage.block.GearboxBlock;
import net.frozenblock.thecopperierage.block.gearbox.GearboxEntityRotationHelper;
import net.frozenblock.thecopperierage.block.gearbox.GearboxRotationSessionInterface;
import net.frozenblock.thecopperierage.tag.TCAEntityTypeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements GearboxRotationSessionInterface {

	@Shadow
	public abstract boolean isClientAuthoritative();

	@Unique
	private static final int theCopperierAge$INACTIVE_PROBE_INTERVAL = 14;
	@Unique
	private static final int theCopperierAge$ACTIVE_FULL_SCAN_INTERVAL = 3;
	@Unique
	private boolean theCopperierAge$activeGearboxRotation = false;
	@Unique
	private int theCopperierAge$nextGearboxProbeTick = 0;
	@Unique
	private int theCopperierAge$nextActiveFullScanTick = 0;
	@Unique
	private boolean theCopperierAge$hasCachedSupportPos = false;
	@Unique
	private final BlockPos.MutableBlockPos theCopperierAge$cachedSupportPos = new BlockPos.MutableBlockPos();
	@Unique
	private float theCopperierAge$cachedGearboxYawDelta = 0F;

	@Unique
	@Override
	public void theCopperierAge$activateGearboxRotationSession(int currentTick, BlockPos supportPos) {
		this.theCopperierAge$activeGearboxRotation = true;
		this.theCopperierAge$nextGearboxProbeTick = currentTick;
		this.theCopperierAge$nextActiveFullScanTick = currentTick;
		this.theCopperierAge$hasCachedSupportPos = true;
		this.theCopperierAge$cachedSupportPos.set(supportPos);
	}

	@Unique
	@Override
	public float theCopperierAge$getGearboxYawDelta() {
		return this.theCopperierAge$cachedGearboxYawDelta;
	}

	@Unique
	@Override
	public BlockPos theCopperierAge$getGearboxPosition() {
		return this.theCopperierAge$cachedSupportPos.immutable();
	}

	@Unique
	@Override
	public boolean theCopperierAge$automaticallyRotatesWithGearbox() {
		return !this.isClientAuthoritative();
	}

	@Unique
	@Override
	public boolean theCopperierAge$rotating() {
		final Entity entity = Entity.class.cast(this);
		if (!this.theCopperierAge$activeGearboxRotation || !entity.onGround() || entity.is(TCAEntityTypeTags.GEARBOX_CANNOT_ROTATE)) return false;

		final BlockState onState = entity.level().getBlockState(entity.getOnPos());
		return onState.getBlock() instanceof GearboxBlock
			&& onState.getValue(GearboxBlock.FACING) == Direction.UP
			&& onState.getValue(GearboxBlock.POWER) > 0;
	}

	@Unique
	private float theCopperierAge$getCachedSupportYawDelta(Entity entity) {
		if (!this.theCopperierAge$hasCachedSupportPos) return 0F;
		if (!GearboxEntityRotationHelper.isStandingOnBlock(entity, this.theCopperierAge$cachedSupportPos)) return 0F;

		final BlockState state = entity.level().getBlockState(this.theCopperierAge$cachedSupportPos);
		if (!(state.getBlock() instanceof GearboxBlock)) return 0F;
		if (state.getValue(GearboxBlock.FACING) != Direction.UP) return 0F;
		return GearboxEntityRotationHelper.getYawDeltaFromPower(state.getValue(GearboxBlock.POWER));
	}

	@Unique
	private void theCopperierAge$clearCachedSupportPos() {
		this.theCopperierAge$hasCachedSupportPos = false;
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void theCopperierAge$rotateFromUpFacingGearbox(CallbackInfo info) {
		this.theCopperierAge$tickRotationSession(false);
	}

	@Unique
	@Override
	public void theCopperierAge$tickRotationSession(boolean invertVisualRot) {
		final Entity entity = Entity.class.cast(this);
		if (!this.theCopperierAge$activeGearboxRotation && entity.tickCount < this.theCopperierAge$nextGearboxProbeTick) return;
		if (entity.is(TCAEntityTypeTags.GEARBOX_CANNOT_ROTATE)) return;

		float yawDelta = 0F;
		boolean ranFullScan = false;
		if (this.theCopperierAge$activeGearboxRotation && entity.onGround() && !entity.isPassenger()) {
			yawDelta = this.theCopperierAge$getCachedSupportYawDelta(entity);
		}

		if (yawDelta == 0F) {
			if (!this.theCopperierAge$activeGearboxRotation || entity.tickCount >= this.theCopperierAge$nextActiveFullScanTick) {
				yawDelta = GearboxEntityRotationHelper.getGearboxYawDelta(entity);
				ranFullScan = true;
				this.theCopperierAge$nextActiveFullScanTick = entity.tickCount + theCopperierAge$ACTIVE_FULL_SCAN_INTERVAL;
			}
		}

		this.theCopperierAge$cachedGearboxYawDelta = yawDelta;
		if (yawDelta == 0F) {
			if (this.theCopperierAge$activeGearboxRotation && !ranFullScan) return;
			this.theCopperierAge$activeGearboxRotation = false;
			this.theCopperierAge$nextGearboxProbeTick = entity.tickCount + theCopperierAge$INACTIVE_PROBE_INTERVAL;
			this.theCopperierAge$clearCachedSupportPos();
			return;
		}

		this.theCopperierAge$activeGearboxRotation = true;
		final BlockPos onPos = entity.getOnPos();
		final BlockState onState = entity.level().getBlockState(onPos);
		if (onState.getBlock() instanceof GearboxBlock && onState.getValue(GearboxBlock.FACING) == Direction.UP && onState.getValue(GearboxBlock.POWER) > 0) {
			this.theCopperierAge$hasCachedSupportPos = true;
			this.theCopperierAge$cachedSupportPos.set(onPos);
		}

		GearboxEntityRotationHelper.applyRotation(entity, yawDelta, invertVisualRot, !this.theCopperierAge$automaticallyRotatesWithGearbox());
	}
}
