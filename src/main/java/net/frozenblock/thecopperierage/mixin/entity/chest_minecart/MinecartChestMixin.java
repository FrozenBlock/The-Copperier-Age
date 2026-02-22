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

package net.frozenblock.thecopperierage.mixin.entity.chest_minecart;

import net.frozenblock.thecopperierage.entity.impl.ChestLidAnimating;
import net.frozenblock.thecopperierage.entity.impl.ChestVehicleAnimationConstants;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestLidController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecartChest.class)
public abstract class MinecartChestMixin extends AbstractMinecartContainer implements ChestLidAnimating {
	@Unique
	private final ChestLidController theCopperierAge$lidController = new ChestLidController();
	@Unique
	private int theCopperierAge$openers;
	@Unique
	private long theCopperierAge$lastLidTickGameTime = Long.MIN_VALUE;

	protected MinecartChestMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "interact", at = @At("TAIL"))
	private void theCopperierAge$interact(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
		if (!this.level().isClientSide() && info.getReturnValue().consumesAction()) {
			if (this.theCopperierAge$openers++ == 0) {
				this.theCopperierAge$setLidShouldBeOpen(true);
				this.level().broadcastEntityEvent(this, ChestVehicleAnimationConstants.OPEN_EVENT);
				this.theCopperierAge$playChestSound(true);
			}
		}
	}

	@Inject(method = "stopOpen", at = @At("TAIL"))
	private void theCopperierAge$stopOpen(ContainerUser user, CallbackInfo info) {
		if (!this.level().isClientSide() && this.theCopperierAge$openers > 0) {
			this.theCopperierAge$openers--;
			if (this.theCopperierAge$openers == 0) {
				this.theCopperierAge$setLidShouldBeOpen(false);
				this.level().broadcastEntityEvent(this, ChestVehicleAnimationConstants.CLOSE_EVENT);
				this.theCopperierAge$playChestSound(false);
			}
		}
	}

	@Override
	@Unique
	public void theCopperierAge$tickLidController() {
		final long gameTime = this.level().getGameTime();
		if (gameTime != this.theCopperierAge$lastLidTickGameTime) {
			this.theCopperierAge$lastLidTickGameTime = gameTime;
			this.theCopperierAge$lidController.tickLid();
		}
	}

	@Override
	@Unique
	public float theCopperierAge$getLidOpenness(float partialTicks) {
		return this.theCopperierAge$lidController.getOpenness(partialTicks);
	}

	@Override
	@Unique
	public void theCopperierAge$setLidShouldBeOpen(boolean open) {
		this.theCopperierAge$lidController.shouldBeOpen(open);
	}

	@Unique
	private void theCopperierAge$playChestSound(boolean opening) {
		if (this.level().isClientSide()) return;

		this.level().playSound(
			null,
			this.getX(),
			this.getY(),
			this.getZ(),
			opening ? SoundEvents.CHEST_OPEN : SoundEvents.CHEST_CLOSE,
			SoundSource.BLOCKS,
			ChestVehicleAnimationConstants.OPEN_CLOSE_SOUND_VOLUME,
			this.level().random.nextFloat() * ChestVehicleAnimationConstants.OPEN_CLOSE_SOUND_PITCH_VARIANCE + ChestVehicleAnimationConstants.OPEN_CLOSE_SOUND_PITCH_BASE
		);
	}
}
