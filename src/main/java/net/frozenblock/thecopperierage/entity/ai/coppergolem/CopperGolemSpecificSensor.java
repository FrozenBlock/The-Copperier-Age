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

package net.frozenblock.thecopperierage.entity.ai.coppergolem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Set;
import net.frozenblock.thecopperierage.registry.TCAMemoryModuleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.animal.golem.CopperGolem;

public class CopperGolemSpecificSensor extends Sensor<CopperGolem> {

	@Override
	public Set<MemoryModuleType<?>> requires() {
		return ImmutableSet.of(
			TCAMemoryModuleTypes.NEARBY_COPPER_GOLEMS,
			MemoryModuleType.NEAREST_LIVING_ENTITIES
		);
	}

	@Override
	protected void doTick(ServerLevel level, CopperGolem body) {
		final Brain<?> brain = body.getBrain();
		final ArrayList<CopperGolem> copperGolems = Lists.newArrayList();
		for (LivingEntity livingEntity : brain.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).orElse(ImmutableList.of())) {
			if (livingEntity instanceof CopperGolem copperGolem) copperGolems.add(copperGolem);
		}
		brain.setMemory(TCAMemoryModuleTypes.NEARBY_COPPER_GOLEMS, copperGolems);
	}
}
