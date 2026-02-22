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

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.frozenblock.thecopperierage.networking.packet.TCACoupleMinecartsPacket;
import net.frozenblock.thecopperierage.registry.TCAItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;

public final class TCAMinecartCouplingClientHandler {
	private static Integer selectedCartId;

	public static void init() {
		ClientTickEvents.END_CLIENT_TICK.register(TCAMinecartCouplingClientHandler::tick);
	}

	public static void onCartClicked(Player player, AbstractMinecart cart) {
		if (Minecraft.getInstance().player != player) return;

		if (selectedCartId == null || selectedCartId == cart.getId()) {
			selectedCartId = cart.getId();
			player.displayClientMessage(Component.translatable("message.thecopperierage.minecart_coupling.first_selected"), true);
			return;
		}

		ClientPlayNetworking.send(new TCACoupleMinecartsPacket(selectedCartId, cart.getId()));
		selectedCartId = null;
	}

	private static void tick(Minecraft minecraft) {
		if (selectedCartId == null) return;

		final LocalPlayer player = minecraft.player;
		if (player == null || minecraft.level == null) {
			selectedCartId = null;
			return;
		}

		final ItemStack mainHand = player.getMainHandItem();
		final ItemStack offHand = player.getOffhandItem();
		if (!mainHand.is(TCAItems.MINECART_COUPLING) && !offHand.is(TCAItems.MINECART_COUPLING)) selectedCartId = null;
	}
}
