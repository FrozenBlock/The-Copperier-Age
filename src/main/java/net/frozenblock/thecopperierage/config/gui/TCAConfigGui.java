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

package net.frozenblock.thecopperierage.config.gui;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import static net.frozenblock.thecopperierage.TCAConstants.text;
import net.frozenblock.thecopperierage.config.TCAConfig;
import static net.frozenblock.thecopperierage.config.gui.TCAConfigGuiHelper.booleanEntry;
import net.minecraft.client.gui.screens.Screen;

@Environment(EnvType.CLIENT)
public final class TCAConfigGui {

	private static void setupEntries(ConfigCategory category, ConfigEntryBuilder builder) {
		category.addEntry(booleanEntry(builder, "copper_fire_enabled", TCAConfig.COPPER_FIRE_ENABLED));
		category.addEntry(booleanEntry(builder, "copper_fire_poisons", TCAConfig.COPPER_FIRE_POISONS));
		category.addEntry(booleanEntry(builder, "copper_buttons_in_trial_chambers", TCAConfig.COPPER_BUTTONS_IN_TRIAL_CHAMBERS));
		category.addEntry(booleanEntry(builder, "copper_chests_in_trial_chambers", TCAConfig.COPPER_CHESTS_IN_TRIAL_CHAMBERS));
		category.addEntry(booleanEntry(builder, "copper_pressure_plates_in_trial_chambers", TCAConfig.COPPER_PRESSURE_PLATES_IN_TRIAL_CHAMBERS));
		category.addEntry(booleanEntry(builder, "oxidizable_copper_equipment", TCAConfig.OXIDIZABLE_COPPER_EQUIPMENT));
		category.addEntry(booleanEntry(builder, "copper_particles", TCAConfig.COPPER_PARTICLES));
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
