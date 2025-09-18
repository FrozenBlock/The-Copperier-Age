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

package net.frozenblock.thecopperierage.client.renderer.item.properties.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record OxidizedItemsEnabled() implements SelectItemModelProperty<Boolean> {
	public static final Codec<Boolean> VALUE_CODEC = Codec.BOOL;
	public static final SelectItemModelProperty.Type<OxidizedItemsEnabled, Boolean> TYPE = SelectItemModelProperty.Type.create(
		MapCodec.unit(new OxidizedItemsEnabled()), VALUE_CODEC
	);

	@Override
	public Boolean get(
		ItemStack stack,
		@Nullable ClientLevel level,
		@Nullable LivingEntity entity,
		int i,
		ItemDisplayContext itemDisplayContext
	) {
		// TODO: Config
		return true;
	}

	@Override
	public SelectItemModelProperty.@NotNull Type<OxidizedItemsEnabled, Boolean> type() {
		return TYPE;
	}

	@Override
	public @NotNull Codec<Boolean> valueCodec() {
		return VALUE_CODEC;
	}
}
