package net.frozenblock.thecopperierage.client.resources.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.model.ModelState;

@Environment(EnvType.CLIENT)
public enum BlockModelOxidization implements ModelState {
	EXPOSED("exposed"),
	WEATHERED("weathered"),
	OXIDIZED("oxidized");

	private final String name;
	BlockModelOxidization(String name) {
		this.name = name;
	}
}
