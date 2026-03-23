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

package net.frozenblock.thecopperierage.config;

import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.json.JsonConfig;
import net.frozenblock.lib.config.api.instance.json.JsonType;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.config.api.sync.SyncBehavior;
import net.frozenblock.lib.config.api.sync.annotation.EntrySyncData;
import net.frozenblock.thecopperierage.TCAConstants;

public class TCAConfig {

	public static final Config<TCAConfig> INSTANCE = ConfigRegistry.register(
		new JsonConfig<>(
			TCAConstants.MOD_ID,
			TCAConfig.class,
			JsonType.JSON5_UNQUOTED_KEYS,
			true
		) {
			@Override
			public void onSave() throws Exception {
				super.onSave();
				this.onSync(null);
			}

			@Override
			public void onSync(TCAConfig syncInstance) {
				var config = this.config();
				OXIDIZABLE_COPPER_EQUIPMENT = config.oxidizableCopperEquipment;
				COPPER_GOLEMS_PRESS_BUTTONS = config.copperGolemsPressButtons;
				COPPER_PARTICLES = config.copperParticles;
				BETTER_COPPER_TOOLTIPS = config.betterCopperTooltips;
				WAXED_ITEM_ICON_OVERLAY = config.waxedItemIconOverlay;
				EXTRA_ITEM_ICON_OVERLAYS = config.extraItemIconOverlays;
				IMPROVED_FURNACE_MINECARTS = config.improvedFurnaceMinecarts;
				IMPROVED_VEHICLE_CHESTS = config.improvedVehicleChests;
			}
		}
	);

	public static volatile boolean OXIDIZABLE_COPPER_EQUIPMENT = true;
	public static volatile boolean COPPER_GOLEMS_PRESS_BUTTONS = true;
	public static volatile boolean COPPER_PARTICLES = true;
	public static volatile boolean BETTER_COPPER_TOOLTIPS = true;
	public static volatile boolean WAXED_ITEM_ICON_OVERLAY = true;
	public static volatile boolean EXTRA_ITEM_ICON_OVERLAYS = false;
	public static volatile boolean IMPROVED_FURNACE_MINECARTS = true;
	public static volatile boolean IMPROVED_VEHICLE_CHESTS = true;

	@EntrySyncData("copperFireEnabled")
	public boolean copperFireEnabled = true;

	@EntrySyncData("copperFirePoisons")
	public boolean copperFirePoisons = true;

	@EntrySyncData("copperButtonsInTrialChambers")
	public boolean copperButtonsInTrialChambers = true;

	@EntrySyncData("copperChestsInTrialChambers")
	public boolean copperChestsInTrialChambers = true;

	@EntrySyncData("copperPressurePlatesInTrialChambers")
	public boolean copperPressurePlatesInTrialChambers = true;

	@EntrySyncData("copperGolemsPressButtons")
	public boolean copperGolemsPressButtons = true;

	@EntrySyncData("oxidizableCopperEquipment")
	public boolean oxidizableCopperEquipment = true;

	@EntrySyncData(value = "copperParticles", behavior = SyncBehavior.UNSYNCABLE)
	public boolean copperParticles = true;

	@EntrySyncData("betterCopperTooltips")
	public boolean betterCopperTooltips = true;

	@EntrySyncData("waxedItemIconOverlay")
	public boolean waxedItemIconOverlay = true;

	@EntrySyncData("extraItemIconOverlays")
	public boolean extraItemIconOverlays = false;

	@EntrySyncData("improvedFurnaceMinecarts")
	public boolean improvedFurnaceMinecarts = true;

	@EntrySyncData("improvedVehicleChests")
	public boolean improvedVehicleChests = true;

	public static TCAConfig get(boolean real) {
		if (real) return INSTANCE.instance();
		return INSTANCE.config();
	}

	public static TCAConfig get() {
		return get(false);
	}

	public static TCAConfig getWithSync() {
		return INSTANCE.configWithSync();
	}
}
