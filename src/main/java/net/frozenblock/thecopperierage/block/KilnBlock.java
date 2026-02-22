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

package net.frozenblock.thecopperierage.block;

import com.mojang.serialization.MapCodec;
import net.frozenblock.thecopperierage.block.entity.KilnBlockEntity;
import net.frozenblock.thecopperierage.registry.TCABlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KilnBlock extends FurnaceBlock {
	public static final MapCodec<FurnaceBlock> CODEC = simpleCodec(KilnBlock::new);
	private static final double TOP_SMOKE_OFFSET_Y = 1.0;
	private static final double TOP_SMOKE_SPEED_Y = 0.04;
	private static final double FACE_PARTICLE_DEPTH = 0.53;
	private static final double FACE_MIN_U = 3.5 / 16.0;
	private static final double FACE_MAX_U = 12.5 / 16.0;
	private static final double FACE_MIN_V = 4.5 / 16.0;
	private static final double FACE_MAX_V = 11.5 / 16.0;
	private static final double FRONT_SMOKE_OFFSET_Y = 0.01;
	private static final double FRONT_SMOKE_SPEED_Y = 0.015;

	public KilnBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull MapCodec<FurnaceBlock> codec() {
		return CODEC;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new KilnBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
		@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType
	) {
		return createFurnaceTicker(level, blockEntityType, TCABlockEntityTypes.KILN);
	}

	@Override
	protected @NotNull InteractionResult useWithoutItem(
		@NotNull BlockState state,
		@NotNull Level level,
		@NotNull BlockPos pos,
		@NotNull Player player,
		@NotNull BlockHitResult hitResult
	) {
		if (level instanceof ServerLevel && level.getBlockEntity(pos) instanceof KilnBlockEntity kiln) {
			player.openMenu(kiln);
			player.awardStat(Stats.INTERACT_WITH_FURNACE);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected @NotNull InteractionResult useItemOn(
		@NotNull ItemStack stack,
		@NotNull BlockState state,
		@NotNull Level level,
		@NotNull BlockPos pos,
		@NotNull Player player,
		@NotNull InteractionHand hand,
		@NotNull BlockHitResult hitResult
	) {
		if (level instanceof ServerLevel && level.getBlockEntity(pos) instanceof KilnBlockEntity kiln) {
			player.openMenu(kiln);
			player.awardStat(Stats.INTERACT_WITH_FURNACE);
		}
		return InteractionResult.TRY_WITH_EMPTY_HAND;
	}

	@Override
	public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
		if (!state.getValue(BlockStateProperties.LIT)) return;

		final double centerX = pos.getX() + 0.5;
		final double centerZ = pos.getZ() + 0.5;

		level.addParticle(ParticleTypes.SMOKE, centerX, pos.getY() + TOP_SMOKE_OFFSET_Y, centerZ, 0.0, TOP_SMOKE_SPEED_Y, 0.0);

		final Direction facing = state.getValue(FACING);
		final double u = FACE_MIN_U + random.nextDouble() * (FACE_MAX_U - FACE_MIN_U);
		final double v = FACE_MIN_V + random.nextDouble() * (FACE_MAX_V - FACE_MIN_V);
		final double lateral = u - 0.5;

		final double frontX = centerX + facing.getStepX() * FACE_PARTICLE_DEPTH + facing.getClockWise().getStepX() * lateral;
		final double frontZ = centerZ + facing.getStepZ() * FACE_PARTICLE_DEPTH + facing.getClockWise().getStepZ() * lateral;
		final double frontY = pos.getY() + (1.0 - v);

		level.addParticle(ParticleTypes.FLAME, frontX, frontY, frontZ, 0.0, 0.0, 0.0);
		level.addParticle(ParticleTypes.SMOKE, frontX, frontY + FRONT_SMOKE_OFFSET_Y, frontZ, 0.0, FRONT_SMOKE_SPEED_Y, 0.0);
	}
}
