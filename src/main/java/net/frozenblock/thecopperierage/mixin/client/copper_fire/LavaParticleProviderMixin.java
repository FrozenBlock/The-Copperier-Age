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

package net.frozenblock.thecopperierage.mixin.client.copper_fire;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.particle.impl.CopperLavaParticleInterface;
import net.frozenblock.thecopperierage.registry.TCAParticleTypes;
import net.minecraft.client.particle.LavaParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.SimpleParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(LavaParticle.Provider.class)
public class LavaParticleProviderMixin {

	@ModifyReturnValue(
		method = "createParticle(Lnet/minecraft/core/particles/SimpleParticleType;Lnet/minecraft/client/multiplayer/ClientLevel;DDDDDDLnet/minecraft/util/RandomSource;)Lnet/minecraft/client/particle/Particle;",
		at = @At("RETURN")
	)
	public Particle theCopperierAge$setAsCopperLava(
		Particle original,
		@Local(argsOnly = true) SimpleParticleType particleType
	) {
		if (particleType == TCAParticleTypes.COPPER_LAVA && original instanceof CopperLavaParticleInterface copperLavaParticleInterface) {
			copperLavaParticleInterface.theCopperierAge$setCopperLava(true);
		}
		return original;
	}

}
