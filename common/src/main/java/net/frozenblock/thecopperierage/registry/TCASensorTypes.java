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

import java.util.function.Supplier;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.entity.ai.coppergolem.CopperGolemSpecificSensor;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

public final class TCASensorTypes {
	private static final DeferredRegister<SensorType<?>> REGISTER = DeferredRegister.create(
		Registries.SENSOR_TYPE,
		TCAConstants.MOD_ID
	);

	public static final DeferredHolder<SensorType<?>, SensorType<CopperGolemSpecificSensor>> COPPER_GOLEM_SPECIFIC_SENSOR = register("copper_golem_specific_sensor", CopperGolemSpecificSensor::new);

	static {
		REGISTER.register();
	}

	public static void init() {}

	private static <U extends Sensor<?>> DeferredHolder<SensorType<?>, SensorType<U>> register(String name, Supplier<U> factory) {
		return REGISTER.register(name, () -> new SensorType<>(factory));
	}
}
