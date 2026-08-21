package net.frozenblock.thecopperierage.references;

import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;

public final class TCAEntityTypeIds {
	public static final ResourceKey<EntityType<?>> CRATE_MINECART = create("crate_minecart");
	public static final ResourceKey<EntityType<?>> COPPER_GOLEM_STATUE_MINECART = create("copper_golem_statue_minecart");
	public static final ResourceKey<EntityType<?>> DISPENSER_MINECART = create("dispenser_minecart");
	public static final ResourceKey<EntityType<?>> DROPPER_MINECART = create("dropper_minecart");
	public static final ResourceKey<EntityType<?>> JUKEBOX_MINECART = create("jukebox_minecart");

	private static ResourceKey<EntityType<?>> create(String name) {
		return ResourceKey.create(Registries.ENTITY_TYPE, TCAConstants.id(name));
	}
}
