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

package net.frozenblock.thecopperierage.mixin.client.chestvehicle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.client.renderer.entity.ChestVehicleRenderHelper;
import net.frozenblock.thecopperierage.client.renderer.entity.state.ChestVehicleRenderStateAccess;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.object.boat.AbstractBoatModel;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(AbstractBoatModel.class)
public class AbstractBoatModelMixin {
	@Unique
	@Nullable
	private ModelPart theCopperierAge$chestBottom;
	@Unique
	@Nullable
	private ModelPart theCopperierAge$chestLid;
	@Unique
	@Nullable
	private ModelPart theCopperierAge$chestLock;

	@Inject(method = "<init>", at = @At("TAIL"))
	public void theCopperierAge$findChestParts(ModelPart root, CallbackInfo info) {
		this.theCopperierAge$chestBottom = theCopperierAge$childOrNull(root, ChestVehicleRenderHelper.CHEST_BOTTOM_PART);
		this.theCopperierAge$chestLid = theCopperierAge$childOrNull(root, ChestVehicleRenderHelper.CHEST_LID_PART);
		this.theCopperierAge$chestLock = theCopperierAge$childOrNull(root, ChestVehicleRenderHelper.CHEST_LOCK_PART);
	}

	@Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/BoatRenderState;)V", at = @At("TAIL"))
	private void theCopperierAge$hideBakedChest(BoatRenderState state, CallbackInfo info) {
		if (this.theCopperierAge$chestBottom == null || this.theCopperierAge$chestLid == null || this.theCopperierAge$chestLock == null) return;

		final boolean hidden = state instanceof ChestVehicleRenderStateAccess chestState && chestState.theCopperierAge$isChestVehicle();
		this.theCopperierAge$chestBottom.visible = !hidden;
		this.theCopperierAge$chestLid.visible = !hidden;
		this.theCopperierAge$chestLock.visible = !hidden;
	}

	@Unique
	@Nullable
	private static ModelPart theCopperierAge$childOrNull(ModelPart root, String name) {
		return root.hasChild(name) ? root.getChild(name) : null;
	}
}
