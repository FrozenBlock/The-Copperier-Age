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

import java.util.List;
import java.util.function.Supplier;
import net.frozenblock.lib.platform.api.registry.DeferredBlockEntityType;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.block.entity.ChimeBlockEntity;
import net.frozenblock.thecopperierage.block.entity.CrateBlockEntity;
import net.frozenblock.thecopperierage.block.entity.StickyGearboxBlockEntity;
import net.frozenblock.thecopperierage.references.TCABlockEntityTypeIds;

public final class TCABlockEntityTypes {
	private static final DeferredRegister.BlockEntities REGISTER = DeferredRegister.createBlockEntities(TCAConstants.MOD_ID);

	public static final DeferredBlockEntityType<ChimeBlockEntity> CHIME = REGISTER.register(TCABlockEntityTypeIds.CHIME,
		ChimeBlockEntity::new,
		() -> TCABlocks.CHIME.map(Supplier::get).asList()
	);
	public static final DeferredBlockEntityType<StickyGearboxBlockEntity> STICKY_GEARBOX = REGISTER.register(TCABlockEntityTypeIds.STICKY_GEARBOX,
		StickyGearboxBlockEntity::new,
		() -> TCABlocks.STICKY_GEARBOX.map(Supplier::get).asList()
	);
	public static final DeferredBlockEntityType<CrateBlockEntity> CRATE = REGISTER.register(TCABlockEntityTypeIds.CRATE,
		CrateBlockEntity::new,
		List.of(TCABlocks.CRATE)
	);

	static {
		REGISTER.register();
	}

	public static void init() {}

	private TCABlockEntityTypes() {}
}
