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

package net.frozenblock.thecopperierage.mixin.client.chest_vehicle;

import net.frozenblock.thecopperierage.client.renderer.entity.ChestVehicleRenderConstants;
import net.frozenblock.thecopperierage.client.renderer.entity.state.ChestLidRenderStateAccess;
import net.frozenblock.thecopperierage.client.renderer.entity.state.ChestVehicleRenderStateAccess;
import net.minecraft.client.model.AbstractBoatModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBoatModel.class)
public abstract class AbstractBoatModelMixin {

	@Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/BoatRenderState;)V", at = @At("TAIL"))
	private void theCopperierAge$setupAnim(BoatRenderState state, CallbackInfo info) {
		final ModelPart root = ((AbstractBoatModel) (Object) this).root();
		if (!root.hasChild(ChestVehicleRenderConstants.CHEST_BOTTOM_PART)
			|| !root.hasChild(ChestVehicleRenderConstants.CHEST_LID_PART)
			|| !root.hasChild(ChestVehicleRenderConstants.CHEST_LOCK_PART)) return;

		final ModelPart chestBottom = root.getChild(ChestVehicleRenderConstants.CHEST_BOTTOM_PART);
		final ModelPart chestLid = root.getChild(ChestVehicleRenderConstants.CHEST_LID_PART);
		final ModelPart chestLock = root.getChild(ChestVehicleRenderConstants.CHEST_LOCK_PART);

		final boolean useVanillaChestRender =
			state instanceof ChestVehicleRenderStateAccess chestVehicleState
			&& chestVehicleState.theCopperierAge$isChestVehicle()
			&& state instanceof ChestLidRenderStateAccess;

		chestBottom.visible = !useVanillaChestRender;
		chestLid.visible = !useVanillaChestRender;
		chestLock.visible = !useVanillaChestRender;
	}
}
