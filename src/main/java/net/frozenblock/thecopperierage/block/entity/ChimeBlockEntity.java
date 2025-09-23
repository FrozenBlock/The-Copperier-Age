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
import net.frozenblock.thecopperierage.registry.TCABlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ChimeBlockEntity extends BlockEntity {
	public List<AbstractInfluence> influences;
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

		chime.influence = chime.getAverageInfluence();

		chime.age += 1;
	}

	public Vec3 getAverageInfluence() {
		double x = 0D;
		double y = 0D;
		double z = 0D;
		double sumOfWeights = 0D;

		for (AbstractInfluence influence : this.influences) {
			final Vec3 movement = influence.getInfluence();
			double weight = movement.length();
			sumOfWeights += weight;
			x += weight * movement.x;
			y += weight * movement.y;
			z += weight * movement.z;
		}

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

		public WindInfluence() {
			this.wind = Vec3.ZERO;
		}

		@Override
		public void tick(Level level, BlockPos pos) {
			final Vec3 targetWind = this.getWind(level, pos);
			this.wind = this.wind.add(targetWind.subtract(this.wind).scale(0.1D));
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

	public static class EntityInfluence extends AbstractInfluence {
		private Vec3 influence;

		public EntityInfluence(Vec3 influence) {
			this.influence = influence;
		}

		@Override
		public void tick(Level level, BlockPos pos) {
			this.influence = this.influence.scale(0.95D);

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

}
