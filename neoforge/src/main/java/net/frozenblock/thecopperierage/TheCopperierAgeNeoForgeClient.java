/*
 * Copyright 2026 FrozenBlock
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

package net.frozenblock.thecopperierage;

import net.frozenblock.lib.platform.ModLoader;
import net.frozenblock.thecopperierage.config.gui.TCAConfigGui;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = TCAConstants.MOD_ID, dist = Dist.CLIENT)
public class TheCopperierAgeNeoForgeClient {

	public TheCopperierAgeNeoForgeClient(IEventBus modBus) {
		TheCopperierAgeClient.init();

		// AFTER register event
		modBus.addListener(FMLClientSetupEvent.class, event -> {
			TheCopperierAgeClient.setup();
		});

		if (ModLoader.isModLoaded("cloth-config") || ModLoader.isModLoaded("cloth_config")) {
			ModLoadingContext.get().registerExtensionPoint(
				IConfigScreenFactory.class,
				() -> (container, parent) -> TCAConfigGui.buildScreen(parent)
			);
		}
	}
}
