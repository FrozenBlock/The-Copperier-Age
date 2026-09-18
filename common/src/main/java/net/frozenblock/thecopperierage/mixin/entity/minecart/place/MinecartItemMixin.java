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

package net.frozenblock.thecopperierage.mixin.entity.minecart.place;

import com.llamalad7.mixinextras.sugar.Local;
import net.frozenblock.thecopperierage.registry.TCASounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecartItem.class)
public class MinecartItemMixin {

	@Inject(
		method = "useOn",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
		)
	)
	private static void theCopperierAge$placeMinecartSound(
		UseOnContext context, CallbackInfoReturnable<InteractionResult> info,
		@Local(name = "level") Level level,
		@Local(name = "spawnPos") Vec3 spawnPos
	) {
		level.playSound(
			null,
			spawnPos.x, spawnPos.y, spawnPos.z,
			TCASounds.ENTITY_MINECART_PLACE.get(),
			SoundSource.NEUTRAL,
			0.75F,
			(level.getRandom().nextFloat() * 0.3F) + 0.85F
		);
	}
}
