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

import net.frozenblock.thecopperierage.entity.impl.ChestVehicleInterface;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestMenu.class)
public abstract class ChestMenuMixin {

	@Shadow
	public abstract Container getContainer();

	@Inject(
		method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;I)V",
		at = @At("TAIL")
	)
	private void theCopperierAge$increaseChestVehicleOpeners(MenuType<?> type, int containerId, Inventory inventory, Container container, int rows, CallbackInfo info) {
		if (container instanceof ChestVehicleInterface lidAnimating) lidAnimating.theCopperierAge$onContainerOpen();
	}

	@Inject(method = "removed", at = @At("TAIL"))
	private void theCopperierAge$decreaseChestVehicleOpeners(Player player, CallbackInfo info) {
		if (this.getContainer() instanceof ChestVehicleInterface lidAnimating) lidAnimating.theCopperierAge$onContainerClose();
	}
}
