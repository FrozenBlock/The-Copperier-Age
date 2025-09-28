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
import net.frozenblock.thecopperierage.block.entity.ChimeBlockEntity;
import net.frozenblock.thecopperierage.block.state.properties.ChimeAttachType;
import net.frozenblock.thecopperierage.registry.TCABlockEntityTypes;
import net.frozenblock.thecopperierage.registry.TCABlockStateProperties;
import net.frozenblock.thecopperierage.registry.TCASounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;
import java.util.function.BiConsumer;

public class ChimeBlock extends BaseEntityBlock {
	public static final MapCodec<ChimeBlock> CODEC = simpleCodec(ChimeBlock::new);
	public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
	public static final EnumProperty<ChimeAttachType> ATTACHMENT = TCABlockStateProperties.CHIME_ATTACHMENT;
	private static final VoxelShape SUPPORT_CHAIN_SHAPE = Block.box(7D, 12D, 7D, 9D, 16D, 9D);
	private static final VoxelShape BAR_SHAPE = Block.box(7D, 10D, 0D, 9D, 12D, 16D);
	private static final VoxelShape OUTLINE_SHAPE = Shapes.or(BAR_SHAPE, Block.box(7D, 0D, 1D, 9D, 10D, 15D));
	private static final Map<Direction, VoxelShape> CEILING_SHAPES_OUTLINE = Shapes.rotateHorizontal(Shapes.or(SUPPORT_CHAIN_SHAPE, OUTLINE_SHAPE));
	private static final Map<Direction, VoxelShape> CEILING_SHAPES_COLLISION = Shapes.rotateHorizontal(Shapes.or(SUPPORT_CHAIN_SHAPE, BAR_SHAPE));
	private static final Map<Direction, VoxelShape> WALL_SHAPES_OUTLINE = Shapes.rotateHorizontal(OUTLINE_SHAPE);
	private static final Map<Direction, VoxelShape> WALL_SHAPES_COLLISION = Shapes.rotateHorizontal(BAR_SHAPE);
	private static final Map<Direction, VoxelShape> ENTITY_INSIDE_SHAPES = Shapes.rotateHorizontal(Block.box(6.5D, -0.5D, -0.5D, 9.5D, 10.5D, 16.5D));

	public ChimeBlock(@NotNull Properties settings) {
		super(settings);
	}

	@NotNull
	@Override
	protected MapCodec<? extends ChimeBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, ATTACHMENT);
	}

	@Override
	protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext collisionContext) {
		return getVoxelShape(state, ChimeShapeType.OUTLINE);
	}

	@Override
	protected @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext collisionContext) {
		return getVoxelShape(state, ChimeShapeType.COLLISION);
	}

	@Override
	protected @NotNull VoxelShape getEntityInsideCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull Entity entity) {
		return getVoxelShape(state, ChimeShapeType.ENTITY_INSIDE);
	}

	private static VoxelShape getVoxelShape(@NotNull BlockState state, ChimeShapeType shapeType) {
		final Direction direction = state.getValue(FACING);
		return switch (state.getValue(ATTACHMENT)) {
			case WALL -> selectShapeListFromType(shapeType, WALL_SHAPES_OUTLINE, WALL_SHAPES_COLLISION, ENTITY_INSIDE_SHAPES).get(direction);
			case CEILING -> selectShapeListFromType(shapeType, CEILING_SHAPES_OUTLINE, CEILING_SHAPES_COLLISION, ENTITY_INSIDE_SHAPES).get(direction);
		};
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		final Direction direction = context.getClickedFace();
		final BlockPos pos = context.getClickedPos();
		final Level level = context.getLevel();

		if (direction == Direction.UP) return null;
		if (direction == Direction.DOWN) {
			final BlockState state = this.defaultBlockState()
				.setValue(ATTACHMENT, ChimeAttachType.CEILING)
				.setValue(FACING, context.getHorizontalDirection().getClockWise());
			return state.canSurvive(level, pos) ? state : null;
		}

		BlockState state = this.defaultBlockState()
			.setValue(FACING, direction)
			.setValue(ATTACHMENT, ChimeAttachType.WALL);
		if (state.canSurvive(level, pos)) return state;

		final BlockPos abovePos = pos.below();
		if (!level.getBlockState(abovePos).isFaceSturdy(level, abovePos, Direction.DOWN)) return null;

		state = state.setValue(ATTACHMENT, ChimeAttachType.CEILING);
		if (state.canSurvive(level, pos)) return state;

		return null;
	}

	@Override
	protected @NotNull BlockState updateShape(
		@NotNull BlockState state,
		@NotNull LevelReader level,
		@NotNull ScheduledTickAccess scheduledTickAccess,
		@NotNull BlockPos pos,
		@NotNull Direction direction,
		@NotNull BlockPos neighborPos,
		@NotNull BlockState neighborState,
		@NotNull RandomSource random
	) {
		final Direction connectedDirection = getConnectedDirection(state);
		if (connectedDirection.getOpposite() == direction && !state.canSurvive(level, pos)) return Blocks.AIR.defaultBlockState();
		return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
	}

	@Override
	public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
		final Direction connectedDirection = getConnectedDirection(state).getOpposite();
		return connectedDirection == Direction.UP
			? Block.canSupportCenter(level, pos.above(), Direction.DOWN)
			: FaceAttachedHorizontalDirectionalBlock.canAttach(level, pos, connectedDirection);
	}

	private static Direction getConnectedDirection(@NotNull BlockState state) {
		if (state.getValue(ATTACHMENT) == ChimeAttachType.CEILING) return Direction.DOWN;
		return state.getValue(FACING);
	}

	@Override
	@Nullable
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new ChimeBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
		return createTickerHelper(blockEntityType, TCABlockEntityTypes.CHIME, ChimeBlockEntity::tick);
	}

	@Override
	protected @NotNull InteractionResult useWithoutItem(
		@NotNull BlockState state,
		@NotNull Level level,
		@NotNull BlockPos pos,
		@NotNull Player player,
		BlockHitResult hitResult
	) {
		if (!(level.getBlockEntity(pos) instanceof ChimeBlockEntity chime)) return super.useWithoutItem(state, level, pos, player, hitResult);

		final Vec3 playerPos = player.getEyePosition();
		final Vec3 barCenter = pos.getCenter().add(0.3125D);
		final Vec3 difference = barCenter.subtract(playerPos);
		final double strength = (barCenter.y() - hitResult.getLocation().y()) * 1.25F;

		return chime.addEntityInfluence(level, pos, state, player, difference.normalize().scale(strength), 0.98D, true, false)
			? InteractionResult.SUCCESS
			: InteractionResult.CONSUME;
	}

	@Override
	public void entityInside(
		@NotNull BlockState state,
		@NotNull Level level,
		@NotNull BlockPos pos,
		@NotNull Entity entity,
		InsideBlockEffectApplier insideBlockEffectApplier
	) {
		final Vec3 movement = entity.getDeltaMovement();
		final double length = movement.length();
		if (length == 0D) return;

		if (!(level.getBlockEntity(pos) instanceof ChimeBlockEntity chime)) return;
		chime.addEntityInfluence(level, pos, state, entity, movement.normalize().scale(Math.min(1D, length * 2D)), 0.96D, false, true);
	}

	@Override
	protected void onExplosionHit(
		@NotNull BlockState state,
		@NotNull ServerLevel level,
		@NotNull BlockPos pos,
		@NotNull Explosion explosion,
		BiConsumer<ItemStack, BlockPos> biConsumer
	) {
		if (level.getBlockEntity(pos) instanceof ChimeBlockEntity chime) {
			float radius = explosion.radius();
			Vec3 difference = pos.getCenter().subtract(explosion.center());
			double closeness = (radius - difference.length()) / radius;
			chime.addInfluence(level, pos, state, difference.normalize().scale(closeness), 0.9875D, true, true);
		}

		super.onExplosionHit(state, level, pos, explosion, biConsumer);
	}

	@Override
	public @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public @NotNull BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public void animateTick(BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
		if (!(level.getBlockEntity(pos) instanceof ChimeBlockEntity chime)) return;

		Vec3 influence = chime.getLerpedInfluence(1F);
		final float influenceSpeed = Math.clamp((float) influence.length(), 0.02F, 1.2F);

		if (random.nextFloat() >= influenceSpeed * 0.25F) return;

		final float volume = Mth.lerp(influenceSpeed, 0.1F, 1F);
		final float pitch = Mth.lerp(influenceSpeed, 0.75F, 1.2F);
		level.playLocalSound(pos, TCASounds.BLOCK_CHIME_AMBIENT, SoundSource.AMBIENT, volume, pitch, false);
	}

	@Contract(pure = true)
	private static Map<Direction, VoxelShape> selectShapeListFromType(
		@NotNull ChimeShapeType shapeType,
		Map<Direction, VoxelShape> outline,
		Map<Direction, VoxelShape> collision,
		Map<Direction, VoxelShape> entityInside
	) {
		return switch (shapeType) {
			case OUTLINE -> outline;
			case COLLISION -> collision;
			case ENTITY_INSIDE -> entityInside;
		};
	}

	public enum ChimeShapeType {
		OUTLINE,
		COLLISION,
		ENTITY_INSIDE
	}
}
