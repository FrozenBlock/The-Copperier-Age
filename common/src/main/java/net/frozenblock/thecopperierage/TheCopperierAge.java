package net.frozenblock.thecopperierage;

import net.frozenblock.lib.feature_flag.api.FeatureFlagApi;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.entity.coupling.MinecartCouplingPhysics;
import net.frozenblock.thecopperierage.item.api.OxidizableItemHelper;
import net.frozenblock.thecopperierage.levelgen.structure.modification.TCAStructureModifications;
import net.frozenblock.thecopperierage.networking.TCANetworking;
import net.frozenblock.thecopperierage.registry.TCAAttachmentTypes;
import net.frozenblock.thecopperierage.registry.TCABlockEntityTypes;
import net.frozenblock.thecopperierage.registry.TCABlocks;
import net.frozenblock.thecopperierage.registry.TCACreativeInventorySorting;
import net.frozenblock.thecopperierage.registry.TCADataComponents;
import net.frozenblock.thecopperierage.registry.TCAEntityTypes;
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

public final class TheCopperierAge {

	public static void init() {
		TCAFeatureFlags.init();
		FeatureFlagApi.rebuild();
		TCAAttachmentTypes.init();
		MinecartCouplingPhysics.init();

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
		TCAStructureModifications.init();
		TCANetworking.init();
		TCAResources.init();

		TCAConfig.CONFIG.load(true);
	}

	public static void setup() {
		TCABlocks.setupBlockProperties();
		TCACreativeInventorySorting.setup();
	}

	private TheCopperierAge() {}
}
