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

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.clothconfig.FrozenClothConfig;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public final class TCAConfigGui {

	private static void setupEntries(@NotNull ConfigCategory category, @NotNull ConfigEntryBuilder entryBuilder) {
		var config = TCAConfig.get(true);
		var modifiedConfig = TCAConfig.getWithSync();
		Config<?> configInstance = TCAConfig.INSTANCE;
		var defaultConfig = TCAConfig.INSTANCE.defaultInstance();

		var copperFireEnabled = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("copper_fire_enabled"), modifiedConfig.copperFireEnabled)
					.setDefaultValue(defaultConfig.copperFireEnabled)
					.setSaveConsumer(newValue -> config.copperFireEnabled = newValue)
					.setTooltip(tooltip("copper_fire_enabled"))
					.build(),
				config.getClass(),
				"copperFireEnabled",
				configInstance
			)
		);

		var copperFirePoisons = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("copper_fire_poisons"), modifiedConfig.copperFirePoisons)
					.setDefaultValue(defaultConfig.copperFirePoisons)
					.setSaveConsumer(newValue -> config.copperFirePoisons = newValue)
					.setTooltip(tooltip("copper_fire_poisons"))
					.build(),
				config.getClass(),
				"copperFirePoisons",
				configInstance
			)
		);

		var copperButtonsInTrialChambers = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("copper_buttons_in_trial_chambers"), modifiedConfig.copperButtonsInTrialChambers)
					.setDefaultValue(defaultConfig.copperButtonsInTrialChambers)
					.setSaveConsumer(newValue -> config.copperButtonsInTrialChambers = newValue)
					.setTooltip(tooltip("copper_buttons_in_trial_chambers"))
					.build(),
				config.getClass(),
				"copperButtonsInTrialChambers",
				configInstance
			)
		);

		var copperChestsInTrialChambers = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("copper_chests_in_trial_chambers"), modifiedConfig.copperChestsInTrialChambers)
					.setDefaultValue(defaultConfig.copperChestsInTrialChambers)
					.setSaveConsumer(newValue -> config.copperChestsInTrialChambers = newValue)
					.setTooltip(tooltip("copper_chests_in_trial_chambers"))
					.build(),
					config.getClass(),
					"copperChestsInTrialChambers",
					configInstance
			)
		);

		var copperPressurePlatesInTrialChambers = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("copper_pressure_plates_in_trial_chambers"), modifiedConfig.copperPressurePlatesInTrialChambers)
					.setDefaultValue(defaultConfig.copperPressurePlatesInTrialChambers)
					.setSaveConsumer(newValue -> config.copperPressurePlatesInTrialChambers = newValue)
					.setTooltip(tooltip("copper_pressure_plates_in_trial_chambers"))
					.build(),
				config.getClass(),
				"copperPressurePlatesInTrialChambers",
				configInstance
			)
		);

		var copperGolemsPressButtons = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("copper_golems_press_buttons"), modifiedConfig.copperGolemsPressButtons)
					.setDefaultValue(defaultConfig.copperGolemsPressButtons)
					.setSaveConsumer(newValue -> config.copperGolemsPressButtons = newValue)
					.setTooltip(tooltip("copper_golems_press_buttons"))
					.build(),
				config.getClass(),
				"copperGolemsPressButtons",
				configInstance
			)
		);

		var gearboxEntityRotation = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startIntSlider(text("gearbox_entity_rotation"), (int) (modifiedConfig.gearboxEntityRotation * 100), 0, 10000)
					.setDefaultValue((int) (defaultConfig.gearboxEntityRotation * 100))
					.setSaveConsumer(newValue -> config.gearboxEntityRotation = newValue / 100F)
					.setTooltip(tooltip("gearbox_entity_rotation"))
					.build(),
				config.getClass(),
				"gearboxEntityRotation",
				configInstance
			)
		);

		var stickyGearboxRotationInterval = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startIntSlider(text("sticky_gearbox_rotation_interval"), modifiedConfig.stickyGearboxRotationInterval, 1, 500)
					.setDefaultValue(defaultConfig.stickyGearboxRotationInterval)
					.setSaveConsumer(newValue -> config.stickyGearboxRotationInterval = newValue)
					.setTooltip(tooltip("sticky_gearbox_rotation_interval"))
					.build(),
				config.getClass(),
				"stickyGearboxRotationInterval",
				configInstance
			)
		);

		var crateHasMenu = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("crate_has_menu"), modifiedConfig.crateHasMenu)
					.setDefaultValue(defaultConfig.crateHasMenu)
					.setSaveConsumer(newValue -> config.crateHasMenu = newValue)
					.setTooltip(tooltip("crate_has_menu"))
					.build(),
				config.getClass(),
				"crateHasMenu",
				configInstance
			)
		);

		var cratesDropWithItems = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("crates_drop_with_items"), modifiedConfig.cratesDropWithItems)
					.setDefaultValue(defaultConfig.cratesDropWithItems)
					.setSaveConsumer(newValue -> config.cratesDropWithItems = newValue)
					.setTooltip(tooltip("crates_drop_with_items"))
					.build(),
				config.getClass(),
				"cratesDropWithItems",
				configInstance
			)
		);

		var oxidizableCopperEquipment = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("oxidizable_copper_equipment"), modifiedConfig.oxidizableCopperEquipment)
					.setDefaultValue(defaultConfig.oxidizableCopperEquipment)
					.setSaveConsumer(newValue -> config.oxidizableCopperEquipment = newValue)
					.setTooltip(tooltip("oxidizable_copper_equipment"))
					.build(),
				config.getClass(),
				"oxidizableCopperEquipment",
				configInstance
			)
		);

		var copperParticles = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("copper_particles"), modifiedConfig.copperParticles)
					.setDefaultValue(defaultConfig.copperParticles)
					.setSaveConsumer(newValue -> config.copperParticles = newValue)
					.setTooltip(tooltip("copper_particles"))
					.build(),
				config.getClass(),
				"copperParticles",
				configInstance
			)
		);

		var betterCopperTooltips = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("better_copper_tooltips"), modifiedConfig.waxedItemIconOverlay)
					.setDefaultValue(defaultConfig.betterCopperTooltips)
					.setSaveConsumer(newValue -> config.betterCopperTooltips = newValue)
					.setTooltip(tooltip("better_copper_tooltips"))
					.build(),
				config.getClass(),
				"betterCopperTooltips",
				configInstance
			)
		);

		var waxedItemIconOverlay = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("waxed_item_icon_overlay"), modifiedConfig.waxedItemIconOverlay)
					.setDefaultValue(defaultConfig.waxedItemIconOverlay)
					.setSaveConsumer(newValue -> config.waxedItemIconOverlay = newValue)
					.setTooltip(tooltip("waxed_item_icon_overlay"))
					.build(),
				config.getClass(),
				"waxedItemIconOverlay",
				configInstance
			)
		);

		var extraItemIconOverlays = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(text("extra_item_icon_overlays"), modifiedConfig.extraItemIconOverlays)
					.setDefaultValue(defaultConfig.extraItemIconOverlays)
					.setSaveConsumer(newValue -> config.extraItemIconOverlays = newValue)
					.setTooltip(tooltip("extra_item_icon_overlays"))
					.build(),
				config.getClass(),
				"extraItemIconOverlays",
				configInstance
			)
		);

	}

	public static Screen buildScreen(Screen parent) {
		var configBuilder = ConfigBuilder.create().setParentScreen(parent).setTitle(text("component.title"));
		configBuilder.setSavingRunnable(TCAConfig.INSTANCE::save);
		var config = configBuilder.getOrCreateCategory(text("config"));
		ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();
		setupEntries(config, entryBuilder);
		return configBuilder.build();
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull Component text(String key) {
		return Component.translatable("option." + TCAConstants.MOD_ID + "." + key);
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull Component tooltip(String key) {
		return Component.translatable("tooltip." + TCAConstants.MOD_ID + "." + key);
	}

	@Contract(value = "_ -> new", pure = true)
	public static @NotNull Component enumNameProvider(String key) {
		return Component.translatable("enum." + TCAConstants.MOD_ID + "." + key);
	}
}
