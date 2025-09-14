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

package net.frozenblock.thecopperierage.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.LargeSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

@Environment(EnvType.CLIENT)
public class LargeCopperSmokeParticle extends LargeSmokeParticle {
	private final float targetGColor;
	private final float startGColor;
	private final int colorLerpEndsAt;
	private int colorLerpTicks;

	protected LargeCopperSmokeParticle(
		ClientLevel level,
		double x, double y, double z,
		double xd, double yd, double zd,
		SpriteSet spriteSet
	) {
		super(level, x, y, z, xd, yd, zd, spriteSet);
		this.targetGColor = this.gCol;
		this.gCol = this.startGColor = Math.min(this.targetGColor + 0.2F, 1F);
		this.colorLerpEndsAt = this.lifetime / 2;
	}

	@Override
	public void tick() {
		this.colorLerpTicks += 1;
		super.tick();
	}

	@Override
	protected int getLightColor(float partialTick) {
		final float colorLerp = Math.min((this.colorLerpTicks + partialTick), this.colorLerpEndsAt) / this.colorLerpEndsAt;
		this.gCol = Mth.lerp(colorLerp, this.startGColor, this.targetGColor);
		return super.getLightColor(partialTick);
	}

	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public Provider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public Particle createParticle(
			SimpleParticleType simpleParticleType,
			ClientLevel level,
			double x, double y, double z,
			double xd, double yd, double zd,
			RandomSource random
		) {
			return new LargeCopperSmokeParticle(level, x, y, z, xd, yd, zd, this.spriteSet);
		}
	}

}
