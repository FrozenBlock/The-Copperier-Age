/*
 * Copyright 2025 FrozenBlock
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

package net.frozenblock.thecopperierage.block;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.particle.client.options.WindParticleOptions;
import net.frozenblock.lib.wind.api.WindDisturbance;
import net.frozenblock.lib.wind.api.WindDisturbanceLogic;
import net.frozenblock.lib.wind.api.WindManager;
import net.frozenblock.lib.wind.client.impl.ClientWindManager;
import net.frozenblock.thecopperierage.mod_compat.FrozenLibIntegration;
import net.frozenblock.thecopperierage.networking.packet.TCACopperFanBlowPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Optional;

public class CopperFanBlock extends DirectionalBlock {
	public static final double FAN_DISTANCE = 9D;
	public static final int FAN_DISTANCE_IN_BLOCKS = 8;
	public static final double PUSH_INTENSITY = 0.15D;
	public static final double BASE_WIND_INTENSITY = 0.5D;
	public static final double FAN_DISTANCE_REVERSE = 5D;
	public static final int FAN_DISTANCE_IN_BLOCKS_REVERSE = 4;
	public static final double PUSH_INTENSITY_REVERSE = 0.075D;
	public static final double BASE_WIND_INTENSITY_REVERSE = 0.3D;
    public static final MapCodec<CopperFanBlock> CODEC = simpleCodec(CopperFanBlock::new);
	private static final WindDisturbanceLogic<? extends CopperFanBlock> DUMMY_WIND_LOGIC = new WindDisturbanceLogic<>((source, level1, windOrigin, affectedArea, windTarget) -> WindDisturbance.DUMMY_RESULT);

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public CopperFanBlock(Properties properties) {
        super(properties);
		this.registerDefaultState(
			this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(POWERED, false)
		);
    }

    @Override
    public @NotNull MapCodec<? extends CopperFanBlock> codec() {
        return CODEC;
    }

	@Override
	public BlockState getStateForPlacement(@NotNull BlockPlaceContext blockPlaceContext) {
		final Direction facing = blockPlaceContext.getNearestLookingDirection().getOpposite();
		return this.defaultBlockState().setValue(FACING, facing);
	}

	@Override
	protected void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, RandomSource random) {
		this.updateFan(state, level, pos);
	}

	@Override
	protected void onPlace(@NotNull BlockState state, @NotNull Level level, BlockPos pos, @NotNull BlockState replacingState, boolean bl) {
		if (level.isClientSide() || state.is(replacingState.getBlock())) return;
		level.scheduleTick(pos, this, 1);
	}

	@Override
	protected void neighborChanged(
		BlockState state,
		@NotNull Level level,
		@NotNull BlockPos pos,
		Block block,
		@Nullable Orientation orientation,
		boolean movedByPiston
	) {
		if (level.isClientSide() || level.getBlockTicks().hasScheduledTick(pos, this)) return;
		level.scheduleTick(pos, this, 1);
	}

	public void updateFan(@NotNull BlockState state, @NotNull ServerLevel level, BlockPos pos) {
		final boolean hasNeighborSignal = level.hasNeighborSignal(pos);
		final boolean powered = state.getValue(POWERED);
		final Direction facing = state.getValue(FACING);

		if (hasNeighborSignal != powered) {
			final BlockState newState = state.setValue(POWERED, hasNeighborSignal);
			// TODO: Sounds
			level.playSound(
				null,
				pos,
				hasNeighborSignal ? SoundEvents.FIRECHARGE_USE : SoundEvents.FIRE_EXTINGUISH,
				SoundSource.BLOCKS
			);
			level.setBlock(pos, newState, UPDATE_ALL);
		}

		if (hasNeighborSignal) {
			this.blow(level, pos, facing);
			TCACopperFanBlowPacket.sendToAll(level, pos);
			level.scheduleTick(pos, this, 1);
		}
	}

	public void blow(Level level, @NotNull BlockPos pos, Direction facing) {
		this.handleBlowing(level, pos, facing, false);
		this.handleBlowing(level, pos, facing.getOpposite(), true);
	}

	public static boolean canFanPassThrough(LevelReader level, BlockPos pos, @NotNull BlockState state, @NotNull Direction direction) {
		return !((state.isFaceSturdy(level, pos, direction.getOpposite(), SupportType.CENTER)));
			//&& !state.is(WWBlockTags.GEYSER_CAN_PASS_THROUGH))
			//|| state.is(WWBlockTags.GEYSER_CANNOT_PASS_THROUGH));
	}

	@NotNull
	@Contract("_, _ -> new")
	private static AABB aabb(@NotNull BlockPos startPos, @NotNull BlockPos endPos) {
		return new AABB(
			Math.min(startPos.getX(), endPos.getX()),
			Math.min(startPos.getY(), endPos.getY()),
			Math.min(startPos.getZ(), endPos.getZ()),
			Math.max(startPos.getX(), endPos.getX()) + 1D,
			Math.max(startPos.getY(), endPos.getY()) + 1D,
			Math.max(startPos.getZ(), endPos.getZ()) + 1D
		);
	}

	private void handleBlowing(Level level, @NotNull BlockPos pos, Direction direction, boolean reverse) {
		Optional<BlockPos> cutoffPos = Optional.empty();
		BlockPos.MutableBlockPos mutablePos = pos.mutable();

		final int fanDistanceInBlocks = !reverse ? FAN_DISTANCE_IN_BLOCKS : FAN_DISTANCE_IN_BLOCKS_REVERSE;
		for (int i = 0; i < fanDistanceInBlocks; i++) {
			final boolean isFirstSearch = i == 0;
			if (!level.hasChunkAt(mutablePos.move(direction))) break;

			final BlockState state = level.getBlockState(mutablePos);
			if (!canFanPassThrough(level, mutablePos, state, direction)) {
				if (isFirstSearch) return;
				break;
			}

			if (!level.getFluidState(mutablePos).isEmpty()) {
				if (isFirstSearch) return;
				cutoffPos = Optional.of(mutablePos.immutable());
				break;
			}
		}

		final Direction oppositeDirection = direction.getOpposite();
		mutablePos.move(oppositeDirection);

		final BlockPos posWithCutoff = cutoffPos
			.map(blockPos -> blockPos.immutable().relative(oppositeDirection))
			.orElse(mutablePos.immutable());
		AABB blowingArea = aabb(pos, posWithCutoff);

		List<Entity> entities = level.getEntities(
			EntityTypeTest.forClass(Entity.class),
			blowingArea,
			EntitySelector.ENTITY_STILL_ALIVE.and(EntitySelector.NO_SPECTATORS)
		);
		Vec3 fanStartPos = Vec3.atCenterOf(pos);

		WindDisturbance<CopperFanBlock> windDisturbance = new WindDisturbance<CopperFanBlock>(
			Optional.of(this),
			fanStartPos,
			blowingArea.inflate(0.5D).move(direction.step().mul(0.5F)),
			WindDisturbanceLogic.getWindDisturbanceLogic(
				!reverse ? FrozenLibIntegration.COPPER_FAN_WIND_DISTURBANCE : FrozenLibIntegration.COPPER_FAN_WIND_DISTURBANCE_REVERSE
			).orElse(DUMMY_WIND_LOGIC)
		);

		if (level instanceof ServerLevel serverLevel) {
			final WindManager windManager = WindManager.getOrCreateWindManager(serverLevel);
			windManager.addWindDisturbance(windDisturbance);
		} else if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			addWindDisturbanceToClient(windDisturbance);
			final RandomSource random = level.getRandom();
			if (random.nextFloat() <= (!reverse ? 0.35F : 0.2F)) {
				Vec3 particlePos;
				Vec3 particleVelocity;
				if (!reverse) {
					particlePos = getParticlePos(pos, direction, random);
					particleVelocity = getParticleVelocity(direction, random, 0.5D, 0.7D);
					particleVelocity = particleVelocity.add(getVelocityFromDistance(pos, direction, particlePos, random, 0.175D));
				} else {
					final BlockPos startPos = pos.relative(direction);
					final BlockPos endPos = posWithCutoff.relative(direction);
					final BlockPos particleBlockPos = BlockPos.containing(Mth.lerp(random.nextDouble(), startPos.getCenter(), endPos.getCenter()));

					particlePos = getParticlePos(particleBlockPos, direction, random);
					particleVelocity = getParticleVelocity(oppositeDirection, random, 0.3D, 0.5D);
					particleVelocity = particleVelocity.add(getVelocityFromDistance(pos, oppositeDirection, particlePos, random, 0.1D));
				}

				level.addAlwaysVisibleParticle(
					new WindParticleOptions(12, particleVelocity),
					particlePos.x,
					particlePos.y,
					particlePos.z,
					0D,
					0D,
					0D
				);
			}
		}

		final double fanDistance = !reverse ? FAN_DISTANCE : FAN_DISTANCE_REVERSE;
		final double pushIntensity = !reverse ? PUSH_INTENSITY : PUSH_INTENSITY_REVERSE;
		final Vec3 movement = Vec3.atLowerCornerOf((!reverse ? direction : oppositeDirection).getUnitVec3i());
		for (Entity entity : entities) {
			AABB boundingBox = entity.getBoundingBox();
			if (!blowingArea.intersects(boundingBox)) continue;

			boolean applyMovement = true;
			if (entity instanceof Player player) {
				if (!player.getAbilities().flying) {
					if (direction == Direction.UP) {
						Vec3 lastImpactPos = player.currentImpulseImpactPos;
						Vec3 playerPos = player.position();
						player.currentImpulseImpactPos = new Vec3(
							playerPos.x(),
							lastImpactPos != null ? Math.min(lastImpactPos.y(), playerPos.y()) : playerPos.y(),
							playerPos.z()
						);
						player.setIgnoreFallDamageFromCurrentImpulse(true);
					}
				} else {
					applyMovement = false;
				}
			}

			if (applyMovement) {
				final double intensity = (fanDistance - Math.min(entity.position().distanceTo(fanStartPos), fanDistance)) / fanDistance;
				final double overallIntensity = intensity * pushIntensity;
				final Vec3 deltaMovement = entity.getDeltaMovement().add(movement.scale(overallIntensity));
				entity.setDeltaMovement(deltaMovement);
			}
		}
	}

	@NotNull
	public static Vec3 getParticleVelocity(@NotNull Direction direction, @NotNull RandomSource random, double min, double max) {
		double difference = max - min;
		double velocity = min + (random.nextDouble() * difference);
		double x = direction.getStepX() * velocity;
		double y = direction.getStepY() * velocity;
		double z = direction.getStepZ() * velocity;
		return new Vec3(x, y, z);
	}

	@NotNull
	public static Vec3 getVelocityFromDistance(BlockPos pos, Direction direction, @NotNull Vec3 vec3, @NotNull RandomSource random, double max) {
		return vec3.subtract(getParticlePosWithoutRandom(pos, direction, random)).scale(random.nextDouble() * max);
	}

	@NotNull
	public static Vec3 getParticlePosWithoutRandom(BlockPos pos, Direction direction, RandomSource random) {
		return Vec3.atLowerCornerOf(pos).add(
			getParticleOffsetX(direction, random, false),
			getParticleOffsetY(direction, random, false),
			getParticleOffsetZ(direction, random, false)
		);
	}

	@NotNull
	public static Vec3 getParticlePos(BlockPos pos, Direction direction, RandomSource random) {
		return Vec3.atLowerCornerOf(pos).add(
			getParticleOffsetX(direction, random, true),
			getParticleOffsetY(direction, random, true),
			getParticleOffsetZ(direction, random, true)
		);
	}

	private static double getRandomParticleOffset(@NotNull RandomSource random) {
		return random.nextDouble() / 3D * (random.nextBoolean() ? 1D : -1D);
	}

	private static double getParticleOffsetX(@NotNull Direction direction, RandomSource random, boolean useRandom) {
		return switch (direction) {
			case UP, DOWN, SOUTH, NORTH -> 0.5D + (useRandom ? getRandomParticleOffset(random) : 0D);
			case EAST -> 1.05D;
			case WEST -> -0.05D;
		};
	}

	private static double getParticleOffsetY(@NotNull Direction direction, RandomSource random, boolean useRandom) {
		return switch (direction) {
			case DOWN -> -0.05D;
			case UP -> 1.05D;
			case NORTH, WEST, EAST, SOUTH -> 0.5D + (useRandom ? getRandomParticleOffset(random) : 0D);
		};
	}

	private static double getParticleOffsetZ(@NotNull Direction direction, RandomSource random, boolean useRandom) {
		return switch (direction) {
			case UP, DOWN, EAST, WEST -> 0.5D + (useRandom ? getRandomParticleOffset(random) : 0D);
			case NORTH -> -0.05D;
			case SOUTH -> 1.05D;
		};
	}

	@Override
	protected @NotNull BlockState rotate(@NotNull BlockState blockState, @NotNull Rotation rotation) {
		return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
	}

	@Override
	protected @NotNull BlockState mirror(@NotNull BlockState blockState, @NotNull Mirror mirror) {
		return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED);
	}

	@Environment(EnvType.CLIENT)
	private static void addWindDisturbanceToClient(@NotNull WindDisturbance windDisturbance) {
		ClientWindManager.addWindDisturbance(windDisturbance);
	}

}
