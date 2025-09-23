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

package net.frozenblock.thecopperierage.mixin.datagen.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.datagen.model.TCAPackModelProvider;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.renderer.block.model.VariantMutator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(VariantMutator.class)
public interface VariantMutatorMixin {

    @Inject(method = "then", at = @At("HEAD"), cancellable = true)
    private void theCopperierAge$removeButtonUVLockIfGeneratingPack(VariantMutator variantMutator, CallbackInfoReturnable<VariantMutator> info) {
		if (TCAPackModelProvider.GENERATING_COPPER_BUTTON && variantMutator == BlockModelGenerators.UV_LOCK) info.setReturnValue(VariantMutator.class.cast(this));
    }
}
