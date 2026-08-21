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

package net.frozenblock.thecopperierage.mod_compat.audioplayer;

import de.maxhenkel.audioplayer.api.AudioPlayerApi;
import de.maxhenkel.audioplayer.api.AudioPlayerConstants;
import de.maxhenkel.audioplayer.api.AudioPlayerModule;
import de.maxhenkel.audioplayer.api.ChannelReference;
import de.maxhenkel.audioplayer.api.data.AudioData;
import de.maxhenkel.audioplayer.api.data.ModuleAccessor;
import de.maxhenkel.audioplayer.api.events.AudioEvents;
import de.maxhenkel.audioplayer.api.events.GetDistanceEvent;
import de.maxhenkel.audioplayer.api.events.GetSoundIdEvent;
import de.maxhenkel.audioplayer.api.events.PlayEvent;
import de.maxhenkel.audioplayer.api.events.PostPlayEvent;
import de.maxhenkel.audioplayer.api.exceptions.ChannelAlreadyOverriddenException;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.LocationalAudioChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.frozenblock.lib.event.api.events.EntityLifecycleEvents;
import net.frozenblock.lib.event.api.events.LifecycleEvents;
import net.frozenblock.lib.event.api.events.TickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class AudioPlayerCompat {
	private static final float DEFAULT_RANGE = 65F;
	private static final int LOAD_TIMEOUT_TICKS = 600;
	private static final Map<UUID, TrackedAudio> TRACKED = new HashMap<>();

	private AudioPlayerCompat() {}

	public static void init() {
		TickEvents.END_SERVER_TICK.register(server -> tick());
		EntityLifecycleEvents.ENTITY_UNLOAD.register((entity, level) -> stop(entity));
		LifecycleEvents.SERVER_STOPPED.register(server -> stopAll());
	}

	public static boolean startMusicDisc(ServerLevel level, Entity source, ItemStack record, @Nullable ServerPlayer causedBy) {
		final AudioPlayerApi api = AudioPlayerApi.instance();
		final AudioData data = api.getAudioData(record).orElse(null);
		if (data == null) return false;

		final GetSoundIdEvent soundIdEvent = new SoundIdEvent(
			data, data.getModule(AudioPlayerModule.KEY).map(AudioPlayerModule::getSoundId).orElse(null)
		);
		AudioEvents.GET_SOUND_ID.invoker().accept(soundIdEvent);
		if (soundIdEvent.getSoundId() == null) return false;

		stop(source);

		final Vec3 position = source.getBoundingBox().getCenter();
		final Float discRange = data.getRange();
		final GetDistanceEvent distanceEvent = new DistanceEvent(data, DEFAULT_RANGE, discRange != null ? discRange : DEFAULT_RANGE, position);
		AudioEvents.GET_DISTANCE.invoker().accept(distanceEvent);

		final MusicDiscPlayEvent playEvent = new MusicDiscPlayEvent(
			data, level, causedBy, soundIdEvent.getSoundId(), DEFAULT_RANGE, distanceEvent.getDistance(), AudioPlayerConstants.MUSIC_DISC_CATEGORY, position
		);
		AudioEvents.PLAY_MUSIC_DISC.invoker().accept(playEvent);
		if (playEvent.isCancelled()) return false;

		ChannelReference<?> channel = playEvent.overrideChannel;
		if (channel == null) {
			channel = api.playLocational(
				level, playEvent.getPosition(), playEvent.getSoundId(), causedBy, playEvent.getDistance(), playEvent.getCategory()
			);
		}
		if (channel == null) return false;

		AudioEvents.POST_PLAY_MUSIC_DISC.invoker().accept(new MusicDiscPostPlayEvent(channel, playEvent));
		TRACKED.put(source.getUUID(), new TrackedAudio(source, channel));
		return true;
	}

	public static boolean isStopped(Entity source) {
		return !TRACKED.containsKey(source.getUUID());
	}

	public static void stop(Entity source) {
		final TrackedAudio tracked = TRACKED.remove(source.getUUID());
		if (tracked != null) tracked.channel.stopPlaying();
	}

	private static void tick() {
		if (TRACKED.isEmpty()) return;
		final VoicechatServerApi voicechat = AudioPlayerApi.instance().getVoicechatServerApi();
		TRACKED.values().removeIf(tracked -> tracked.tickAndShouldDrop(voicechat));
	}

	private static void stopAll() {
		TRACKED.values().forEach(tracked -> tracked.channel.stopPlaying());
		TRACKED.clear();
	}

	private static final class TrackedAudio {
		private final Entity source;
		private final ChannelReference<?> channel;
		private int ticksWaitingToStart;

		private TrackedAudio(Entity source, ChannelReference<?> channel) {
			this.source = source;
			this.channel = channel;
		}

		private boolean tickAndShouldDrop(@Nullable VoicechatServerApi voicechat) {
			if (this.source.isRemoved()) {
				this.channel.stopPlaying();
				return true;
			}
			if (this.channel.isStopped()) return true;
			if (!this.channel.isInitialized() && ++this.ticksWaitingToStart > LOAD_TIMEOUT_TICKS) return true;

			if (voicechat == null || !(this.channel.getChannel() instanceof LocationalAudioChannel locational)) return false;
			final Vec3 center = this.source.getBoundingBox().getCenter();
			locational.updateLocation(voicechat.createPosition(center.x, center.y, center.z));
			return false;
		}
	}

	private static final class SoundIdEvent implements GetSoundIdEvent {
		private final AudioData data;
		@Nullable
		private UUID soundId;

		private SoundIdEvent(AudioData data, @Nullable UUID soundId) {
			this.data = data;
			this.soundId = soundId;
		}

		@Override
		public ModuleAccessor getData() {
			return this.data;
		}

		@Nullable
		@Override
		public UUID getSoundId() {
			return this.soundId;
		}

		@Override
		public void setSoundId(UUID soundId) {
			this.soundId = soundId;
		}
	}

	private static final class DistanceEvent implements GetDistanceEvent {
		private final AudioData data;
		private final float defaultDistance;
		private final float itemDistance;
		private final Vec3 position;
		private float distance;

		private DistanceEvent(AudioData data, float defaultDistance, float itemDistance, Vec3 position) {
			this.data = data;
			this.defaultDistance = defaultDistance;
			this.itemDistance = itemDistance;
			this.position = position;
			this.distance = itemDistance;
		}

		@Override
		public ModuleAccessor getData() {
			return this.data;
		}

		@Override
		public float getDefaultDistance() {
			return this.defaultDistance;
		}

		@Override
		public float getItemDistance() {
			return this.itemDistance;
		}

		@Override
		public void setDistance(float distance) {
			this.distance = distance;
		}

		@Override
		public float getDistance() {
			return this.distance;
		}

		@Override
		public Vec3 getPosition() {
			return this.position;
		}
	}

	private static final class MusicDiscPlayEvent implements PlayEvent {
		private final AudioData data;
		private final ServerLevel level;
		@Nullable
		private final ServerPlayer player;
		private final float defaultDistance;
		@Nullable
		private ChannelReference<?> overrideChannel;
		private UUID soundId;
		private float distance;
		private String category;
		private Vec3 position;
		private boolean cancelled;

		private MusicDiscPlayEvent(
			AudioData data,
			ServerLevel level,
			@Nullable ServerPlayer player,
			UUID soundId,
			float defaultDistance,
			float distance,
			String category,
			Vec3 position
		) {
			this.data = data;
			this.level = level;
			this.player = player;
			this.soundId = soundId;
			this.defaultDistance = defaultDistance;
			this.distance = distance;
			this.category = category;
			this.position = position;
		}

		@Override
		public ModuleAccessor getData() {
			return this.data;
		}

		@Override
		public void overrideChannel(ChannelReference<?> channel) throws ChannelAlreadyOverriddenException {
			if (this.overrideChannel != null) {
				throw new ChannelAlreadyOverriddenException(
					"Channel already overridden with audio ID %s".formatted(this.overrideChannel.getAudioId())
				);
			}
			this.overrideChannel = channel;
		}

		@Override
		public boolean isOverridden() {
			return this.overrideChannel != null;
		}

		@Override
		public void setSoundId(UUID soundId) {
			this.soundId = soundId;
		}

		@Override
		public UUID getSoundId() {
			return this.soundId;
		}

		@Override
		public void setCategory(String category) {
			this.category = category;
		}

		@Override
		public String getCategory() {
			return this.category;
		}

		@Override
		public void setPosition(Vec3 position) {
			this.position = position;
		}

		@Override
		public Vec3 getPosition() {
			return this.position;
		}

		@Override
		public ServerLevel getLevel() {
			return this.level;
		}

		@Nullable
		@Override
		public ServerPlayer getPlayer() {
			return this.player;
		}

		@Override
		public float getDefaultDistance() {
			return this.defaultDistance;
		}

		@Override
		public void setDistance(float distance) {
			this.distance = distance;
		}

		@Override
		public float getDistance() {
			return this.distance;
		}

		@Override
		public void cancel() {
			this.cancelled = true;
		}

		@Override
		public boolean isCancelled() {
			return this.cancelled;
		}
	}

	private record MusicDiscPostPlayEvent(ChannelReference<?> channel, MusicDiscPlayEvent playEvent) implements PostPlayEvent {
		@Override
		public ChannelReference<?> getChannel() {
			return this.channel;
		}

		@Override
		public ModuleAccessor getData() {
			return this.playEvent.getData();
		}

		@Override
		public UUID getSoundId() {
			return this.playEvent.getSoundId();
		}

		@Override
		public String getCategory() {
			return this.playEvent.getCategory();
		}

		@Override
		public Vec3 getPosition() {
			return this.playEvent.getPosition();
		}

		@Override
		public ServerLevel getLevel() {
			return this.playEvent.getLevel();
		}

		@Nullable
		@Override
		public ServerPlayer getPlayer() {
			return this.playEvent.getPlayer();
		}

		@Override
		public float getDistance() {
			return this.playEvent.getDistance();
		}
	}
}
