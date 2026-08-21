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

package net.frozenblock.thecopperierage.mixin.entity.copper_golem;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import java.util.ArrayList;
import java.util.List;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.entity.impl.CopperGolemPressButtonInterface;
import net.frozenblock.thecopperierage.entity.impl.TCACopperGolemStates;
import net.frozenblock.thecopperierage.registry.TCAMemoryModuleTypes;
import net.frozenblock.thecopperierage.registry.TCASensorTypes;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.entity.animal.golem.CopperGolemState;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CopperGolem.class)
public abstract class CopperGolemMixin extends AbstractGolem implements CopperGolemPressButtonInterface {

	protected CopperGolemMixin(EntityType<? extends AbstractGolem> type, Level level) {
		super(type, level);
	}

	@Shadow
	public abstract CopperGolemState getState();

	@Shadow
	@Final
	private AnimationState idleAnimationState;

	@Shadow
	private int idleAnimationStartTick;

	@Shadow
	@Final
	private AnimationState interactionGetItemAnimationState;

	@Shadow
	@Final
	private AnimationState interactionDropNoItemAnimationState;

	@Shadow
	@Final
	private AnimationState interactionDropItemAnimationState;

	@Shadow
	@Final
	private AnimationState interactionGetNoItemAnimationState;

	@Unique
	private final AnimationState theCopperierAge$pressingButtonAnimationState = new AnimationState();
	@Unique
	private boolean theCopperierAge$previouslyHoldingItem;

	@ModifyExpressionValue(
		method = "<clinit>",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;"
		)
	)
	private static List theCopperierAge$addCopperGolemSpecificSensor(List original) {
		final ArrayList newSensors = new ArrayList<>();
		newSensors.addAll(original);
		newSensors.add(TCASensorTypes.COPPER_GOLEM_SPECIFIC_SENSOR);
		return List.copyOf(newSensors);
	}

	@Inject(method = "setupAnimationStates", at = @At("HEAD"))
	private void theCopperierAge$setupPressingButtonAnimationState(CallbackInfo info) {
		if (this.getState() != TCACopperGolemStates.PRESSING_BUTTON) {
			this.theCopperierAge$pressingButtonAnimationState.stop();
			return;
		}

		this.idleAnimationState.stop();
		this.idleAnimationStartTick = 0;
		this.interactionGetItemAnimationState.stop();
		this.interactionDropNoItemAnimationState.stop();
		this.interactionDropItemAnimationState.stop();
		this.interactionGetNoItemAnimationState.stop();
		this.theCopperierAge$pressingButtonAnimationState.startIfStopped(this.tickCount);
	}

	@Unique
	@Override
	public AnimationState theCopperierAge$getPressingButtonAnimationState() {
		return this.theCopperierAge$pressingButtonAnimationState;
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void theCopperierAge$triggerNearbyButtonSearchAfterDeposit(CallbackInfo info) {
		final boolean currentlyHoldingItem = !this.getMainHandItem().isEmpty() || !this.getOffhandItem().isEmpty();
		if (TCAConfig.COPPER_GOLEMS_PRESS_BUTTONS.get() && !this.level().isClientSide() && this.theCopperierAge$previouslyHoldingItem && !currentlyHoldingItem) {
			this.theCopperierAge$forceNearbyButtonSearch();
		}
		this.theCopperierAge$previouslyHoldingItem = currentlyHoldingItem;
	}

	@Unique
	private void theCopperierAge$forceNearbyButtonSearch() {
		this.getBrain().setMemory(TCAMemoryModuleTypes.NEARBY_BUTTON_SEARCH_TICKS.get(), 120);
		this.getBrain().eraseMemory(TCAMemoryModuleTypes.BUTTON_PRESS_COOLDOWN_TICKS.get());
		this.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
		this.getNavigation().stop();
	}
}
