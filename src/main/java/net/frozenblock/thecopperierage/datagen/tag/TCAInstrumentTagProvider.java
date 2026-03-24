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

package net.frozenblock.thecopperierage.datagen.tag;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.frozenblock.thecopperierage.registry.TCAInstruments;
import net.frozenblock.thecopperierage.tag.TCAInstrumentTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Instrument;

public final class TCAInstrumentTagProvider extends FabricTagsProvider<Instrument> {

	public TCAInstrumentTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, Registries.INSTRUMENT, registries);
	}

	@Override
	public void addTags(HolderLookup.Provider arg) {
		this.builder(TCAInstrumentTags.COPPER_HORNS)
			.add(TCAInstruments.RECORDER_COPPER_HORN)
			.add(TCAInstruments.FLUTE_COPPER_HORN)
			.add(TCAInstruments.OBOE_COPPER_HORN)
			.add(TCAInstruments.CLARINET_COPPER_HORN)
			.add(TCAInstruments.SAX_COPPER_HORN)
			.add(TCAInstruments.TRUMPET_COPPER_HORN)
			.add(TCAInstruments.TROMBONE_COPPER_HORN)
			.add(TCAInstruments.TUBA_COPPER_HORN);
	}
}
