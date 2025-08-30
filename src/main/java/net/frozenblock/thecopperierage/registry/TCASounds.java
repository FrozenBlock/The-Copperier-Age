/*
 * Copyright 2025 FrozenBlock
 * This file is part of Wilder Wild.
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
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.NotNull;

public final class TCASounds {

	// ITEM
	public static final Holder.Reference<SoundEvent> ITEM_COPPER_HORN_SAX = registerForHolder("item.copper_horn.saxophone");
	public static final Holder.Reference<SoundEvent> ITEM_COPPER_HORN_TUBA = registerForHolder("item.copper_horn.tuba");
	public static final Holder.Reference<SoundEvent> ITEM_COPPER_HORN_RECORDER = registerForHolder("item.copper_horn.recorder");
	public static final Holder.Reference<SoundEvent> ITEM_COPPER_HORN_FLUTE = registerForHolder("item.copper_horn.flute");
	public static final Holder.Reference<SoundEvent> ITEM_COPPER_HORN_OBOE = registerForHolder("item.copper_horn.oboe");
	public static final Holder.Reference<SoundEvent> ITEM_COPPER_HORN_CLARINET = registerForHolder("item.copper_horn.clarinet");
	public static final Holder.Reference<SoundEvent> ITEM_COPPER_HORN_TRUMPET = registerForHolder("item.copper_horn.trumpet");
	public static final Holder.Reference<SoundEvent> ITEM_COPPER_HORN_TROMBONE = registerForHolder("item.copper_horn.trombone");

	private TCASounds() {
		throw new UnsupportedOperationException("TCASounds contains only static declarations.");
	}

	@NotNull
	private static Holder.Reference<SoundEvent> registerForHolder(@NotNull String string) {
		return registerForHolder(TCAConstants.id(string));
	}

	@NotNull
	private static Holder.Reference<SoundEvent> registerForHolder(@NotNull ResourceLocation resourceLocation) {
		return registerForHolder(resourceLocation, resourceLocation);
	}

	@NotNull
	public static SoundEvent register(@NotNull String path) {
		var id = TCAConstants.id(path);
		return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
	}

	@NotNull
	private static Holder.Reference<SoundEvent> registerForHolder(@NotNull ResourceLocation resourceLocation, @NotNull ResourceLocation resourceLocation2) {
		return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, resourceLocation, SoundEvent.createVariableRangeEvent(resourceLocation2));
	}

	public static void init() {
		TCAConstants.logWithModId("Registering SoundEvents for", TCAConstants.UNSTABLE_LOGGING);
	}

}
