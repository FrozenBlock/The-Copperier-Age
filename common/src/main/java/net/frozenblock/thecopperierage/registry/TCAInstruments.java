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

package net.frozenblock.thecopperierage.registry;

import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Instruments;

public final class TCAInstruments {
	public static final ResourceKey<Instrument> SAX_COPPER_HORN = ResourceKey.create(Registries.INSTRUMENT, TCAConstants.id("sax_copper_horn"));
	public static final ResourceKey<Instrument> TUBA_COPPER_HORN = ResourceKey.create(Registries.INSTRUMENT, TCAConstants.id("tuba_copper_horn"));
	public static final ResourceKey<Instrument> RECORDER_COPPER_HORN = ResourceKey.create(Registries.INSTRUMENT, TCAConstants.id("recorder_copper_horn"));
	public static final ResourceKey<Instrument> FLUTE_COPPER_HORN = ResourceKey.create(Registries.INSTRUMENT, TCAConstants.id("flute_copper_horn"));
	public static final ResourceKey<Instrument> OBOE_COPPER_HORN = ResourceKey.create(Registries.INSTRUMENT, TCAConstants.id("oboe_copper_horn"));
	public static final ResourceKey<Instrument> CLARINET_COPPER_HORN = ResourceKey.create(Registries.INSTRUMENT, TCAConstants.id("clarinet_copper_horn"));
	public static final ResourceKey<Instrument> TRUMPET_COPPER_HORN = ResourceKey.create(Registries.INSTRUMENT, TCAConstants.id("trumpet_copper_horn"));
	public static final ResourceKey<Instrument> TROMBONE_COPPER_HORN = ResourceKey.create(Registries.INSTRUMENT, TCAConstants.id("trombone_copper_horn"));

	public static void bootstrap(BootstrapContext<Instrument> registry) {
		Instruments.register(registry, RECORDER_COPPER_HORN, TCASounds.ITEM_COPPER_HORN_RECORDER.asHolder(), 32767, 64F);
		Instruments.register(registry, FLUTE_COPPER_HORN, TCASounds.ITEM_COPPER_HORN_FLUTE.asHolder(), 32767, 64F);
		Instruments.register(registry, OBOE_COPPER_HORN, TCASounds.ITEM_COPPER_HORN_OBOE.asHolder(), 32767, 64F);
		Instruments.register(registry, CLARINET_COPPER_HORN, TCASounds.ITEM_COPPER_HORN_CLARINET.asHolder(), 32767, 64F);
		Instruments.register(registry, SAX_COPPER_HORN, TCASounds.ITEM_COPPER_HORN_SAX.asHolder(), 32767, 64F);
		Instruments.register(registry, TRUMPET_COPPER_HORN, TCASounds.ITEM_COPPER_HORN_TRUMPET.asHolder(), 32767, 64F);
		Instruments.register(registry, TROMBONE_COPPER_HORN, TCASounds.ITEM_COPPER_HORN_TROMBONE.asHolder(), 32767, 64F);
		Instruments.register(registry, TUBA_COPPER_HORN, TCASounds.ITEM_COPPER_HORN_TUBA.asHolder(), 32767, 64F);
	}
}
