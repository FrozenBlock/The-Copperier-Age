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
