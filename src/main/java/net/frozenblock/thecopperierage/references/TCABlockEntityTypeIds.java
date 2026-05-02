package net.frozenblock.thecopperierage.references;

import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class TCABlockEntityTypeIds {
	public static final ResourceKey<BlockEntityType<?>> CHIME = create("chime");
	public static final ResourceKey<BlockEntityType<?>> STICKY_GEARBOX =  create("sticky_gearbox");
	public static final ResourceKey<BlockEntityType<?>> CRATE = create("crate");

	private static ResourceKey<BlockEntityType<?>> create(String name) {
		return ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, TCAConstants.id(name));
	}
}
