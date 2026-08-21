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

package net.frozenblock.thecopperierage.mixin.entity.copper_golem;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import net.frozenblock.thecopperierage.entity.ai.coppergolem.CopperGolemPressButton;
import net.frozenblock.thecopperierage.registry.TCAMemoryModuleTypes;
import net.frozenblock.thecopperierage.tag.TCABlockItemTags;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.animal.golem.CopperGolemAi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = CopperGolemAi.class, priority = 997)
public class CopperGolemAiMixin {

	@ModifyExpressionValue(
		method = "initCoreActivity",
		at = @At(
			value = "INVOKE",
			target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;"
		)
	)
	private static ImmutableList theCopperierAge$addPressButtonCountCooldown(ImmutableList original) {
		final ArrayList behaviors = new ArrayList<>(original);
		behaviors.add(new CountDownCooldownTicks(TCAMemoryModuleTypes.BUTTON_PRESS_COOLDOWN_TICKS.get()));
		behaviors.add(new CountDownCooldownTicks(TCAMemoryModuleTypes.NEARBY_BUTTON_SEARCH_TICKS.get()));
		return ImmutableList.copyOf(behaviors);
	}

	@ModifyExpressionValue(
		method = "initIdleActivity",
		at = @At(
			value = "INVOKE",
			target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;",
			ordinal = 0
		)
	)
	private static ImmutableList theCopperierAge$addPressButton(ImmutableList original) {
		final ArrayList behaviors = new ArrayList<>(original);
		behaviors.add(
			Pair.of(
				0,
				new CopperGolemPressButton(
					1F,
					state -> state.is(TCABlockItemTags.COPPER_BUTTONS.block()),
					12,
					6
				)
			)
		);
		return ImmutableList.copyOf(behaviors);
	}
}
