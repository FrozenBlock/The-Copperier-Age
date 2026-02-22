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
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record CouplingData(Optional<Couple> coupledTo, Optional<Couple> coupledFrom) {
	public static final Codec<CouplingData> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			Couple.CODEC.optionalFieldOf("coupled_to").forGetter(CouplingData::coupledTo),
			Couple.CODEC.optionalFieldOf("coupled_from").forGetter(CouplingData::coupledFrom)
		).apply(instance, CouplingData::new)
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, CouplingData> STREAM_CODEC = StreamCodec.composite(
		Couple.STREAM_CODEC.apply(ByteBufCodecs::optional), CouplingData::coupledTo,
		Couple.STREAM_CODEC.apply(ByteBufCodecs::optional), CouplingData::coupledFrom,
		CouplingData::new
	);
	public static CouplingData EMPTY = new CouplingData(Optional.empty(), Optional.empty());

	public boolean isCoupledFrom(UUID uuid) {
		return this.coupledFrom.isPresent() && this.coupledFrom.get().equals(uuid);
	}

	public boolean isCoupledTo(UUID uuid) {
		return this.coupledTo.isPresent() && this.coupledTo.get().equals(uuid);
	}

	public boolean hasAnyCoupling(UUID uuid) {
		return this.isCoupledFrom(uuid) || this.isCoupledTo(uuid);
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
		return this.coupledTo.map(couple -> level.getEntity(couple.uuid()));
	}

	public Optional<Entity> getCoupledFrom(Level level) {
		return this.coupledFrom.map(couple -> level.getEntity(couple.uuid()));
	}

	public Optional<ItemStack> getCoupledToItem() {
		return this.coupledTo.map(couple -> couple.item);
	}

	public Optional<ItemStack> getCoupledFromItem() {
		return this.coupledFrom.map(couple -> couple.item);
	}

	public CouplingData coupleTo(UUID uuid, ItemStack item) {
		return new CouplingData(Optional.of(new Couple(uuid, item)), this.coupledFrom);
	}

	public CouplingData coupleFrom(UUID uuid, ItemStack item) {
		return new CouplingData(this.coupledTo, Optional.of(new Couple(uuid, item)));
	}

	public CouplingData uncoupleTo() {
		return new CouplingData(Optional.empty(), this.coupledFrom);
	}

	public CouplingData uncoupleFrom() {
		return new CouplingData(this.coupledTo, Optional.empty());
	}

	public record Couple(UUID uuid, ItemStack item) {
		public static final Codec<Couple> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
				UUIDUtil.CODEC.fieldOf("uuid").forGetter(Couple::uuid),
				ItemStack.CODEC.fieldOf("item").forGetter(Couple::item)
			).apply(instance, Couple::new)
		);
		public static final StreamCodec<RegistryFriendlyByteBuf, Couple> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, Couple::uuid,
			ItemStack.STREAM_CODEC, Couple::item,
			Couple::new
		);
	}
}
