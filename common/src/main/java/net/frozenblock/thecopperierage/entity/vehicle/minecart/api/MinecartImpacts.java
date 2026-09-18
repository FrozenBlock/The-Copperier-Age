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

package net.frozenblock.thecopperierage.entity.vehicle.minecart.api;

import java.util.List;
import net.frozenblock.thecopperierage.block.CopperRail;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.entity.vehicle.minecart.coupling.CouplingToEntityInterface;
import net.frozenblock.thecopperierage.registry.TCADamageTypes;
import net.frozenblock.thecopperierage.registry.TCASounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class MinecartImpacts {
	public static final double IMPACT_SPEED_THRESHOLD = 0.15D;
	private static final double DAMAGE_PER_TRANSFERRED_SPEED = 20D;
	private static final double MAX_DAMAGE = 10D;
	private static final double MAX_KNOCKBACK_UP = 0.3D;
	private static final double KNOCKBACK_UP_PER_SPEED = 0.5D;
	private static final double MIN_ENTITY_MASS = 0.05D;
	private static final double MAX_ENTITY_MASS = 4D;
	private static final double CART_MASS = 1D;
	private static final double MIN_FRICTION_FACTOR = 0.25D;
	private static final double REFERENCE_RETENTION = 0.96D;
	private static final double MIN_SOUND_VOLUME = 0.35D;
	private static final double FULL_SOUND_VOLUME_SPEED = 0.4D;
	private static final double OVERLAP_MARGIN = 0.02D;
	private static final double STACKED_VERTICAL_OVERLAP = 0.2D;
	private static final double MIN_SEPARATION_SQR = 1.0E-4D;

	public static boolean enabled() {
		return TCAConfig.MINECART_COLLISIONS.get();
	}

	public static boolean isSolverHandled(AbstractMinecart cart, AbstractMinecart other) {
		return enabled() && cart.isOnRails() && other.isOnRails() && !isStacked(cart, other);
	}

	public static boolean isStacked(Entity first, Entity second) {
		final AABB firstBox = first.getBoundingBox();
		final AABB secondBox = second.getBoundingBox();
		return Math.min(firstBox.maxY, secondBox.maxY) - Math.max(firstBox.minY, secondBox.minY) < STACKED_VERTICAL_OVERLAP;
	}

	public static double closingSpeed(AbstractMinecart cart, Entity entity) {
		final Vec3 toEntity = entity.position().subtract(cart.position()).horizontal();
		if (toEntity.lengthSqr() < MIN_SEPARATION_SQR) return 0D;

		final Vec3 relativeVelocity = cart.getDeltaMovement().subtract(entity.getDeltaMovement()).horizontal();
		return relativeVelocity.dot(toEntity.normalize());
	}

	public static boolean shouldPassThrough(AbstractMinecart cart, Entity entity) {
		if (!enabled() || !(entity instanceof LivingEntity) || !entity.isPushable()) return false;
		if (cart.hasPassenger(entity) || entity.isPassenger()) return false;

		if (cart.getDeltaMovement().horizontalDistanceSqr() < IMPACT_SPEED_THRESHOLD * IMPACT_SPEED_THRESHOLD) return false;
		if (cart.getBoundingBox().intersects(entity.getBoundingBox())) return true;
		return closingSpeed(cart, entity) >= IMPACT_SPEED_THRESHOLD;
	}

	public static double runOverEntities(ServerLevel level, AbstractMinecart cart) {
		if (!enabled() || cart.noPhysics) return 0D;
		if (cart.getDeltaMovement().horizontalDistanceSqr() < IMPACT_SPEED_THRESHOLD * IMPACT_SPEED_THRESHOLD) return 0D;

		final List<LivingEntity> hits = level.getEntitiesOfClass(
			LivingEntity.class,
			cart.getBoundingBox().inflate(OVERLAP_MARGIN, 0D, OVERLAP_MARGIN),
			living -> living.isAlive() && living.isPushable() && !living.isSpectator() && !cart.hasPassenger(living) && !living.isPassenger()
		);

		double strongest = 0D;
		for (LivingEntity living : hits) strongest = Math.max(strongest, collide(level, cart, living));
		return strongest;
	}

	public static double collide(ServerLevel level, AbstractMinecart cart, LivingEntity entity) {
		final double closing = closingSpeed(cart, entity);
		if (closing < IMPACT_SPEED_THRESHOLD) return 0D;

		final Vec3 normal = entity.position().subtract(cart.position()).horizontal().normalize();
		final double cartMass = massOf(cart);
		final double entityMass = massOf(entity);
		final double cartShare = cartMass / (cartMass + entityMass);
		final double entityShare = 1D - cartShare;
		final double transferredSpeed = closing * cartShare;

		final double damage = Math.min(MAX_DAMAGE, DAMAGE_PER_TRANSFERRED_SPEED * (closing - IMPACT_SPEED_THRESHOLD) * cartShare * frictionFactor(cart));
		if (damage < 0D) return 0D;

		final DamageSource damageSource = level.damageSources().source(
			// TODO: lewd detection
			TCADamageTypes.MINECART_IMPACT,
			cart.getFirstPassenger() != null
				? cart.getFirstPassenger()
				: cart
		);
		if (!entity.hurtServer(level, damageSource, (float) damage)) return 0D;

		entity.knockback(transferredSpeed, normal.x, normal.z, damageSource, (float) damage);

		// TODO: is there another way to do this while respecting overriden implementations of .knockback? (Creaking, Dragon, Sulfur Cube)
		//final double upwards = Math.min(MAX_KNOCKBACK_UP, transferredSpeed * KNOCKBACK_UP_PER_SPEED);
		//entity.push(normal.x * transferredSpeed, upwards, normal.z * transferredSpeed);

		final Vec3 cartVelocity = cart.getDeltaMovement();
		final double cartLoss = closing * entityShare;
		cart.setDeltaMovement(cartVelocity.x - normal.x * cartLoss, cartVelocity.y, cartVelocity.z - normal.z * cartLoss);
		return closing;
	}

	public static double massOf(Entity entity) {
		if (entity instanceof AbstractMinecart cart) {
			final int trainSize = cart instanceof CouplingToEntityInterface access ? Math.max(1, access.theCopperierAge$getTrainSize()) : 1;
			double mass = CART_MASS * trainSize;
			for (Entity passenger : cart.getPassengers()) mass += massOf(passenger);
			return mass;
		}

		final AABB box = entity.getBoundingBox();
		return Mth.clamp(box.getXsize() * box.getYsize() * box.getZsize(), MIN_ENTITY_MASS, MAX_ENTITY_MASS);
	}

	public static double frictionFactor(AbstractMinecart cart) {
		double retention = cart.getBehavior().getSlowdownFactor();
		final CopperRail rail = CopperRail.railUnder(cart, cart.level());
		if (rail != null) {
			final WeatheringCopper.WeatherState weatherState = rail.getWeatherState();
			retention = CopperRail.decelerationFactor(weatherState, retention);
		}
		return Mth.clamp(retention / REFERENCE_RETENTION, MIN_FRICTION_FACTOR, 1D);
	}

	public static void playImpactSound(ServerLevel level, AbstractMinecart cart, Vec3 pos, double speed) {
		if (cart.isSilent() || !enabled()) return;

		final float volume = (float) Mth.clamp(speed / FULL_SOUND_VOLUME_SPEED, MIN_SOUND_VOLUME, 1D);
		level.playSound(
			null,
			pos.x, pos.y, pos.z,
			TCASounds.ENTITY_MINECART_HIT.get(),
			cart.getSoundSource(),
			volume,
			(cart.getRandom().nextFloat() * 0.2F) + 0.9F
		);
	}

	private MinecartImpacts() {}
}
