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

package net.frozenblock.thecopperierage.mixin.client.copper_fire;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.particle.impl.CopperLavaParticleInterface;
import net.frozenblock.thecopperierage.registry.TCAParticleTypes;
import net.minecraft.client.particle.LavaParticle;
import net.minecraft.core.particles.SimpleParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(LavaParticle.class)
public class LavaParticleMixin implements CopperLavaParticleInterface {

	@Unique
	private boolean theCopperierAge$isCopperLava = false;

	@Override
	public void theCopperierAge$setCopperLava(boolean copperLava) {
		this.theCopperierAge$isCopperLava = copperLava;
	}

	@ModifyExpressionValue(
		method = "tick",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/core/particles/ParticleTypes;SMOKE:Lnet/minecraft/core/particles/SimpleParticleType;"
		)
	)
	public SimpleParticleType theCopperierAge$replaceSmokeWithCopperSmoke(SimpleParticleType original) {
		if (!this.theCopperierAge$isCopperLava) return original;
		return TCAParticleTypes.COPPER_SMOKE;
	}


}
