/*
 * Copyright 2025 FrozenBlock
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

package net.frozenblock.thecopperierage.mixin.worldgen;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.registry.TCAResources;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldLoader.PackConfig.class)
public class TrickierTrialsPackCheckerMixin {

	@ModifyExpressionValue(
		method = "createResourceManager",
		at = @At(
			value = "NEW",
			target = "Lnet/minecraft/server/packs/resources/MultiPackResourceManager;"
		)
	)
	public MultiPackResourceManager createResourceManager(MultiPackResourceManager multiPackResourceManager) {
		TCAResources.HAS_TRICKIER_TRIALS_PACK = multiPackResourceManager.listPacks().anyMatch(packResources -> {
			if (packResources.knownPackInfo().isPresent()) return packResources.knownPackInfo().get().id().equals(TCAConstants.string("trickier_trials"));
			return false;
		});
		TCAConstants.log(TCAResources.HAS_TRICKIER_TRIALS_PACK ? "Has Trickier Trials pack!" : "Does not have Trickier Trials pack!", TCAConstants.UNSTABLE_LOGGING);
		return multiPackResourceManager;
	}

}
