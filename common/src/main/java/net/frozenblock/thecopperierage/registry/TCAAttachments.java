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

package net.frozenblock.thecopperierage.registry;

import com.mojang.serialization.Codec;
import net.frozenblock.lib.platform.api.attachment.DataAttachmentSyncPredicate;
import net.frozenblock.lib.platform.api.attachment.DataAttachmentType;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.entity.coupling.CouplingData;
import net.minecraft.network.codec.ByteBufCodecs;

public final class TCAAttachments {
	public static final DataAttachmentType<Integer> CHEST_VEHICLE_OPENERS = DataAttachmentType.create(
		TCAConstants.id("chest_vehicle_openers"),
		builder -> {
			builder.initializer(() -> 0)
			.syncWith(ByteBufCodecs.VAR_INT, DataAttachmentSyncPredicate.all());
		}
	);
	public static final DataAttachmentType<Boolean> CHEST_VEHICLE_CAN_BUBBLE = DataAttachmentType.create(
		TCAConstants.id("chest_vehicle_can_bubble"),
		builder -> {
			builder.persistent(Codec.BOOL);
			builder.initializer(() -> true);
		}
	);
	public static final DataAttachmentType<CouplingData> MINECART_COUPLING = DataAttachmentType.create(
		TCAConstants.id("minecart_coupled_uuid"),
		builder -> {
			builder.persistent(CouplingData.CODEC);
			builder.syncWith(CouplingData.STREAM_CODEC, DataAttachmentSyncPredicate.all());
			builder.initializer(() -> CouplingData.EMPTY);
		}
	);

	private TCAAttachments() {
	}

	public static void init() {
	}
}
