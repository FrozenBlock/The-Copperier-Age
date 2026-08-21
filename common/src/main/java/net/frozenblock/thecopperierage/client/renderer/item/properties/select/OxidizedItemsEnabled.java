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

package net.frozenblock.thecopperierage.client.renderer.item.properties.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@ClientOnly
public record OxidizedItemsEnabled() implements SelectItemModelProperty<Boolean> {
	public static final OxidizedItemsEnabled INSTANCE = new OxidizedItemsEnabled();
	private static final Codec<Boolean> VALUE_CODEC = Codec.BOOL;
	public static final SelectItemModelProperty.Type<OxidizedItemsEnabled, Boolean> TYPE = SelectItemModelProperty.Type.create(
		MapCodec.unit(INSTANCE), VALUE_CODEC
	);

	@Override
	public Boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext context) {
		return TCAConfig.OXIDIZABLE_COPPER_EQUIPMENT.get();
	}

	@Override
	public SelectItemModelProperty.Type<OxidizedItemsEnabled, Boolean> type() {
		return TYPE;
	}

	@Override
	public Codec<Boolean> valueCodec() {
		return VALUE_CODEC;
	}
}
