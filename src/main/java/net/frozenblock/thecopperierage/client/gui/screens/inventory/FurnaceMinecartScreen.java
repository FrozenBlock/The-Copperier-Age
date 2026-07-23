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

package net.frozenblock.thecopperierage.client.gui.screens.inventory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.entity.inventory.FurnaceMinecartMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class FurnaceMinecartScreen extends AbstractContainerScreen<FurnaceMinecartMenu> {
	private static final Identifier BACKGROUND_TEXTURE = TCAConstants.id("textures/gui/container/furnace_minecart.png");
	private static final Identifier LIT_PROGRESS_SPRITE = Identifier.withDefaultNamespace("container/furnace/lit_progress");
	private static final int LIT_PROGRESS_SPRITE_WIDTH = 14;
	private static final int LIT_PROGRESS_SPRITE_HEIGHT = 14;
	private static final int FLAME_X = 80;
	private static final int FLAME_BASE_Y = 27;

	public FurnaceMinecartScreen(FurnaceMinecartMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title, 176, 166);
		this.inventoryLabelY = 72;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
		guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, this.leftPos, this.topPos, 0F, 0F, this.imageWidth, this.imageHeight, 256, 256);
		this.renderLitProgress(guiGraphics);
	}

	private void renderLitProgress(GuiGraphicsExtractor guiGraphics) {
		if (!this.menu.isLit()) return;

		final int progress = this.menu.getLitProgress();
		guiGraphics.blitSprite(
			RenderPipelines.GUI_TEXTURED,
			LIT_PROGRESS_SPRITE,
			LIT_PROGRESS_SPRITE_WIDTH,
			LIT_PROGRESS_SPRITE_HEIGHT,
			0,
			LIT_PROGRESS_SPRITE_HEIGHT - progress,
			this.leftPos + FLAME_X,
			this.topPos + FLAME_BASE_Y + LIT_PROGRESS_SPRITE_HEIGHT - progress,
			LIT_PROGRESS_SPRITE_WIDTH,
			progress
		);
	}
}
