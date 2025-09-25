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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.registry.TCABlocks;
import net.frozenblock.thecopperierage.registry.TCAParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(CampfireBlock.class)
public class CampfireBlockMixin {

	@ModifyExpressionValue(
		method = "animateTick",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/core/particles/ParticleTypes;LAVA:Lnet/minecraft/core/particles/SimpleParticleType;"
		)
	)
	private SimpleParticleType theCopperierAge$spawnCopperLava(
		SimpleParticleType original, @Local(argsOnly = true) BlockState state
	) {
		return state.is(TCABlocks.COPPER_CAMPFIRE) ? TCAParticleTypes.COPPER_LAVA : original;
	}

	@Inject(method = "makeParticles", at = @At("HEAD"))
	private static void theCopperierAge$setupCopperSmoke(
		Level level, BlockPos pos, boolean isSignalFire, boolean spawnExtraSmoke, CallbackInfo info,
		@Share("theCopperierAge$blockState") LocalRef<BlockState> state
	) {
		state.set(level.getBlockState(pos));
	}

	@ModifyExpressionValue(
		method = "makeParticles",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/core/particles/ParticleTypes;CAMPFIRE_SIGNAL_SMOKE:Lnet/minecraft/core/particles/SimpleParticleType;"
		)
	)
	private static SimpleParticleType theCopperierAge$copperSignalSmoke(
		SimpleParticleType original,
		@Share("theCopperierAge$blockState") LocalRef<BlockState> state
	) {
		return TCAConfig.COPPER_PARTICLES && state.get().is(TCABlocks.COPPER_CAMPFIRE) ? TCAParticleTypes.COPPER_CAMPFIRE_SIGNAL_SMOKE : original;
	}

	@ModifyExpressionValue(
		method = "makeParticles",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/core/particles/ParticleTypes;CAMPFIRE_COSY_SMOKE:Lnet/minecraft/core/particles/SimpleParticleType;"
		)
	)
	private static SimpleParticleType theCopperierAge$soulCosySmoke(
		SimpleParticleType original,
		@Share("theCopperierAge$blockState") LocalRef<BlockState> state
	) {
		return TCAConfig.COPPER_PARTICLES && state.get().is(TCABlocks.COPPER_CAMPFIRE) ? TCAParticleTypes.COPPER_CAMPFIRE_COSY_SMOKE : original;
	}

	@ModifyExpressionValue(
		method = "makeParticles",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/core/particles/ParticleTypes;SMOKE:Lnet/minecraft/core/particles/SimpleParticleType;"
		)
	)
	private static SimpleParticleType theCopperierAge$soulSmoke(
		SimpleParticleType original,
		@Share("theCopperierAge$blockState") LocalRef<BlockState> state
	) {
		return TCAConfig.COPPER_PARTICLES && state.get().is(TCABlocks.COPPER_CAMPFIRE) ? TCAParticleTypes.COPPER_SMOKE : original;
	}

}
