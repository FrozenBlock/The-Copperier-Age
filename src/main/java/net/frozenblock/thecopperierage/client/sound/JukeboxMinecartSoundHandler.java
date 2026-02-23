/*
 * Copyright 2025-2026 FrozenBlock
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

package net.frozenblock.thecopperierage.client.sound;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.frozenblock.thecopperierage.entity.MinecartJukebox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.JukeboxSong;

@Environment(EnvType.CLIENT)
public final class JukeboxMinecartSoundHandler {
	private static final float VOLUME = 4F;
	private static final float PITCH = 1F;
	private static final Map<Integer, PlayingSong> PLAYING_SONGS = new HashMap<>();

	public static void init() {
		ClientTickEvents.END_CLIENT_TICK.register(JukeboxMinecartSoundHandler::tick);
	}

	private static void tick(Minecraft minecraft) {
		if (minecraft.level == null) {
			stopAll(minecraft.getSoundManager());
			return;
		}

		final SoundManager soundManager = minecraft.getSoundManager();
		final Set<Integer> activeSongMinecartIds = new HashSet<>();
		for (Entity entity : minecraft.level.entitiesForRendering()) {
			if (!(entity instanceof MinecartJukebox minecart) || !minecart.isSongPlaying() || minecart.isSongSilent()) continue;

			final Optional<Holder<JukeboxSong>> song = minecart.getSong();
			if (song.isEmpty()) continue;

			activeSongMinecartIds.add(minecart.getId());

			final PlayingSong current = PLAYING_SONGS.get(minecart.getId());
			// Skip if song is still playing.
			if (current != null && current.song().equals(song.get())) continue;
			// Stop is song has changed.
			if (current != null) soundManager.stop(current.soundInstance());

			final SoundInstance sound = new EntityBoundSoundInstance(
				song.get().value().soundEvent().value(),
				SoundSource.RECORDS,
				VOLUME,
				PITCH,
				minecart,
				minecart.level().random.nextLong()
			);

			soundManager.play(sound);
			PLAYING_SONGS.put(minecart.getId(), new PlayingSong(song.get(), sound));
		}

		PLAYING_SONGS.entrySet().removeIf(entry -> {
			if (activeSongMinecartIds.contains(entry.getKey())) return false;
			soundManager.stop(entry.getValue().soundInstance());
			return true;
		});
	}

	private static void stopAll(SoundManager soundManager) {
		for (PlayingSong playingSong : PLAYING_SONGS.values()) {
			soundManager.stop(playingSong.soundInstance());
		}

		PLAYING_SONGS.clear();
	}

	private record PlayingSong(Holder<JukeboxSong> song, SoundInstance soundInstance) {
	}
}
