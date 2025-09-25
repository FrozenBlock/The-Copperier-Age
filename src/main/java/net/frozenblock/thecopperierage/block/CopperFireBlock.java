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
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.tag.TCABlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CopperFireBlock extends BaseFireBlock {
    public static final MapCodec<CopperFireBlock> CODEC = simpleCodec(CopperFireBlock::new);

    public CopperFireBlock(BlockBehaviour.Properties properties) {
        super(properties, 1F);
    }

    @Override
    public @NotNull MapCodec<CopperFireBlock> codec() {
        return CODEC;
    }

	@Override
	protected @NotNull BlockState updateShape(
		@NotNull BlockState state,
		@NotNull LevelReader level,
		ScheduledTickAccess scheduledTickAccess,
		@NotNull BlockPos pos,
		Direction direction,
		BlockPos neighborPos,
		BlockState neighborState,
		RandomSource randomSource
	) {
		return this.canSurvive(state, level, pos) ? this.defaultBlockState() : Blocks.AIR.defaultBlockState();
	}

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        return canSurviveOnBlock(level, pos.below());
    }

    public static boolean canSurviveOnBlock(@NotNull BlockGetter level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
        if (!state.is(TCABlockTags.COPPER_FIRE_BASE_BLOCKS)) return false;
		return state.isFaceSturdy(level, pos, Direction.UP);
    }

	@Override
	protected void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
		if (level.isRainingAt(pos)) level.removeBlock(pos, false);
	}

	@Override
    protected boolean canBurn(BlockState state) {
        return true;
    }

	public static void poisonEntity(@NotNull Level level, Entity entity) {
		if (level.isClientSide() || !(entity instanceof LivingEntity livingEntity)) return;
		if (!TCAConfig.get().copperFirePoisons) return;
		livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 119));
	}

    @Override
    protected void entityInside(BlockState state, @NotNull Level level, BlockPos pos, @NotNull Entity entity, InsideBlockEffectApplier insideBlockEffectApplier) {
        super.entityInside(state, level, pos, entity, insideBlockEffectApplier);
        poisonEntity(level, entity);
    }
}
