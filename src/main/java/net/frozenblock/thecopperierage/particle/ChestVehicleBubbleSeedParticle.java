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

package net.frozenblock.thecopperierage.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.entity.impl.ChestVehicleBubbleInterface;
import net.frozenblock.thecopperierage.mod_compat.TCAModIntegrations;
import net.frozenblock.thecopperierage.particle.options.ChestVehicleBubbleSeedParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class ChestVehicleBubbleSeedParticle extends NoRenderParticle {
	private final VehicleEntity vehicleEntity;

	ChestVehicleBubbleSeedParticle(
		ClientLevel level,
		double x, double y, double z,
		int entityId
	) {
		super(level, x, y, z, 0D, 0D, 0D);
		this.lifetime = 5;

		final Entity entity = level.getEntity(entityId);
		if (entity instanceof VehicleEntity vehicle) {
			this.vehicleEntity = vehicle;
		} else {
			this.vehicleEntity = null;
		}
	}

	@Override
	public void tick() {
		if (this.vehicleEntity == null
			|| !this.vehicleEntity.isAlive()
			|| !this.vehicleEntity.isUnderWater()
			|| !(this.vehicleEntity instanceof ChestVehicleBubbleInterface bubbleInterface)
			|| !TCAModIntegrations.WILDER_WILD_INTEGRATION.getIntegration().chestBubbling()
		) {
			this.remove();
			return;
		}

		final Vec3 chestPos = bubbleInterface.theCopperierAge$bubbleEmitPosition();
		for (int i = 0; i < this.random.nextInt(4, 10); i++) {
			final double particleX = chestPos.x + this.random.nextGaussian() * 0.1640625D;
			final double particleZ = chestPos.z + this.random.nextGaussian() * 0.1640625D;

			this.level.addParticle(
				ParticleTypes.BUBBLE,
				particleX,
				chestPos.y,
				particleZ,
				this.random.nextGaussian() * 0.2D,
				this.random.nextDouble() * 0.2D,
				this.random.nextGaussian() * 0.2D
			);
		}

		this.age++;
		if (this.age == this.lifetime) this.remove();
	}

	public record Provider(SpriteSet spriteSet) implements ParticleProvider<ChestVehicleBubbleSeedParticleOptions> {
		@Override
		public Particle createParticle(
			ChestVehicleBubbleSeedParticleOptions options,
			ClientLevel level,
			double x, double y, double z,
			double xd, double yd, double zd,
			RandomSource random
		) {
			return new ChestVehicleBubbleSeedParticle(level, x, y, z, options.entityId());
		}
	}
}
