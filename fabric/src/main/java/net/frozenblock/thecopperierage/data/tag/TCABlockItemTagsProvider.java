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

import java.util.function.Function;
import net.frozenblock.thecopperierage.references.TCABlockItemIds;
import net.frozenblock.thecopperierage.tag.TCABlockItemTags;
import net.minecraft.data.tags.BlockItemTagsProvider;
import net.minecraft.tags.BlockItemTagId;
import net.minecraft.tags.BlockItemTags;

public final class TCABlockItemTagsProvider extends BlockItemTagsProvider {

	TCABlockItemTagsProvider(Function<BlockItemTagId, CombinedAppender> tagSupplier) {
		super(tagSupplier);
	}

	@Override
	protected void run() {
		this.tag(BlockItemTags.LANTERNS)
			.add(TCABlockItemIds.CUPRIC_LANTERN);

		this.tag(BlockItemTags.BUTTONS)
			.addTag(TCABlockItemTags.COPPER_BUTTONS);

		this.tag(TCABlockItemTags.GEARBOXES)
			.addAll(TCABlockItemIds.GEARBOX.asList())
			.addTag(TCABlockItemTags.STICKY_GEARBOXES);

		this.tag(TCABlockItemTags.STICKY_GEARBOXES)
			.addAll(TCABlockItemIds.STICKY_GEARBOX.asList());

		this.tag(TCABlockItemTags.COPPER_FANS)
			.addAll(TCABlockItemIds.COPPER_FAN.asList());

		this.tag(TCABlockItemTags.CHIMES)
			.addAll(TCABlockItemIds.CHIME.asList());

		this.tag(TCABlockItemTags.COPPER_BUTTONS)
			.addAll(TCABlockItemIds.COPPER_BUTTON.asList());

		this.tag(TCABlockItemTags.COPPER_PRESSURE_PLATES)
			.addAll(TCABlockItemIds.WEIGHTED_PRESSURE_PLATE.asList());
	}
}
