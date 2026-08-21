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
import net.frozenblock.thecopperierage.block.entity.StickyGearboxBlockEntity;
import net.frozenblock.thecopperierage.registry.TCABlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

public class StickyGearboxBlock extends GearboxBlock implements EntityBlock {
	public static final MapCodec<StickyGearboxBlock> CODEC = simpleCodec(StickyGearboxBlock::new);

	@Override
	public MapCodec<? extends StickyGearboxBlock> codec() {
		return CODEC;
	}

	public StickyGearboxBlock(Properties properties) {
		super(properties);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new StickyGearboxBlockEntity(pos, state);
	}

	@Unique
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide() || type != TCABlockEntityTypes.STICKY_GEARBOX) return null;
		return (levelx, posx, statex, blockEntityx) -> StickyGearboxBlockEntity.tick(levelx, posx, statex, (StickyGearboxBlockEntity) blockEntityx);
	}
}
