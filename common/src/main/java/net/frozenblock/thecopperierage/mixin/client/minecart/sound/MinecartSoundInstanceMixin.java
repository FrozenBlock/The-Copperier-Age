/*
 * Copyright 2026 FrozenBlock
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

package net.frozenblock.thecopperierage.mixin.client.minecart.sound;

import net.frozenblock.thecopperierage.client.resources.sounds.InterpolatedAttributeSoundInstance;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.MinecartSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ClientOnly
@Mixin(MinecartSoundInstance.class)
public abstract class MinecartSoundInstanceMixin extends AbstractTickableSoundInstance implements InterpolatedAttributeSoundInstance {
	@Unique
	private float theCopperierAge$currentVolume = 0F;

	protected MinecartSoundInstanceMixin(SoundEvent event, SoundSource source, RandomSource random) {
		super(event, source, random);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void theCopperierAge$interpolateVolumeAndPitch(CallbackInfo info) {
		final boolean volumeInRecession = this.volume <= 0F;
		this.theCopperierAge$currentVolume += (this.volume - this.theCopperierAge$currentVolume) * (volumeInRecession ? 0.6F : 0.4F);
	}

	@Unique
	@Override
	public boolean theCopperierAge$interpolateVolume() {
		return true;
	}

	@Unique
	@Override
	public float theCopperierAge$interpolatedVolume() {
		return this.theCopperierAge$interpolateVolume()
			? this.theCopperierAge$currentVolume
			: this.volume;
	}
}
