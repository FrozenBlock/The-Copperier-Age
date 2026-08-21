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

package net.frozenblock.thecopperierage.networking.packet;

import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.entity.coupling.MinecartCouplingUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

public record TCACoupleMinecartsPacket(boolean usedOffHand, int firstCartId, int secondCartId) implements CustomPacketPayload {
	public static final Type<TCACoupleMinecartsPacket> PACKET_TYPE = new Type<>(TCAConstants.id("couple_minecarts"));
	public static final StreamCodec<RegistryFriendlyByteBuf, TCACoupleMinecartsPacket> CODEC = StreamCodec.ofMember(TCACoupleMinecartsPacket::write, TCACoupleMinecartsPacket::new);

	public TCACoupleMinecartsPacket(RegistryFriendlyByteBuf buf) {
		this(buf.readBoolean(), buf.readVarInt(), buf.readVarInt());
	}

	public void write(RegistryFriendlyByteBuf buf) {
		buf.writeBoolean(this.usedOffHand);
		buf.writeVarInt(this.firstCartId);
		buf.writeVarInt(this.secondCartId);
	}

	public static void handle(TCACoupleMinecartsPacket packet, MinecraftServer server, ServerPlayer player) {
		if (player.isRemoved()) return;
		MinecartCouplingUtil.attemptCouple(
			player,
			player.level(),
			packet.usedOffHand() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND,
			packet.firstCartId,
			packet.secondCartId
		);
	}

	@Override
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
