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

package net.frozenblock.thecopperierage.item.coupling;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.frozenblock.thecopperierage.registry.TCAItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TCAMinecartCouplingManager {
	private static final String SAVE_FILE = "thecopperierage_couplings.json";
	private static final String ROOT_LIST_KEY = "dimensions";
	private static final String DIMENSION_KEY = "dimension";
	private static final String COUPLINGS_KEY = "couplings";
	private static final String FIRST_KEY = "first";
	private static final String SECOND_KEY = "second";
	private static final String LENGTH_KEY = "length";

	private static final int MAX_COUPLINGS_PER_CART = 2;
	private static final int MAX_COUPLING_DISTANCE = 3;
	private static final int MAX_COUPLING_SNAP_DISTANCE = 8;
	private static final float MIN_COUPLING_LENGTH = 1.5F;
	private static final float COUPLING_WIGGLE_ROOM = 0.2F;
	private static final float TARGET_COUPLING_LENGTH = MIN_COUPLING_LENGTH + COUPLING_WIGGLE_ROOM;
	private static final float MAX_SOFT_CORRECTION_PER_TICK = 1.0F;
	private static final float MAX_HARD_CORRECTION_PER_TICK = 1.75F;
	private static final double EPSILON = 1.0E-6D;

	private static final Map<ResourceKey<Level>, Set<CouplingData>> COUPLINGS = new HashMap<>();
	private static final Map<ResourceKey<Level>, Map<UUID, Set<UUID>>> GRAPH = new HashMap<>();
	private static final Map<ResourceKey<Level>, Map<UUID, Vec3>> LAST_KNOWN_CART_POSITIONS = new HashMap<>();
	private static MinecraftServer currentServer;
	private static boolean dirty;

	public static void init() {
		ServerLifecycleEvents.SERVER_STARTED.register(TCAMinecartCouplingManager::onServerStarted);
		ServerTickEvents.END_WORLD_TICK.register(TCAMinecartCouplingManager::tickWorld);
		ServerLifecycleEvents.SERVER_STOPPING.register(TCAMinecartCouplingManager::onServerStopping);
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			currentServer = null;
			clear();
		});
	}

	public static boolean tryToCouple(Player player, Level level, int cartId1, int cartId2) {
		if (!(level instanceof ServerLevel serverLevel)) return false;
		if (cartId1 == cartId2) return false;

		final Entity entity1 = serverLevel.getEntity(cartId1);
		final Entity entity2 = serverLevel.getEntity(cartId2);
		if (!(entity1 instanceof AbstractMinecart cart1) || !(entity2 instanceof AbstractMinecart cart2)) return false;

		final UUID id1 = cart1.getUUID();
		final UUID id2 = cart2.getUUID();
		if (isDirectlyCoupled(serverLevel.dimension(), id1, id2)) return false;

		final double distance = cart1.position().distanceTo(cart2.position());
		if (distance > MAX_COUPLING_DISTANCE) return false;

		final Map<UUID, Set<UUID>> graph = graph(serverLevel.dimension());
		if (degree(graph, id1) >= MAX_COUPLINGS_PER_CART || degree(graph, id2) >= MAX_COUPLINGS_PER_CART) return false;
		if (createsLoop(graph, id1, id2)) return false;

		if (!consumeOneCoupling(player)) return false;

		final float length = TARGET_COUPLING_LENGTH;
		couplings(serverLevel.dimension()).add(new CouplingData(id1, id2, length));
		connect(graph, id1, id2);
		markDirty();
		return true;
	}

	public static boolean hasCoupling(Level level, UUID cartId) {
		return degree(graph(level.dimension()), cartId) > 0;
	}

	public static int decoupleCart(Level level, UUID cartId) {
		final ResourceKey<Level> dimension = level.dimension();
		final Set<CouplingData> couplings = couplings(dimension);
		if (couplings.isEmpty()) return 0;

		int removed = 0;
		for (Iterator<CouplingData> iterator = couplings.iterator(); iterator.hasNext();) {
			final CouplingData coupling = iterator.next();
			if (!coupling.contains(cartId)) continue;

			iterator.remove();
			removeTrackedPositions(dimension, coupling);
			disconnect(graph(dimension), coupling.first(), coupling.second());
			removed++;
		}

		if (removed > 0) markDirty();
		return removed;
	}

	private static void tickWorld(ServerLevel level) {
		final Set<CouplingData> couplings = couplings(level.dimension());
		if (couplings.isEmpty()) return;

		final Map<UUID, Set<UUID>> graph = graph(level.dimension());
		boolean removedAny = false;
		for (Iterator<CouplingData> iterator = couplings.iterator(); iterator.hasNext();) {
			final CouplingData coupling = iterator.next();
			final Entity firstEntity = level.getEntity(coupling.first());
			final Entity secondEntity = level.getEntity(coupling.second());
			final boolean firstMissing = firstEntity == null;
			final boolean secondMissing = secondEntity == null;

			if (firstEntity instanceof AbstractMinecart firstMinecart && !isDestroyed(firstMinecart)) {
				trackPosition(level.dimension(), coupling.first(), firstMinecart.position());
			}
			if (secondEntity instanceof AbstractMinecart secondMinecart && !isDestroyed(secondMinecart)) {
				trackPosition(level.dimension(), coupling.second(), secondMinecart.position());
			}

			if (firstMissing && secondMissing) continue;

			if (firstMissing != secondMissing) {
				final UUID droppedFirstId = firstMissing ? coupling.first() : coupling.second();
				dropCouplingItem(level, firstDroppedPosition(level.dimension(), droppedFirstId, firstEntity, secondEntity));
				iterator.remove();
				removeTrackedPositions(level.dimension(), coupling);
				disconnect(graph, coupling.first(), coupling.second());
				removedAny = true;
				continue;
			}

			if (!(firstEntity instanceof AbstractMinecart first) || !(secondEntity instanceof AbstractMinecart second)) {
				iterator.remove();
				removeTrackedPositions(level.dimension(), coupling);
				disconnect(graph, coupling.first(), coupling.second());
				removedAny = true;
				continue;
			}

			if (isDestroyed(first) != isDestroyed(second)) {
				final UUID droppedFirstId = isDestroyed(first) ? coupling.first() : coupling.second();
				dropCouplingItem(level, firstDroppedPosition(level.dimension(), droppedFirstId, first, second));
				iterator.remove();
				removeTrackedPositions(level.dimension(), coupling);
				disconnect(graph, coupling.first(), coupling.second());
				removedAny = true;
				continue;
			}

			if (isDestroyed(first) && isDestroyed(second)) {
				dropCouplingItem(level, first.position().add(second.position()).scale(0.5D));
				iterator.remove();
				removeTrackedPositions(level.dimension(), coupling);
				disconnect(graph, coupling.first(), coupling.second());
				removedAny = true;
				continue;
			}

			if (!tickCoupling(level, first, second, coupling.length())) {
				iterator.remove();
				removeTrackedPositions(level.dimension(), coupling);
				disconnect(graph, coupling.first(), coupling.second());
				removedAny = true;
			}
		}

		if (removedAny) markDirty();

		if (level == level.getServer().overworld()) flushIfDirty();
	}

	private static boolean tickCoupling(ServerLevel level, AbstractMinecart first, AbstractMinecart second, float couplingLength) {
		if (first.isRemoved() || second.isRemoved()) {
			return false;
		}

		if (first.distanceToSqr(second) > (MAX_COUPLING_SNAP_DISTANCE * MAX_COUPLING_SNAP_DISTANCE)) {
			return false;
		}

		if (level.tickRateManager().isEntityFrozen(first) && level.tickRateManager().isEntityFrozen(second)) {
			return true;
		}

		softCollisionStep(level, first, second, couplingLength);
		hardCollisionStep(level, first, second, couplingLength);
		return true;
	}

	private static void softCollisionStep(ServerLevel level, AbstractMinecart first, AbstractMinecart second, float couplingLength) {
		final boolean firstCanAddMotion = canAddMotion(first);
		final boolean secondCanAddMotion = canAddMotion(second);
		if (!firstCanAddMotion && !secondCanAddMotion) {
			return;
		}

		Vec3 firstMotion = clamp(first.getDeltaMovement(), 1F);
		Vec3 secondMotion = clamp(second.getDeltaMovement(), 1F);
		final Vec3 nextFirst = first.position().add(firstMotion);
		final Vec3 nextSecond = second.position().add(secondMotion);

		final RailShape firstShape = getRailShape(level, nextFirst);
		final RailShape secondShape = getRailShape(level, nextSecond);

		final float futureStress = (float) (couplingLength - nextFirst.distanceTo(nextSecond));
		if (Mth.equal(futureStress, 0D)) {
			return;
		}

		for (boolean current : new boolean[] {true, false}) {
			final boolean currentCanAddMotion = current ? firstCanAddMotion : secondCanAddMotion;
			final boolean otherCanAddMotion = current ? secondCanAddMotion : firstCanAddMotion;
			if (!currentCanAddMotion) {
				continue;
			}

			final Vec3 currentPos = current ? nextFirst : nextSecond;
			final Vec3 otherPos = current ? nextSecond : nextFirst;
			final Vec3 link = otherPos.subtract(currentPos);
			if (link.lengthSqr() <= EPSILON) {
				continue;
			}

			float correctionMagnitude = -futureStress / 2F;
			if (currentCanAddMotion != otherCanAddMotion) {
				correctionMagnitude = !currentCanAddMotion ? 0F : correctionMagnitude * 2F;
			}

			Vec3 correction;
			final RailShape shape = current ? firstShape : secondShape;
			if (shape != null) {
				final Vec3 railVec = getRailVec(shape);
				correction = followLinkOnRail(link, currentPos, correctionMagnitude, railVec).subtract(currentPos);
			} else {
				correction = link.normalize().scale(correctionMagnitude);
			}

			final float maxSpeed = current ? getMaxCartSpeed(first) : getMaxCartSpeed(second);
			correction = clamp(correction, maxSpeed);
			if (current) {
				firstMotion = firstMotion.add(correction);
			} else {
				secondMotion = secondMotion.add(correction);
			}
		}

		first.setDeltaMovement(clamp(firstMotion, getMaxCartSpeed(first)));
		second.setDeltaMovement(clamp(secondMotion, getMaxCartSpeed(second)));
	}

	private static void hardCollisionStep(ServerLevel level, AbstractMinecart first, AbstractMinecart second, float couplingLength) {
		AbstractMinecart firstCart = first;
		AbstractMinecart secondCart = second;
		if (!canAddMotion(secondCart) && canAddMotion(firstCart)) {
			final AbstractMinecart swap = firstCart;
			firstCart = secondCart;
			secondCart = swap;
		}

		boolean firstLoop = true;
		for (boolean current : new boolean[] {true, false, true}) {
			final AbstractMinecart cart = current ? firstCart : secondCart;
			final AbstractMinecart otherCart = current ? secondCart : firstCart;

			float stress = (float) (couplingLength - cart.position().distanceTo(otherCart.position()));
			if (Math.abs(stress) < 1F / 8F) {
				continue;
			}

			final Vec3 pos = cart.position();
			final Vec3 link = otherCart.position().subtract(pos);
			if (link.lengthSqr() <= EPSILON) {
				continue;
			}

			float correctionMagnitude = firstLoop ? -stress / 2F : -stress;
			if (!canAddMotion(cart)) {
				correctionMagnitude /= 2F;
			}

			Vec3 correction = link.normalize().scale(correctionMagnitude);
			correction = clamp(correction, Math.min(MAX_HARD_CORRECTION_PER_TICK, getMaxCartSpeed(cart)));
			cart.move(MoverType.SELF, correction);
			cart.setDeltaMovement(cart.getDeltaMovement().scale(0.95F));

			firstLoop = false;
		}
	}

	private static Vec3 followLinkOnRail(Vec3 link, Vec3 cartPos, float diffToReduce, Vec3 railAxis) {
		final double dotProduct = railAxis.dot(link);
		if (Double.isNaN(dotProduct) || dotProduct == 0 || diffToReduce == 0) {
			return cartPos;
		}

		final Vec3 axis = railAxis.scale(-Math.signum(dotProduct));
		final Vec3 center = cartPos.add(link);
		final double radius = link.length() - diffToReduce;
		final Vec3 intersectSphere = intersectSphere(cartPos, axis, center, radius);

		if (intersectSphere == null) {
			return cartPos.add(project(link, axis));
		}

		return intersectSphere;
	}

	private static @Nullable Vec3 intersectSphere(Vec3 lineOrigin, Vec3 lineDirection, Vec3 sphereCenter, double sphereRadius) {
		final double directionLengthSq = lineDirection.lengthSqr();
		if (directionLengthSq <= EPSILON || sphereRadius <= 0) {
			return null;
		}

		final Vec3 delta = lineOrigin.subtract(sphereCenter);
		final double a = directionLengthSq;
		final double b = 2.0D * delta.dot(lineDirection);
		final double c = delta.lengthSqr() - sphereRadius * sphereRadius;
		final double discriminant = b * b - 4.0D * a * c;
		if (discriminant < 0D) {
			return null;
		}

		final double sqrtDiscriminant = Math.sqrt(discriminant);
		final double t1 = (-b - sqrtDiscriminant) / (2.0D * a);
		final double t2 = (-b + sqrtDiscriminant) / (2.0D * a);
		double t = t1;
		if (Math.abs(t2) < Math.abs(t1)) {
			t = t2;
		}

		return lineOrigin.add(lineDirection.scale(t));
	}

	private static boolean canAddMotion(AbstractMinecart cart) {
		if (cart instanceof MinecartFurnace furnace) {
			return Mth.equal((float) furnace.push.x, 0) && Mth.equal((float) furnace.push.z, 0);
		}

		return !cart.isRemoved() && !cart.noPhysics;
	}

	private static float getMaxCartSpeed(AbstractMinecart cart) {
		return cart.isInWater() ? 0.2F : 0.4F;
	}

	private static Vec3 clamp(Vec3 vec, float maxLength) {
		final double length = vec.length();
		if (length <= maxLength || length <= EPSILON) {
			return vec;
		}

		return vec.scale(maxLength / length);
	}

	private static @Nullable RailShape getRailShape(ServerLevel level, Vec3 vec) {
		final int x = Mth.floor(vec.x());
		final int y = Mth.floor(vec.y());
		final int z = Mth.floor(vec.z());

		BlockPos pos = new BlockPos(x, y - 1, z);
		BlockState railState = level.getBlockState(pos);
		if (!railState.is(BlockTags.RAILS)) {
			pos = pos.above();
			railState = level.getBlockState(pos);
		}
		if (!(railState.getBlock() instanceof BaseRailBlock railBlock)) {
			return null;
		}

		return railState.getValue(railBlock.getShapeProperty());
	}

	private static Vec3 getRailVec(RailShape shape) {
		return switch (shape) {
			case EAST_WEST -> new Vec3(1, 0, 0);
			case ASCENDING_EAST, ASCENDING_WEST -> new Vec3(1, 0, 0);
			case NORTH_SOUTH -> new Vec3(0, 0, 1);
			case ASCENDING_NORTH, ASCENDING_SOUTH -> new Vec3(0, 0, 1);
			case NORTH_EAST, SOUTH_WEST -> new Vec3(1, 0, 1).normalize();
			case NORTH_WEST, SOUTH_EAST -> new Vec3(1, 0, -1).normalize();
		};
	}

	private static Vec3 project(Vec3 vec, Vec3 onto) {
		final double denominator = onto.lengthSqr();
		if (denominator <= EPSILON) {
			return Vec3.ZERO;
		}

		return onto.scale(vec.dot(onto) / denominator);
	}

	private static boolean isDirectlyCoupled(ResourceKey<Level> dimension, UUID first, UUID second) {
		return couplings(dimension).contains(new CouplingData(first, second, MIN_COUPLING_LENGTH));
	}

	private static boolean consumeOneCoupling(Player player) {
		for (InteractionHand hand : InteractionHand.values()) {
			final ItemStack held = player.getItemInHand(hand);
			if (!held.is(TCAItems.MINECART_COUPLING)) continue;

			held.consume(1, player);
			return true;
		}

		return false;
	}

	private static boolean createsLoop(Map<UUID, Set<UUID>> graph, UUID start, UUID target) {
		if (start.equals(target)) {
			return true;
		}

		final Set<UUID> visited = new HashSet<>();
		final ArrayDeque<UUID> queue = new ArrayDeque<>();
		queue.add(start);

		while (!queue.isEmpty()) {
			final UUID current = queue.removeFirst();
			if (!visited.add(current)) {
				continue;
			}
			if (current.equals(target)) {
				return true;
			}

			for (UUID next : graph.getOrDefault(current, Set.of())) {
				if (!visited.contains(next)) {
					queue.addLast(next);
				}
			}
		}

		return false;
	}

	private static void connect(Map<UUID, Set<UUID>> graph, UUID first, UUID second) {
		graph.computeIfAbsent(first, key -> new HashSet<>()).add(second);
		graph.computeIfAbsent(second, key -> new HashSet<>()).add(first);
	}

	private static void disconnect(Map<UUID, Set<UUID>> graph, UUID first, UUID second) {
		graph.computeIfPresent(first, (key, value) -> {
			value.remove(second);
			return value.isEmpty() ? null : value;
		});
		graph.computeIfPresent(second, (key, value) -> {
			value.remove(first);
			return value.isEmpty() ? null : value;
		});
	}

	private static int degree(Map<UUID, Set<UUID>> graph, UUID uuid) {
		return graph.getOrDefault(uuid, Set.of()).size();
	}

	private static Set<CouplingData> couplings(ResourceKey<Level> dimension) {
		return COUPLINGS.computeIfAbsent(dimension, key -> new HashSet<>());
	}

	private static Map<UUID, Set<UUID>> graph(ResourceKey<Level> dimension) {
		return GRAPH.computeIfAbsent(dimension, key -> new HashMap<>());
	}

	private static Map<UUID, Vec3> lastKnownPositions(ResourceKey<Level> dimension) {
		return LAST_KNOWN_CART_POSITIONS.computeIfAbsent(dimension, key -> new HashMap<>());
	}

	private static void trackPosition(ResourceKey<Level> dimension, UUID cartId, Vec3 position) {
		lastKnownPositions(dimension).put(cartId, position);
	}

	private static Vec3 firstDroppedPosition(ResourceKey<Level> dimension, UUID droppedFirstId, @Nullable Entity firstEntity, @Nullable Entity secondEntity) {
		final Vec3 tracked = lastKnownPositions(dimension).get(droppedFirstId);
		if (tracked != null) {
			return tracked;
		}

		if (firstEntity != null) {
			return firstEntity.position();
		}
		if (secondEntity != null) {
			return secondEntity.position();
		}

		return Vec3.ZERO;
	}

	private static void removeTrackedPositions(ResourceKey<Level> dimension, CouplingData coupling) {
		final Map<UUID, Vec3> tracked = LAST_KNOWN_CART_POSITIONS.get(dimension);
		if (tracked == null) {
			return;
		}

		tracked.remove(coupling.first());
		tracked.remove(coupling.second());
		if (tracked.isEmpty()) {
			LAST_KNOWN_CART_POSITIONS.remove(dimension);
		}
	}

	private static boolean isDestroyed(AbstractMinecart minecart) {
		return minecart.isRemoved() || !minecart.isAlive();
	}

	private static void dropCouplingItem(ServerLevel level, Vec3 position) {
		final ItemEntity droppedCoupling = new ItemEntity(level, position.x, position.y, position.z, new ItemStack(TCAItems.MINECART_COUPLING));
		droppedCoupling.setDefaultPickUpDelay();
		level.addFreshEntity(droppedCoupling);
	}

	private static void onServerStarted(MinecraftServer server) {
		currentServer = server;
		loadFromDisk(server);
	}

	private static void onServerStopping(MinecraftServer server) {
		flushIfDirty();
	}

	private static void markDirty() {
		dirty = true;
	}

	private static void flushIfDirty() {
		if (!dirty || currentServer == null) return;
		saveToDisk(currentServer);
		dirty = false;
	}

	private static void loadFromDisk(MinecraftServer server) {
		clear();
		final java.nio.file.Path path = server.getWorldPath(LevelResource.ROOT).resolve("data").resolve(SAVE_FILE);
		if (!java.nio.file.Files.exists(path)) return;

		try {
			final String raw = java.nio.file.Files.readString(path, StandardCharsets.UTF_8);
			final JsonElement parsed = JsonParser.parseString(raw);
			if (!parsed.isJsonObject()) return;

			final JsonObject root = parsed.getAsJsonObject();
			if (!root.has(ROOT_LIST_KEY) || !root.get(ROOT_LIST_KEY).isJsonArray()) return;

			final JsonArray dimensions = root.getAsJsonArray(ROOT_LIST_KEY);
			for (JsonElement dimensionElement : dimensions) {
				if (!dimensionElement.isJsonObject()) continue;

				final JsonObject dimensionTag = dimensionElement.getAsJsonObject();
				if (!dimensionTag.has(DIMENSION_KEY) || !dimensionTag.get(DIMENSION_KEY).isJsonPrimitive()) continue;

				final ResourceLocation dimensionLocation = ResourceLocation.tryParse(dimensionTag.get(DIMENSION_KEY).getAsString());
				if (dimensionLocation == null) continue;

				final ResourceKey<Level> dimension = ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, dimensionLocation);
				if (!dimensionTag.has(COUPLINGS_KEY) || !dimensionTag.get(COUPLINGS_KEY).isJsonArray()) continue;

				for (JsonElement couplingElement : dimensionTag.getAsJsonArray(COUPLINGS_KEY)) {
					if (!couplingElement.isJsonObject()) continue;

					final JsonObject couplingTag = couplingElement.getAsJsonObject();
					if (!couplingTag.has(FIRST_KEY) || !couplingTag.has(SECOND_KEY) || !couplingTag.has(LENGTH_KEY)) continue;

					final UUID firstId = parseUuid(couplingTag.get(FIRST_KEY));
					final UUID secondId = parseUuid(couplingTag.get(SECOND_KEY));
					if (firstId == null || secondId == null || !couplingTag.get(LENGTH_KEY).isJsonPrimitive()) continue;

					final CouplingData coupling = new CouplingData(
						firstId,
						secondId,
						couplingTag.get(LENGTH_KEY).getAsFloat()
					);
					couplings(dimension).add(coupling);
					connect(graph(dimension), coupling.first(), coupling.second());
				}
			}
		} catch (IOException exception) {
			clear();
		} catch (RuntimeException exception) {
			clear();
		}
	}

	private static void saveToDisk(MinecraftServer server) {
		final java.nio.file.Path folder = server.getWorldPath(LevelResource.ROOT).resolve("data");
		final java.nio.file.Path file = folder.resolve(SAVE_FILE);

		try {
			java.nio.file.Files.createDirectories(folder);
			final JsonObject root = new JsonObject();
			final JsonArray dimensions = new JsonArray();

			for (Map.Entry<ResourceKey<Level>, Set<CouplingData>> entry : COUPLINGS.entrySet()) {
				if (entry.getValue().isEmpty()) continue;

				final JsonObject dimensionTag = new JsonObject();
				dimensionTag.addProperty(DIMENSION_KEY, entry.getKey().location().toString());
				final JsonArray couplings = new JsonArray();
				for (CouplingData coupling : entry.getValue()) {
					final JsonObject couplingTag = new JsonObject();
					couplingTag.addProperty(FIRST_KEY, coupling.first().toString());
					couplingTag.addProperty(SECOND_KEY, coupling.second().toString());
					couplingTag.addProperty(LENGTH_KEY, coupling.length());
					couplings.add(couplingTag);
				}

				dimensionTag.add(COUPLINGS_KEY, couplings);
				dimensions.add(dimensionTag);
			}

			root.add(ROOT_LIST_KEY, dimensions);
			java.nio.file.Files.writeString(file, root.toString(), StandardCharsets.UTF_8);
		} catch (IOException ignored) {
		}
	}

	private static @Nullable UUID parseUuid(JsonElement element) {
		if (element == null || !element.isJsonPrimitive()) return null;

		try {
			return UUID.fromString(element.getAsString());
		} catch (IllegalArgumentException exception) {
			return null;
		}
	}

	private static void clear() {
		COUPLINGS.clear();
		GRAPH.clear();
		LAST_KNOWN_CART_POSITIONS.clear();
		dirty = false;
	}

	private record CouplingData(UUID first, UUID second, float length) {
		private CouplingData(UUID first, UUID second, float length) {
			if (first.compareTo(second) <= 0) {
				this.first = first;
				this.second = second;
			} else {
				this.first = second;
				this.second = first;
			}
			this.length = length;
		}

		public boolean contains(@NotNull UUID uuid) {
			return first.equals(uuid) || second.equals(uuid);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof CouplingData other)) return false;
			return first.equals(other.first) && second.equals(other.second);
		}

		@Override
		public int hashCode() {
			return 31 * first.hashCode() + second.hashCode();
		}
	}
}
