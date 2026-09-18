/*
 * Copyright 2026 FrozenBlock
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

package net.frozenblock.thecopperierage.registry;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.frozenblock.lib.levelgen.structure.api.processor.BlockStateRespectingProcessorRule;
import net.frozenblock.lib.levelgen.structure.api.processor.BlockStateRespectingRuleProcessor;
import net.frozenblock.lib.levelgen.structure.api.processor.StructureProcessorListAdditions;
import net.frozenblock.lib.levelgen.structure.impl.processor.StructureProcessorListAddition;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;

public final class TCAStructureProcessorListAdditions {

	public static void bootstrap(BootstrapContext<StructureProcessorListAddition> context) {
		final HolderGetter<Structure> structureSets = context.lookup(Registries.STRUCTURE);
		final HolderSet<Structure> trialChambers = HolderSet.direct(structureSets.getOrThrow(BuiltinStructures.TRIAL_CHAMBERS));

		StructureProcessorListAdditions.register(
			context,
			TCAConstants.id("trial_chambers_copper_buttons"),
			trialChambers,
			List.of(
				new BlockStateRespectingRuleProcessor(
					ImmutableList.of(
						new BlockStateRespectingProcessorRule(new BlockMatchTest(Blocks.OAK_BUTTON), AlwaysTrueTest.INSTANCE, TCABlocks.COPPER_BUTTON.waxed().unaffected().get())
					)
				)
			),
			TCAConfig.COPPER_BUTTONS_IN_TRIAL_CHAMBERS.equalTo(true)
		);

		StructureProcessorListAdditions.register(
			context,
			TCAConstants.id("trial_chambers_copper_chests"),
			trialChambers,
			List.of(
				new BlockStateRespectingRuleProcessor(
					ImmutableList.of(
						new BlockStateRespectingProcessorRule(new BlockMatchTest(Blocks.CHEST), AlwaysTrueTest.INSTANCE, Blocks.COPPER_CHEST.waxed().unaffected())
					)
				)
			),
			TCAConfig.COPPER_CHESTS_IN_TRIAL_CHAMBERS.equalTo(true)
		);

		StructureProcessorListAdditions.register(
			context,
			TCAConstants.id("trial_chambers_copper_pressure_plates"),
			trialChambers,
			List.of(
				new BlockStateRespectingRuleProcessor(
					ImmutableList.of(
						new BlockStateRespectingProcessorRule(new BlockMatchTest(Blocks.OAK_PRESSURE_PLATE), AlwaysTrueTest.INSTANCE, TCABlocks.WEIGHTED_PRESSURE_PLATE.waxed().unaffected().get())
					)
				)
			),
			TCAConfig.COPPER_PRESSURE_PLATES_IN_TRIAL_CHAMBERS.equalTo(true)
		);
	}

	private TCAStructureProcessorListAdditions() {}
}
