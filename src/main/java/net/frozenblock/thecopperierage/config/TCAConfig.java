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
				COPPER_PARTICLES = config.copperParticles;
			}
		}
	);

	public static volatile boolean OXIDIZABLE_COPPER_EQUIPMENT = true;
	public static volatile boolean COPPER_PARTICLES = true;

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

	@EntrySyncData("oxidizableCopperEquipment")
	public boolean oxidizableCopperEquipment = true;

	@EntrySyncData(value = "copperParticles", behavior = SyncBehavior.UNSYNCABLE)
	public boolean copperParticles = true;

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
