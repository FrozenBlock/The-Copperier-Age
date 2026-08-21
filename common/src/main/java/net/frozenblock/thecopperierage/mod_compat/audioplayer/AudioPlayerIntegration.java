package net.frozenblock.thecopperierage.mod_compat.audioplayer;

import net.mehvahdjukaar.candlelight.api.PlatformImpl;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class AudioPlayerIntegration {

	private AudioPlayerIntegration() {}

	@PlatformImpl
	public static void init() {
		throw new AssertionError();
	}

	@PlatformImpl
	public static boolean startMusicDisc(ServerLevel level, Entity source, ItemStack record, @Nullable ServerPlayer causedBy) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static boolean isStopped(Entity source) {
		throw new AssertionError();
	}

	@PlatformImpl
	public static void stop(Entity source) {
		throw new AssertionError();
	}
}
