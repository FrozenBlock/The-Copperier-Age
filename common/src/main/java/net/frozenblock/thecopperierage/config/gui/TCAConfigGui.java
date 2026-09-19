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

package net.frozenblock.thecopperierage.config.gui;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.frozenblock.lib.config.clothconfig.FrozenLibClothConfigGuiHelper;
import static net.frozenblock.lib.config.clothconfig.FrozenLibClothConfigGuiHelper.createSubCategory;
import static net.frozenblock.thecopperierage.TCAConstants.text;
import static net.frozenblock.thecopperierage.TCAConstants.tooltip;
import net.frozenblock.thecopperierage.config.TCAConfig;
import static net.frozenblock.thecopperierage.config.gui.TCAConfigGuiHelper.booleanEntry;
import static net.frozenblock.thecopperierage.config.gui.TCAConfigGuiHelper.intSliderEntry;
import net.frozenblock.thecopperierage.references.TCABlockIds;
import net.frozenblock.thecopperierage.references.TCABlockItemIds;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.DependantName;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypeIds;
import net.minecraft.world.level.block.Block;

@ClientOnly
public final class TCAConfigGui {

	private static void setupEntries(ConfigCategory category, ConfigEntryBuilder builder) {
		final DependantName<Block, Component> blockName = id -> Component.translatable(Util.makeDescriptionId("block", id.identifier()));
		final DependantName<EntityType<?>, Component> entityName = id -> Component.translatable(Util.makeDescriptionId("entity", id.identifier()));

		createSubCategory(builder, category, blockName.get(TCABlockIds.COPPER_FIRE), tooltip("copper_fire"),
			booleanEntry(builder, "copper_fire_enabled", TCAConfig.COPPER_FIRE_ENABLED),
			booleanEntry(builder, "copper_fire_poisons", TCAConfig.COPPER_FIRE_POISONS),
			booleanEntry(builder, "copper_particles", TCAConfig.COPPER_PARTICLES)
		);

		createSubCategory(builder, category, text("trial_chambers"), tooltip("trial_chambers"),
			booleanEntry(builder, "copper_buttons_in_trial_chambers", TCAConfig.COPPER_BUTTONS_IN_TRIAL_CHAMBERS),
			booleanEntry(builder, "copper_chests_in_trial_chambers", TCAConfig.COPPER_CHESTS_IN_TRIAL_CHAMBERS),
			booleanEntry(builder, "copper_pressure_plates_in_trial_chambers", TCAConfig.COPPER_PRESSURE_PLATES_IN_TRIAL_CHAMBERS)
		);

		createSubCategory(builder, category, entityName.get(EntityTypeIds.COPPER_GOLEM), tooltip("copper_golem"),
			booleanEntry(builder, "copper_golems_press_buttons", TCAConfig.COPPER_GOLEMS_PRESS_BUTTONS)
		);

		createSubCategory(builder, category, blockName.get(TCABlockItemIds.GEARBOX.weathering().unaffected().block()), tooltip("gearbox"),
			FrozenLibClothConfigGuiHelper.syncedEntry(
				(AbstractConfigListEntry) builder.startIntSlider(text("gearbox_entity_rotation"), (int) (TCAConfig.GEARBOX_ENTITY_ROTATION.get() * 100), 0, 10000)
					.setDefaultValue((int) (TCAConfig.GEARBOX_ENTITY_ROTATION.defaultValue() * 100))
					.setSaveConsumer(newValue -> TCAConfig.GEARBOX_ENTITY_ROTATION.setValue(newValue / 100F))
					.setTooltip(tooltip("gearbox_entity_rotation"))
					.build(),
				TCAConfig.GEARBOX_ENTITY_ROTATION
			),
			intSliderEntry(builder, "sticky_gearbox_rotation_interval", TCAConfig.STICKY_GEARBOX_ROTATION_INTERVAL, 1, 500)
		);

		createSubCategory(builder, category, blockName.get(TCABlockItemIds.CRATE.block()), tooltip("crate"),
			booleanEntry(builder, "crate_has_menu", TCAConfig.CRATE_HAS_MENU),
			booleanEntry(builder, "crates_drop_with_items", TCAConfig.CRATES_DROP_WITH_ITEMS)
		);

		createSubCategory(builder, category, text("copper_equipment"), tooltip("copper_equipment"),
			booleanEntry(builder, "oxidizable_copper_equipment", TCAConfig.OXIDIZABLE_COPPER_EQUIPMENT),
			booleanEntry(builder, "oxidizing_affects_stats", TCAConfig.OXIDIZING_AFFECTS_STATS)
		);

		createSubCategory(builder, category, text("item"), tooltip("item"),
			booleanEntry(builder, "better_copper_tooltips", TCAConfig.BETTER_COPPER_TOOLTIPS),
			booleanEntry(builder, "waxed_item_icon_overlay", TCAConfig.WAXED_ITEM_ICON_OVERLAY),
			booleanEntry(builder, "extra_item_icon_overlays", TCAConfig.EXTRA_ITEM_ICON_OVERLAYS)
		);

		createSubCategory(builder, category, entityName.get(EntityTypeIds.MINECART), tooltip("minecart"),
			booleanEntry(builder, "improved_furnace_minecarts", TCAConfig.IMPROVED_FURNACE_MINECARTS),
			booleanEntry(builder, "minecart_collisions", TCAConfig.MINECART_COLLISIONS),
			booleanEntry(builder, "minecart_camera_follows_motion", TCAConfig.MINECART_CAMERA_FOLLOWS_MOTION),
			booleanEntry(builder, "debug_minecart_motion", TCAConfig.DEBUG_MINECART_MOTION)
		);
	}

	public static Screen buildScreen(Screen parent) {
		final ConfigBuilder configBuilder = ConfigBuilder.create().setParentScreen(parent).setTitle(text("component.title"));
		configBuilder.setSavingRunnable(TCAConfig.CONFIG::save);

		final ConfigCategory category = configBuilder.getOrCreateCategory(text("config"));
		final ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
		setupEntries(category, entryBuilder);

		return configBuilder.build();
	}

	private TCAConfigGui() {}
}
