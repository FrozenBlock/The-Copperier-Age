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

package net.frozenblock.thecopperierage.registry;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.block.entity.ChimeBlockEntity;
import net.frozenblock.thecopperierage.block.entity.CrateBlockEntity;
import net.frozenblock.thecopperierage.block.entity.KilnBlockEntity;
import net.frozenblock.thecopperierage.block.entity.StickyGearboxBlockEntity;
import net.frozenblock.thecopperierage.references.TCABlockEntityTypeIds;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class TCABlockEntityTypes {
	private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TCAConstants.MOD_ID);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChimeBlockEntity>> CHIME = register(
		TCABlockEntityTypeIds.CHIME,
		ChimeBlockEntity::new,
		() -> TCABlocks.asBlocks(TCABlocks.CHIME).asList()
	);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StickyGearboxBlockEntity>> STICKY_GEARBOX = register(
		TCABlockEntityTypeIds.STICKY_GEARBOX,
		StickyGearboxBlockEntity::new,
		() -> TCABlocks.asBlocks(TCABlocks.STICKY_GEARBOX).asList()
	);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrateBlockEntity>> CRATE = register(
		TCABlockEntityTypeIds.CRATE,
		CrateBlockEntity::new,
		() -> List.of(TCABlocks.CRATE.get())
	);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KilnBlockEntity>> KILN = register(
		TCABlockEntityTypeIds.KILN,
		KilnBlockEntity::new,
		() -> List.of(TCABlocks.KILN.get())
	);

	static {
		REGISTER.register();
	}

	public static void init() {}

	private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(
		ResourceKey<BlockEntityType<?>> id,
		BlockEntityType.BlockEntitySupplier<T> builder,
		Supplier<Collection<Block>> blocks
	) {
		return REGISTER.register(id, () -> new BlockEntityType<>(builder, Set.copyOf(blocks.get())));
	}
}
