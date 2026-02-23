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
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecart.class)
public class AbstractMinecartMixin {

	@Inject(
		method = "createMinecart",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/EntityType;createDefaultStackConfig(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Ljava/util/function/Consumer;"
		)
	)
	private static <T extends AbstractMinecart> void theCopperierAge$placeMinecartSound(
		Level level,
		double x, double y, double z,
		EntityType<T> type,
		EntitySpawnReason reason,
		ItemStack stack,
		@Nullable Player player,
		CallbackInfoReturnable<T> infoReturnable
	) {
		level.playSound(
			null,
			x, y, z,
			TCASounds.ENTITY_MINECART_PLACE,
			SoundSource.NEUTRAL,
			1F,
			(level.getRandom().nextFloat() * 0.3F) + 0.85F
		);
	}

}
