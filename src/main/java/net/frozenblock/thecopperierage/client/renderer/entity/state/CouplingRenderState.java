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

package net.frozenblock.thecopperierage.client.renderer.entity.state;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.frozenblock.lib.render.FrozenLibRenderTypes;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.entity.impl.CouplingToEntityInterface;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class CouplingRenderState {
	private static float Y_OFFSET = 1.5F / 16F;
	private static final RenderType COUPLING_1_RENDER_TYPE = FrozenLibRenderTypes.entityCutoutNoShading(TCAConstants.id("textures/entity/minecart/coupling1.png"));
	private static final RenderType COUPLING_2_RENDER_TYPE = FrozenLibRenderTypes.entityCutoutNoShading(TCAConstants.id("textures/entity/minecart/coupling2.png"));
	public static final RenderStateDataKey<CouplingRenderState> COUPLING_RENDER_STATE = RenderStateDataKey.create();
	private static final float COUPLING_ROTATION = 0F;
	public Vec3 vector = Vec3.ZERO;

	public CouplingRenderState() {
	}

	public static void extract(AbstractMinecart minecart, MinecartRenderState renderState, float partialTicks) {
		if (!(minecart instanceof CouplingToEntityInterface coupleInterface)) return;

		final Entity coupledTo = coupleInterface.theCopperierAge$getCoupledTo();
		if (coupledTo == null) return;

		final CouplingRenderState couplingRenderState = new CouplingRenderState();
		couplingRenderState.vector = coupledTo.getPosition(partialTicks).subtract(minecart.getPosition(partialTicks));
		renderState.setData(COUPLING_RENDER_STATE, couplingRenderState);
	}

	public static void renderCoupling(
		PoseStack poseStack,
		SubmitNodeCollector collector,
		MinecartRenderState renderState,
		int lightCoords
	) {
		final CouplingRenderState couplingRenderState = renderState.getData(COUPLING_RENDER_STATE);
		if (couplingRenderState == null) return;

		Vec3 couplingVector = couplingRenderState.vector;
		float length = (float) couplingVector.length() / 2F;
		couplingVector = couplingVector.normalize();
		float xRot = (float) Math.acos(couplingVector.y);
		float yRot = (float) ((Mth.PI / 2F) - Math.atan2(couplingVector.z, couplingVector.x));

		poseStack.pushPose();
		poseStack.translate(0F, Y_OFFSET, 0F);

		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(yRot * (180F / Mth.PI)));
		poseStack.mulPose(Axis.XP.rotationDegrees(xRot * (180F / Mth.PI)));

		float rr1 = 0.2F;
		float wx = Mth.cos((COUPLING_ROTATION + Mth.PI)) * rr1;
		float wz = Mth.sin((COUPLING_ROTATION + Mth.PI)) * rr1;
		float ex = Mth.cos((COUPLING_ROTATION + 0F)) * rr1;
		float ez = Mth.sin((COUPLING_ROTATION + 0F)) * rr1;
		float minU = 0F;
		float maxU = 3F / 16F;
		float minV = 0F;
		float maxV = minV + length * 2.5F;


		collector.submitCustomGeometry(poseStack, COUPLING_1_RENDER_TYPE, (pose, buffer) -> {
			vertex(buffer, pose, wx, length, wz, maxU, maxV, lightCoords);
			vertex(buffer, pose, wx, 0F, wz, maxU, minV, lightCoords);
			vertex(buffer, pose, ex, 0F, ez, minU, minV, lightCoords);
			vertex(buffer, pose, ex, length, ez, minU, maxV, lightCoords);
		});

		collector.submitCustomGeometry(poseStack, COUPLING_2_RENDER_TYPE, (pose, buffer) -> {
			vertex(buffer, pose, wx, 0F, wz, maxU, maxV, lightCoords);
			vertex(buffer, pose, wx, length, wz, maxU, minV, lightCoords);
			vertex(buffer, pose, ex, length, ez, minU, minV, lightCoords);
			vertex(buffer, pose, ex, 0F, ez, minU, maxV, lightCoords);
		});

		poseStack.popPose();
		poseStack.popPose();
	}

	private static void vertex(VertexConsumer builder, PoseStack.Pose pose, float x, float y, float z, float u, float v, int lightCoords) {
		builder.addVertex(pose, x, y, z)
			.setColor(1F, 1F, 1F, 1F)
			.setUv(u, v)
			.setOverlay(OverlayTexture.NO_OVERLAY)
			.setLight(lightCoords)
			.setNormal(pose, 0F, 1F, 0F);
	}
}
