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

import net.frozenblock.lib.wind.api.WindManager;
import net.frozenblock.lib.wind.client.impl.ClientWindManager;
import net.frozenblock.thecopperierage.block.ChimeBlock;
import net.frozenblock.thecopperierage.registry.TCABlockEntityTypes;
import net.frozenblock.thecopperierage.registry.TCASounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ChimeBlockEntity extends BlockEntity {
	private final List<AbstractInfluence> influences;
	protected Vec3 prevInfluence = Vec3.ZERO;
	protected Vec3 influence = Vec3.ZERO;
	public int age;

	public ChimeBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		super(TCABlockEntityTypes.CHIME, pos, state);
		this.influences = new ArrayList<>();
		this.influences.add(new WindInfluence());
	}

	public static void tick(Level level, BlockPos pos, @NotNull BlockState state, @NotNull ChimeBlockEntity chime) {
		chime.prevInfluence = chime.influence;

		chime.influences.forEach(influence -> influence.tick(level, pos));
		chime.influences.removeIf(AbstractInfluence::shouldRemove);

		chime.influence = chime.getAverageInfluence().yRot(state.getValue(ChimeBlock.FACING).toYRot() * Mth.DEG_TO_RAD);

		chime.age += 1;
	}

	public void addInfluence(@NotNull Level level, BlockPos pos, Vec3 influence, boolean playsSound) {
		if (!level.isClientSide() && playsSound) {
			final float influenceSpeed = Math.clamp((float) influence.length(), 0.02F, 1.2F);
			final float volume = Mth.lerp(influenceSpeed, 0.1F, 0.6F);
			final float pitch = Mth.lerp(influenceSpeed, 0.75F, 1.2F);
			level.playSound(null, pos, TCASounds.BLOCK_CHIME_DISTURB, SoundSource.BLOCKS, volume, pitch);
		}
		this.influences.add(new VectorBasedInfluence(influence));
	}

	public boolean addEntityInfluence(@NotNull Level level, BlockPos pos, Entity entity, Vec3 influence, boolean cancelIfSoundCannotPlay) {
		boolean playedSound = false;
		List<AbstractInfluence> entityInfluences = this.influences
			.stream()
			.filter(abstractInfluence -> {
				return abstractInfluence instanceof EntityInfluence entityInfluence
					&& entityInfluence.isSoundBearing()
					&& entityInfluence.getTicksSinceStart() <= 30;
			})
			.toList();
		if (entityInfluences.isEmpty()) {
			final float influenceSpeed = Math.clamp((float) influence.length(), 0.01F, 1.2F);
			final float volume = Mth.lerp(influenceSpeed, 0.02F, 0.6F);
			final float pitch = Mth.lerp(influenceSpeed, 0.75F, 1.2F);
			level.playSound(entity, pos, TCASounds.BLOCK_CHIME_DISTURB, SoundSource.BLOCKS, volume, pitch);
			playedSound = true;
		}
		if (cancelIfSoundCannotPlay && !playedSound) return false;
		this.influences.add(new EntityInfluence(entity, influence, playedSound));
		return playedSound || level.isClientSide();
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

	public Vec3 getLerpedInfluence(float partialTick) {
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

		public VectorBasedInfluence(Vec3 influence) {
			this.influence = influence;
		}

		@Override
		public void tick(Level level, BlockPos pos) {
			this.influence = this.influence.scale(0.96D);

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

		public EntityInfluence(Entity entity, Vec3 influence, boolean soundBearing) {
			super(influence);
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
