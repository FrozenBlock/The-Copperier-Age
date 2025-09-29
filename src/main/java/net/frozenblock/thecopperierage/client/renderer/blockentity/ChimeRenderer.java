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

package net.frozenblock.thecopperierage.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.render.FrozenLibRenderTypes;
import net.frozenblock.thecopperierage.block.ChimeBlock;
import net.frozenblock.thecopperierage.block.entity.ChimeBlockEntity;
import net.frozenblock.thecopperierage.block.state.properties.ChimeAttachType;
import net.frozenblock.thecopperierage.client.TCAModelLayers;
import net.frozenblock.thecopperierage.client.model.ChimeModel;
import net.frozenblock.thecopperierage.client.renderer.blockentity.state.ChimeRenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ChimeRenderer<T extends ChimeBlockEntity> implements BlockEntityRenderer<T, ChimeRenderState> {
	private final ChimeModel model;
	private final ChimeModel chainsModel;

	public ChimeRenderer(@NotNull Context context) {
		this.model = new ChimeModel(context.bakeLayer(TCAModelLayers.CHIME), RenderType::entityCutout, false);
		this.chainsModel = new ChimeModel(context.bakeLayer(TCAModelLayers.CHIME), FrozenLibRenderTypes::entityCutoutNoShading, true);
	}

	@Override
	public void submit(
		@NotNull ChimeRenderState renderState,
		@NotNull PoseStack poseStack,
		@NotNull SubmitNodeCollector submitNodeCollector,
		@NotNull CameraRenderState cameraRenderState
	) {
		poseStack.pushPose();
		poseStack.translate(0.5F, 1.5F, 0.5F);
		poseStack.mulPose(Axis.XP.rotationDegrees(-180F));
		poseStack.mulPose(Axis.YP.rotationDegrees(-renderState.visualDirection.toYRot()));

		submitNodeCollector.submitModel(
			this.model,
			renderState,
			poseStack,
			this.model.renderType(renderState.texture),
			renderState.lightCoords,
			OverlayTexture.NO_OVERLAY,
			0,
			renderState.breakProgress
		);
		submitNodeCollector.submitModel(
			this.chainsModel,
			renderState,
			poseStack,
			this.chainsModel.renderType(renderState.texture),
			renderState.lightCoords,
			OverlayTexture.NO_OVERLAY,
			0,
			renderState.breakProgress
		);

		poseStack.popPose();
	}

	@Override
	public @NotNull ChimeRenderState createRenderState() {
		return new ChimeRenderState();
	}

	@Override
	public void extractRenderState(
		@NotNull T chime,
		@NotNull ChimeRenderState renderState,
		float partialTick,
		@NotNull Vec3 cameraPos,
		@Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay
	) {
		BlockEntityRenderer.super.extractRenderState(chime, renderState, partialTick, cameraPos, crumblingOverlay);

		final BlockState state = chime.getBlockState();
		renderState.extractTexture(state);

		renderState.animationProgress = (chime.age + partialTick + chime.animationOffset + Mth.lerp(partialTick, chime.prevAccumulatedStrength, chime.accumulatedStrength)) * 0.15F;

		final Direction facing = state.getValue(ChimeBlock.FACING);
		renderState.hanging = state.getValue(ChimeBlock.ATTACHMENT) == ChimeAttachType.CEILING;
		renderState.direction = facing;
		renderState.visualDirection = facing.getAxis() == Direction.Axis.Z ? facing.getOpposite() : facing;
		renderState.influence = chime.getInfluence(partialTick).scale(0.4D);
		renderState.relativeInfluence = renderState.influence.yRot(facing.toYRot() * Mth.DEG_TO_RAD);
	}

}
