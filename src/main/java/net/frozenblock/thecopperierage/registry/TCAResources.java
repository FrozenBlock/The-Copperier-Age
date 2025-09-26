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

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.ModContainer;
import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.network.chat.Component;

public final class TCAResources {
	public static boolean HAS_TRICKIER_TRIALS_PACK = false;

	public static void init(ModContainer container) {
		ResourceManagerHelper.registerBuiltinResourcePack(
			TCAConstants.id("copperier_copper"),
			container,
			Component.translatable("pack.thecopperierage.copperier_copper"),
			ResourcePackActivationType.DEFAULT_ENABLED
		);

		ResourceManagerHelper.registerBuiltinResourcePack(
			TCAConstants.id("green_copper_bulbs"),
			container,
			Component.translatable("pack.thecopperierage.green_copper_bulbs"),
			ResourcePackActivationType.NORMAL
		);

		ResourceManagerHelper.registerBuiltinResourcePack(
			TCAConstants.id("trickier_trials"),
			container,
			Component.translatable("pack.thecopperierage.trickier_trials"),
			ResourcePackActivationType.DEFAULT_ENABLED
		);
	}
}
