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

package net.frozenblock.thecopperierage.mixin.item;

import net.frozenblock.thecopperierage.block.api.WeatheringCopperBlocksHelper;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

	@Inject(method = "getDescriptionId()Ljava/lang/String;", at = @At("RETURN"), cancellable = true, require = 0)
	private void theCopperierAge$normalizeWeatheringCopperDescriptionId(CallbackInfoReturnable<String> info) {
		if (!TCAConfig.BETTER_COPPER_TOOLTIPS) return;
		Item item = Item.class.cast(this);
		if (!(item instanceof BlockItem blockItem)) return;
		normalizeDescriptionId(blockItem, info);
	}

	@Unique
	private static void normalizeDescriptionId(BlockItem blockItem, CallbackInfoReturnable<String> info) {
		ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(blockItem.getBlock());
		String blockPath = blockId.getPath();
		if (!shouldNormalizeName(blockItem, blockPath)) return;

		String basePath = getBaseWeatheringPath(blockPath);
		info.setReturnValue("block." + blockId.getNamespace() + "." + basePath);
	}

	@Unique
	private static boolean shouldNormalizeName(BlockItem item, String path) {
		if (WeatheringCopperBlocksHelper.isTracked(item.getBlock())) return true;
		return isFallbackWeatheringCopperBlockPath(path);
	}

	@Unique
	private static boolean isFallbackWeatheringCopperBlockPath(String path) {
		return path.contains("copper") && (isWeatheringVariantPath(path) || isWaxedPath(path));
	}

	@Unique
	private static boolean isWaxedPath(String path) {
		return path.startsWith("waxed_");
	}

	@Unique
	private static boolean isWeatheringVariantPath(String path) {
		String normalizedPath = isWaxedPath(path) ? path.substring("waxed_".length()) : path;
		return normalizedPath.startsWith("oxidized_") || normalizedPath.startsWith("weathered_") || normalizedPath.startsWith("exposed_");
	}

	@Unique
	private static String getBaseWeatheringPath(String path) {
		String normalizedPath = isWaxedPath(path) ? path.substring("waxed_".length()) : path;
		if (normalizedPath.startsWith("oxidized_")) return normalizedPath.substring("oxidized_".length());
		if (normalizedPath.startsWith("weathered_")) return normalizedPath.substring("weathered_".length());
		if (normalizedPath.startsWith("exposed_")) return normalizedPath.substring("exposed_".length());
		return normalizedPath;
	}

}
