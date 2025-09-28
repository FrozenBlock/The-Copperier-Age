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

package net.frozenblock.thecopperierage.registry;

import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.NotNull;

public final class TCASounds {

	// BLOCK
	public static final SoundEvent BLOCK_GEARBOX_ON = register("block.gearbox.on");
	public static final SoundEvent BLOCK_GEARBOX_OFF = register("block.gearbox.off");
	public static final SoundEvent BLOCK_GEARBOX_IDLE = register("block.gearbox.idle");
	public static final SoundEvent BLOCK_CHIME_AMBIENT = register("block.chime.ambient");
	public static final SoundEvent BLOCK_CHIME_DISTURB = register("block.chime.disturb");
	public static final SoundEvent BLOCK_CHIME_BREAK = register("block.chime.break");
	public static final SoundEvent BLOCK_CHIME_STEP = register("block.chime.step");
	public static final SoundEvent BLOCK_CHIME_PLACE = register("block.chime.place");
	public static final SoundEvent BLOCK_CHIME_HIT = register("block.chime.hit");
	public static final SoundEvent BLOCK_CHIME_FALL = register("block.chime.fall");
	public static final SoundType CHIME = new SoundType(
		1F,
		1F,
		BLOCK_CHIME_BREAK,
		BLOCK_CHIME_STEP,
		BLOCK_CHIME_PLACE,
		BLOCK_CHIME_HIT,
		BLOCK_CHIME_FALL
	);

	public static final SoundEvent BLOCK_COPPER_FAN_ON = register("block.copper_fan.on");
	public static final SoundEvent BLOCK_COPPER_FAN_OFF = register("block.copper_fan.off");
	public static final SoundEvent BLOCK_COPPER_FAN_IDLE_HUM = register("block.copper_fan.idle_hum");

	// ITEM
	public static final Holder.Reference<SoundEvent> ITEM_COPPER_HORN_SAX = registerForHolder("item.copper_horn.saxophone");
	public static final Holder.Reference<SoundEvent> ITEM_COPPER_HORN_TUBA = registerForHolder("item.copper_horn.tuba");
	public static final Holder.Reference<SoundEvent> ITEM_COPPER_HORN_RECORDER = registerForHolder("item.copper_horn.recorder");
	public static final Holder.Reference<SoundEvent> ITEM_COPPER_HORN_FLUTE = registerForHolder("item.copper_horn.flute");
	public static final Holder.Reference<SoundEvent> ITEM_COPPER_HORN_OBOE = registerForHolder("item.copper_horn.oboe");
	public static final Holder.Reference<SoundEvent> ITEM_COPPER_HORN_CLARINET = registerForHolder("item.copper_horn.clarinet");
	public static final Holder.Reference<SoundEvent> ITEM_COPPER_HORN_TRUMPET = registerForHolder("item.copper_horn.trumpet");
	public static final Holder.Reference<SoundEvent> ITEM_COPPER_HORN_TROMBONE = registerForHolder("item.copper_horn.trombone");

	public static final SoundEvent ITEM_WRENCH_USE = register("item.wrench.use");

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
