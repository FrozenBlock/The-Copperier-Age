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
