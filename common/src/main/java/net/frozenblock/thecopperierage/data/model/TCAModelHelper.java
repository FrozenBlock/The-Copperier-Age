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

package net.frozenblock.thecopperierage.data.model;

import java.util.Optional;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.block.GearboxBlock;
import net.frozenblock.thecopperierage.client.renderer.item.properties.select.OxidizedItemsEnabled;
import net.frozenblock.thecopperierage.client.renderer.item.properties.select.WeatherState;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import static net.minecraft.client.renderer.item.ItemModel.Unbaked;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

@ClientOnly
public final class TCAModelHelper {
	// GEARBOX
	static final PropertyDispatch<VariantMutator> GEARBOX_ROTATION = PropertyDispatch.modify(GearboxBlock.FACING)
		.select(Direction.DOWN, BlockModelGenerators.X_ROT_90)
		.select(Direction.UP, BlockModelGenerators.X_ROT_270.then(BlockModelGenerators.Y_ROT_180))
		.select(Direction.NORTH, BlockModelGenerators.NOP)
		.select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
		.select(Direction.WEST, BlockModelGenerators.Y_ROT_270)
		.select(Direction.EAST, BlockModelGenerators.Y_ROT_90);
	static final ModelTemplate GEARBOX_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_gearbox")),
		Optional.empty(),
		TextureSlot.SIDE, TextureSlot.FRONT
	);
	static final ModelTemplate GEARBOX_COUNTER_CLOCKWISE_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_gearbox_on")),
		Optional.of("_counter_clockwise"),
		TextureSlot.SIDE, TextureSlot.FRONT
	);
	static final ModelTemplate GEARBOX_CLOCKWISE_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_gearbox_on")),
		Optional.of("_clockwise"),
		TextureSlot.SIDE, TextureSlot.FRONT
	);
	// COPPER FAN
	static final ModelTemplate COPPER_FAN_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_copper_fan")),
		Optional.empty(),
		TextureSlot.SIDE, TextureSlot.BOTTOM
	);
	static final ModelTemplate COPPER_FAN_POWERED_MODEL = new ModelTemplate(
		Optional.of(TCAConstants.id("block/template_copper_fan")),
		Optional.of("_powered"),
		TextureSlot.FRONT, TextureSlot.SIDE, TextureSlot.BOTTOM
	);
	// KILN
	static final ModelTemplate KILN_MODEL = new ModelTemplate(
		Optional.of(Identifier.withDefaultNamespace("block/orientable_with_bottom")),
		Optional.empty(),
		TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE, TextureSlot.FRONT
	);
	static final PropertyDispatch<VariantMutator> KILN_ROTATION = PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
		.select(Direction.EAST, BlockModelGenerators.Y_ROT_90)
		.select(Direction.SOUTH, BlockModelGenerators.Y_ROT_180)
		.select(Direction.WEST, BlockModelGenerators.Y_ROT_270)
		.select(Direction.NORTH, BlockModelGenerators.NOP);

	public static Unbaked createOxidizableDispatch(Unbaked unaffected, Unbaked exposed, Unbaked weathered, Unbaked oxidized) {
		return ItemModelUtils.select(
			OxidizedItemsEnabled.INSTANCE,
			unaffected,
			ItemModelUtils.when(
				true,
				ItemModelUtils.select(
					WeatherState.INSTANCE,
					unaffected,
					ItemModelUtils.when(WeatheringCopper.WeatherState.EXPOSED, exposed),
					ItemModelUtils.when(WeatheringCopper.WeatherState.WEATHERED, weathered),
					ItemModelUtils.when(WeatheringCopper.WeatherState.OXIDIZED, oxidized)
				)
			)
		);
	}
}
