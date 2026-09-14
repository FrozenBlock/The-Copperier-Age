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
