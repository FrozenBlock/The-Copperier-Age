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
import net.frozenblock.thecopperierage.client.coupling.MinecartCouplingClientHandler;
import net.frozenblock.thecopperierage.entity.impl.CouplingToEntityInterface;
import net.minecraft.client.Minecraft;
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
	private static final float THREE_AND_A_HALF_PIXELS = 1.5F / 16F;
	private static final RenderType COUPLING_RENDER_TYPE = FrozenLibRenderTypes.entityCutoutNoShading(TCAConstants.id("textures/entity/minecart/coupling.png"));
	public static final RenderStateDataKey<CouplingRenderState> COUPLING_RENDER_STATE = RenderStateDataKey.create();
	public static final RenderStateDataKey<CouplingRenderState> COUPLING_HELD_RENDER_STATE = RenderStateDataKey.create();
	public Vec3 vector = Vec3.ZERO;

	public CouplingRenderState() {
	}

	public static void extract(AbstractMinecart minecart, MinecartRenderState renderState, float partialTicks) {
		if (!(minecart instanceof CouplingToEntityInterface coupleInterface)) return;

		final Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player != null) {
			MinecartCouplingClientHandler.createRenderState(minecraft.player, minecart, partialTicks)
				.ifPresent(couplingRenderState -> renderState.setData(COUPLING_HELD_RENDER_STATE, couplingRenderState));
		}

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
		int lightCoords,
		RenderStateDataKey<CouplingRenderState> key
	) {
		final CouplingRenderState couplingRenderState = renderState.getData(key);
		if (couplingRenderState == null) return;

		Vec3 couplingVector = couplingRenderState.vector;
		float length = (float) couplingVector.length();
		couplingVector = couplingVector.normalize();
		float xRot = (float) Math.acos(couplingVector.y);
		float yRot = (float) ((Mth.PI / 2F) - Math.atan2(couplingVector.z, couplingVector.x));

		poseStack.pushPose();
		poseStack.translate(0F, THREE_AND_A_HALF_PIXELS, 0F);

		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotation(yRot));
		poseStack.mulPose(Axis.XP.rotation(xRot));

		float x1 = Mth.cos(Mth.PI) * THREE_AND_A_HALF_PIXELS;
		float x2 = Mth.cos(0F) * THREE_AND_A_HALF_PIXELS;
		float z1 = Mth.sin(0F) * THREE_AND_A_HALF_PIXELS;
		float z2 = Mth.sin(Mth.PI) * THREE_AND_A_HALF_PIXELS;

		final float minU = 0F;
		final float maxU = 3F / 16F;
		final float minV = 0F;

		final int lengthInPixels = (int) (length * 16);
		int lengthRendered = 0;

		while (lengthRendered < lengthInPixels) {
			final int prevLength = lengthRendered;
			lengthRendered = Math.min(lengthRendered + 16, lengthInPixels);
			final int lengthToRender = lengthRendered - prevLength;

			final float lengthStart = prevLength / 16F;
			final float lengthEnd = lengthRendered / 16F;
			final float maxV = lengthToRender / 16F;

			collector.submitCustomGeometry(poseStack, COUPLING_RENDER_TYPE, (pose, buffer) -> {
				vertex(buffer, pose, x1, lengthEnd, z2, maxU, maxV, lightCoords);
				vertex(buffer, pose, x1, lengthStart, z2, maxU, minV, lightCoords);
				vertex(buffer, pose, x2, lengthStart, z1, minU, minV, lightCoords);
				vertex(buffer, pose, x2, lengthEnd, z1, minU, maxV, lightCoords);

				vertex(buffer, pose, x1, lengthStart, z2, maxU, minV, lightCoords);
				vertex(buffer, pose, x1, lengthEnd, z2, maxU, maxV, lightCoords);
				vertex(buffer, pose, x2, lengthEnd, z1, minU, maxV, lightCoords);
				vertex(buffer, pose, x2, lengthStart, z1, minU, minV, lightCoords);
			});
		}

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
