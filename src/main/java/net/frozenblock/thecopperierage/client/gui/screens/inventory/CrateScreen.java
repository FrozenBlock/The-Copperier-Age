package net.frozenblock.thecopperierage.client.gui.screens.inventory;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.block.CopperCrateBlock;
import net.frozenblock.thecopperierage.block.entity.inventory.CrateSlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
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

	@Override
	public void renderSlot(GuiGraphics guiGraphics, Slot slot) {
		renderBlockedSlot: {
			if (!(slot instanceof CrateSlot crateSlot) || slot.hasItem()) break renderBlockedSlot;

			final ItemStack carried = this.menu.getCarried();
			final CopperCrateBlock.SlotResult slotResult = CopperCrateBlock.verifyStackForPlacement(carried, this.menu.getContainer());
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

}
