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

package net.frozenblock.thecopperierage.registry;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public final class TCAParticleTypes {
	public static final SimpleParticleType COPPER_SMOKE = register("copper_smoke");
	public static final SimpleParticleType LARGE_COPPER_SMOKE = register("large_copper_smoke");
	public static final SimpleParticleType COPPER_LAVA = register("copper_lava");
	public static final SimpleParticleType COPPER_CAMPFIRE_COSY_SMOKE = register("copper_campfire_cosy_smoke");
	public static final SimpleParticleType COPPER_CAMPFIRE_SIGNAL_SMOKE = register("copper_campfire_signal_smoke");

	private TCAParticleTypes() {
		throw new UnsupportedOperationException("TCAParticleTypes only static declarations.");
	}

	public static void registerParticles() {
		TCAConstants.logWithModId("Registering Particles for", TCAConstants.UNSTABLE_LOGGING);
	}

	private static SimpleParticleType register(String name, boolean alwaysShow) {
		return Registry.register(BuiltInRegistries.PARTICLE_TYPE, TCAConstants.id(name), FabricParticleTypes.simple(alwaysShow));
	}

	private static SimpleParticleType register(String name) {
		return register(name, false);
	}

	private static <T extends ParticleOptions> ParticleType<T> register(
		String string,
		boolean alwaysShow,
		Function<ParticleType<T>, MapCodec<T>> function,
		Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> function2
	) {
		return register(TCAConstants.id(string), alwaysShow, function, function2);
	}

	private static <T extends ParticleOptions> ParticleType<T> register(
		Identifier identifier,
		boolean alwaysShow,
		Function<ParticleType<T>, MapCodec<T>> function,
		Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> function2
	) {
		return Registry.register(BuiltInRegistries.PARTICLE_TYPE, identifier, new ParticleType<T>(alwaysShow) {
			@Override
			public MapCodec<T> codec() {
				return function.apply(this);
			}

			@Override
			public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
				return function2.apply(this);
			}
		});
	}
}
