package net.frozenblock.thecopperierage;

import net.frozenblock.lib.block.impl.fire.FireData;
import net.frozenblock.lib.event.api.events.EntityLifecycleEvents;
import net.frozenblock.lib.feature_flag.api.FeatureFlagApi;
import net.frozenblock.lib.registry.FrozenLibRegistries;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.item.api.OxidizableItemHelper;
import net.frozenblock.thecopperierage.levelgen.structure.modification.TCAStructureModifications;
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

public final class TheCopperierAge {

	public static void init() {
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
		TCAParticleTypes.init();
		TCAMenuTypes.init();
		TCASoundPredicates.init();
		TCAWindDisturbances.init();
		TCANetworking.init();
		TCAResources.init();

		EntityLifecycleEvents.ENTITY_LOAD.register(((entity, level) -> {
			if (!(entity.is(TCAEntityTypeTags.COPPER) && TCAConfig.COPPER_FIRE_ENABLED.get())) return;
			FireData.trySet(entity, level.registryAccess().lookupOrThrow(FrozenLibRegistries.FIRE_TYPE).getOrThrow(TCAFireTypes.COPPER_FIRE));
		}));

		TCAConfig.CONFIG.load(true);
	}

	public static void setup() {
		TCABlocks.registerBlockProperties();
		TCACreativeInventorySorting.init();
		TCAStructureModifications.init();
	}
}
