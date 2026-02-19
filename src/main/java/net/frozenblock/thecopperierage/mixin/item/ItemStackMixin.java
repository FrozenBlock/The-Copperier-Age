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

import java.util.function.Consumer;
import net.frozenblock.thecopperierage.block.api.WeatheringCopperBlocksHelper;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.item.api.OxidizableItemHelper;
import net.frozenblock.thecopperierage.tag.TCAItemTags;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.WeatheringCopper;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.ChatFormatting;

@Mixin(ItemStack.class)
public class ItemStackMixin {

	@Unique
	private static final Component THECOPPERIERAGE$WAXED_TOOLTIP = Component.translatable("item.thecopperierage.waxed").withStyle(ChatFormatting.GOLD);
	@Unique
	private static final Component THECOPPERIERAGE$WEATHERING_WAXED_TOOLTIP = Component.translatable("item.thecopperierage.weathering.waxed").withStyle(ChatFormatting.GOLD);

	@Inject(method = "set(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;", at = @At("HEAD"))
	public <T> void theCopperierAge$onDamageSet(DataComponentType<T> dataComponentType, @Nullable T value, CallbackInfoReturnable<T> info) {
		if (dataComponentType != DataComponents.DAMAGE) return;
		int damage = value == null ? 0 : Integer.class.cast(value).intValue();
		OxidizableItemHelper.onDamageUpdated(ItemStack.class.cast(this), damage);
	}

	@Inject(method = "set(Lnet/minecraft/core/component/TypedDataComponent;)Ljava/lang/Object;", at = @At("HEAD"))
	public <T> void theCopperierAge$onDamageSet(TypedDataComponent<T> typedDataComponent, CallbackInfoReturnable<T> info) {
		if (typedDataComponent.type() != DataComponents.DAMAGE) return;
		final T value = typedDataComponent.value();
		int damage = value == null ? 0 : Integer.class.cast(value).intValue();
		OxidizableItemHelper.onDamageUpdated(ItemStack.class.cast(this), damage);
	}

	@Inject(method = "remove", at = @At("HEAD"))
	public <T> void theCopperierAge$onDamageSet(DataComponentType<T> dataComponentType, CallbackInfoReturnable<T> info) {
		if (dataComponentType != DataComponents.DAMAGE) return;
		OxidizableItemHelper.onDamageUpdated(ItemStack.class.cast(this), 0);
	}

	@Inject(
		method = "addDetailsToTooltip",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/core/component/DataComponents;UNBREAKABLE:Lnet/minecraft/core/component/DataComponentType;",
			opcode = Opcodes.GETSTATIC,
			ordinal = 0,
			shift = At.Shift.BEFORE
		)
	)
	public void theCopperierAge$addWaxedTooltip(
		Item.TooltipContext context, TooltipDisplay display, @Nullable Player player, TooltipFlag flag, Consumer<Component> consumer, CallbackInfo info
	) {
		ItemStack stack = ItemStack.class.cast(this);
		if (OxidizableItemHelper.isWaxed(stack)) consumer.accept(THECOPPERIERAGE$WAXED_TOOLTIP);
		if (stack.is(TCAItemTags.OXIDIZABLE_EQUIPMENT)) {
			addWeatherStateTooltip(
				consumer,
				OxidizableItemHelper.getValueForOxidization(
					stack,
					WeatheringCopper.WeatherState.UNAFFECTED,
					WeatheringCopper.WeatherState.EXPOSED,
					WeatheringCopper.WeatherState.WEATHERED,
					WeatheringCopper.WeatherState.OXIDIZED
				)
			);
		}

		if (!TCAConfig.BETTER_COPPER_TOOLTIPS) return;

		if (!(stack.getItem() instanceof BlockItem blockItem)) return;
		ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(blockItem.getBlock());
		String blockPath = blockId.getPath();

		if (WeatheringCopperBlocksHelper.isTracked(blockItem.getBlock())) {
			if (WeatheringCopperBlocksHelper.isWaxed(blockItem.getBlock())) consumer.accept(THECOPPERIERAGE$WEATHERING_WAXED_TOOLTIP);
			WeatheringCopperBlocksHelper.getWeatherState(blockItem.getBlock()).ifPresent(
				weatherState -> addWeatherStateTooltip(consumer, weatherState)
			);
			return;
		}

		if (!isFallbackWeatheringCopperBlockPath(blockPath)) return;
		if (isWaxedPath(blockPath)) consumer.accept(THECOPPERIERAGE$WEATHERING_WAXED_TOOLTIP);
		addWeatherStateTooltip(consumer, getWeatherStateFromPath(blockPath));
	}

	@Unique
	private static void addWeatherStateTooltip(Consumer<Component> consumer, WeatheringCopper.WeatherState weatherState) {
		if (weatherState == WeatheringCopper.WeatherState.UNAFFECTED) return;
		consumer.accept(Component.translatable(getWeatherStateTranslationKey(weatherState)).withStyle(ChatFormatting.GRAY));
	}

	@Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true, require = 0)
	private void theCopperierAge$normalizeWeatheringCopperDisplayName(CallbackInfoReturnable<Component> info) {
		theCopperierAge$normalizeWeatheringCopperName(info);
	}

	@Inject(method = "getHoverName", at = @At("RETURN"), cancellable = true, require = 0)
	private void theCopperierAge$normalizeWeatheringCopperHoverName(CallbackInfoReturnable<Component> info) {
		theCopperierAge$normalizeWeatheringCopperName(info);
	}

	@Unique
	private void theCopperierAge$normalizeWeatheringCopperName(CallbackInfoReturnable<Component> info) {
		if (!TCAConfig.BETTER_COPPER_TOOLTIPS) return;
		ItemStack stack = ItemStack.class.cast(this);
		if (stack.has(DataComponents.CUSTOM_NAME)) return;
		if (!(stack.getItem() instanceof BlockItem blockItem)) return;

		ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(blockItem.getBlock());
		String blockPath = blockId.getPath();
		if (!shouldNormalizeName(blockItem, blockPath)) return;

		String basePath = getBaseWeatheringPath(blockPath);
		info.setReturnValue(Component.translatable("block." + blockId.getNamespace() + "." + basePath));
	}

	@Unique
	private static boolean isWaxedPath(String path) {
		return path.startsWith("waxed_");
	}

	@Unique
	private static boolean shouldNormalizeName(BlockItem blockItem, String path) {
		if (WeatheringCopperBlocksHelper.isTracked(blockItem.getBlock())) return true;
		return isFallbackWeatheringCopperBlockPath(path);
	}

	@Unique
	private static boolean isFallbackWeatheringCopperBlockPath(String path) {
		return path.contains("copper") && isWeatheringVariantPath(path);
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

	@Unique
	private static WeatheringCopper.WeatherState getWeatherStateFromPath(String path) {
		String normalizedPath = isWaxedPath(path) ? path.substring("waxed_".length()) : path;
		if (normalizedPath.startsWith("oxidized_")) return WeatheringCopper.WeatherState.OXIDIZED;
		if (normalizedPath.startsWith("weathered_")) return WeatheringCopper.WeatherState.WEATHERED;
		if (normalizedPath.startsWith("exposed_")) return WeatheringCopper.WeatherState.EXPOSED;
		return WeatheringCopper.WeatherState.UNAFFECTED;
	}

	@Unique
	private static String getWeatherStateTranslationKey(WeatheringCopper.WeatherState weatherState) {
		if (weatherState == WeatheringCopper.WeatherState.UNAFFECTED) return "item.thecopperierage.weathering.state.unaffected";
		if (weatherState == WeatheringCopper.WeatherState.EXPOSED) return "item.thecopperierage.weathering.state.exposed";
		if (weatherState == WeatheringCopper.WeatherState.WEATHERED) return "item.thecopperierage.weathering.state.weathered";
		if (weatherState == WeatheringCopper.WeatherState.OXIDIZED) return "item.thecopperierage.weathering.state.oxidized";
		return "item.thecopperierage.weathering.state.unknown";
	}

}
