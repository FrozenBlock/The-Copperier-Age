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

package net.frozenblock.thecopperierage.networking;

import net.frozenblock.lib.networking.api.NetworkingHelper;
import net.frozenblock.thecopperierage.networking.packet.TCAChimeInfluencePacket;
import net.frozenblock.thecopperierage.networking.packet.TCACopperFanBlowPacket;
import net.frozenblock.thecopperierage.networking.packet.TCACoupleMinecartsPacket;

public final class TCANetworking {
	public static void init() {
		NetworkingHelper.registerS2CPayloadType(TCACopperFanBlowPacket.PACKET_TYPE, TCACopperFanBlowPacket.CODEC);
		NetworkingHelper.registerS2CPayloadType(TCAChimeInfluencePacket.PACKET_TYPE, TCAChimeInfluencePacket.CODEC);

		NetworkingHelper.registerC2SPayloadType(TCACoupleMinecartsPacket.PACKET_TYPE, TCACoupleMinecartsPacket.CODEC);
		NetworkingHelper.registerGlobalServerReceiver(TCACoupleMinecartsPacket.PACKET_TYPE, TCACoupleMinecartsPacket::handle);
	}
}
