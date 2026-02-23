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

package net.frozenblock.thecopperierage.client.coupling;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.frozenblock.thecopperierage.client.renderer.entity.state.CouplingRenderState;
import net.frozenblock.thecopperierage.entity.impl.CouplingToEntityInterface;
import net.frozenblock.thecopperierage.networking.packet.TCACoupleMinecartsPacket;
import net.frozenblock.thecopperierage.registry.TCAItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public final class MinecartCouplingClientHandler {
	private static Integer selectedCartId;
	private static AbstractMinecart selectedCart;

	public static void init() {
		ClientTickEvents.END_CLIENT_TICK.register(MinecartCouplingClientHandler::tick);
	}

	public static void onCartClicked(Player player, InteractionHand hand, AbstractMinecart cart) {
		if (Minecraft.getInstance().player != player) return;

		if (selectedCartId == null || selectedCartId == cart.getId()) {
			selectedCartId = cart.getId();
			selectedCart = cart;
			player.displayClientMessage(Component.translatable("message.thecopperierage.minecart_coupling.first_selected"), true);
			return;
		}

		ClientPlayNetworking.send(new TCACoupleMinecartsPacket(hand == InteractionHand.OFF_HAND, selectedCartId, cart.getId()));
		clearSelection();
	}

	private static void tick(Minecraft minecraft) {
		if (selectedCartId == null || selectedCart == null) {
			clearSelection();
			return;
		}

		final LocalPlayer player = minecraft.player;
		if (player == null || minecraft.level == null || !(minecraft.level.getEntity(selectedCartId) instanceof AbstractMinecart)) {
			clearSelection();
			return;
		}

		final ItemStack mainHand = player.getMainHandItem();
		final ItemStack offHand = player.getOffhandItem();
		if (!mainHand.is(TCAItems.MINECART_COUPLING) && !offHand.is(TCAItems.MINECART_COUPLING)) clearSelection();
	}

	public static boolean isMinecartSelected(AbstractMinecart cart) {
		return selectedCart != null && selectedCart == cart;
	}

	public static Optional<CouplingRenderState> createRenderState(LocalPlayer player, AbstractMinecart cart, float partialTicks) {
		if (!isMinecartSelected(cart)) return Optional.empty();

		final Vec3 ropeHoldPosition = player.getRopeHoldPosition(partialTicks);
		final Vec3 minecartPosition = selectedCart.getPosition(partialTicks);
		final CouplingRenderState renderState = new CouplingRenderState();
		renderState.vector = ropeHoldPosition.subtract(minecartPosition);
		return Optional.of(renderState);
	}

	public static AABB modifyBoundingBoxForCoupling(AbstractMinecart minecart, AABB box) {
		if (!(minecart instanceof CouplingToEntityInterface coupleInterface)) return box;

		final Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player != null && isMinecartSelected(minecart)) box = box.minmax(minecraft.player.getBoundingBox());

		final Entity coupledTo = coupleInterface.theCopperierAge$getCoupledTo();
		if (coupledTo == null) return box;

		return box.minmax(coupledTo.getBoundingBox());
	}

	private static void clearSelection() {
		selectedCartId = null;
		selectedCart = null;
	}
}
