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

package net.frozenblock.thecopperierage.client.gui.screens.inventory;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.block.CopperCrateBlock;
import net.frozenblock.thecopperierage.block.entity.inventory.CrateSlot;
import net.frozenblock.thecopperierage.registry.TCASounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class CrateScreen extends ContainerScreen {
	private static final ResourceLocation BLOCKED_SLOT_SPRITE = TCAConstants.id("container/crate/blocked_slot");
	private final Player player;

	public CrateScreen(ChestMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
		this.player = inventory.player;
	}

	private ItemStack getHoveredItem() {
		final Slot hoveredSlot = this.hoveredSlot;
		if (hoveredSlot != null) return hoveredSlot.getItem();
		return null;
	}

	private boolean shouldForceBlock(ItemStack slotStack, ItemStack stack) {
		return slotStack.has(DataComponents.BUNDLE_CONTENTS) && stack != slotStack && stack != null && !stack.isEmpty() && !stack.has(DataComponents.BUNDLE_CONTENTS);
	}

	@Override
	public void renderSlot(GuiGraphics guiGraphics, Slot slot) {
		renderBlockedSlot: {
			if (!(slot instanceof CrateSlot crateSlot)) break renderBlockedSlot;

			ItemStack selectedStack = this.menu.getCarried();
			if (selectedStack == null || selectedStack.isEmpty()) selectedStack = this.getHoveredItem();

			final ItemStack slotStack = slot.getItem();
			boolean forceBlock = this.shouldForceBlock(slotStack, selectedStack);
			if (!forceBlock && !slotStack.isEmpty()) break renderBlockedSlot;

			final CopperCrateBlock.SlotResult slotResult = forceBlock
				? CopperCrateBlock.SlotResult.FAILURE_CONTAINER_ITEM
				: CopperCrateBlock.verifyStackForPlacement(selectedStack, this.menu.getContainer());
			if (slotResult.isSuccess() || slotResult.isEmptyItem()) break renderBlockedSlot;

			this.renderBlockedSlot(guiGraphics, crateSlot);
			return;
		}

		super.renderSlot(guiGraphics, slot);
	}

	private void renderBlockedSlot(GuiGraphics guiGraphics, CrateSlot slot) {
		guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, BLOCKED_SLOT_SPRITE, slot.x - 1, slot.y - 1, 18, 18);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int x, int y, float partialTick) {
		super.render(guiGraphics, x, y, partialTick);

		renderTooltip: {
			if (!(this.hoveredSlot instanceof CrateSlot) || this.player.isSpectator()) break renderTooltip;

			final ItemStack carried = this.menu.getCarried();
			final CopperCrateBlock.SlotResult slotResult = CopperCrateBlock.verifyStackForPlacement(carried, this.menu.getContainer());
			if (slotResult.isSuccess()) break renderTooltip;

			final Optional<Component> tooltip = slotResult.getTooltip();
			tooltip.ifPresent(component -> guiGraphics.setTooltipForNextFrame(this.font, component, x, y));
		}
	}

	@Override
	protected void slotClicked(Slot slot, int x, int y, ClickType type) {
		playBlockedSlotSound: {
			if (!(slot instanceof CrateSlot) || slot.hasItem()) break playBlockedSlotSound;

			final ItemStack carried = this.menu.getCarried();
			final CopperCrateBlock.SlotResult slotResult = CopperCrateBlock.verifyStackForPlacement(carried, this.menu.getContainer());
			if (slotResult.isSuccess() || slotResult.isEmptyItem()) break playBlockedSlotSound;

			playBlockedSlotClickSound(Minecraft.getInstance().getSoundManager());
		}

		super.slotClicked(slot, x, y, type);
	}

	public static void playBlockedSlotClickSound(SoundManager soundManager) {
		soundManager.play(SimpleSoundInstance.forUI(TCASounds.UI_CRATE_CLICK_FAIL, 1F));
	}

}
