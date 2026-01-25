package net.frozenblock.thecopperierage.mixin.entity.copper_golem;

import net.frozenblock.thecopperierage.entity.impl.CopperGolemPressButtonInterface;
import net.frozenblock.thecopperierage.entity.impl.TCACopperGolemStates;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.coppergolem.CopperGolem;
import net.minecraft.world.entity.animal.coppergolem.CopperGolemState;
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
}
