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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.item.api.OxidizableItemHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.WeatheringCopper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record WeatherState() implements SelectItemModelProperty<WeatheringCopper.WeatherState> {
	private static final WeatherState INSTANCE = new WeatherState();
	public static final SelectItemModelProperty.Type<WeatherState, WeatheringCopper.WeatherState> TYPE = SelectItemModelProperty.Type.create(
		MapCodec.unit(new WeatherState()), WeatheringCopper.WeatherState.CODEC
	);

	@Override
	public WeatheringCopper.WeatherState get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner, int seed, ItemDisplayContext context) {
		return OxidizableItemHelper.getWeatherState(stack);
	}

	@Override
	public SelectItemModelProperty.Type<WeatherState, WeatheringCopper.WeatherState> type() {
		return TYPE;
	}

	@Override
	public Codec<WeatheringCopper.WeatherState> valueCodec() {
		return WeatheringCopper.WeatherState.CODEC;
	}
}
