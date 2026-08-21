package net.frozenblock.thecopperierage.mod_compat.audioplayer.platform;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

// no-op
public final class AudioPlayerIntegrationImpl {

	private AudioPlayerIntegrationImpl() {}

	public static void init() {}

	public static boolean startMusicDisc(ServerLevel level, Entity source, ItemStack record, @Nullable ServerPlayer causedBy) {
		return false;
	}

	public static boolean isStopped(Entity source) {
		return true;
	}

	public static void stop(Entity source) {}
}
