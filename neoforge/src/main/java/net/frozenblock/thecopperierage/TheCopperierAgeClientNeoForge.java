package net.frozenblock.thecopperierage;

import net.frozenblock.thecopperierage.config.gui.TCAConfigGui;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = TCAConstants.MOD_ID, dist = Dist.CLIENT)
public class TheCopperierAgeClientNeoForge {

	public TheCopperierAgeClientNeoForge(IEventBus modBus) {
		TheCopperierAgeClient.init();

		modBus.addListener(FMLClientSetupEvent.class, event -> {
			TheCopperierAgeClient.setup();
		});

		ModLoadingContext.get().registerExtensionPoint(
			IConfigScreenFactory.class,
			() -> (container, parent) ->
				TCAConfigGui.buildScreen(parent)
		);
	}
}
