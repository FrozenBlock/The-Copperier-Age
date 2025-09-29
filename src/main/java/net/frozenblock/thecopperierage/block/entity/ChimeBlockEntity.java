/*
 * Copyright 2025 FrozenBlock
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

package net.frozenblock.thecopperierage.block.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.frozenblock.lib.wind.api.WindManager;
import net.frozenblock.lib.wind.client.impl.ClientWindManager;
import net.frozenblock.thecopperierage.networking.packet.TCAChimeInfluencePacket;
import net.frozenblock.thecopperierage.registry.TCABlockEntityTypes;
import net.frozenblock.thecopperierage.registry.TCASounds;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ChimeBlockEntity extends BlockEntity {
	private static final float MAX_BLOCKS_PER_SECOND = 22F;
	private static final float MAX_BLOCKS_PER_SECOND_IN_TICKS = MAX_BLOCKS_PER_SECOND / 20F;
	private static final float BLOCKS_PER_SECOND_TO_VIBRATION = 22F / 15F;
	private final List<AbstractInfluence> influences;
	public final float animationOffset;
	protected Vec3 prevInfluence = Vec3.ZERO;
	protected Vec3 influence = Vec3.ZERO;
	public int age;
	public float prevAccumulatedStrength;
	public float accumulatedStrength;

	public ChimeBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		super(TCABlockEntityTypes.CHIME, pos, state);
		this.animationOffset = (float) pos.getX() * 6F + pos.getY() * 6F + pos.getZ() * 6F;
		this.influences = new ArrayList<>();
		this.influences.add(new WindInfluence());
	}

	public static void tick(Level level, BlockPos pos, @NotNull BlockState state, @NotNull ChimeBlockEntity chime) {
		chime.prevInfluence = chime.influence;
		chime.influences.forEach(influence -> influence.tick(level, pos));
		chime.influences.removeIf(AbstractInfluence::shouldRemove);
		chime.influence = chime.getAverageInfluence();

		chime.prevAccumulatedStrength = chime.accumulatedStrength;
		chime.accumulatedStrength += (float) (chime.influence.length()) * 0.5F;

		chime.age += 1;
	}

	public void addInfluence(
		@NotNull Level level,
		BlockPos pos,
		BlockState state,
		Vec3 influence,
		double scalePerTick,
		boolean playsSound,
		boolean sendPacket
	) {
		this.influences.add(new VectorBasedInfluence(influence, scalePerTick));
		if (sendPacket && level instanceof ServerLevel serverLevel) TCAChimeInfluencePacket.sendToAll(serverLevel, pos, influence, scalePerTick, null);
		if (level.isClientSide() || !playsSound) return;

		final float influenceSpeed = Math.clamp((float) this.getAverageInfluence().length(), 0.02F, 1.2F);
		final float volume = Mth.lerp(influenceSpeed, 0.1F, 0.6F);
		final float pitch = Mth.lerp(influenceSpeed, 0.75F, 1.2F);
		level.playSound(null, pos, TCASounds.BLOCK_CHIME_DISTURB, SoundSource.BLOCKS, volume, pitch);

		level.gameEvent(getResonanceEventByFrequency((int) (Math.min(influenceSpeed, 1F) * 15)), pos, GameEvent.Context.of(state));
	}

	public boolean addEntityInfluence(
		@NotNull Level level,
		BlockPos pos,
		BlockState state,
		Entity entity,
		Vec3 influence,
		double scalePerTick,
		boolean cancelIfSoundCannotPlay,
		boolean sendPacket
	) {
		List<AbstractInfluence> entityInfluences = this.influences
			.stream()
			.filter(abstractInfluence -> {
				return abstractInfluence instanceof EntityInfluence entityInfluence
					&& entityInfluence.isSoundBearing()
					&& entityInfluence.getTicksSinceStart() <= 30;
			})
			.toList();

		final boolean canPlaySound = entityInfluences.isEmpty();
		if (cancelIfSoundCannotPlay && !canPlaySound) return false;
		this.influences.add(new EntityInfluence(entity, influence, scalePerTick, canPlaySound));

		if (canPlaySound) {
			final float influenceSpeed = Math.clamp((float) this.getAverageInfluence().length(), 0.01F, 1.2F);
			final float volume = Mth.lerp(influenceSpeed, 0.02F, 0.6F);
			final float pitch = Mth.lerp(influenceSpeed, 0.75F, 1.2F);
			level.playSound(entity, pos, TCASounds.BLOCK_CHIME_DISTURB, SoundSource.BLOCKS, volume, pitch);

			// We are measuring with a max of 22 Blocks per second- creative sprint flying, Thanks, wiki!
			final float speedInBlocksPerSecond = Math.min(influenceSpeed, MAX_BLOCKS_PER_SECOND_IN_TICKS) * 20F;
			final float vibrationStrength = speedInBlocksPerSecond / BLOCKS_PER_SECOND_TO_VIBRATION;
			level.gameEvent(getResonanceEventByFrequency(vibrationStrength), pos, GameEvent.Context.of(entity, state));
		}

		if (sendPacket && level instanceof ServerLevel serverLevel) TCAChimeInfluencePacket.sendToAll(serverLevel, pos, influence, scalePerTick, entity);

		return canPlaySound;
	}

	private @NotNull ResourceKey<GameEvent> getResonanceEventByFrequency(float frequency) {
		return VibrationSystem.getResonanceEventByFrequency(Math.clamp((int) frequency, 1, 15));
	}

	public void addClientInfluence(@NotNull Level level, Vec3 influence, double scaleEachTick, Optional<Integer> entityID) {
		if (!level.isClientSide()) return;
		final Entity entity = entityID.map(level::getEntity).orElse(null);
		if (entity != null) {
			this.influences.add(new EntityInfluence(entity, influence, scaleEachTick, false));
		} else {
			this.influences.add(new VectorBasedInfluence(influence, scaleEachTick));
		}
	}

	public Vec3 getAverageInfluence() {
		double x = 0D;
		double y = 0D;
		double z = 0D;
		double sumOfWeights = 0D;

		for (AbstractInfluence influence : this.influences) {
			final Vec3 movement = influence.getInfluence();
			final double weight = movement.length();
			sumOfWeights += weight;
			x += weight * movement.x;
			y += weight * movement.y;
			z += weight * movement.z;
		}

		if (sumOfWeights <= 0) return Vec3.ZERO;

		final double finalX = x / sumOfWeights;
		final double finalY = y / sumOfWeights;
		final double finalZ = z / sumOfWeights;

		return new Vec3(finalX, finalY, finalZ);
	}

	public Vec3 getInfluence(float partialTick) {
		return Mth.lerp(partialTick, this.prevInfluence, this.influence);
	}

	public abstract static class AbstractInfluence {
		public abstract Vec3 getInfluence();
		public abstract void tick(Level level, BlockPos pos);
		public abstract boolean shouldRemove();
	}

	public static class WindInfluence extends AbstractInfluence {
		private Vec3 wind;
		private boolean isFirstTick = true;

		public WindInfluence() {
			this.wind = Vec3.ZERO;
		}

		@Override
		public void tick(Level level, BlockPos pos) {
			final Vec3 targetWind = this.getWind(level, pos);
			this.wind = this.wind.add(targetWind.subtract(this.wind).scale(this.isFirstTick ? 0.9D : 0.025D));
			this.isFirstTick = false;
		}

		@Override
		public boolean shouldRemove() {
			return false;
		}

		private Vec3 getWind(@NotNull Level level, BlockPos pos) {
			if (level.isClientSide()) return ClientWindManager.getWindMovement(level, pos.getCenter(), 1D, 1D, 2D);
			if (!(level instanceof ServerLevel serverLevel)) return Vec3.ZERO;
			return WindManager.getOrCreateWindManager(serverLevel).getWindMovement(pos.getCenter(), 1D, 1D, 2D);
		}

		@Override
		public Vec3 getInfluence() {
			return this.wind;
		}
	}

	public static class VectorBasedInfluence extends AbstractInfluence {
		private Vec3 influence;
		private final double scalePerTick;

		public VectorBasedInfluence(Vec3 influence, double scalePerTick) {
			this.influence = influence;
			this.scalePerTick = scalePerTick;
		}

		@Override
		public void tick(Level level, BlockPos pos) {
			this.influence = this.influence.scale(this.scalePerTick);

			if (this.influence.length() < 0.02D) this.influence = Vec3.ZERO;
		}

		@Override
		public boolean shouldRemove() {
			return this.influence.length() <= 0D;
		}

		@Override
		public Vec3 getInfluence() {
			return this.influence;
		}
	}

	public static class EntityInfluence extends VectorBasedInfluence {
		private final Entity entity;
		private final boolean soundBearing;
		private int ticksSinceStart;

		public EntityInfluence(Entity entity, Vec3 influence, double scalePerTick, boolean soundBearing) {
			super(influence, scalePerTick);
			this.entity = entity;
			this.soundBearing = soundBearing;
		}

		@Override
		public void tick(Level level, BlockPos pos) {
			this.ticksSinceStart += 1;
			super.tick(level, pos);
		}

		public Entity getEntity() {
			return this.entity;
		}

		public boolean isSoundBearing() {
			return this.soundBearing;
		}

		public int getTicksSinceStart() {
			return this.ticksSinceStart;
		}
	}

}
