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

import net.frozenblock.lib.platform.api.resource.FrozenLibResourceLoader;
import net.frozenblock.lib.platform.api.resource.PackActivationType;
import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.network.chat.Component;

public final class TCAResources {
	public static boolean HAS_TRICKIER_TRIALS_PACK = false;

	public static void init() {
		var modId = TCAConstants.MOD_ID;
		FrozenLibResourceLoader.registerBuiltinPack(
			TCAConstants.id("copperier_copper"),
			modId,
			Component.translatable("pack.thecopperierage.copperier_copper"),
			PackActivationType.DEFAULT_ENABLED
		);

		FrozenLibResourceLoader.registerBuiltinPack(
			TCAConstants.id("green_copper_bulbs"),
			modId,
			Component.translatable("pack.thecopperierage.green_copper_bulbs"),
			PackActivationType.NORMAL
		);

		FrozenLibResourceLoader.registerBuiltinPack(
			TCAConstants.id("trickier_trials"),
			modId,
			Component.translatable("pack.thecopperierage.trickier_trials"),
			PackActivationType.DEFAULT_ENABLED
		);
	}
}
