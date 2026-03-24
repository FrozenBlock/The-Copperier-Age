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
import net.frozenblock.thecopperierage.registry.TCABlockStateProperties;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class RedstoneGritBlock extends ColoredFallingBlock {
	public static final int MAX_STABILITY = 10;
    public static final IntegerProperty STABILITY = TCABlockStateProperties.STABILITY;
    public static final MapCodec<RedstoneGritBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ColorRGBA.CODEC.fieldOf("falling_dust_color").forGetter(redstoneGritBlock -> redstoneGritBlock.dustColor),
		propertiesCodec()
	).apply(instance, RedstoneGritBlock::new));

    @Override
    public MapCodec<RedstoneGritBlock> codec() {
        return CODEC;
    }

    public RedstoneGritBlock(ColorRGBA color, Properties properties) {
        super(color, properties);
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
            if (state.getValue(STABILITY) == MAX_STABILITY) {
                level.setBlock(pos, state.setValue(STABILITY, 0), Block.UPDATE_ALL);
                return;
            }
            if (movedByPiston && level instanceof ServerLevel serverLevel) spawnActivationParticles(serverLevel, pos);
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
			return;
        }

		if (stability >= MAX_STABILITY) return;

		final int newStability = stability + 1;
		final boolean isActivated = newStability == MAX_STABILITY;
		level.setBlock(pos, state.setValue(STABILITY, newStability), isActivated ? Block.UPDATE_ALL : Block.UPDATE_CLIENTS);
		if (isActivated) {
			spawnActivationParticles(level, pos);
		} else {
			level.scheduleTick(pos, this, 2);
		}
    }

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		super.animateTick(state, level, pos, random);
		if (isStable(state)) spawnClientParticles(level, pos);
	}

	@Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(STABILITY) == MAX_STABILITY ? 15 : 0;
    }

	public static boolean isStable(BlockState state) {
		return state.getValue(STABILITY) == MAX_STABILITY;
	}

    private void spawnActivationParticles(ServerLevel level, BlockPos pos) {
        final int color = this.dustColor.rgba();
        final DustParticleOptions particle = new DustParticleOptions(color, 1F);
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

	private static void spawnClientParticles(Level level, BlockPos pos) {
		final RandomSource random = level.getRandom();
		for (Direction direction : Direction.values()) {
			final BlockPos offsetPos = pos.relative(direction);
			if (level.getBlockState(offsetPos).isSolidRender()) continue;

			final Direction.Axis axis = direction.getAxis();
			final double x = axis == Direction.Axis.X ? 0.5D + 0.5625D * direction.getStepX() : random.nextFloat();
			final double y = axis == Direction.Axis.Y ? 0.5D + 0.5625D * direction.getStepY() : random.nextFloat();
			final double z = axis == Direction.Axis.Z ? 0.5D + 0.5625D * direction.getStepZ() : random.nextFloat();
			level.addParticle(DustParticleOptions.REDSTONE, pos.getX() + x, pos.getY() + y, pos.getZ() + z, 0D, 0D, 0D);
		}
	}
}
