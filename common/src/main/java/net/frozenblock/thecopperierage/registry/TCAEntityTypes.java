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

package net.frozenblock.thecopperierage.registry;

import net.frozenblock.lib.platform.api.registry.DeferredEntityType;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.TCAFeatureFlags;
import net.frozenblock.thecopperierage.entity.vehicle.minecart.MinecartCrate;
import net.frozenblock.thecopperierage.entity.vehicle.minecart.MinecartDispenser;
import net.frozenblock.thecopperierage.entity.vehicle.minecart.MinecartDropper;
import net.frozenblock.thecopperierage.entity.vehicle.minecart.MinecartJukebox;
import net.frozenblock.thecopperierage.references.TCAEntityTypeIds;
import net.minecraft.world.entity.MobCategory;

public final class TCAEntityTypes {
	private static final DeferredRegister.Entities REGISTER = DeferredRegister.createEntities(TCAConstants.MOD_ID).requiredFeatures(TCAFeatureFlags.FEATURE_FLAG);

	public static final DeferredEntityType<MinecartCrate> CRATE_MINECART = REGISTER.register(TCAEntityTypeIds.CRATE_MINECART,
		MinecartCrate::new,
		MobCategory.MISC,
		builder -> builder.sized(0.98F, 0.7F).clientTrackingRange(8).updateInterval(3)
	);

	public static final DeferredEntityType<MinecartDispenser> DISPENSER_MINECART = REGISTER.register(TCAEntityTypeIds.DISPENSER_MINECART,
		MinecartDispenser::new,
		MobCategory.MISC,
		builder -> builder.sized(0.98F, 0.7F).clientTrackingRange(8).updateInterval(3)
	);

	public static final DeferredEntityType<MinecartDropper> DROPPER_MINECART = REGISTER.register(TCAEntityTypeIds.DROPPER_MINECART,
		MinecartDropper::new,
		MobCategory.MISC,
		builder -> builder.sized(0.98F, 0.7F).clientTrackingRange(8).updateInterval(3)
	);

	public static final DeferredEntityType<MinecartJukebox> JUKEBOX_MINECART = REGISTER.register(TCAEntityTypeIds.JUKEBOX_MINECART,
		MinecartJukebox::new,
		MobCategory.MISC,
		builder -> builder.sized(0.98F, 0.7F).clientTrackingRange(8).updateInterval(3)
	);

	static {
		REGISTER.register();
	}

	public static void init() {}

	private TCAEntityTypes() {}
}
