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


package net.frozenblock.thecopperierage.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.frozenblock.thecopperierage.block.CopperFanBlock;
import net.frozenblock.thecopperierage.networking.packet.TCACopperFanBlowPacket;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public final class TCAClientNetworking {

	public static void registerPacketReceivers() {
		receiveCopperFanBlowPacket();
	}

	public static void receiveCopperFanBlowPacket() {
		ClientPlayNetworking.registerGlobalReceiver(TCACopperFanBlowPacket.PACKET_TYPE, (packet, ctx) -> {
			final ClientLevel level = ctx.client().level;
			if (level == null) return;

			final BlockPos pos = packet.pos();
			final BlockState state = level.getBlockState(pos);
			if (!(state.getBlock() instanceof CopperFanBlock copperFanBlock)) return;

			copperFanBlock.blow(level, pos, state.getValue(CopperFanBlock.FACING));
		});
	}


}
