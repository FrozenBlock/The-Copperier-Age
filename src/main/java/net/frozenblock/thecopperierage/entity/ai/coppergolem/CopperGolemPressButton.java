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

package net.frozenblock.thecopperierage.entity.ai.coppergolem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.frozenblock.thecopperierage.entity.impl.TCACopperGolemStates;
import net.frozenblock.thecopperierage.registry.TCAMemoryModuleTypes;
import net.frozenblock.thecopperierage.registry.TCAPoiTypes;
import net.frozenblock.thecopperierage.registry.TCASounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.coppergolem.CopperGolem;
import net.minecraft.world.entity.animal.coppergolem.CopperGolemState;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import net.minecraft.Util;

public class CopperGolemPressButton extends Behavior<CopperGolem> {
	public static final int TARGET_INTERACTION_TIME = 20;
	public static final int TARGET_INTERACTION_FINISH_TIME = 30;
	private static final int MAX_UNREACHABLE_POSITIONS = 25;
	private static final int PASSENGER_MOB_TARGET_SEARCH_DISTANCE = 1;
	private static final int POST_DEPOSIT_BUTTON_SEARCH_DISTANCE = 6;
	private static final IntProvider COOLDOWN = UniformInt.of(140, 4800);
	private static final IntProvider SMALL_COOLDOWN = UniformInt.of(140, 400);
	private static final double CLOSE_ENOUGH_TO_START_INTERACTING_WITH_TARGET_DISTANCE = 0.5D;
	private static final double BUTTON_CLIP_SEARCH_WIDTH = 0.45D;
	private static final double CLOSE_ENOUGH_TO_START_INTERACTING_WITH_TARGET_PATH_END_DISTANCE = 1D;
	private static final double CLOSE_ENOUGH_TO_CONTINUE_INTERACTING_WITH_TARGET = 2D;
	private final float speedModifier;
	private final int horizontalSearchDistance;
	private final int verticalSearchDistance;
	private final Predicate<BlockState> destinationBlockType;
	@Nullable
	private PressButtonTarget target = null;
	private PressButtonState state;
	private int ticksSinceReachingTarget;

	public CopperGolemPressButton(
		final float speedModifier,
		final Predicate<BlockState> destinationBlockType,
		final int horizontalSearchDistance,
		final int verticalSearchDistance
	) {
		super(
			ImmutableMap.of(
				TCAMemoryModuleTypes.UNREACHABLE_BUTTON_PRESS_BLOCK_POSITIONS, MemoryStatus.REGISTERED,
				TCAMemoryModuleTypes.TARGETED_BUTTON, MemoryStatus.VALUE_ABSENT,
				TCAMemoryModuleTypes.BUTTON_PRESS_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT,
					TCAMemoryModuleTypes.NEARBY_BUTTON_SEARCH_TICKS, MemoryStatus.REGISTERED,
				TCAMemoryModuleTypes.NEARBY_COPPER_GOLEMS, MemoryStatus.REGISTERED,
				MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_ABSENT,
				MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED,
					MemoryModuleType.TRANSPORT_ITEMS_COOLDOWN_TICKS, MemoryStatus.REGISTERED
			)
		);
		this.speedModifier = speedModifier;
		this.destinationBlockType = destinationBlockType;
		this.horizontalSearchDistance = horizontalSearchDistance;
		this.verticalSearchDistance = verticalSearchDistance;
		this.state = PressButtonState.TRAVELLING;
	}

	@Override
	protected void start(final ServerLevel level, final CopperGolem body, final long timestamp) {
		if (body.getNavigation() instanceof GroundPathNavigation pathNavigation) pathNavigation.setCanPathToTargetsBelowSurface(true);
	}

	@Override
	protected boolean checkExtraStartConditions(final ServerLevel level, final CopperGolem body) {
		return !body.isLeashed() && this.canStartButtonPressing(body);
	}

	@Override
	protected boolean canStillUse(final ServerLevel level, final CopperGolem body, final long timestamp) {
		return body.getBrain().checkMemory(TCAMemoryModuleTypes.BUTTON_PRESS_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT)
			&& !body.isPanicking()
			&& !body.isLeashed()
			&& this.canStartButtonPressing(body);
	}

	@Override
	protected boolean timedOut(final long timestamp) {
		return false;
	}

	@Override
	protected void tick(final ServerLevel level, final CopperGolem body, final long timestamp) {
		final boolean updatedInvalidTarget = this.updateInvalidTarget(level, body);
		if (this.target == null) {
			this.stop(level, body, timestamp);
			return;
		}
		if (updatedInvalidTarget) return;

		if (this.state.equals(PressButtonState.TRAVELLING)) this.onTravelToTarget(this.target, level, body);
		if (this.state.equals(PressButtonState.INTERACTING)) this.onReachedTarget(this.target, level, body);
	}

	private boolean updateInvalidTarget(final ServerLevel level, final CopperGolem body) {
		if (this.hasValidTarget(level, body)) return false;

		this.stopTargetingCurrentTarget(body);
		final Optional<PressButtonTarget> target = this.getPressButtonTarget(level, body);
		if (target.isPresent()) {
			this.target = target.get();
			body.getBrain().setMemory(TCAMemoryModuleTypes.TARGETED_BUTTON, new GlobalPos(level.dimension(), this.target.pos));
			this.onStartTravelling(body);
			return true;
		}

		this.stopTargetingCurrentTarget(body);
		body.getBrain().eraseMemory(TCAMemoryModuleTypes.NEARBY_BUTTON_SEARCH_TICKS);
		body.getBrain().setMemory(TCAMemoryModuleTypes.BUTTON_PRESS_COOLDOWN_TICKS, SMALL_COOLDOWN.sample(body.getRandom()));
		return true;
	}

	protected void onTravelToTarget(final PressButtonTarget target, final Level level, final CopperGolem body) {
		if (this.isWithinTargetDistance(getInteractionRange(body), target, level, body, this.getCenterPos(body))) {
			this.setPressingState(PressButtonState.INTERACTING);
		} else {
			this.walkTowardsTarget(body);
		}
	}

	private Vec3 getCenterPos(final CopperGolem body) {
		return this.setMiddleYPosition(body, body.position());
	}

	protected void onReachedTarget(final PressButtonTarget target, final Level level, final CopperGolem body) {
		if (
			this.ticksSinceReachingTarget < TARGET_INTERACTION_TIME
			&& !this.isWithinTargetDistance(CLOSE_ENOUGH_TO_CONTINUE_INTERACTING_WITH_TARGET, target, level, body, this.getCenterPos(body))
		) {
			this.onStartTravelling(body);
			return;
		}

		++this.ticksSinceReachingTarget;
		this.onTargetInteraction(target, body);
		if (this.ticksSinceReachingTarget >= TARGET_INTERACTION_FINISH_TIME) {
			this.stopTargetingCurrentTarget(body);
			body.getBrain().eraseMemory(TCAMemoryModuleTypes.NEARBY_BUTTON_SEARCH_TICKS);
			body.getBrain().setMemory(TCAMemoryModuleTypes.BUTTON_PRESS_COOLDOWN_TICKS, COOLDOWN.sample(body.getRandom()));
			this.onStartTravelling(body);
		}
	}

	private void walkTowardsTarget(final CopperGolem body) {
		if (this.target != null) BehaviorUtils.setWalkAndLookTargetMemories(body, this.target.pos, this.speedModifier, 0);
	}

	private void onStartTravelling(final CopperGolem body) {
		body.setState(CopperGolemState.IDLE);
		this.setPressingState(PressButtonState.TRAVELLING);
		this.ticksSinceReachingTarget = 0;
	}

	private void setPressingState(final PressButtonState state) {
		this.state = state;
	}

	private void onTargetInteraction(final PressButtonTarget target, final CopperGolem body) {
		body.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(target.pos));
		this.stopInPlace(body);

		if (this.ticksSinceReachingTarget == 1) {
			body.setState(TCACopperGolemStates.PRESSING_BUTTON);
			body.playSound(TCASounds.ENTITY_COPPER_GOLEM_BUTTON_PRESS);
		} else if (this.ticksSinceReachingTarget == TARGET_INTERACTION_TIME) {
			final Block block = target.state.getBlock();
			if (!(block instanceof ButtonBlock button)) return;
			if (target.state.getValueOrElse(ButtonBlock.POWERED, true)) return;
			button.press(target.state, body.level(), target.pos, null);
		}
	}

	private Optional<PressButtonTarget> getPressButtonTarget(final ServerLevel level, final CopperGolem body) {
		final boolean forceNearbySearch = this.isForcedNearbyButtonSearch(body);
		final int horizontalSearchDistance = forceNearbySearch
			? Math.min(POST_DEPOSIT_BUTTON_SEARCH_DISTANCE, this.getHorizontalSearchDistance(body))
			: this.getHorizontalSearchDistance(body);
		final int verticalSearchDistance = forceNearbySearch
			? Math.min(POST_DEPOSIT_BUTTON_SEARCH_DISTANCE, this.getVerticalSearchDistance(body))
			: this.getVerticalSearchDistance(body);
		final AABB targetBlockSearchArea = this.getTargetSearchArea(body, horizontalSearchDistance, verticalSearchDistance);
		final Set<GlobalPos> unreachablePositions = getUnreachablePositions(body);
		final Set<Pair<Holder<PoiType>, BlockPos>> copperButtons = level.getPoiManager().findAllWithType(
			holder -> holder.is(TCAPoiTypes.COPPER_BUTTON_KEY),
			poiPos -> true,
			body.blockPosition(),
			Math.max(horizontalSearchDistance, verticalSearchDistance),
			PoiManager.Occupancy.ANY
		).collect(Collectors.toSet());

		final ArrayList<PressButtonTarget> possibleTargets = new ArrayList<>();
		for (Pair<Holder<PoiType>, BlockPos> poi : copperButtons) {
			final BlockPos poiPos = poi.getSecond();
			final PressButtonTarget targetValidToPick = this.isTargetValidToPick(body, level, poiPos, unreachablePositions, targetBlockSearchArea);
			if (targetValidToPick == null) continue;
			possibleTargets.add(targetValidToPick);
		}

		if (possibleTargets.isEmpty()) return Optional.empty();
		if (!forceNearbySearch) return Optional.of(Util.getRandom(possibleTargets, body.getRandom()));

		return possibleTargets.stream()
			.min((first, second) -> Double.compare(first.pos.distSqr(body.blockPosition()), second.pos.distSqr(body.blockPosition())));
	}

	@Nullable
	private CopperGolemPressButton.PressButtonTarget isTargetValidToPick(
		final CopperGolem body,
		final ServerLevel level,
		BlockPos pos,
		Set<GlobalPos> unreachablePositions,
		final AABB targetBlockSearchArea
	) {
		final boolean isWithinSearchArea = targetBlockSearchArea.contains(pos.getX(), pos.getY(), pos.getZ());
		if (!isWithinSearchArea) return null;

		final PressButtonTarget pressButtonTarget = PressButtonTarget.tryCreatePossibleTarget(level, pos);
		if (pressButtonTarget == null) return null;

		boolean isValidTarget = !this.isPositionAlreadyVisited(unreachablePositions, pressButtonTarget, level)
			&& this.isWantedBlock(pressButtonTarget.state)
			&& !this.isPositionAlreadyClaimed(body, pressButtonTarget.pos);
		return isValidTarget ? pressButtonTarget : null;
	}

	private boolean hasValidTarget(final Level level, final CopperGolem body) {
		if (this.ticksSinceReachingTarget >= TARGET_INTERACTION_TIME) return true;
		final boolean targetIsOfValidType = this.target != null
			&& this.isWantedBlock(this.target.state)
			&& this.targetHasNotChanged(level, this.target);
		if (!targetIsOfValidType) return false;
		if (!this.state.equals(PressButtonState.TRAVELLING)) return true;
		if (!this.isPositionAlreadyClaimed(body, this.target.pos) && this.hasValidTravellingPath(level, this.target, body)) return true;

		this.markBlockPosAsUnreachable(body, level, this.target.pos);
		return false;
	}

	private boolean hasValidTravellingPath(final Level level, final PressButtonTarget target, final CopperGolem body) {
		final Path path = body.getNavigation().getPath() == null ? body.getNavigation().createPath(target.pos, 0) : body.getNavigation().getPath();
		final Vec3 posFromWhichToReachTarget = this.getPositionToReachTargetFrom(path, body);
		final boolean canReachTarget = this.isWithinTargetDistance(getInteractionRange(body), target, level, body, posFromWhichToReachTarget);
		final boolean hasNotYetCreatedPathToTarget = path == null && !canReachTarget;
		return hasNotYetCreatedPathToTarget || this.targetIsReachableFromPosition(level, canReachTarget, posFromWhichToReachTarget, target, body);
	}

	private Vec3 getPositionToReachTargetFrom(final @Nullable Path path, final CopperGolem body) {
		final boolean haveNoValidPath = path == null || path.getEndNode() == null;
		final Vec3 bottomCenter = haveNoValidPath ? body.position() : path.getEndNode().asBlockPos().getBottomCenter();
		return this.setMiddleYPosition(body, bottomCenter);
	}

	private Vec3 setMiddleYPosition(final CopperGolem body, final Vec3 pos) {
		return pos.add(0D, body.getBoundingBox().getYsize() / 2D, 0D);
	}

	private boolean targetHasNotChanged(final Level level, final PressButtonTarget target) {
		return target.state.trySetValue(ButtonBlock.POWERED, false).equals(level.getBlockState(target.pos).trySetValue(ButtonBlock.POWERED, false));
	}

	private AABB getTargetSearchArea(final CopperGolem mob, final int horizontalSearchDistance, final int verticalSearchDistance) {
		return new AABB(mob.blockPosition()).inflate(horizontalSearchDistance, verticalSearchDistance, horizontalSearchDistance);
	}

	private int getHorizontalSearchDistance(final CopperGolem mob) {
		return mob.isPassenger() ? PASSENGER_MOB_TARGET_SEARCH_DISTANCE : this.horizontalSearchDistance;
	}

	private int getVerticalSearchDistance(final CopperGolem mob) {
		return mob.isPassenger() ? PASSENGER_MOB_TARGET_SEARCH_DISTANCE : this.verticalSearchDistance;
	}

	private static Set<GlobalPos> getUnreachablePositions(final CopperGolem body) {
		return body.getBrain().getMemory(TCAMemoryModuleTypes.UNREACHABLE_BUTTON_PRESS_BLOCK_POSITIONS).orElse(Set.of());
	}

	private boolean isPositionAlreadyVisited(Set<GlobalPos> unreachablePositions, PressButtonTarget target, Level level) {
		final GlobalPos globalPos = new GlobalPos(level.dimension(), target.pos);
		return unreachablePositions.contains(globalPos);
	}

	private static boolean hasFinishedPath(final CopperGolem body) {
		return body.getNavigation().getPath() != null && body.getNavigation().getPath().isDone();
	}

	protected void markBlockPosAsUnreachable(final CopperGolem body, final Level level, final BlockPos pos) {
		final Set<GlobalPos> unreachablePositions = new HashSet<>(getUnreachablePositions(body));
		unreachablePositions.add(new GlobalPos(level.dimension(), pos));
		if (unreachablePositions.size() > MAX_UNREACHABLE_POSITIONS) {
			this.enterCooldownAfterNoMatchingTargetFound(body);
		} else {
			body.getBrain().setMemoryWithExpiry(TCAMemoryModuleTypes.UNREACHABLE_BUTTON_PRESS_BLOCK_POSITIONS, unreachablePositions, 6000L);
		}
	}

	private boolean isWantedBlock(final BlockState state) {
		return this.destinationBlockType.test(state);
	}

	private boolean isPositionAlreadyClaimed(final CopperGolem body, final BlockPos pos) {
		final GlobalPos mockGlobalPos = new GlobalPos(body.level().dimension(), pos);
		for (CopperGolem copperGolem : body.getBrain().getMemory(TCAMemoryModuleTypes.NEARBY_COPPER_GOLEMS).orElse(ImmutableList.of())) {
			final Optional<GlobalPos> targetedButton = copperGolem.getBrain().getMemory(TCAMemoryModuleTypes.TARGETED_BUTTON);
			if (targetedButton.filter(globalPos -> globalPos.equals(mockGlobalPos)).isPresent()) return true;
		}

		return false;
	}

	private static double getInteractionRange(final CopperGolem body) {
		return hasFinishedPath(body) ? CLOSE_ENOUGH_TO_START_INTERACTING_WITH_TARGET_PATH_END_DISTANCE : CLOSE_ENOUGH_TO_START_INTERACTING_WITH_TARGET_DISTANCE;
	}

	private boolean isWithinTargetDistance(final double distance, final PressButtonTarget target, final Level level, final CopperGolem body, final Vec3 fromPos) {
		final AABB boundingBox = body.getBoundingBox();
		final AABB movedBoundBox = AABB.ofSize(fromPos, boundingBox.getXsize(), boundingBox.getYsize(), boundingBox.getZsize());
		return new AABB(target.pos)
			.inflate(distance, CLOSE_ENOUGH_TO_START_INTERACTING_WITH_TARGET_DISTANCE, distance)
			.intersects(movedBoundBox);
	}

	private boolean targetIsReachableFromPosition(final Level level, final boolean canReachTarget, final Vec3 pos, final PressButtonTarget target, final CopperGolem body) {
		return canReachTarget && this.canSeeAnyTargetSide(target, level, body, pos);
	}

	private boolean canSeeAnyTargetSide(final PressButtonTarget target, final Level level, final CopperGolem body, final Vec3 eyePosition) {
		final Vec3 center = target.pos.getCenter();
		return Direction.stream()
			.map(
				direction -> center.add(
					BUTTON_CLIP_SEARCH_WIDTH * direction.getStepX(),
					BUTTON_CLIP_SEARCH_WIDTH * direction.getStepY(),
					BUTTON_CLIP_SEARCH_WIDTH * direction.getStepZ()
				)
			)
			.map(hitTarget -> level.clip(new ClipContext(eyePosition, hitTarget, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, body)))
			.anyMatch(hitResult -> hitResult.getType() == HitResult.Type.BLOCK && hitResult.getBlockPos().equals(target.pos));
	}

	private boolean canStartButtonPressing(final CopperGolem body) {
		return this.isForcedNearbyButtonSearch(body)
			|| body.getBrain().checkMemory(MemoryModuleType.TRANSPORT_ITEMS_COOLDOWN_TICKS, MemoryStatus.VALUE_PRESENT);
	}

	private boolean isForcedNearbyButtonSearch(final CopperGolem body) {
		return body.getBrain().checkMemory(TCAMemoryModuleTypes.NEARBY_BUTTON_SEARCH_TICKS, MemoryStatus.VALUE_PRESENT);
	}

	protected void stopTargetingCurrentTarget(final CopperGolem body) {
		this.ticksSinceReachingTarget = 0;
		this.target = null;
		body.getNavigation().stop();
		final Brain<?> brain = body.getBrain();
		brain.eraseMemory(MemoryModuleType.WALK_TARGET);
		brain.eraseMemory(TCAMemoryModuleTypes.TARGETED_BUTTON);
	}

	private void enterCooldownAfterNoMatchingTargetFound(final CopperGolem body) {
		this.stopTargetingCurrentTarget(body);
		body.getBrain().setMemory(TCAMemoryModuleTypes.BUTTON_PRESS_COOLDOWN_TICKS, SMALL_COOLDOWN.sample(body.getRandom()));
		body.getBrain().eraseMemory(TCAMemoryModuleTypes.UNREACHABLE_BUTTON_PRESS_BLOCK_POSITIONS);
	}

	@Override
	protected void stop(final ServerLevel level, final CopperGolem body, final long timestamp) {
		this.onStartTravelling(body);
		if (body.getNavigation() instanceof GroundPathNavigation pathNavigation) pathNavigation.setCanPathToTargetsBelowSurface(false);
		body.getBrain().eraseMemory(TCAMemoryModuleTypes.TARGETED_BUTTON);
	}

	private void stopInPlace(final CopperGolem mob) {
		mob.getNavigation().stop();
		mob.setXxa(0F);
		mob.setYya(0F);
		mob.setSpeed(0F);
		mob.setDeltaMovement(0D, mob.getDeltaMovement().y, 0D);
	}

	public enum PressButtonState {
		TRAVELLING,
		INTERACTING
	}

	public record PressButtonTarget(BlockPos pos, BlockState state) {
		@Nullable
		public static PressButtonTarget tryCreatePossibleTarget(final ServerLevel level, final BlockPos pos) {
			final BlockState state = level.getBlockState(pos);
			if (!state.is(BlockTags.BUTTONS) || !(state.getBlock() instanceof ButtonBlock)) return null;
			if (!level.getPoiManager().existsAtPosition(TCAPoiTypes.COPPER_BUTTON_KEY, pos)) return null;
			return new PressButtonTarget(pos, state);
		}
	}
}
