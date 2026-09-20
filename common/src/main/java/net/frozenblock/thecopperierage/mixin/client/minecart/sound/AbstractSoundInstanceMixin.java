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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.frozenblock.thecopperierage.client.resources.sounds.InterpolatedAttributeSoundInstance;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@ClientOnly
@Mixin(AbstractSoundInstance.class)
public abstract class AbstractSoundInstanceMixin {

	@ModifyExpressionValue(
		method = "getVolume",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/resources/sounds/AbstractSoundInstance;volume:F",
			opcode = Opcodes.GETFIELD
		)
	)
	public float theCopperierAge$getInterpolatedVolume(float original) {
		if (AbstractSoundInstance.class.cast(this) instanceof InterpolatedAttributeSoundInstance soundInterface) {
			if (soundInterface.theCopperierAge$interpolateVolume()) return soundInterface.theCopperierAge$interpolatedVolume();
		}

		return original;
	}
}
