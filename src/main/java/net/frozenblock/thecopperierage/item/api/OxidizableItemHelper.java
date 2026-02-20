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

package net.frozenblock.thecopperierage.item.api;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.registry.TCADataComponents;
import net.frozenblock.thecopperierage.tag.TCAItemTags;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.ChatFormatting;

public final class OxidizableItemHelper {
	public static final List<String> OXIDIZING_SUFFIXES = ImmutableList.of("exposed", "weathered", "oxidized");
	private static final List<String> OXIDIZING_AUTO_MODEL_SEARCH_TERMS = ImmutableList.of(
		"_helmet", "_chestplate", "_leggings", "_boots", "_axe", "_pickaxe", "_shovel", "_sword", "_hoe", "_spear", "brush", "wrench"
	);
	private static final Map<Item, Pair<ItemAttributeModifiers, ItemAttributeModifiers>> OXIDIZABLE_ATTRIBUTES = new Object2ObjectLinkedOpenHashMap<>();
	private static final float COPPER_MINING_SPEED = ToolMaterial.COPPER.speed();
	private static final float IRON_MINING_SPEED = ToolMaterial.IRON.speed();
	public static final float EXPOSED_THRESHOLD = 0.2F;
	public static final float WEATHERED_THRESHOLD = 0.45F;
	public static final float OXIDIZED_THRESHOLD = 0.65F;
	public static final Component WAXED_TOOLTIP = TCAConstants.itemComponent("waxed").withStyle(ChatFormatting.GOLD);
	public static final Component WEATHERING_WAXED_TOOLTIP = TCAConstants.itemComponent("weathering.waxed").withStyle(ChatFormatting.GOLD);
	private static final MutableComponent[] WEATHERING_NAMES = new MutableComponent[] {
		TCAConstants.itemComponent("weathering.state.unaffected").withStyle(ChatFormatting.GRAY),
		TCAConstants.itemComponent("weathering.state.exposed").withStyle(ChatFormatting.GRAY),
		TCAConstants.itemComponent("weathering.state.weathered").withStyle(ChatFormatting.GRAY),
		TCAConstants.itemComponent("weathering.state.oxidized").withStyle(ChatFormatting.GRAY),
		TCAConstants.itemComponent("weathering.state.unknown").withStyle(ChatFormatting.GRAY)
	};

	public static void bootstrap() {
		addOxidizableAttributesItem(Items.COPPER_SWORD, Items.IRON_SWORD);
		addOxidizableAttributesItem(Items.COPPER_SHOVEL, Items.IRON_SHOVEL);
		addOxidizableAttributesItem(Items.COPPER_PICKAXE, Items.IRON_PICKAXE);
		addOxidizableAttributesItem(Items.COPPER_AXE, Items.IRON_AXE);
		addOxidizableAttributesItem(Items.COPPER_HOE, Items.IRON_HOE);
	}

	public static Optional<Item> getNonWeatheringNonWaxedEquivalent(Item item) {
		if (!(item instanceof BlockItem blockItem)) return Optional.empty();
		final Optional<Block> baseBlock = getNonWeatheringNonWaxedEquivalent(blockItem.getBlock());
		return baseBlock.map(Block::asItem);
	}

	public static Optional<Item> getNonWaxedEquivalent(Item item) {
		if (!(item instanceof BlockItem blockItem)) return Optional.empty();
		final Optional<Block> baseBlock = getNonWaxedEquivalent(blockItem.getBlock());
		return baseBlock.map(Block::asItem);
	}

	public static Optional<Item> getNonWeatheringEquivalent(Item item) {
		if (!(item instanceof BlockItem blockItem)) return Optional.empty();
		final Optional<Block> baseBlock = getNonWeatheringEquivalent(blockItem.getBlock());
		return baseBlock.map(Block::asItem);
	}

	public static Optional<Block> getNonWeatheringNonWaxedEquivalent(Block block) {
		Block baseBlock = getNonWeatheringEquivalent(getNonWaxedEquivalent(block).orElse(block)).orElse(block);
		if (block == baseBlock) return Optional.empty();
		return Optional.of(baseBlock);
	}

	public static Optional<Block> getNonWaxedEquivalent(Block block) {
		final Block nonWaxedBlock = HoneycombItem.WAX_OFF_BY_BLOCK.get().get(block);
		if (nonWaxedBlock == null) return Optional.empty();
		return Optional.of(nonWaxedBlock);
	}

	public static Optional<Block> getNonWeatheringEquivalent(Block block) {
		final Block nonWeatheringBlock = WeatheringCopper.getFirst(block);
		if (nonWeatheringBlock == block) return Optional.empty();
		return Optional.of(nonWeatheringBlock);
	}

	public static Optional<WeatheringCopper.WeatherState> getWeatherStateByTag(ItemStack stack) {
		if (stack.is(TCAItemTags.WEATHERING_UNAFFECTED)) return Optional.of(WeatheringCopper.WeatherState.UNAFFECTED);
		if (stack.is(TCAItemTags.WEATHERING_EXPOSED)) return Optional.of(WeatheringCopper.WeatherState.EXPOSED);
		if (stack.is(TCAItemTags.WEATHERING_WEATHERED)) return Optional.of(WeatheringCopper.WeatherState.WEATHERED);
		if (stack.is(TCAItemTags.WEATHERING_OXIDIZED)) return Optional.of(WeatheringCopper.WeatherState.OXIDIZED);
		return Optional.empty();
	}

	public static MutableComponent getWeatheringStateName(WeatheringCopper.WeatherState weatherState) {
		if (weatherState == WeatheringCopper.WeatherState.UNAFFECTED) return WEATHERING_NAMES[0];
		if (weatherState == WeatheringCopper.WeatherState.EXPOSED) return WEATHERING_NAMES[1];
		if (weatherState == WeatheringCopper.WeatherState.WEATHERED) return  WEATHERING_NAMES[2];
		if (weatherState == WeatheringCopper.WeatherState.OXIDIZED) return WEATHERING_NAMES[3];
		return WEATHERING_NAMES[4];
	}

	public static List<String> getOxidizingModelSearchTerms() {
		return ImmutableList.copyOf(OXIDIZING_AUTO_MODEL_SEARCH_TERMS);
	}

	public static void addOxidizingModelSearchTerm(String term) {
		OXIDIZING_AUTO_MODEL_SEARCH_TERMS.add(term);
	}

	public static void addOxidizableAttributesItem(Item copper, Item iron) {
		final DataComponentMap copperComponents = copper.components();
		final DataComponentMap ironComponents = iron.components();

		final ItemAttributeModifiers copperAttributes = copperComponents.get(DataComponents.ATTRIBUTE_MODIFIERS);
		final ItemAttributeModifiers ironAttributes = ironComponents.get(DataComponents.ATTRIBUTE_MODIFIERS);
		if (copperAttributes == null || ironAttributes == null) return;

		OXIDIZABLE_ATTRIBUTES.put(copper, Pair.of(copperAttributes, ironAttributes));
	}

	public static boolean isWaxed(ItemStack stack) {
		return stack.has(TCADataComponents.WAXED);
	}

	public static int getDamageOrWaxedDamage(ItemStack stack) {
		return stack.getComponents().getOrDefault(TCADataComponents.WAXED, stack.getDamageValue());
	}

	public static float getOxidizeProgress(ItemStack stack) {
		return getOxidizeProgress(stack, OptionalInt.empty());
	}

	public static float getOxidizeProgress(ItemStack stack, OptionalInt optionalInt) {
		if (!TCAConfig.OXIDIZABLE_COPPER_EQUIPMENT) return 0F;

		final float damage = optionalInt.orElse(getDamageOrWaxedDamage(stack));
		final float maxDamage = stack.getMaxDamage();
		final float damageProgress = Mth.clamp(damage / maxDamage, 0F, 1F);
		if (damageProgress >= OXIDIZED_THRESHOLD) return 1F;
		if (damageProgress >= WEATHERED_THRESHOLD) return 0.65F;
		if (damageProgress >= EXPOSED_THRESHOLD) return 0.35F;
		return 0F;
	}

	public static <T> T getValueForOxidization(ItemStack stack, T unaffected, T exposed, T weathered, T oxidized) {
		final float oxidizeProgress = getOxidizeProgress(stack);
		if (oxidizeProgress == 0.35F) return exposed;
		if (oxidizeProgress == 0.65F) return weathered;
		if (oxidizeProgress == 1F) return oxidized;
		return unaffected;
	}

	public static void onDamageUpdated(ItemStack stack, int damageValue) {
		final Item item = stack.getItem();
		final int damageToUse = stack.getComponents().getOrDefault(TCADataComponents.WAXED, damageValue);
		updateMiningSpeed(stack, item, damageToUse);
		updateAttributes(stack, item, damageToUse);
	}

	private static void updateMiningSpeed(ItemStack stack, Item item, int damageValue) {
		if (!OXIDIZABLE_ATTRIBUTES.containsKey(item) || stack.is(ItemTags.SWORDS)) return;

		final Tool stackTool = stack.get(DataComponents.TOOL);
		if (stackTool == null) return;

		final float oxidizeProgress = getOxidizeProgress(stack, OptionalInt.of(damageValue));
		final float newSpeed = Mth.lerp(oxidizeProgress, COPPER_MINING_SPEED, IRON_MINING_SPEED);

		boolean isEqual = true;
		final List<Tool.Rule> ruleList = new ArrayList<>();
		for (Tool.Rule rule : stackTool.rules()) {
			if (rule.speed().isPresent()) {
				final float ruleSpeed = rule.speed().get();
				if (ruleSpeed >= COPPER_MINING_SPEED && ruleSpeed <= IRON_MINING_SPEED) {
					if (ruleSpeed != newSpeed) isEqual = false;
					ruleList.add(new Tool.Rule(rule.blocks(), Optional.of(newSpeed), rule.correctForDrops()));
					continue;
				}
			}
			ruleList.add(rule);
		}

		if (isEqual) return;

		TCAConstants.log("Copper Item Mining Speed Updated!", TCAConstants.UNSTABLE_LOGGING);
		final Tool finalTool = new Tool(List.copyOf(ruleList), stackTool.defaultMiningSpeed(), stackTool.damagePerBlock(), stackTool.canDestroyBlocksInCreative());
		stack.set(DataComponents.TOOL, finalTool);
	}

	private static void updateAttributes(ItemStack stack, Item item, int damageValue) {
		final Pair<ItemAttributeModifiers, ItemAttributeModifiers> attributePair = OXIDIZABLE_ATTRIBUTES.get(item);
		if (attributePair == null) return;

		final ItemAttributeModifiers stackAttributes = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
		if (stackAttributes == null) return;

		final ItemAttributeModifiers copperAttributes = attributePair.getFirst();
		final ItemAttributeModifiers ironAttributes = attributePair.getSecond();
		final float oxidizeProgress = getOxidizeProgress(stack, OptionalInt.of(damageValue));

		AtomicBoolean isEqual = new AtomicBoolean(true);
		final List<ItemAttributeModifiers.Entry> entryList = new ArrayList<>();
		for (ItemAttributeModifiers.Entry entry : stackAttributes.modifiers()) {
			if (entry.attribute() == Attributes.ATTACK_DAMAGE && entry.modifier().is(Item.BASE_ATTACK_DAMAGE_ID)) {
				final Optional<ItemAttributeModifiers.Entry> lerpedAttackDamage = getLerpedAttributeEntry(
					copperAttributes,
					ironAttributes,
					Attributes.ATTACK_DAMAGE,
					Item.BASE_ATTACK_DAMAGE_ID,
					oxidizeProgress
				);
				lerpedAttackDamage.ifPresentOrElse(
					attackDamageEntry -> {
						if (!attackDamageEntry.equals(entry)) isEqual.set(false);
						entryList.add(attackDamageEntry);
					},
					() -> entryList.add(entry)
				);
				continue;
			} else if (entry.attribute() == Attributes.ATTACK_SPEED && entry.modifier().is(Item.BASE_ATTACK_SPEED_ID)) {
				final Optional<ItemAttributeModifiers.Entry> lerpedAttackSpeed = getLerpedAttributeEntry(
					copperAttributes,
					ironAttributes,
					Attributes.ATTACK_SPEED,
					Item.BASE_ATTACK_SPEED_ID,
					oxidizeProgress
				);
				lerpedAttackSpeed.ifPresentOrElse(
					attackSpeedEntry -> {
						if (!attackSpeedEntry.equals(entry)) isEqual.set(false);
						entryList.add(attackSpeedEntry);
					},
					() -> entryList.add(entry)
				);
				continue;
			}
			entryList.add(entry);
		}

		if (isEqual.get()) return;

		TCAConstants.log("Copper Item Attributes Updated!", TCAConstants.UNSTABLE_LOGGING);
		final ItemAttributeModifiers finalAttributeModifiers = new ItemAttributeModifiers(ImmutableList.copyOf(entryList));
		stack.set(DataComponents.ATTRIBUTE_MODIFIERS, finalAttributeModifiers);
	}

	private static Optional<ItemAttributeModifiers.Entry> getLerpedAttributeEntry(
		ItemAttributeModifiers copper,
		ItemAttributeModifiers iron,
		Holder<Attribute> attribute,
		ResourceLocation attributeID,
		float oxidizeProgress
	) {
		final Predicate<ItemAttributeModifiers.Entry> matchingEntry = entry -> entry.matches(attribute, attributeID);
		Optional<ItemAttributeModifiers.Entry> optionalCopper = copper.modifiers().stream().filter(matchingEntry).findFirst();
		Optional<ItemAttributeModifiers.Entry> optionalIron = iron.modifiers().stream().filter(matchingEntry).findFirst();
		if (optionalCopper.isEmpty() || optionalIron.isEmpty()) return Optional.empty();

		final ItemAttributeModifiers.Entry copperEntry = optionalCopper.get();
		final double amount = Mth.lerp(oxidizeProgress, copperEntry.modifier().amount(), optionalIron.get().modifier().amount());
		return Optional.of(
			new ItemAttributeModifiers.Entry(
				attribute,
				new AttributeModifier(attributeID, amount, AttributeModifier.Operation.ADD_VALUE),
				copperEntry.slot(),
				copperEntry.display()
			)
		);
	}
}
