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

package net.frozenblock.thecopperierage.item.api;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public final class OxidizableItemHelper {
	private static final Map<Item, Pair<ItemAttributeModifiers, ItemAttributeModifiers>> OXIDIZABLE_ATTRIBUTES = new Object2ObjectLinkedOpenHashMap<>();
	private static final float COPPER_MINING_SPEED = ToolMaterial.COPPER.speed();
	private static final float IRON_MINING_SPEED = ToolMaterial.IRON.speed();
	public static final float EXPOSED_THRESHOLD = 0.45F;
	public static final float WEATHERED_THRESHOLD = 0.65F;
	public static final float OXIDIZED_THRESHOLD = 0.85F;

	public static void bootstrap() {
		addOxidizableAttributesItem(Items.COPPER_SWORD, Items.IRON_SWORD);
		addOxidizableAttributesItem(Items.COPPER_SHOVEL, Items.IRON_SHOVEL);
		addOxidizableAttributesItem(Items.COPPER_PICKAXE, Items.IRON_PICKAXE);
		addOxidizableAttributesItem(Items.COPPER_AXE, Items.IRON_AXE);
		addOxidizableAttributesItem(Items.COPPER_HOE, Items.IRON_HOE);
	}

	public static void addOxidizableAttributesItem(@NotNull Item copper, @NotNull Item iron) {
		final DataComponentMap copperComponents = copper.components();
		final DataComponentMap ironComponents = iron.components();

		final ItemAttributeModifiers copperAttributes = copperComponents.get(DataComponents.ATTRIBUTE_MODIFIERS);
		final ItemAttributeModifiers ironAttributes = ironComponents.get(DataComponents.ATTRIBUTE_MODIFIERS);
		if (copperAttributes == null || ironAttributes == null) return;

		OXIDIZABLE_ATTRIBUTES.put(copper, Pair.of(copperAttributes, ironAttributes));
	}

	public static float getOxidizeProgress(@NotNull ItemStack stack) {
		return getOxidizeProgress(stack, OptionalInt.empty());
	}

	public static float getOxidizeProgress(@NotNull ItemStack stack, @NotNull OptionalInt optionalInt) {
		final float damage = optionalInt.orElse(stack.getDamageValue());
		final float maxDamage = stack.getMaxDamage();
		final float damageProgress = Mth.clamp(damage / maxDamage, 0F, 1F);
		if (damageProgress >= OXIDIZED_THRESHOLD) return 1F;
		if (damageProgress >= WEATHERED_THRESHOLD) return 0.65F;
		if (damageProgress >= EXPOSED_THRESHOLD) return 0.35F;
		return 0F;
	}

	public static void onDamageUpdated(@NotNull ItemStack stack, int damageValue) {
		// TODO: Config
		final Item item = stack.getItem();
		updateMiningSpeed(stack, item, damageValue);
		updateAttributes(stack, item, damageValue);
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
		@NotNull ItemAttributeModifiers copper,
		@NotNull ItemAttributeModifiers iron,
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
