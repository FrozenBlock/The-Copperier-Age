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
	public static final double PUSH_INTENSITY_REVERSE = 0.75D;
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
	public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
		/*
		final int power =  state.getValue(POWER);
		if (power % 2 == 0) return;
		if (random.nextFloat() > 0.05F) return;

		level.playLocalSound(
			pos.getX() + 0.5D,
			pos.getY() + 0.5D,
			pos.getZ() + 0.5D,
			TCASounds.BLOCK_GEARBOX_IDLE,
			SoundSource.BLOCKS,
			0.1F,
			0.1F + (power / 5F) + (random.nextFloat() * 0.1F),
			false
		);
		 */
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
			if (!level.hasChunkAt(mutablePos.move(direction))) continue;

			final BlockState state = level.getBlockState(mutablePos);
			if (!canFanPassThrough(level, mutablePos, state, direction)) break;

			if (!level.getFluidState(mutablePos).isEmpty()) {
				if (cutoffPos.isEmpty()) cutoffPos = Optional.of(mutablePos.immutable());
			}
		}

		mutablePos.move(direction.getOpposite());
		AABB blowingArea = cutoffPos.map(blockPos ->
				aabb(pos, blockPos.immutable().relative(direction.getOpposite())))
			.orElseGet(() -> aabb(pos, mutablePos.immutable()));

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
		}

		final double fanDistance = !reverse ? FAN_DISTANCE : FAN_DISTANCE_REVERSE;
		final double pushIntensity = !reverse ? PUSH_INTENSITY : PUSH_INTENSITY_REVERSE;
		final Vec3 movement = Vec3.atLowerCornerOf((!reverse ? direction : direction.getOpposite()).getUnitVec3i());
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
