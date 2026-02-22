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

package net.frozenblock.thecopperierage.mixin.entity.chest_vehicle;

import net.frozenblock.thecopperierage.entity.impl.ChestLidAnimating;
import net.frozenblock.thecopperierage.entity.impl.ChestVehicleAnimationConstants;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
	@Inject(method = "handleEntityEvent", at = @At("HEAD"))
	private void theCopperierAge$handleEntityEvent(byte eventId, CallbackInfo info) {
		Entity entity = (Entity) (Object) this;
		if (!(entity instanceof ChestLidAnimating lidAnimating)) return;
		if (!entity.level().isClientSide()) return;

		if (eventId == ChestVehicleAnimationConstants.OPEN_EVENT) {
			lidAnimating.theCopperierAge$setLidShouldBeOpen(true);
		} else if (eventId == ChestVehicleAnimationConstants.CLOSE_EVENT) {
			lidAnimating.theCopperierAge$setLidShouldBeOpen(false);
		}
	}
}
