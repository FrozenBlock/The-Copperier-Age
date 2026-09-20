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

import net.frozenblock.thecopperierage.item.api.OxidizableTooltipHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.tooltip.TooltipLocation;
import net.neoforged.neoforge.event.RegisterTooltipAppendersEvent;

@Mod(TCAConstants.MOD_ID)
public final class TheCopperierAgeNeoForge {

	public TheCopperierAgeNeoForge(IEventBus modBus) {
		TheCopperierAge.init();

		modBus.addListener(FMLCommonSetupEvent.class, event -> {
			TheCopperierAge.setup();
		});

		modBus.addListener(RegisterTooltipAppendersEvent.class, event ->
			event.registerAppender(TooltipLocation.POST_CUSTOM, OxidizableTooltipHelper::addWeatheringAndWaxedTooltips)
		);
	}
}
