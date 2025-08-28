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
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class CopperSmokeParticle extends SmokeParticle {
	private final float targetGColor;
	private final float startGColor;
	private final int colorLerpEndsAt;
	private int colorLerpTicks;

	protected CopperSmokeParticle(
		ClientLevel level,
		double x, double y, double z,
		double xSpeed, double ySpeed, double zSpeed,
		float quadSizeMultiplier,
		SpriteSet sprites
	) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed, quadSizeMultiplier, sprites);
		this.targetGColor = this.gCol;
		this.gCol = this.startGColor = Math.min(this.targetGColor + 0.2F, 1F);
		this.colorLerpEndsAt = this.lifetime;
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
		private final SpriteSet sprites;

		public Provider(SpriteSet spriteSet) {
			this.sprites = spriteSet;
		}

		@Override
		public Particle createParticle(
			SimpleParticleType simpleParticleType,
			ClientLevel clientLevel,
			double x, double y, double z,
			double xSpeed, double ySpeed, double zSpeed
		) {
			return new CopperSmokeParticle(clientLevel, x, y, z, xSpeed, ySpeed, zSpeed, 1F, this.sprites);
		}
	}

}
