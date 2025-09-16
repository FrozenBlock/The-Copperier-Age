/*
 * Copyright 2025 FrozenBlock
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

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public record TCACopperFanBlowPacket(BlockPos pos) implements CustomPacketPayload {
	public static final Type<TCACopperFanBlowPacket> PACKET_TYPE = new Type<>(TCAConstants.id("copper_fan_blow"));

	public static final StreamCodec<FriendlyByteBuf, TCACopperFanBlowPacket> CODEC = StreamCodec.ofMember(TCACopperFanBlowPacket::write, TCACopperFanBlowPacket::new);

	public TCACopperFanBlowPacket(@NotNull FriendlyByteBuf buf) {
		this(buf.readBlockPos());
	}

	public static void sendToAll(ServerLevel serverLevel, BlockPos pos) {
		for (ServerPlayer player : PlayerLookup.tracking(serverLevel, pos)) {
			ServerPlayNetworking.send(player, new TCACopperFanBlowPacket(pos));
		}
	}

	public void write(@NotNull FriendlyByteBuf buf) {
		buf.writeBlockPos(this.pos());
	}

	@NotNull
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
