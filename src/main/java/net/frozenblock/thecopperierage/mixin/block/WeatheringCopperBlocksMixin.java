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

package net.frozenblock.thecopperierage.mixin.block;

import net.frozenblock.thecopperierage.block.api.WeatheringCopperBlocksHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopperBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WeatheringCopperBlocks.class)
public class WeatheringCopperBlocksMixin {

	@Inject(method = "<init>", at = @At("TAIL"))
	private void theCopperierAge$registerBlockSetInfo(
		Block unaffected,
		Block exposed,
		Block weathered,
		Block oxidized,
		Block waxed,
		Block waxedExposed,
		Block waxedWeathered,
		Block waxedOxidized,
		CallbackInfo info
	) {
		WeatheringCopperBlocksHelper.registerSet(unaffected, exposed, weathered, oxidized, waxed, waxedExposed, waxedWeathered, waxedOxidized);
	}

}
