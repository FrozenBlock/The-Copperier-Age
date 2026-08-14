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
import net.frozenblock.thecopperierage.client.renderer.entity.state.ChestVehicleRenderStateAccess;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(MinecartRenderState.class)
public class MinecartRenderStateMixin implements ChestVehicleRenderStateAccess {
	@Unique
	private boolean theCopperierAge$chestVehicle;
	@Unique
	private float theCopperierAge$lidOpenness;

	@Override
	public boolean theCopperierAge$isChestVehicle() {
		return this.theCopperierAge$chestVehicle;
	}

	@Override
	public void theCopperierAge$setChestVehicle(boolean chestVehicle) {
		this.theCopperierAge$chestVehicle = chestVehicle;
	}

	@Override
	public float theCopperierAge$getLidOpenness() {
		return this.theCopperierAge$lidOpenness;
	}

	@Override
	public void theCopperierAge$setLidOpenness(float openness) {
		this.theCopperierAge$lidOpenness = openness;
	}
}
