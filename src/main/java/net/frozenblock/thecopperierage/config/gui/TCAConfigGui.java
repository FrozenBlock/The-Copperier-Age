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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.clothconfig.FrozenClothConfig;
import net.frozenblock.thecopperierage.TCAConstants;
import static net.frozenblock.thecopperierage.TCAConstants.text;
import net.frozenblock.thecopperierage.config.TCAConfig;
import static net.frozenblock.thecopperierage.config.gui.TCAConfigGuiHelper.booleanEntry;
import static net.frozenblock.thecopperierage.config.gui.TCAConfigGuiHelper.intSliderEntry;
import net.minecraft.client.gui.screens.Screen;

@Environment(EnvType.CLIENT)
public final class TCAConfigGui {

	private static void setupEntries(ConfigCategory category, ConfigEntryBuilder builder) {
		category.addEntry(booleanEntry(builder, "copper_fire_enabled", TCAConfig.COPPER_FIRE_ENABLED));
		category.addEntry(booleanEntry(builder, "copper_fire_poisons", TCAConfig.COPPER_FIRE_POISONS));
		category.addEntry(booleanEntry(builder, "copper_buttons_in_trial_chambers", TCAConfig.COPPER_BUTTONS_IN_TRIAL_CHAMBERS));
		category.addEntry(booleanEntry(builder, "copper_chests_in_trial_chambers", TCAConfig.COPPER_CHESTS_IN_TRIAL_CHAMBERS));
		category.addEntry(booleanEntry(builder, "copper_pressure_plates_in_trial_chambers", TCAConfig.COPPER_PRESSURE_PLATES_IN_TRIAL_CHAMBERS));
		category.addEntry(
			FrozenClothConfig.syncedEntry(
				(AbstractConfigListEntry) builder.startIntSlider(text("gearbox_entity_rotation"), (int) (TCAConfig.GEARBOX_ENTITY_ROTATION.get() * 100), 0, 10000)
					.setDefaultValue((int) (TCAConfig.GEARBOX_ENTITY_ROTATION.defaultValue() * 100))
					.setSaveConsumer(newValue -> TCAConfig.GEARBOX_ENTITY_ROTATION.setValue(newValue / 100F))
					.setTooltip(TCAConstants.tooltip("gearbox_entity_rotation"))
					.build(),
				TCAConfig.GEARBOX_ENTITY_ROTATION
			)
		);
		category.addEntry(booleanEntry(builder, "crate_has_menu", TCAConfig.CRATE_HAS_MENU));
		category.addEntry(booleanEntry(builder, "crates_drop_with_items", TCAConfig.CRATES_DROP_WITH_ITEMS));
		category.addEntry(intSliderEntry(builder, "sticky_gearbox_rotation_interval", TCAConfig.STICKY_GEARBOX_ROTATION_INTERVAL, 1, 500));
		category.addEntry(booleanEntry(builder, "copper_golems_press_buttons", TCAConfig.COPPER_GOLEMS_PRESS_BUTTONS));
		category.addEntry(booleanEntry(builder, "oxidizable_copper_equipment", TCAConfig.OXIDIZABLE_COPPER_EQUIPMENT));
		category.addEntry(booleanEntry(builder, "oxidizing_affects_stats", TCAConfig.OXIDIZING_AFFECTS_STATS));
		category.addEntry(booleanEntry(builder, "copper_particles", TCAConfig.COPPER_PARTICLES));
		category.addEntry(booleanEntry(builder, "better_copper_tooltips", TCAConfig.BETTER_COPPER_TOOLTIPS));
		category.addEntry(booleanEntry(builder, "waxed_item_icon_overlay", TCAConfig.WAXED_ITEM_ICON_OVERLAY));
		category.addEntry(booleanEntry(builder, "extra_item_icon_overlays", TCAConfig.EXTRA_ITEM_ICON_OVERLAYS));
	}

	public static Screen buildScreen(Screen parent) {
		final ConfigBuilder configBuilder = ConfigBuilder.create().setParentScreen(parent).setTitle(text("component.title"));
		configBuilder.setSavingRunnable(TCAConfig.CONFIG::save);

		final ConfigCategory category = configBuilder.getOrCreateCategory(text("config"));
		final ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
		setupEntries(category, entryBuilder);

		return configBuilder.build();
	}
}
