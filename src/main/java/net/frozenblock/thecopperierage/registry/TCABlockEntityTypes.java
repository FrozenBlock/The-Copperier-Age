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

package net.frozenblock.thecopperierage.registry;

import java.util.Collection;
import java.util.Set;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.block.entity.ChimeBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;
import net.minecraft.Util;

public final class TCABlockEntityTypes {

	public static final BlockEntityType<ChimeBlockEntity> CHIME = register(
		"chime",
		ChimeBlockEntity::new,
		TCABlocks.CHIME.asList()
	);

	public static void init() {
		TCAConstants.logWithModId("Registering BlockEntities for", TCAConstants.UNSTABLE_LOGGING);
	}

	@NotNull
	private static <T extends BlockEntity> BlockEntityType<T> register(@NotNull String path, BlockEntityType.BlockEntitySupplier<T> builder, Collection<Block> blocks) {
		Util.fetchChoiceType(References.BLOCK_ENTITY, TCAConstants.string(path));
		return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, TCAConstants.id(path), new BlockEntityType<>(builder, Set.copyOf(blocks)));
	}


}
