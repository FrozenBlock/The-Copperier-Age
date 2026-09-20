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

package net.frozenblock.thecopperierage.mixin.client.minecart.camera;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.ArrayList;
import java.util.List;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@ClientOnly
@Mixin(AccessibilityOptionsScreen.class)
public class AccessibilityOptionsScreenMixin {

	@ModifyReturnValue(method = "options", at = @At("RETURN"))
	private static OptionInstance<?>[] theCopperierAge$removeVanillaMinecartRotationOption(OptionInstance<?>[] original, Options options) {
		final List<OptionInstance<?>> newOptions = new ArrayList<>();
		for (OptionInstance<?> option : original) {
			if (option != options.rotateWithMinecart()) newOptions.add(option);
		}
		return newOptions.toArray(new OptionInstance[0]);
	}

	@Inject(method = "isMinecartOptionEnabled", at = @At("HEAD"), cancellable = true)
	private void theCopperierAge$ignoreVanillaMinecartRotationOption(CallbackInfoReturnable<Boolean> info) {
		info.setReturnValue(false);
	}
}
