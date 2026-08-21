package net.frozenblock.thecopperierage.mod_compat.audioplayer.platform;

import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.thecopperierage.mod_compat.audioplayer.AudioPlayerCompat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class AudioPlayerIntegrationImpl {
	private static final boolean LOADED = FabricLoader.getInstance().isModLoaded("audioplayer");

	private AudioPlayerIntegrationImpl() {}

	public static void init() {
		if (LOADED) AudioPlayerCompat.init();
	}

	public static boolean startMusicDisc(ServerLevel level, Entity source, ItemStack record, @Nullable ServerPlayer causedBy) {
		return LOADED && AudioPlayerCompat.startMusicDisc(level, source, record, causedBy);
	}

	public static boolean isStopped(Entity source) {
		return !LOADED || AudioPlayerCompat.isStopped(source);
	}

	public static void stop(Entity source) {
		if (LOADED) AudioPlayerCompat.stop(source);
	}
}
