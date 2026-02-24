package net.frozenblock.thecopperierage.entity.impl;

import net.frozenblock.thecopperierage.mod_compat.TCAModIntegrations;
import net.frozenblock.thecopperierage.mod_compat.wilderwild.AbstractWWIntegration;
import net.frozenblock.thecopperierage.particle.options.ChestVehicleBubbleSeedParticleOptions;
import net.frozenblock.thecopperierage.registry.TCAAttachments;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public interface ChestVehicleBubbleInterface {

	default boolean theCopperierAge$canBubble() {
		if (!(this instanceof Entity entity)) return false;
		return entity.getAttachedOrCreate(TCAAttachments.CHEST_VEHICLE_CAN_BUBBLE);
	}

	default void theCopperierAge$setCanBubble(boolean canBubble) {
		if (!(this instanceof Entity entity)) return;
		entity.setAttached(TCAAttachments.CHEST_VEHICLE_CAN_BUBBLE, canBubble);
	}

	default void theCopperierAge$bubble(AbstractWWIntegration wwIntegration) {
		if (!(this instanceof Entity entity) || !(entity.level() instanceof ServerLevel serverLevel)) return;
		if (!this.theCopperierAge$canBubble() || !entity.isUnderWater()) return;
		if (!wwIntegration.chestBubbling()) {
			this.theCopperierAge$setCanBubble(false);
			return;
		}

		serverLevel.sendParticles(
			new ChestVehicleBubbleSeedParticleOptions(entity.getId()),
			entity.getX(), entity.getY(), entity.getZ(),
			1,
			0D, 0D, 0D,
			0D
		);
		this.theCopperierAge$setCanBubble(false);
	}

	default void theCopperierAge$bubbleBurst() {
		if (!(this instanceof Entity entity) || !(entity.level() instanceof ServerLevel serverLevel)) return;
		if (!this.theCopperierAge$canBubble() || !entity.isUnderWater()) return;
		if (!TCAModIntegrations.WILDER_WILD_INTEGRATION.getIntegration().chestBubbling()) return;

		final Vec3 particlePos = this.theCopperierAge$bubbleEmitPosition();
		serverLevel.sendParticles(
			ParticleTypes.BUBBLE,
			particlePos.x(), particlePos.y(), particlePos.z(),
			serverLevel.getRandom().nextInt(18, 25),
			0.1640625D, 0D, 0.1640625D,
			0.25D
		);
	}

	default void theCopperierAge$tickBubble() {
		if (!(this instanceof Entity entity) || !(entity.level() instanceof ServerLevel) || entity.firstTick) return;
		final boolean canBubble = this.theCopperierAge$canBubble();
		if (!canBubble && !entity.isUnderWater()) this.theCopperierAge$setCanBubble(true);
	}

	Vec3 theCopperierAge$bubbleEmitPosition();
}
