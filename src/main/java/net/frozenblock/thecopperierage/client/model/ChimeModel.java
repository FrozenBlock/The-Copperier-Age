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

package net.frozenblock.thecopperierage.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.client.renderer.blockentity.state.ChimeRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class ChimeModel extends Model<ChimeRenderState> {
	private final ModelPart support;
	private final ModelPart supportChain;
	private final ModelPart bar;
	private final ModelPart chime1;
	private final ModelPart tube1;
	private final ModelPart chime2;
	private final ModelPart tube2;
	private final ModelPart chime3;
	private final ModelPart tube3;
	private final ModelPart chime4;
	private final ModelPart tube4;
	private final ModelPart chime5;
	private final ModelPart tube5;
	private final List<Pair<ModelPart, ModelPart>> chimes;

	public ChimeModel(ModelPart root) {
		super(root, RenderType::entityCutout);
		this.support = root.getChild("support");
		this.supportChain = this.support.getChild("chain");
		this.bar = this.support.getChild("bar");
		this.chime1 = this.bar.getChild("chime1");
		this.tube1 = this.chime1.getChild("tube");
		this.chime2 = this.bar.getChild("chime2");
		this.tube2 = this.chime2.getChild("tube");
		this.chime3 = this.bar.getChild("chime3");
		this.tube3 = this.chime3.getChild("tube");
		this.chime4 = this.bar.getChild("chime4");
		this.tube4 = this.chime4.getChild("tube");
		this.chime5 = this.bar.getChild("chime5");
		this.tube5 = this.chime5.getChild("tube");

		this.chimes = ImmutableList.of(
			Pair.of(this.chime1, this.tube1),
			Pair.of(this.chime2, this.tube2),
			Pair.of(this.chime3, this.tube3),
			Pair.of(this.chime4, this.tube4),
			Pair.of(this.chime5, this.tube5)
		);
	}

	public static @NotNull LayerDefinition createLayerDefinition() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition root = meshdefinition.getRoot();

		PartDefinition supportChain = root.addOrReplaceChild("support", CubeListBuilder.create(), PartPose.offset(0F, 8F, 0F));
		supportChain.addOrReplaceChild(
			"chain",
			CubeListBuilder.create()
				.texOffs(1, 4)
				.addBox(-1.5F, 1F, 0F, 3F, 3F, 0F)
				.texOffs(1, -2)
				.addBox(0F, 0F, -1.5F, 0F, 2F, 3F),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, -0.7854F, 0F)
		);

		PartDefinition bar = supportChain.addOrReplaceChild(
			"bar",
			CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-1F, 0F, -8F, 2F, 2F, 16),
			PartPose.offset(0F, 4F, 0F)
		);

		PartDefinition chime1 = bar.addOrReplaceChild("chime1", CubeListBuilder.create(), PartPose.offset(0F, 2F, -6F));
		chime1.addOrReplaceChild(
			"chain",
			CubeListBuilder.create()
				.texOffs(1, 10)
				.addBox(-1.5F, 0F, 0F, 3F, 1F, 0F)
				.texOffs(1, 5)
				.addBox(0F, 0F, -1.5F, 0F, 1F, 3F),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, -0.7854F, 0F)
		);
		chime1.addOrReplaceChild(
			"tube",
			CubeListBuilder.create()
				.texOffs(32, 18)
				.addBox(-1F, 0F, -1F, 2F, 3F, 2F),
			PartPose.offset(0F, 1F, 0F)
		);

		PartDefinition chime2 = bar.addOrReplaceChild("chime2", CubeListBuilder.create(), PartPose.offset(0F, 2F, -3F));
		chime2.addOrReplaceChild(
			"chain",
			CubeListBuilder.create()
				.texOffs(1, 5)
				.addBox(0F, 0F, -1.5F, 0F, 1F, 3F)
				.texOffs(1, 10)
				.addBox(-1.5F, 0F, 0F, 3F, 1F, 0F),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, -0.7854F, 0F)
		);
		chime2.addOrReplaceChild(
			"tube",
			CubeListBuilder.create()
				.texOffs(24, 18)
				.addBox(-1F, 0F, -1F, 2F, 5F, 2F),
			PartPose.offset(0F, 1F, 0F)
		);

		PartDefinition chime3 = bar.addOrReplaceChild("chime3", CubeListBuilder.create(), PartPose.offset(0F, 2F, 0F));
		chime3.addOrReplaceChild(
			"chain",
			CubeListBuilder.create()
				.texOffs(1, 10)
				.addBox(-1.5F, 0F, 0F, 3F, 1F, 0F)
				.texOffs(1, 5)
				.addBox(0F, 0F, -1.5F, 0F, 1F, 3F),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, -0.7854F, 0F)
		);
		chime3.addOrReplaceChild(
			"tube",
			CubeListBuilder.create()
				.texOffs(16, 18)
				.addBox(-1F, 0F, -1F, 2F, 7F, 2F),
			PartPose.offset(0F, 1F, 0F)
		);

		PartDefinition chime4 = bar.addOrReplaceChild("chime4", CubeListBuilder.create(), PartPose.offset(0F, 2F, 3F));
		chime4.addOrReplaceChild(
			"chain",
			CubeListBuilder.create()
				.texOffs(1, 10)
				.addBox(-1.5F, 0F, 0F, 3F, 1F, 0F)
				.texOffs(1, 5)
				.addBox(0F, 0F, -1.5F, 0F, 1F, 3F),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, -0.7854F, 0F)
		);
		chime4.addOrReplaceChild(
			"tube",
			CubeListBuilder.create()
				.texOffs(8, 18)
				.addBox(-1F, 0F, -1F, 2F, 9F, 2F),
			PartPose.offset(0F, 1F, 0F)
		);

		PartDefinition chime5 = bar.addOrReplaceChild("chime5", CubeListBuilder.create(), PartPose.offset(0F, 2F, 6F));
		chime5.addOrReplaceChild(
			"chain",
			CubeListBuilder.create()
				.texOffs(1, 10)
				.addBox(-1.5F, 0F, 0F, 3F, 1F, 0F)
				.texOffs(1, 5)
				.addBox(0F, 0F, -1.5F, 0F, 1F, 3F),
			PartPose.offsetAndRotation(0F, 0F, 0F, 0F, -0.7854F, 0F)
		);
		chime5.addOrReplaceChild(
			"tube",
			CubeListBuilder.create()
				.texOffs(0, 18)
				.addBox(-1F, 0F, -1F, 2F, 4F, 2F),
			PartPose.offset(0F, 1F, 0F)
		);

		return LayerDefinition.create(meshdefinition, 48, 48);
	}

	@Override
	public void setupAnim(@NotNull ChimeRenderState renderState) {
		super.setupAnim(renderState);

		final Vec3 movement = renderState.chimeMovement;
		final float animProgress = renderState.animationProgress * 0.15F;

		if (renderState.hanging) {
			final Pair<Float, Float> supportRot = getRotationForMovement(0F, animProgress, movement);
			this.support.xRot += supportRot.getFirst();
			this.support.zRot += supportRot.getSecond();

			final Pair<Float, Float> barRot = getRotationForMovement(-1F, animProgress, movement);
			this.bar.xRot += barRot.getFirst();
			this.bar.zRot += barRot.getSecond();

			this.supportChain.skipDraw = false;
			this.bar.skipDraw = false;
		} else {
			this.supportChain.skipDraw = true;
			this.bar.skipDraw = true;
		}

		float movementOffset = 0.5F;
		for (Pair<ModelPart, ModelPart> pair : this.chimes) {
			movementOffset += 1F;
			final ModelPart chime = pair.getFirst();
			final ModelPart tube = pair.getSecond();

			final Pair<Float, Float> chimeRotA = getRotationForMovement(movementOffset, animProgress, movement);
			chime.xRot += chimeRotA.getFirst();
			chime.zRot += chimeRotA.getSecond();

			final Pair<Float, Float> chimeRotB = getRotationForMovement(movementOffset - 0.5F, animProgress, movement);
			tube.xRot += chimeRotB.getFirst();
			tube.zRot += chimeRotB.getSecond();
		}
	}

	@Contract("_, _, _ -> null")
	private static @NotNull Pair<Float, Float> getRotationForMovement(float animationOffset, float ageInTicks, @NotNull Vec3 movement) {
		final float yAge = ageInTicks * 0.4F;

		final float zMovement =Mth.clamp((float) movement.z + (Mth.cos(yAge) * (float) movement.y * 0.5F), -0.5F, 0.5F);
		final float xRotOffset = Math.min(1F, Mth.abs(zMovement));
		float xRot = (Mth.cos(ageInTicks + zMovement - (animationOffset + 2F)) * 0.75F) + (xRotOffset * 0.75F);

		final float xMovement = Mth.clamp((float) movement.x + (Mth.sin(yAge) * (float) movement.y * 0.5F), -0.5F, 0.5F);
		final float zRotOffset = Math.min(1F, Mth.abs(xMovement));
		float zRot = (Mth.cos(ageInTicks + xMovement - animationOffset) * 0.75F) + (zRotOffset * 0.75F);

		xRot *= (zMovement * 0.75F) + 0.01F;
		zRot *= (xMovement * 0.75F) + 0.01F;

		return Pair.of(xRot, zRot);
	}
}
