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

package net.frozenblock.thecopperierage.particle.options;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.frozenblock.thecopperierage.registry.TCAParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ChestVehicleBubbleSeedParticleOptions(int entityId) implements ParticleOptions {
	public static final MapCodec<ChestVehicleBubbleSeedParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance ->
		instance.group(
			Codec.INT.fieldOf("entity_id").forGetter(options -> options.entityId)
		).apply(instance, ChestVehicleBubbleSeedParticleOptions::new)
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, ChestVehicleBubbleSeedParticleOptions> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_INT, ChestVehicleBubbleSeedParticleOptions::entityId,
		ChestVehicleBubbleSeedParticleOptions::new
	);

	@Override
	public ParticleType<?> getType() {
		return TCAParticleTypes.CHEST_VEHICLE_BUBBLE_SPAWNER;
	}
}
