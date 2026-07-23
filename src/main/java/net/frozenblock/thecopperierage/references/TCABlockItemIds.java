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

package net.frozenblock.thecopperierage.references;

import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.WeatheringCopperCollection;

public final class TCABlockItemIds {
    public static final BlockItemId COPPER_CAMPFIRE = create("copper_campfire");
    public static final BlockItemId COPPER_JACK_O_LANTERN = create("copper_jack_o_lantern");
    public static final BlockItemId REDSTONE_JACK_O_LANTERN = create("redstone_jack_o_lantern");
    public static final BlockItemId REDSTONE_GRIT = create("redstone_grit");
	public static final WeatheringCopperCollection<BlockItemId> GEARBOX = createSimpleCopper("gearbox");
	public static final WeatheringCopperCollection<BlockItemId> STICKY_GEARBOX = createSimpleCopper("sticky_gearbox");
    public static final WeatheringCopperCollection<BlockItemId> COPPER_FAN = createSimpleCopper("copper_fan");
    public static final WeatheringCopperCollection<BlockItemId> CHIME = createSimpleCopper("chime");
    public static final BlockItemId CRATE = create("crate");
    public static final BlockItemId KILN = create("kiln");
    public static final WeatheringCopperCollection<BlockItemId> COPPER_BUTTON = createSimpleCopper("copper_button");
    public static final WeatheringCopperCollection<BlockItemId> WEIGHTED_PRESSURE_PLATE = createSimpleCopper("weighted_pressure_plate");

    private static BlockItemId create(String name) {
        final Identifier id = TCAConstants.id(name);
        return BlockItemId.create(id, id);
    }

	private static WeatheringCopperCollection<BlockItemId> createSimpleCopper(String name) {
		return WeatheringCopperCollection.prefixWithState(WeatheringCopperCollection.create(name)).map(TCABlockItemIds::create);
	}
}
