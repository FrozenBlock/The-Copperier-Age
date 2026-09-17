/*
 * Copyright 2026 FrozenBlock
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

package net.frozenblock.thecopperierage.entity;

import net.frozenblock.thecopperierage.registry.TCADamageTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class MinecartImpactDamageSource extends DamageSource {
	private static final String MESSAGE_PREFIX = "death.attack.";
	private static final double MIN_SEPARATION_SQR = 1.0E-4D;

	public MinecartImpactDamageSource(Holder<DamageType> type, AbstractMinecart cart, @Nullable LivingEntity rider) {
		super(type, cart, rider);
	}

	public static DamageSource create(ServerLevel level, AbstractMinecart cart) {
		final Holder<DamageType> type = level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(TCADamageTypes.MINECART_IMPACT);
		return new MinecartImpactDamageSource(type, cart, riderOf(cart));
	}

	@Nullable
	public static LivingEntity riderOf(AbstractMinecart cart) {
		for (Entity passenger : cart.getPassengers()) {
			if (passenger instanceof LivingEntity living) return living;
		}
		return null;
	}

	@Override
	public Component getLocalizedDeathMessage(LivingEntity victim) {
		final Entity cart = this.getDirectEntity();
		if (cart == null) return super.getLocalizedDeathMessage(victim);

		final String key = MESSAGE_PREFIX + this.type().msgId();
		final Entity rider = this.getEntity();
		final Component victimName = victim.getDisplayName();

		if (wasRailed(victim, cart)) {
			return rider != null
				? Component.translatable(key + ".railed.rider", victimName, rider.getDisplayName())
				: Component.translatable(key + ".railed", victimName);
		}

		return rider != null
			? Component.translatable(key + ".rider", victimName, cart.getDisplayName(), rider.getDisplayName())
			: Component.translatable(key, victimName, cart.getDisplayName());
	}

	private static boolean wasRailed(LivingEntity victim, Entity cart) {
		if (!victim.isCrouching() && !victim.isShiftKeyDown() && !victim.isVisuallyCrawling()) return false;

		final Vec3 toCart = cart.position().subtract(victim.position()).horizontal();
		if (toCart.lengthSqr() < MIN_SEPARATION_SQR) return false;
		return victim.getLookAngle().horizontal().dot(toCart) < 0D;
	}
}
