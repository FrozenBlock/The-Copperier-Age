/*
 * Copyright 2025-2026 FrozenBlock
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

package net.frozenblock.thecopperierage.mixin.client.waxed_items;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.item.api.OxidizableItemHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {

	@Shadow
	public abstract void blitSprite(RenderPipeline renderPipeline, ResourceLocation resourceLocation, int i, int j, int k, int l);

	@Unique
	private static final ResourceLocation THECOPPERIERAGE$WAXED_OVERLAY = TCAConstants.id("container/slot_waxed_overlay");

	@Inject(
		method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/render/state/GuiRenderState;submitItem(Lnet/minecraft/client/gui/render/state/GuiItemRenderState;)V",
			shift = At.Shift.AFTER
		)
	)
	private void theCopperierAge$blitWaxedOverlay(LivingEntity owner, Level level, ItemStack stack, int x, int y, int seed, CallbackInfo info) {
		// TODO: config
		if (!OxidizableItemHelper.isWaxed(stack)) return;
		this.blitSprite(RenderPipelines.GUI_TEXTURED, THECOPPERIERAGE$WAXED_OVERLAY, x - 2, y - 2, 20, 20);
	}

}
