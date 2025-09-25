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
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;

public record TCAChimeInfluencePacket(BlockPos pos, Vec3 influence, double scaleEachTick, Optional<Integer> entityID) implements CustomPacketPayload {
	public static final Type<TCAChimeInfluencePacket> PACKET_TYPE = new Type<>(TCAConstants.id("chime_influence"));

	public static final StreamCodec<FriendlyByteBuf, TCAChimeInfluencePacket> CODEC = StreamCodec.ofMember(TCAChimeInfluencePacket::write, TCAChimeInfluencePacket::new);

	public TCAChimeInfluencePacket(@NotNull FriendlyByteBuf buf) {
		this(buf.readBlockPos(), buf.readVec3(), buf.readDouble(), buf.readOptional(ByteBufCodecs.VAR_INT));
	}

	public static void sendToAll(
		ServerLevel serverLevel,
		BlockPos pos,
		Vec3 influence,
		double scaleEachTick,
		@Nullable Entity entity
	) {
		for (ServerPlayer player : PlayerLookup.tracking(serverLevel, pos)) {
			ServerPlayNetworking.send(
				player,
				new TCAChimeInfluencePacket(pos, influence, scaleEachTick, entity == null ? Optional.empty() : Optional.of(entity.getId()))
			);
		}
	}

	public void write(@NotNull FriendlyByteBuf buf) {
		buf.writeBlockPos(this.pos());
		buf.writeVec3(this.influence());
		buf.writeDouble(this.scaleEachTick());
		buf.writeOptional(this.entityID(), ByteBufCodecs.VAR_INT);
	}

	@NotNull
	public Type<?> type() {
		return PACKET_TYPE;
	}
}
