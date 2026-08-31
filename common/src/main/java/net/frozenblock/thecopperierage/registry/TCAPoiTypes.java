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

import java.util.function.Supplier;
import net.frozenblock.lib.platform.RegistryHelper;
import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.world.entity.ai.village.poi.PoiType;

public final class TCAPoiTypes {
	private static final DeferredRegister.PoiTypes REGISTER = RegistryHelper.createDeferredPoiTypesRegister(TCAConstants.MOD_ID);

	public static final DeferredHolder<PoiType, PoiType> COPPER_BUTTON = REGISTER.register(
		"copper_button", 1, 1,
		TCABlocks.COPPER_BUTTON.asList().toArray(new Supplier[0])
	);

	static {
		REGISTER.register();
	}

	public static void init() {}
}
