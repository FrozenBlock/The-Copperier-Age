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
import net.frozenblock.thecopperierage.item.coupling.TCAMinecartCouplingManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record TCACoupleMinecartsPacket(int firstCartId, int secondCartId) implements CustomPacketPayload {
	public static final Type<TCACoupleMinecartsPacket> PACKET_TYPE = new Type<>(TCAConstants.id("couple_minecarts"));

	public static final StreamCodec<FriendlyByteBuf, TCACoupleMinecartsPacket> CODEC = StreamCodec.ofMember(TCACoupleMinecartsPacket::write, TCACoupleMinecartsPacket::new);

	public TCACoupleMinecartsPacket(@NotNull FriendlyByteBuf buf) {
		this(buf.readVarInt(), buf.readVarInt());
	}

	public void write(@NotNull FriendlyByteBuf buf) {
		buf.writeVarInt(firstCartId);
		buf.writeVarInt(secondCartId);
	}

	public static void handle(TCACoupleMinecartsPacket packet, net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context context) {
		context.server().execute(() -> {
			if (context.player().isRemoved()) {
				return;
			}

			TCAMinecartCouplingManager.tryToCouple(
				context.player(),
				context.player().level(),
				packet.firstCartId,
				packet.secondCartId
			);
		});
	}

	@NotNull
	@Override
	public Type<?> type() {
		return PACKET_TYPE;
	}
}