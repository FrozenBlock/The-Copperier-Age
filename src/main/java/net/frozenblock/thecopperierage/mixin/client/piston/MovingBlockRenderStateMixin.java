/*
 * Copyright 2025 FrozenBlock
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

package net.frozenblock.thecopperierage.mixin.client.piston;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.client.renderer.blockentity.state.impl.MovingBlockRenderStateImpl;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(MovingBlockRenderState.class)
public class MovingBlockRenderStateMixin implements MovingBlockRenderStateImpl {

	@Unique
	private BlockEntityRenderState theCopperierAge$blockEntityRenderState = null;

	@Override
	public void theCopperierAge$setBlockEntityRenderState(BlockEntityRenderState renderState) {
		this.theCopperierAge$blockEntityRenderState = renderState;
	}

	@Override
	public BlockEntityRenderState theCopperierAge$getBlockEntityRenderState() {
		return this.theCopperierAge$blockEntityRenderState;
	}
}
