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

package net.frozenblock.thecopperierage.mixin.entity.minecart;

import net.frozenblock.thecopperierage.registry.TCASounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VehicleEntity.class)
public class VehicleEntityMixin {

	@Inject(
		method = "hurtServer",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/vehicle/VehicleEntity;discard()V"
		)
	)
	public void theCopperierAge$destroyMinecartSoundOnDiscard(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> info) {
		if (!(VehicleEntity.class.cast(this) instanceof AbstractMinecart minecart)) return;
		minecart.playSound(TCASounds.ENTITY_MINECART_BREAK.get(), 1F, (minecart.getRandom().nextFloat() * 0.3F) + 0.85F);
	}

	@Inject(method = "destroy(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/Item;)V", at = @At("HEAD"))
	public void theCopperierAge$destroyMinecartSoundOnDestroy(ServerLevel serverLevel, Item dropItem, CallbackInfo info) {
		if (!(VehicleEntity.class.cast(this) instanceof AbstractMinecart minecart)) return;
		minecart.playSound(TCASounds.ENTITY_MINECART_BREAK.get(), 1F, (minecart.getRandom().nextFloat() * 0.3F) + 0.85F);
	}

}
