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

package net.frozenblock.thecopperierage.datagen.tag;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.frozenblock.thecopperierage.registry.TCAItems;
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
			.add(TCAItems.RECORDER_COPPER_HORN)
			.add(TCAItems.FLUTE_COPPER_HORN)
			.add(TCAItems.OBOE_COPPER_HORN)
			.add(TCAItems.CLARINET_COPPER_HORN)
			.add(TCAItems.SAX_COPPER_HORN)
			.add(TCAItems.TRUMPET_COPPER_HORN)
			.add(TCAItems.TROMBONE_COPPER_HORN)
			.add(TCAItems.TUBA_COPPER_HORN);
	}
}
