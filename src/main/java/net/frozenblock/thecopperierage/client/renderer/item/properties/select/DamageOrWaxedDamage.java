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
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.item.api.OxidizableItemHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record DamageOrWaxedDamage(boolean normalize) implements RangeSelectItemModelProperty {
	public static final MapCodec<DamageOrWaxedDamage> MAP_CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
			Codec.BOOL.optionalFieldOf("normalize", true).forGetter(DamageOrWaxedDamage::normalize)
		).apply(instance, DamageOrWaxedDamage::new)
	);

	@Override
	public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int i) {
		float damage = OxidizableItemHelper.getDamageOrWaxedDamage(stack);
		float maxDamage = stack.getMaxDamage();
		return this.normalize ? Mth.clamp(damage / maxDamage, 0F, 1F) : Mth.clamp(damage, 0F, maxDamage);
	}

	@Override
	public MapCodec<DamageOrWaxedDamage> type() {
		return MAP_CODEC;
	}
}
