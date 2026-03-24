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

package net.frozenblock.thecopperierage.mixin.client.copper_golem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.entity.impl.CopperGolemPressButtonInterface;
import net.minecraft.client.renderer.entity.CopperGolemRenderer;
import net.minecraft.client.renderer.entity.state.CopperGolemRenderState;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(CopperGolemRenderer.class)
public class CopperGolemRendererMixin {

	@Inject(
		method = "extractRenderState(Lnet/minecraft/world/entity/animal/golem/CopperGolem;Lnet/minecraft/client/renderer/entity/state/CopperGolemRenderState;F)V",
		at = @At("TAIL")
	)
	public void theCopperierAge$extractRenderState(CopperGolem copperGolem, CopperGolemRenderState renderState, float partialTicks, CallbackInfo info) {
		if (!(copperGolem instanceof CopperGolemPressButtonInterface copperGolemInterface)) return;
		if (!(renderState instanceof CopperGolemPressButtonInterface renderStateInterface)) return;
		renderStateInterface.theCopperierAge$getPressingButtonAnimationState().copyFrom(copperGolemInterface.theCopperierAge$getPressingButtonAnimationState());
	}

}
