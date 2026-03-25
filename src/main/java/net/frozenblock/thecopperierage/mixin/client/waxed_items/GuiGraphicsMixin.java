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
import it.unimi.dsi.fastutil.objects.Reference2ByteMap;
import it.unimi.dsi.fastutil.objects.Reference2ByteOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.item.api.OxidizableItemHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
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
	public abstract void blitSprite(RenderPipeline renderPipeline, Identifier resourceLocation, int i, int j, int k, int l);

	@Unique
	private static final Identifier THECOPPERIERAGE$WAXED_OVERLAY = TCAConstants.id("container/slot_waxed_overlay");
	@Unique
	private static final Identifier THECOPPERIERAGE$REDSTONE_OVERLAY = TCAConstants.id("container/slot_redstone_overlay");
	@Unique
	private static final Identifier THECOPPERIERAGE$INFESTED_OVERLAY = TCAConstants.id("container/slot_infested_overlay");
	@Unique
	private static final byte THECOPPERIERAGE$OVERLAY_FLAG_INFESTED = 1;
	@Unique
	private static final byte THECOPPERIERAGE$OVERLAY_FLAG_REDSTONE = 2;
	@Unique
	private static final byte THECOPPERIERAGE$OVERLAY_FLAGS_NOT_CACHED = -1;
	@Unique
	private static final Reference2ByteMap<Item> THECOPPERIERAGE$OVERLAY_FLAGS_CACHE = new Reference2ByteOpenHashMap<>();

	static {
		THECOPPERIERAGE$OVERLAY_FLAGS_CACHE.defaultReturnValue(THECOPPERIERAGE$OVERLAY_FLAGS_NOT_CACHED);
	}

	@Inject(
		method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/render/state/GuiRenderState;submitItem(Lnet/minecraft/client/gui/render/state/GuiItemRenderState;)V",
			shift = At.Shift.AFTER
		)
	)
	private void theCopperierAge$blitWaxedOverlay(LivingEntity owner, Level level, ItemStack stack, int x, int y, int seed, CallbackInfo info) {
		if (!TCAConfig.WAXED_ITEM_ICON_OVERLAY) return;
		final Item item = stack.getItem();
		if (OxidizableItemHelper.isWaxed(stack)) {
			this.blitSprite(RenderPipelines.GUI_TEXTURED, THECOPPERIERAGE$WAXED_OVERLAY, x - 3, y - 3, 24, 24);
		}
		if (TCAConfig.EXTRA_ITEM_ICON_OVERLAYS) {
			final byte overlayFlags = theCopperierAge$getOverlayFlags(item);
			if ((overlayFlags & THECOPPERIERAGE$OVERLAY_FLAG_INFESTED) != 0) {
				this.blitSprite(RenderPipelines.GUI_TEXTURED, THECOPPERIERAGE$INFESTED_OVERLAY, x - 3, y - 3, 24, 24);
			}
			if ((overlayFlags & THECOPPERIERAGE$OVERLAY_FLAG_REDSTONE) != 0) {
				this.blitSprite(RenderPipelines.GUI_TEXTURED, THECOPPERIERAGE$REDSTONE_OVERLAY, x - 3, y - 3, 24, 24);
			}
		}
	}

	@Unique
	private static byte theCopperierAge$getOverlayFlags(Item item) {
		byte overlayFlags = THECOPPERIERAGE$OVERLAY_FLAGS_CACHE.getByte(item);
		if (overlayFlags != THECOPPERIERAGE$OVERLAY_FLAGS_NOT_CACHED) {
			return overlayFlags;
		}

		overlayFlags = 0;
		if (item instanceof BlockItem blockItem) {
			if (blockItem.getBlock() instanceof InfestedBlock) {
				overlayFlags |= THECOPPERIERAGE$OVERLAY_FLAG_INFESTED;
			}
			if (blockItem.getBlock() instanceof TrappedChestBlock) {
				overlayFlags |= THECOPPERIERAGE$OVERLAY_FLAG_REDSTONE;
			}
		}

		THECOPPERIERAGE$OVERLAY_FLAGS_CACHE.put(item, overlayFlags);
		return overlayFlags;
	}
}
