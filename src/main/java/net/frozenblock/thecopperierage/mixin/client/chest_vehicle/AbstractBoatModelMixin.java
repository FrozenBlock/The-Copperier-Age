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

import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.client.renderer.entity.ChestVehicleRenderHelper;
import net.frozenblock.thecopperierage.client.renderer.entity.state.ChestLidRenderStateInterface;
import net.frozenblock.thecopperierage.client.renderer.entity.state.ChestVehicleRenderStateAccess;
import net.minecraft.client.model.AbstractBoatModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBoatModel.class)
public class AbstractBoatModelMixin {
	@Unique
	private ModelPart theCopperierAge$chestBottom = null;
	@Unique
	private ModelPart theCopperierAge$chestLid = null;
	@Unique
	private ModelPart theCopperierAge$chestLock = null;

	@Inject(method = "<init>", at = @At("TAIL"))
	public void theCopperierAge$init(ModelPart root, CallbackInfo info) {
		this.theCopperierAge$chestBottom = root.hasChild(ChestVehicleRenderHelper.CHEST_BOTTOM_PART) ? root.getChild(ChestVehicleRenderHelper.CHEST_BOTTOM_PART) : null;
		this.theCopperierAge$chestLid = root.hasChild(ChestVehicleRenderHelper.CHEST_LID_PART) ? root.getChild(ChestVehicleRenderHelper.CHEST_LID_PART) : null;
		this.theCopperierAge$chestLock = root.hasChild(ChestVehicleRenderHelper.CHEST_LOCK_PART) ? root.getChild(ChestVehicleRenderHelper.CHEST_LOCK_PART) : null;

	}

	@Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/BoatRenderState;)V", at = @At("TAIL"))
	private void theCopperierAge$setupAnim(BoatRenderState state, CallbackInfo info) {
		if (this.theCopperierAge$chestBottom == null || this.theCopperierAge$chestLid == null || this.theCopperierAge$chestLock == null) return;

		final boolean useVanillaChestRender =
			state instanceof ChestVehicleRenderStateAccess chestVehicleState
			&& chestVehicleState.theCopperierAge$isChestVehicle()
			&& state instanceof ChestLidRenderStateInterface
			&& TCAConfig.get().improvedVehicleChests;

		this.theCopperierAge$chestBottom.visible = !useVanillaChestRender;
		this.theCopperierAge$chestLid.visible = !useVanillaChestRender;
		this.theCopperierAge$chestLock.visible = !useVanillaChestRender;
	}
}
