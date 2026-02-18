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

package net.frozenblock.thecopperierage.config;

import net.frozenblock.lib.config.v2.config.ConfigData;
import net.frozenblock.lib.config.v2.config.ConfigSettings;
import net.frozenblock.lib.config.v2.entry.ConfigEntry;
import net.frozenblock.lib.config.v2.entry.EntryType;
import net.frozenblock.lib.config.v2.registry.ID;
import net.frozenblock.thecopperierage.TCAConstants;

public class TCAConfig {
	public static final ConfigData<?> CONFIG = ConfigData.createAndRegister(ID.of(TCAConstants.id("main")), ConfigSettings.JSON5);

	public static final ConfigEntry<Boolean> COPPER_FIRE_ENABLED = CONFIG.entry("copperFireEnabled", EntryType.BOOL, true);
	public static final ConfigEntry<Boolean> COPPER_FIRE_POISONS = CONFIG.entry("copperFirePoisons", EntryType.BOOL, true);
	public static final ConfigEntry<Boolean> COPPER_BUTTONS_IN_TRIAL_CHAMBERS = CONFIG.entryBuilder("copperButtonsInTrialChambers", EntryType.BOOL, true).requireRestart().build();
	public static final ConfigEntry<Boolean> COPPER_CHESTS_IN_TRIAL_CHAMBERS = CONFIG.entryBuilder("copperChestsInTrialChambers", EntryType.BOOL, true).requireRestart().build();
	public static final ConfigEntry<Boolean> COPPER_PRESSURE_PLATES_IN_TRIAL_CHAMBERS = CONFIG.entryBuilder("copperPressurePlatesInTrialChambers", EntryType.BOOL, true).requireRestart().build();
	public static final ConfigEntry<Boolean> OXIDIZABLE_COPPER_EQUIPMENT = CONFIG.entry("oxidizableCopperEquipment", EntryType.BOOL, true);
	public static final ConfigEntry<Boolean> COPPER_PARTICLES = CONFIG.unsyncableEntry("copperParticles", EntryType.BOOL, true);
}
