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

import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.block.entity.inventory.CrateMenu;
import net.frozenblock.thecopperierage.block.entity.inventory.KilnMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public final class TCAMenuTypes {
	public static final MenuType<CrateMenu> CRATE = register("crate", CrateMenu::create);
	public static final MenuType<KilnMenu> KILN = register("kiln", KilnMenu::new);

	public static void init() {
		TCAConstants.logWithModId("Registering MenuTypes for", TCAConstants.UNSTABLE_LOGGING);
	}

	private static <T extends AbstractContainerMenu> MenuType<T> register(String id, MenuType.MenuSupplier<T> supplier) {
		return Registry.register(BuiltInRegistries.MENU, TCAConstants.id(id), new MenuType<>(supplier, FeatureFlags.VANILLA_SET));
	}

	private static <T extends AbstractContainerMenu> MenuType<T> register(String id, MenuType.MenuSupplier<T> supplier, FeatureFlag... flags) {
		return Registry.register(BuiltInRegistries.MENU, TCAConstants.id(id), new MenuType<>(supplier, FeatureFlags.REGISTRY.subset(flags)));
	}
}
