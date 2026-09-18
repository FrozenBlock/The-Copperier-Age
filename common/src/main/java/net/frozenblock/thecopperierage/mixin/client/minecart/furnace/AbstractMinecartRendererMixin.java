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

package net.frozenblock.thecopperierage.mixin.client.minecart.furnace;

import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.entity.vehicle.minecart.api.MinecartFacingHelper;
import net.mehvahdjukaar.candlelight.api.ClientOnly;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@ClientOnly
@Mixin(AbstractMinecartRenderer.class)
public class AbstractMinecartRendererMixin {

	@Shadow
	@Final
	private BlockModelResolver blockModelResolver;

	@Inject(
		method = "extractRenderState(Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;F)V",
		at = @At("TAIL")
	)
	private <T extends AbstractMinecart, S extends MinecartRenderState> void theCopperierAge$orientFurnaceDisplay(
		T entity, S state, float partialTicks, CallbackInfo info
	) {
		if (!MinecartFacingHelper.isFurnaceMinecart(entity)) return;
		if (!TCAConfig.IMPROVED_FURNACE_MINECARTS.get()) return;

		final Vec3 facing = MinecartFacingHelper.getFacing(entity);
		if (!MinecartFacingHelper.hasFacing(facing)) return;

		final BlockState displayState = entity.getDisplayBlockState();
		if (!displayState.hasProperty(FurnaceBlock.FACING)) return;

		final Direction displayFacing = MinecartFacingHelper.displayFacingFor(
			theCopperierAge$renderYaw(state) * Mth.DEG_TO_RAD,
			facing,
			MinecartFacingHelper.getDisplayFacing(entity)
		);
		MinecartFacingHelper.setDisplayFacing(entity, displayFacing);

		if (displayState.getValue(FurnaceBlock.FACING) == displayFacing) return;

		this.blockModelResolver.update(
			state.displayBlockModel,
			displayState.setValue(FurnaceBlock.FACING, displayFacing),
			AbstractMinecartRenderer.BLOCK_DISPLAY_CONTEXT
		);
	}

	@Unique
	private static float theCopperierAge$renderYaw(MinecartRenderState state) {
		if (state.isNewRender) return state.yRot;

		float rotation = state.yRot;
		if (state.posOnRail != null && state.frontPos != null && state.backPos != null) {
			final Vec3 direction = state.backPos.subtract(state.frontPos);
			if (direction.length() != 0D) {
				final Vec3 normalized = direction.normalize();
				rotation = (float) (Math.atan2(normalized.z, normalized.x) * 180D / Math.PI);
			}
		}
		return 180F - rotation;
	}
}
