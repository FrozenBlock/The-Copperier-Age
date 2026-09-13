package net.frozenblock.thecopperierage.mod_compat.audioplayer.platform;

import net.frozenblock.thecopperierage.mod_compat.audioplayer.AudioPlayerCompat;
import net.frozenblock.thecopperierage.mod_compat.audioplayer.AudioPlayerIntegration;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class AudioPlayerIntegrationImpl {

	public static void init() {
		if (AudioPlayerIntegration.LOADED) AudioPlayerCompat.init();
	}

	public static boolean startMusicDisc(ServerLevel level, Entity source, ItemStack record, @Nullable ServerPlayer causedBy) {
		return AudioPlayerIntegration.LOADED && AudioPlayerCompat.startMusicDisc(level, source, record, causedBy);
	}

	public static boolean isStopped(Entity source) {
		return !AudioPlayerIntegration.LOADED || AudioPlayerCompat.isStopped(source);
	}

	public static void stop(Entity source) {
		if (AudioPlayerIntegration.LOADED) AudioPlayerCompat.stop(source);
	}

	private AudioPlayerIntegrationImpl() {}
}
