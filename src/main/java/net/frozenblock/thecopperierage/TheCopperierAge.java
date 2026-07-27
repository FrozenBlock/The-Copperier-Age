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

package net.frozenblock.thecopperierage;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.loader.api.ModContainer;
import net.frozenblock.lib.block.impl.fire.FireData;
import net.frozenblock.lib.entrypoint.api.FrozenModInitializer;
import net.frozenblock.lib.feature_flag.api.FeatureFlagApi;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.item.api.OxidizableItemHelper;
import net.frozenblock.thecopperierage.levelgen.structure.modification.TCAStructureModifications;
import net.frozenblock.thecopperierage.mod_compat.audioplayer.AudioPlayerIntegration;
import net.frozenblock.thecopperierage.networking.TCANetworking;
import net.frozenblock.thecopperierage.registry.TCAAttachments;
import net.frozenblock.thecopperierage.registry.TCABlockEntityTypes;
import net.frozenblock.thecopperierage.registry.TCABlocks;
import net.frozenblock.thecopperierage.registry.TCACreativeInventorySorting;
import net.frozenblock.thecopperierage.registry.TCADataComponents;
import net.frozenblock.thecopperierage.registry.TCAEntityTypes;
import net.frozenblock.thecopperierage.registry.TCAFireTypes;
import net.frozenblock.thecopperierage.registry.TCAItems;
import net.frozenblock.thecopperierage.registry.TCAMemoryModuleTypes;
import net.frozenblock.thecopperierage.registry.TCAMenuTypes;
import net.frozenblock.thecopperierage.registry.TCAParticleTypes;
import net.frozenblock.thecopperierage.registry.TCAPoiTypes;
import net.frozenblock.thecopperierage.registry.TCARecipeTypes;
import net.frozenblock.thecopperierage.registry.TCAResources;
import net.frozenblock.thecopperierage.registry.TCASensorTypes;
import net.frozenblock.thecopperierage.registry.TCASoundPredicates;
import net.frozenblock.thecopperierage.registry.TCASounds;
import net.frozenblock.thecopperierage.registry.TCAStats;
import net.frozenblock.thecopperierage.registry.TCAWindDisturbances;
import net.frozenblock.thecopperierage.tag.TCAEntityTypeTags;

public final class TheCopperierAge extends FrozenModInitializer {

	public TheCopperierAge() {
		super(TCAConstants.MOD_ID);
	}

	@Override
	public void onInitialize(String modId, ModContainer container) {
		TCAFeatureFlags.init();
		FeatureFlagApi.rebuild();
		TCAAttachments.init();

		TCABlocks.init();
		TCABlockEntityTypes.init();
		TCAEntityTypes.init();
		TCAItems.init();
		TCADataComponents.init();
		TCARecipeTypes.init();
		OxidizableItemHelper.init();
		TCAMemoryModuleTypes.init();
		TCASensorTypes.init();
		TCAPoiTypes.init();
		TCASounds.init();
		TCAStats.init();
		TCACreativeInventorySorting.init();
		TCAParticleTypes.init();
		TCAMenuTypes.init();
		TCABlocks.registerBlockProperties();
		TCASoundPredicates.init();
		TCAWindDisturbances.init();
		TCAStructureModifications.init();
		TCANetworking.init();
		TCAResources.init(container);
		AudioPlayerIntegration.init();

		ServerEntityEvents.ENTITY_LOAD.register(((entity, level) -> {
			if (!(entity.is(TCAEntityTypeTags.COPPER) && TCAConfig.COPPER_FIRE_ENABLED.get())) return;
			FireData.trySet(entity, level.registryAccess().lookupOrThrow(FrozenLibRegistries.FIRE_TYPE).getOrThrow(TCAFireTypes.COPPER_FIRE));
		}));

		TCAConfig.CONFIG.load(true);
	}

}
