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

package net.frozenblock.thecopperierage.mixin.client.copper_golem;

import net.frozenblock.thecopperierage.entity.impl.CopperGolemPressButtonInterface;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.renderer.entity.state.CopperGolemRenderState;
import net.minecraft.world.entity.AnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@ClientOnly
@Mixin(CopperGolemRenderState.class)
public class CopperGolemRenderStateMixin implements CopperGolemPressButtonInterface {

	@Unique
	public final AnimationState theCopperierAge$pressingButton = new AnimationState();

	@Unique
	@Override
	public AnimationState theCopperierAge$getPressingButtonAnimationState() {
		return this.theCopperierAge$pressingButton;
	}
}
