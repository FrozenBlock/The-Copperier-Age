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

package net.frozenblock.thecopperierage.data.loot;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.frozenblock.thecopperierage.registry.TCABlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public final class TCABlockLootProvider extends FabricBlockLootSubProvider {

	public TCABlockLootProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registries) {
		super(dataOutput, registries);
	}

	@Override
	public void generate() {
		final HolderLookup.RegistryLookup<Enchantment> enchantments = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

		TCABlocks.GEARBOX.forEach(this::dropSelf);
		TCABlocks.STICKY_GEARBOX.forEach(this::dropSelf);
		TCABlocks.COPPER_FAN.forEach(this::dropSelf);
		TCABlocks.CHIME.forEach(this::dropSelf);
		TCABlocks.COPPER_BUTTON.forEach(this::dropSelf);
		TCABlocks.WEIGHTED_PRESSURE_PLATE.forEach(this::dropSelf);

		this.crateDrop(TCABlocks.CRATE);

		this.dropSelf(TCABlocks.COPPER_JACK_O_LANTERN);
		this.dropSelf(TCABlocks.REDSTONE_JACK_O_LANTERN);
		this.dropSelf(TCABlocks.REDSTONE_GRIT);
	}

	public void crateDrop(Block block) {
		this.add(
			block,
			LootTable.lootTable().withPool(
				this.applyExplosionCondition(
					block,
					LootPool.lootPool().setRolls(ConstantValue.exactly(1F)).add(
						LootItem.lootTableItem(block).apply(
							CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
								.include(DataComponents.CUSTOM_NAME)
								.include(DataComponents.CONTAINER)
								.include(DataComponents.LOCK)
								.include(DataComponents.CONTAINER_LOOT)
								.include(DataComponents.MAX_STACK_SIZE)
						)
					)
				)
			)
		);
	}

}
