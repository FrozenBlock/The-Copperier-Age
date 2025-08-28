/*
 * Copyright 2025 FrozenBlock
 * This file is part of Netherier Nether.
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
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class CopperCampfireSmokeParticle extends CampfireSmokeParticle {
	private final float targetRColor;
	private final float startRColor;
	private final float targetBColor;
	private final float startBColor;
	private final int colorLerpEndsAt;
	private int colorLerpTicks;

	protected CopperCampfireSmokeParticle(
		ClientLevel level,
		double x, double y, double z,
		double xSpeed, double ySpeed, double zSpeed,
		boolean isSignal
	) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed, isSignal);
		this.targetRColor = this.rCol;
		this.rCol = this.startRColor = Math.max(this.targetRColor - 0.3F, 0F);
		this.targetBColor = this.bCol;
		this.bCol = this.startBColor = Math.max(this.targetBColor - 0.3F, 0F);
		this.colorLerpEndsAt = level.random.nextInt(20, 40);
	}

	@Override
	public void tick() {
		this.colorLerpTicks += 1;
		super.tick();
	}

	@Override
	protected int getLightColor(float partialTick) {
		final float colorLerp = Math.min((this.colorLerpTicks + partialTick), this.colorLerpEndsAt) / this.colorLerpEndsAt;
		this.rCol = Mth.lerp(colorLerp, this.startRColor, this.targetRColor);
		this.bCol = Mth.lerp(colorLerp, this.startBColor, this.targetBColor);
		return super.getLightColor(partialTick);
	}

	@Environment(EnvType.CLIENT)
	public static class CosyProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public CosyProvider(SpriteSet spriteSet) {
			this.sprites = spriteSet;
		}

		@Override
		public Particle createParticle(
			SimpleParticleType simpleParticleType,
			ClientLevel clientLevel,
			double d, double e, double f,
			double g, double h, double i
		) {
			CopperCampfireSmokeParticle particle = new CopperCampfireSmokeParticle(clientLevel, d, e, f, g, h, i, false);
			particle.setAlpha(0.9F);
			particle.pickSprite(this.sprites);
			return particle;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class SignalProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet sprites;

		public SignalProvider(SpriteSet spriteSet) {
			this.sprites = spriteSet;
		}

		@Override
		public Particle createParticle(
			SimpleParticleType simpleParticleType,
			ClientLevel clientLevel,
			double d, double e, double f,
			double g, double h, double i
		) {
			CopperCampfireSmokeParticle particle = new CopperCampfireSmokeParticle(clientLevel, d, e, f, g, h, i, true);
			particle.setAlpha(0.95F);
			particle.pickSprite(this.sprites);
			return particle;
		}
	}

}
