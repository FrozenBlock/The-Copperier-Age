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

package net.frozenblock.thecopperierage.item.impl;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.WeatheringCopper;

public interface ItemOxidizationCacheInterface {
	void theCopperierAge$setWeatherState(WeatheringCopper.WeatherState weatherState);
	void theCopperierAge$setWaxed(boolean waxed);
	void theCopperierAge$setBaseItem(Item item);
	WeatheringCopper.WeatherState theCopperierAge$weatherState();
	boolean theCopperierAge$waxed();
	Item theCopperierAge$baseItem();
	void theCopperierAge$clearOxidizationCache();
}
