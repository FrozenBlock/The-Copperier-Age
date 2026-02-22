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
import net.frozenblock.thecopperierage.entity.CopperGolemStatueMinecart;
import net.frozenblock.thecopperierage.entity.CrateMinecart;
import net.frozenblock.thecopperierage.entity.DispenserMinecart;
import net.frozenblock.thecopperierage.entity.DropperMinecart;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class TCAEntityTypes {
	public static final EntityType<CrateMinecart> CRATE_MINECART = register(
		"crate_minecart",
		EntityType.Builder.<CrateMinecart>of(CrateMinecart::new, MobCategory.MISC)
			.sized(0.98F, 0.7F)
			.clientTrackingRange(8)
			.updateInterval(3)
	);

	public static final EntityType<CopperGolemStatueMinecart> COPPER_GOLEM_STATUE_MINECART = register(
		"copper_golem_statue_minecart",
		EntityType.Builder.<CopperGolemStatueMinecart>of(CopperGolemStatueMinecart::new, MobCategory.MISC)
			.sized(0.98F, 0.7F)
			.clientTrackingRange(8)
			.updateInterval(3)
	);

	public static final EntityType<DispenserMinecart> DISPENSER_MINECART = register(
		"dispenser_minecart",
		EntityType.Builder.<DispenserMinecart>of(DispenserMinecart::new, MobCategory.MISC)
			.sized(0.98F, 0.7F)
			.clientTrackingRange(8)
			.updateInterval(3)
	);

	public static final EntityType<DropperMinecart> DROPPER_MINECART = register(
		"dropper_minecart",
		EntityType.Builder.<DropperMinecart>of(DropperMinecart::new, MobCategory.MISC)
			.sized(0.98F, 0.7F)
			.clientTrackingRange(8)
			.updateInterval(3)
	);

	public static void init() {
		TCAConstants.logWithModId("Registering EntityTypes for", TCAConstants.UNSTABLE_LOGGING);
	}

	private static <T extends Entity> EntityType<T> register(String path, EntityType.Builder<T> builder) {
		return Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			TCAConstants.id(path),
			builder.build(ResourceKey.create(Registries.ENTITY_TYPE, TCAConstants.id(path)))
		);
	}

	private TCAEntityTypes() {
		throw new UnsupportedOperationException("TCAEntityTypes contains only static declarations.");
	}
}
