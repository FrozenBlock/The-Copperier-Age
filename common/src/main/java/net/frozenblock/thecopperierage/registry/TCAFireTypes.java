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

import net.frozenblock.lib.block.api.fire.FireTypes;
import net.frozenblock.lib.block.impl.fire.FireType;
import net.frozenblock.lib.config.v2.entry.predicates.ConfigPredicate;
import net.frozenblock.lib.particle.options.ColoredSmokeParticleOptions;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.tag.TCABlockTags;
import net.frozenblock.thecopperierage.tag.TCAEntityTypeTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public final class TCAFireTypes {
	public static final ResourceKey<FireType> COPPER_FIRE = FireTypes.createKey(TCAConstants.id("copper_fire"));

	public static void init() {}

	public static void bootstrap(BootstrapContext<FireType> context) {
		final HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);
		final HolderGetter<EntityType<?>> entityTypes = context.lookup(Registries.ENTITY_TYPE);
		FireTypes.register(
			context,
			COPPER_FIRE,
			FireType.builder()
				.fireSourceBlocks(blocks.getOrThrow(TCABlockTags.COPPER_FIRE_BLOCKS))
				.supportingBlocks(blocks.getOrThrow(TCABlockTags.COPPER_FIRE_BASE_BLOCKS))
				.alwaysApplyTo(entityTypes.getOrThrow(TCAEntityTypeTags.COPPER), HolderSet.empty())
				.textures(TCAConstants.id("copper_fire_0"), TCAConstants.id("copper_fire_1"))
				.smokeParticles(
					ColoredSmokeParticleOptions.smoke(0F, 0.075F, 0F),
					ColoredSmokeParticleOptions.largeSmoke(0F, 0.075F, 0F),
					ConfigPredicate.equalTo(TCAConfig.COPPER_PARTICLES, true)
				)
				.campfireSmokeParticles(
					ColoredSmokeParticleOptions.campfireCosy(-0.15F, 0F, -0.15F),
					ColoredSmokeParticleOptions.campfireSignal(-0.15F, 0F, -0.15F),
					ConfigPredicate.equalTo(TCAConfig.COPPER_PARTICLES, true)
				)
				.lavaParticle(TCAParticleTypes.COPPER_LAVA.get())
				.enabledWhen(ConfigPredicate.equalTo(TCAConfig.COPPER_FIRE_ENABLED, true))
		);
	}
}
