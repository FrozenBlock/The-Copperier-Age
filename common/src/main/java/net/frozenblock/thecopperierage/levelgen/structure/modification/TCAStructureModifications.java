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

package net.frozenblock.thecopperierage.levelgen.structure.modification;

import com.google.common.collect.ImmutableList;
import net.frozenblock.lib.levelgen.structure.api.processor.BlockStateRespectingProcessorRule;
import net.frozenblock.lib.levelgen.structure.api.processor.BlockStateRespectingRuleProcessor;
import net.frozenblock.lib.levelgen.structure.api.processor.StructureProcessorApi;
import net.frozenblock.lib.levelgen.structure.api.pools.TemplatePoolApi;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.registry.TCABlocks;
import net.frozenblock.thecopperierage.registry.TCAResources;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

public final class TCAStructureModifications {

	public static void init() {
		final Identifier trialChambers = BuiltinStructures.TRIAL_CHAMBERS.identifier();

		if (TCAConfig.COPPER_BUTTONS_IN_TRIAL_CHAMBERS.get()) {
			StructureProcessorApi.addProcessor(
				trialChambers,
				new BlockStateRespectingRuleProcessor(
					ImmutableList.of(
						new BlockStateRespectingProcessorRule(new BlockMatchTest(Blocks.OAK_BUTTON), AlwaysTrueTest.INSTANCE, TCABlocks.COPPER_BUTTON.waxed().unaffected().get())
					)
				)
			);
		}

		if (TCAConfig.COPPER_CHESTS_IN_TRIAL_CHAMBERS.get()) {
			StructureProcessorApi.addProcessor(
				trialChambers,
				new BlockStateRespectingRuleProcessor(
					ImmutableList.of(
						new BlockStateRespectingProcessorRule(new BlockMatchTest(Blocks.CHEST), AlwaysTrueTest.INSTANCE, Blocks.COPPER_CHEST.waxed().unaffected())
					)
				)
			);
		}

		if (TCAConfig.COPPER_PRESSURE_PLATES_IN_TRIAL_CHAMBERS.get()) {
			StructureProcessorApi.addProcessor(
				trialChambers,
				new BlockStateRespectingRuleProcessor(
					ImmutableList.of(
						new BlockStateRespectingProcessorRule(new BlockMatchTest(Blocks.OAK_PRESSURE_PLATE), AlwaysTrueTest.INSTANCE, TCABlocks.WEIGHTED_PRESSURE_PLATE.waxed().unaffected().get())
					)
				)
			);
		}

		TemplatePoolApi.ADD_ADDITIONAL_TEMPLATE_POOLS.register((processorLookup, context) -> {
			if (!TCAResources.HAS_TRICKIER_TRIALS_PACK) return;

			final Holder<StructureProcessorList> copperBulbDegradation = processorLookup
				.get(ProcessorLists.TRIAL_CHAMBERS_COPPER_BULB_DEGRADATION)
				.orElseGet(() -> processorLookup.getOrThrow(ProcessorLists.EMPTY));

			final Identifier hallway = Identifier.withDefaultNamespace("trial_chambers/hallway");
			final Identifier end =Identifier.withDefaultNamespace("trial_chambers/chambers/end");

			// Trials
			context.addElement(
				hallway,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/chamber/gated_treasure"), copperBulbDegradation),
				150,
				StructureTemplatePool.Projection.RIGID
			);
			context.addElement(
				hallway,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/chamber/jumping_wind"), copperBulbDegradation),
				150,
				StructureTemplatePool.Projection.RIGID
			);
			context.addElement(
				hallway,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/chamber/mexican_restaurant"), copperBulbDegradation),
				150,
				StructureTemplatePool.Projection.RIGID
			);
			context.addElement(
				hallway,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/chamber/wind_chamber"), copperBulbDegradation),
				150,
				StructureTemplatePool.Projection.RIGID
			);
			context.addElement(
				hallway,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/chamber/wind_trap"), copperBulbDegradation),
				150,
				StructureTemplatePool.Projection.RIGID
			);
			context.addElement(
				hallway,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/chamber/windy_pit"), copperBulbDegradation),
				150,
				StructureTemplatePool.Projection.RIGID
			);

			// Common Encounters
			context.addElement(
				hallway,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/hallway/factory_encounter"), copperBulbDegradation),
				50,
				StructureTemplatePool.Projection.RIGID
			);
			context.addElement(
				hallway,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/hallway/large_fan_encounter"), copperBulbDegradation),
				50,
				StructureTemplatePool.Projection.RIGID
			);

			// Encounters
			context.addElement(
				hallway,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/hallway/fan_encounter_1"), copperBulbDegradation),
				1,
				StructureTemplatePool.Projection.RIGID
			);
			context.addElement(
				hallway,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/hallway/fan_encounter_2"), copperBulbDegradation),
				1,
				StructureTemplatePool.Projection.RIGID
			);
			context.addElement(
				hallway,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/hallway/fan_encounter_3"), copperBulbDegradation),
				1,
				StructureTemplatePool.Projection.RIGID
			);

			// End
			context.addElement(
				end,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/chamber/fan_eruption"), copperBulbDegradation),
				1,
				StructureTemplatePool.Projection.RIGID
			);
		});
	}
}
