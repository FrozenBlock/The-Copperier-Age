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

package net.frozenblock.thecopperierage.data.tag;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.frozenblock.thecopperierage.registry.TCADamageTypes;
import net.frozenblock.thecopperierage.tag.TCADamageTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;

public final class TCADamageTypeTagsProvider extends FabricTagsProvider<DamageType> {

	public TCADamageTypeTagsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, Registries.DAMAGE_TYPE, registries);
	}

	@Override
	public void addTags(HolderLookup.Provider arg) {
		this.builder(TCADamageTypeTags.MINECART)
			.add(TCADamageTypes.MINECART_IMPACT)
			.add(TCADamageTypes.MINECART_IMPACT_LEWD);

		this.builder(DamageTypeTags.SULFUR_CUBE_WITH_BLOCK_IMMUNE_TO)
			.addTag(TCADamageTypeTags.MINECART);

		this.builder(DamageTypeTags.ALWAYS_KILLS_ARMOR_STANDS)
			.addTag(TCADamageTypeTags.MINECART);

		this.builder(DamageTypeTags.PANIC_CAUSES)
			.addTag(TCADamageTypeTags.MINECART);

		this.builder(DamageTypeTags.NO_KNOCKBACK)
			.addTag(TCADamageTypeTags.MINECART);
	}
}
