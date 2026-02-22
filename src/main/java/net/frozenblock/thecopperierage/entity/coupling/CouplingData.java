/*
 * Copyright 2026 FrozenBlock
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

package net.frozenblock.thecopperierage.entity.coupling;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record CouplingData(Optional<UUID> coupledTo, Optional<UUID> coupledFrom) {
	public static final Codec<CouplingData> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			UUIDUtil.CODEC.optionalFieldOf("coupled_to").forGetter(CouplingData::coupledTo),
			UUIDUtil.CODEC.optionalFieldOf("coupled_from").forGetter(CouplingData::coupledFrom)
		).apply(instance, CouplingData::new)
	);
	public static final StreamCodec<ByteBuf, CouplingData> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs::optional), CouplingData::coupledTo,
		UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs::optional), CouplingData::coupledFrom,
		CouplingData::new
	);
	public static CouplingData EMPTY = new CouplingData(Optional.empty(), Optional.empty());

	public boolean isCoupledFrom(UUID uuid) {
		return this.coupledFrom.isPresent() && this.coupledFrom.get().equals(uuid);
	}

	public boolean isCoupledTo(UUID uuid) {
		return this.coupledTo.isPresent() && this.coupledTo.get().equals(uuid);
	}

	public boolean isCoupledFrom() {
		return this.coupledFrom.isPresent();
	}

	public boolean isCoupledTo() {
		return this.coupledTo.isPresent();
	}

	public boolean hasAnyCoupling() {
		return this.isCoupledFrom() || this.isCoupledTo();
	}

	public Optional<Entity> getCoupledTo(Level level) {
		return this.coupledTo.map(level::getEntity);
	}

	public Optional<Entity> getCoupledFrom(Level level) {
		return this.coupledFrom.map(level::getEntity);
	}

	public CouplingData coupleTo(UUID uuid) {
		return new CouplingData(Optional.of(uuid), this.coupledFrom);
	}

	public CouplingData coupleFrom(UUID uuid) {
		return new CouplingData(this.coupledTo, Optional.of(uuid));
	}

	public CouplingData uncoupleTo() {
		return new CouplingData(Optional.empty(), this.coupledFrom);
	}

	public CouplingData uncoupleFrom() {
		return new CouplingData(this.coupledTo, Optional.empty());
	}

}
