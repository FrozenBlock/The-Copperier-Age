package net.frozenblock.thecopperierage;

import net.frozenblock.lib.menu.api.SplashTextEvents;
import net.frozenblock.thecopperierage.client.TCAModelLayers;
import net.frozenblock.thecopperierage.client.TCAParticleEngine;
import net.frozenblock.thecopperierage.client.coupling.MinecartCouplingClientHandler;
import net.frozenblock.thecopperierage.client.sound.JukeboxMinecartSoundHandler;
import net.frozenblock.thecopperierage.networking.TCAClientNetworking;
import net.frozenblock.thecopperierage.registry.TCAMenuScreens;
import net.mehvahdjukaar.candlelight.api.ClientOnly;

@ClientOnly
public class TheCopperierAgeClient {

	public static void init() {
		SplashTextEvents.ADD_SOURCE_FILES.register(sourceFiles -> sourceFiles.add(TCAConstants.id("texts/splashes.txt")));

		TCAParticleEngine.init();
		TCAModelLayers.init();
		MinecartCouplingClientHandler.init();
		JukeboxMinecartSoundHandler.init();
		TCAClientNetworking.registerPacketReceivers();
	}

	public static void setup() {
		TCAModelLayers.setup();
		TCAMenuScreens.setup();
	}
}
