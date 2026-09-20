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

package net.frozenblock.thecopperierage.entity.vehicle.minecart.coupling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.frozenblock.lib.event.api.events.TickEvents;
import net.frozenblock.thecopperierage.block.RelayerRailBlock;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.entity.vehicle.minecart.api.MinecartImpacts;
import net.frozenblock.thecopperierage.entity.vehicle.minecart.api.MinecartTrackHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class MinecartCouplingPhysics {
	public static final double CONTACT_DISTANCE = 0.99D;
	private static final int SOLVER_ITERATIONS = 6;
	private static final double CONTACT_RESTITUTION = 0D;
	private static final double RESTITUTION_MIN_APPROACH = 0.15D;
	private static final double VELOCITY_TOLERANCE = 1.0E-5D;
	private static final double COUPLING_POSITION_THRESHOLD = 0.3D;
	private static final double CONTACT_POSITION_THRESHOLD = 0.06D;
	private static final double POSITION_CORRECTION_RATE = 0.5D;
	private static final double MAX_POSITION_CORRECTION_PER_TICK = 0.06D;
	private static final double CONTACT_SEARCH_RADIUS = 0.6D;
	private static final double CONTACT_ENGAGED_SLACK = 0.6D;
	private static final double BLOCK_ALIGNMENT = 0.5D;
	private static final double CONTACT_BAND = 0.05D;
	private static final double MAX_SLOPE_RISE = 1.5D;
	private static final double RIDDEN_MOVE_SCALE = 0.75D;
	private static final double MIN_MOVING_SPEED_SQR = 1.0E-8D;
	private static final double JAM_SPEED_SQR = 1.0E-6D;
	private static final double JAM_DISPLACEMENT_SQR = 1.0E-6D;
	private static final double ESCAPE_PROBE = 0.1D;
	private static final double MIN_DISPLACEMENT_SQR = 1.0E-6D;
	private static final float MIN_LERP_STEP_WEIGHT = 1.0E-3F;
	private static final double EPSILON = 1.0E-7D;

	private static final Map<ServerLevel, List<AbstractMinecart>> QUEUED_CARTS = new WeakHashMap<>();

	public static void init() {
		TickEvents.END_LEVEL_TICK.register(MinecartCouplingPhysics::solve);
	}

	static void queue(ServerLevel level, AbstractMinecart cart) {
		QUEUED_CARTS.computeIfAbsent(level, unused -> new ArrayList<>()).add(cart);
	}

	private static void solve(ServerLevel level) {
		final List<AbstractMinecart> queued = QUEUED_CARTS.remove(level);
		if (queued == null || queued.isEmpty()) return;

		final boolean experimental = AbstractMinecart.useExperimentalMovement(level);
		final boolean collisions = TCAConfig.MINECART_COLLISIONS.get();
		final Set<AbstractMinecart> queuedSet = Collections.newSetFromMap(new IdentityHashMap<>());
		queuedSet.addAll(queued);
		final Map<AbstractMinecart, Body> bodies = new IdentityHashMap<>();
		final List<Link> links = new ArrayList<>();

		for (AbstractMinecart cart : queued) {
			if (!cart.isAlive()) continue;

			final AbstractMinecart partner = MinecartCouplingUtil.getCoupledToCart(cart);
			if (partner != null && partner.isAlive() && partner.level() == level) {
				final Body first = bodies.computeIfAbsent(cart, unused -> new Body(level, cart, experimental));
				final Body second = bodies.computeIfAbsent(partner, unused -> new Body(level, partner, experimental));
				links.add(Link.coupling(
					level,
					first,
					second,
					MinecartCouplingUtil.getMinCouplingLength(cart, partner),
					MinecartCouplingUtil.getMaxCouplingLength(cart, partner)
				));
				Body.union(first, second);
			}

			if (!collisions || !cart.isOnRails()) continue;
			for (AbstractMinecart other : level.getEntitiesOfClass(AbstractMinecart.class, cart.getBoundingBox().inflate(CONTACT_SEARCH_RADIUS), other -> other != cart && other.isAlive())) {
				if (other.getId() < cart.getId() && queuedSet.contains(other)) continue;
				if (MinecartCouplingUtil.areCoupledTogether(cart, other) || !MinecartImpacts.isSolverHandled(cart, other)) continue;

				final Body first = bodies.computeIfAbsent(cart, unused -> new Body(level, cart, experimental));
				final Body second = bodies.computeIfAbsent(other, unused -> new Body(level, other, experimental));
				links.add(Link.contact(level, first, second, CONTACT_DISTANCE + MinecartCouplingUtil.getCouplingPadding(cart, other)));
			}
		}
		if (links.isEmpty()) return;

		for (Link link : links) link.resolveFreeTangents();
		for (Body body : bodies.values()) {
			if (body.cart instanceof CouplingToEntityInterface access) access.theCopperierAge$setTrainSize(body.root().size);
		}

		for (int iteration = 0; iteration < SOLVER_ITERATIONS; iteration++) {
			for (Link link : links) {
				link.correctPositions();
				link.correctVelocities();
			}
		}

		for (Body body : bodies.values()) body.apply();
		if (collisions) {
			propagateBlocked(links, bodies.values(), level.getGameTime());
			for (Link link : links) link.tryPlayBufferImpact();
		}
	}

	private static final class Link {
		private final ServerLevel level;
		private final Body first;
		private final Body second;
		private final double minLength;
		private final double maxLength;
		private final double restitution;
		private final double positionThreshold;
		private double bufferImpact;

		private Link(ServerLevel level, Body first, Body second, double minLength, double maxLength, double restitution, double positionThreshold) {
			this.level = level;
			this.first = first;
			this.second = second;
			this.minLength = minLength;
			this.maxLength = maxLength;
			this.restitution = restitution;
			this.positionThreshold = positionThreshold;
		}

		private static Link coupling(ServerLevel level, Body first, Body second, double minLength, double maxLength) {
			return new Link(level, first, second, minLength, maxLength, 0D, COUPLING_POSITION_THRESHOLD);
		}

		private static Link contact(ServerLevel level, Body first, Body second, double minLength) {
			return new Link(level, first, second, minLength, Double.POSITIVE_INFINITY, CONTACT_RESTITUTION, CONTACT_POSITION_THRESHOLD);
		}

		private void resolveFreeTangents() {
			final Vec3 direction = this.second.position.subtract(this.first.position).horizontal();
			this.first.resolveFreeTangent(direction);
			this.second.resolveFreeTangent(direction.scale(-1D));
		}

		private void correctPositions() {
			final Vec3 delta = this.second.position.subtract(this.first.position);
			final double distance = delta.length();
			if (distance < EPSILON) return;

			final double violation;
			if (distance > this.maxLength + this.positionThreshold) {
				violation = distance - this.maxLength;
			} else if (distance < this.minLength - this.positionThreshold) {
				violation = distance - this.minLength;
			} else {
				return;
			}

			final Vec3 normal = delta.scale(1D / distance);
			final double firstGradient = normal.dot(this.first.slopeTangent);
			final double secondGradient = normal.dot(this.second.slopeTangent);
			final double denominator = this.first.inverseMass * firstGradient * firstGradient
				+ this.second.inverseMass * secondGradient * secondGradient;
			if (denominator < EPSILON) return;

			final double lambda = POSITION_CORRECTION_RATE * violation / denominator;
			this.first.shift(lambda * this.first.inverseMass * firstGradient);
			this.second.shift(-lambda * this.second.inverseMass * secondGradient);
		}

		private void correctVelocities() {
			final double currentDistance = this.second.position.distanceTo(this.first.position);
			final Vec3 delta = this.second.predictedPosition().subtract(this.first.predictedPosition());
			final double predictedDistance = delta.length();
			if (predictedDistance < EPSILON) return;

			double error;
			if (predictedDistance > this.maxLength) {
				final double target = Math.max(this.maxLength, currentDistance);
				error = predictedDistance - target;
				if (error <= VELOCITY_TOLERANCE) return;
			} else if (predictedDistance < this.minLength) {
				final double approach = currentDistance - predictedDistance;
				double target = Math.min(this.minLength, currentDistance);
				final boolean touching = currentDistance <= this.minLength + CONTACT_BAND;
				if (this.restitution > 0D && touching && approach > RESTITUTION_MIN_APPROACH) {
					target = Math.max(target, currentDistance + this.restitution * approach);
				}
				error = predictedDistance - target;
				if (error >= -VELOCITY_TOLERANCE) return;
				this.bufferImpact = Math.max(this.bufferImpact, approach);
			} else {
				return;
			}

			final Vec3 normal = delta.scale(1D / predictedDistance);
			final double firstGradient = this.first.moveScale * normal.dot(this.first.slopeTangent);
			final double secondGradient = this.second.moveScale * normal.dot(this.second.slopeTangent);
			final double denominator = this.first.inverseMass * firstGradient * firstGradient
				+ this.second.inverseMass * secondGradient * secondGradient;
			if (denominator < EPSILON) return;

			final double lambda = error / denominator;
			this.first.accelerate(lambda * this.first.inverseMass * firstGradient);
			this.second.accelerate(-lambda * this.second.inverseMass * secondGradient);
		}

		private boolean propagateBlocked() {
			if (Double.isFinite(this.maxLength)) return false;

			final Vec3 delta = this.second.position.subtract(this.first.position).horizontal();
			final double distance = delta.length();
			if (distance < EPSILON || distance > this.minLength + CONTACT_ENGAGED_SLACK) return false;

			final Vec3 normal = delta.scale(1D / distance);
			return this.first.blockToward(normal, this.second) | this.second.blockToward(normal.scale(-1D), this.first);
		}

		private void tryPlayBufferImpact() {
			if (this.bufferImpact < MinecartImpacts.IMPACT_SPEED_THRESHOLD) return;
			MinecartImpacts.tryPlayCartImpactSoundAndSetCooldowns(this.level, this.first.cart, this.second.cart, this.bufferImpact);
		}
	}

	private static final class Body {
		private final ServerLevel level;
		private final AbstractMinecart cart;
		private final double maxSpeed;
		private final double moveScale;
		private final boolean experimental;
		private Vec3 position;
		@Nullable
		private Vec3 tangent;
		private Vec3 slopeTangent = Vec3.ZERO;
		@Nullable
		private Vec3 blocked;
		private double inverseMass;
		private boolean jammed;
		private double speed;
		private double shifted;
		private boolean dirty;
		private Body parent = this;
		private int size = 1;

		private Body(ServerLevel level, AbstractMinecart cart, boolean experimental) {
			this.level = level;
			this.cart = cart;
			this.experimental = experimental;
			this.position = cart.position();
			this.moveScale = !experimental && cart.isVehicle() ? RIDDEN_MOVE_SCALE : 1D;

			final CouplingToEntityInterface access = cart instanceof CouplingToEntityInterface couplingAccess ? couplingAccess : null;
			this.maxSpeed = access != null ? access.theCopperierAge$getMaxSpeed(level) : 0.4D;

			final Vec3 tickStart = access != null ? access.theCopperierAge$getTickStartPosition() : null;
			final Vec3 displacement = tickStart != null ? this.position.subtract(tickStart) : Vec3.ZERO;

			if (tickStart != null) {
				final Vec3 horizontal = displacement.horizontal();
				if (horizontal.lengthSqr() > MIN_DISPLACEMENT_SQR) {
					final double length = horizontal.length();
					final double rise = Mth.clamp(displacement.y / length, -MAX_SLOPE_RISE, MAX_SLOPE_RISE);
					final Vec3 direction = horizontal.scale(1D / length);
					this.setTangent(direction, new Vec3(direction.x, rise, direction.z));
				}
			}

			if (this.tangent == null) {
				final MinecartTrackHelper.TrackTangent track = MinecartTrackHelper.tangentUnder(cart);
				if (track != null) this.setTangent(track.horizontal(), track.slope());
			}

			this.jammed = this.isJammed(access, displacement);
			this.inverseMass = isImmovable(level, cart) || this.jammed ? 0D : 1D;
		}

		private boolean isJammed(@Nullable CouplingToEntityInterface access, Vec3 displacement) {
			if (access == null || this.tangent == null || !access.theCopperierAge$isTerrainJammed()) return false;
			if (this.cart.getDeltaMovement().horizontalDistanceSqr() >= JAM_SPEED_SQR) return false;
			if (displacement.horizontal().lengthSqr() >= JAM_DISPLACEMENT_SQR) return false;

			return !this.canOccupy(this.position.add(this.slopeTangent.scale(ESCAPE_PROBE)))
				|| !this.canOccupy(this.position.subtract(this.slopeTangent.scale(ESCAPE_PROBE)));
		}

		private Body root() {
			Body body = this;
			while (body.parent != body) body = body.parent;
			Body cursor = this;
			while (cursor.parent != body) {
				final Body next = cursor.parent;
				cursor.parent = body;
				cursor = next;
			}
			return body;
		}

		private static void union(Body first, Body second) {
			final Body firstRoot = first.root();
			final Body secondRoot = second.root();
			if (firstRoot == secondRoot) return;
			if (firstRoot.size < secondRoot.size) {
				firstRoot.parent = secondRoot;
				secondRoot.size += firstRoot.size;
			} else {
				secondRoot.parent = firstRoot;
				firstRoot.size += secondRoot.size;
			}
		}

		private void resolveFreeTangent(Vec3 linkDirection) {
			if (this.tangent != null) return;

			final Vec3 velocity = this.cart.getDeltaMovement().horizontal();
			Vec3 direction = velocity.lengthSqr() > MIN_MOVING_SPEED_SQR ? velocity : linkDirection;
			if (direction.lengthSqr() < EPSILON) direction = new Vec3(1D, 0D, 0D);
			direction = direction.normalize();
			this.setTangent(direction, direction);
		}

		private void setTangent(Vec3 horizontal, Vec3 slope) {
			this.tangent = horizontal;
			this.slopeTangent = slope;
			final Vec3 velocity = this.cart.getDeltaMovement().horizontal();
			final double along = velocity.x * horizontal.x + velocity.z * horizontal.z;
			final double magnitude = along < 0D ? -velocity.length() : velocity.length();
			this.speed = Mth.clamp(magnitude, -this.maxSpeed, this.maxSpeed);
		}

		private Vec3 predictedPosition() {
			return this.position.add(this.slopeTangent.scale(this.speed * this.moveScale));
		}

		private void shift(double amount) {
			if (this.inverseMass <= 0D || Math.abs(amount) < EPSILON) return;

			final double target = Mth.clamp(this.shifted + amount, -MAX_POSITION_CORRECTION_PER_TICK, MAX_POSITION_CORRECTION_PER_TICK);
			final double applied = target - this.shifted;
			if (Math.abs(applied) < EPSILON) return;

			final Vec3 candidate = this.position.add(this.slopeTangent.scale(applied));
			if (!this.canOccupy(candidate)) return;

			this.position = candidate;
			this.shifted = target;
			this.dirty = true;
		}

		private boolean canOccupy(Vec3 candidate) {
			final AABB box = this.cart.getBoundingBox().move(candidate.subtract(this.cart.position()));
			return this.level.noCollision(this.cart, box);
		}

		private boolean blocksToward(Vec3 direction) {
			if (this.inverseMass > 0D) return this.blocked != null && this.blocked.dot(direction) > BLOCK_ALIGNMENT;
			if (!this.jammed) return true;
			return !this.canOccupy(this.position.add(direction.scale(ESCAPE_PROBE)));
		}

		private boolean blockToward(Vec3 direction, Body neighbour) {
			if (this.inverseMass <= 0D || !neighbour.blocksToward(direction)) return false;
			if (this.blocked != null && this.blocked.dot(direction) > BLOCK_ALIGNMENT) return false;

			this.blocked = direction;
			return true;
		}

		private void publishBlocked(long gameTime) {
			if (!(this.cart instanceof CouplingToEntityInterface access)) return;
			if (this.inverseMass <= 0D) {
				access.theCopperierAge$setBlocked(Vec3.ZERO, gameTime);
			} else if (this.blocked != null) {
				access.theCopperierAge$setBlocked(this.blocked, gameTime);
			}
		}

		private void accelerate(double amount) {
			if (this.inverseMass <= 0D || Math.abs(amount) < EPSILON) return;
			this.speed = Mth.clamp(this.speed + amount, -this.maxSpeed, this.maxSpeed);
			this.dirty = true;
		}

		private void apply() {
			if (!this.dirty || this.inverseMass <= 0D || this.tangent == null) return;

			final Vec3 velocity = this.cart.getDeltaMovement();
			final Vec3 newVelocity = new Vec3(this.tangent.x * this.speed, velocity.y, this.tangent.z * this.speed);
			this.cart.setDeltaMovement(newVelocity);

			final double magnitude = Math.abs(this.shifted);
			if (magnitude <= 0D) return;
			this.cart.setPos(this.position);
			if (this.experimental && this.cart.getBehavior() instanceof NewMinecartBehavior behavior) {
				behavior.lerpSteps.add(new NewMinecartBehavior.MinecartStep(
					this.position, newVelocity, this.cart.getYRot(), this.cart.getXRot(), Math.max((float) magnitude, MIN_LERP_STEP_WEIGHT)
				));
			}
		}
	}

	private static void propagateBlocked(List<Link> links, Collection<Body> bodies, long gameTime) {
		boolean changed = true;
		for (int pass = 0; changed && pass < bodies.size(); pass++) {
			changed = false;
			for (Link link : links) changed |= link.propagateBlocked();
		}
		for (Body body : bodies) body.publishBlocked(gameTime);
	}

	private static boolean isImmovable(ServerLevel level, AbstractMinecart cart) {
		return cart.noPhysics || level.tickRateManager().isEntityFrozen(cart) || RelayerRailBlock.isDockedAt(level, cart);
	}

	private MinecartCouplingPhysics() {}
}
