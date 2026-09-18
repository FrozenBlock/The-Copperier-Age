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

import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public final class TCADamageTypes {
	public static final ResourceKey<DamageType> MINECART_IMPACT = createKey("minecart_impact");
	public static final ResourceKey<DamageType> MINECART_IMPACT_LEWD = createKey("minecart_impact_lewd");

	public static void bootstrap(BootstrapContext<DamageType> context) {
		context.register(MINECART_IMPACT, new DamageType("minecart_impact", 0.1F));
		context.register(MINECART_IMPACT_LEWD, new DamageType("minecart_impact.lewd", 0.1F));
	}

	private static ResourceKey<DamageType> createKey(String name) {
		return ResourceKey.create(Registries.DAMAGE_TYPE, TCAConstants.id(name));
	}

	private TCADamageTypes() {}
}
