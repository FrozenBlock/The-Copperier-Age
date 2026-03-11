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
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class RedGritBlock extends ColoredFallingBlock {
    public static final IntegerProperty STABILITY = IntegerProperty.create("stability", 0, 10);
    public static final MapCodec<RedGritBlock> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(ColorRGBA.CODEC.fieldOf("falling_dust_color").forGetter((redGritBlock) -> {
            return redGritBlock.dustColor;
        }), propertiesCodec()).apply(instance, RedGritBlock::new);
    });

    @Override
    public MapCodec<RedGritBlock> codec() {
        return CODEC;
    }

    public RedGritBlock(ColorRGBA colorRGBA, BlockBehaviour.Properties properties) {
        super(colorRGBA, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(STABILITY, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(STABILITY);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(STABILITY, 0);
    }

    @Override
    public void onLand(Level level, BlockPos pos, BlockState fallingBlockState, BlockState currentStateOnLand, FallingBlockEntity entity) {
        super.onLand(level, pos, fallingBlockState.setValue(STABILITY, 0), currentStateOnLand, entity);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(this) || movedByPiston) {
            if (state.getValue(STABILITY) == 10) {
                level.setBlock(pos, state.setValue(STABILITY, 0), Block.UPDATE_ALL);
                return;
            }
            if (movedByPiston && level instanceof ServerLevel serverLevel) {
                spawnActivationParticles(serverLevel, pos);
            }
            super.onPlace(state, level, pos, oldState, movedByPiston);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int stability = state.getValue(STABILITY);
        if (isFree(level.getBlockState(pos.below()))) {
			level.setBlock(pos, state.setValue(STABILITY, 0), Block.UPDATE_CLIENTS);
			spawnActivationParticles(level, pos);
            super.tick(state.setValue(STABILITY, 0), level, pos, random);
        } else {
            if (stability < 10) {
                level.setBlock(pos, state.setValue(STABILITY, stability + 1), stability + 1 == 10 ? Block.UPDATE_ALL : Block.UPDATE_CLIENTS);
                if (stability + 1 == 10) {
                    spawnActivationParticles(level, pos);
                } else {
                    level.scheduleTick(pos, this, 2);
                }
            }
        }
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(STABILITY) == 10 ? 15 : 0;
    }

    @Override
    public int getDustColor(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return this.dustColor.rgba();
    }

    private void spawnActivationParticles(ServerLevel level, BlockPos pos) {
        int color = this.dustColor.rgba();
        DustParticleOptions particle = new DustParticleOptions(color, 1.0F);
        level.sendParticles(
            particle,
            pos.getX() + 0.5D,
            pos.getY() + 0.5D,
            pos.getZ() + 0.5D,
            10,
            0.4D,
            0.4D,
            0.4D,
            0.0D
        );
    }
}
