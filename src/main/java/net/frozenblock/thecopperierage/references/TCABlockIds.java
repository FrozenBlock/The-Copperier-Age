package net.frozenblock.thecopperierage.references;

import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public final class TCABlockIds {
	public static final ResourceKey<Block> COPPER_FIRE = create("copper_fire");

	private static ResourceKey<Block> create(String name) {
		return ResourceKey.create(Registries.BLOCK, TCAConstants.id(name));
	}
}
