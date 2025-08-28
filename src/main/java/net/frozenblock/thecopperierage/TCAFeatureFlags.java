/*
 * Copyright 2025 FrozenBlock
 * This file is part of Netherier Nether.
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

package net.frozenblock.thecopperierage;

import net.frozenblock.lib.FrozenBools;
import net.frozenblock.lib.feature_flag.api.FeatureFlagApi;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

public final class TCAFeatureFlags {
	public static final FeatureFlag THE_COPPERIER_AGE = FeatureFlagApi.builder.create(TCAConstants.id(TCAConstants.MOD_ID));
	public static final FeatureFlagSet THE_COPPERIER_AGE_FLAG_SET = FeatureFlagSet.of(THE_COPPERIER_AGE);

	public static final FeatureFlag FEATURE_FLAG = FrozenBools.IS_DATAGEN ? THE_COPPERIER_AGE : FeatureFlags.VANILLA;

	public static void init() {
	}
}
