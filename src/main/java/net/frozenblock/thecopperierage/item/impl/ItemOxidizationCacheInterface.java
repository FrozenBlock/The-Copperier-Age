package net.frozenblock.thecopperierage.item.impl;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.WeatheringCopper;

public interface ItemOxidizationCacheInterface {
	public void theCopperierAge$setWeatherState(WeatheringCopper.WeatherState weatherState);
	public void theCopperierAge$setWaxed(boolean waxed);
	public void theCopperierAge$setBaseItem(Item item);
	public WeatheringCopper.WeatherState theCopperierAge$weatherState();
	public boolean theCopperierAge$waxed();
	public Item theCopperierAge$baseItem();
	public void theCopperierAge$clearOxidizationCache();
}
