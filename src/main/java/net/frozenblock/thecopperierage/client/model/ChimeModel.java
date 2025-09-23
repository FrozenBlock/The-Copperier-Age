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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
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
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import java.util.List;

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
		super(root, RenderType::entitySolid);
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

		PartDefinition supportChain = root.addOrReplaceChild("support", CubeListBuilder.create(), PartPose.offsetAndRotation(0, 8, 0, 0, 0F, 0));
		supportChain.addOrReplaceChild(
			"chain",
			CubeListBuilder.create()
				.texOffs(1, 4)
				.addBox(-1.5F, 1, 0, 3, 3, 0)
				.texOffs(1, -2)
				.addBox(0, 0, -1.5F, 0, 2, 3),
			PartPose.offsetAndRotation(0, 0, 0, 0, -0.7854F, 0)
		);

		PartDefinition bar = supportChain.addOrReplaceChild(
			"bar",
			CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-1, -1, 0, 2, 2, 16),
			PartPose.offset(0, 5, -8)
		);

		PartDefinition chime1 = bar.addOrReplaceChild("chime1", CubeListBuilder.create(), PartPose.offset(0, 1, 2));
		chime1.addOrReplaceChild(
			"chain",
			CubeListBuilder.create()
				.texOffs(1, 10)
				.addBox(-1.5F, 0, 0, 3, 1, 0)
				.texOffs(1, 5)
				.addBox(0, 0, -1.5F, 0, 1, 3),
			PartPose.offsetAndRotation(0, 0, 0, 0, -0.7854F, 0)
		);
		chime1.addOrReplaceChild(
			"tube",
			CubeListBuilder.create()
				.texOffs(32, 18)
				.addBox(-1, 0, -1, 2, 3, 2),
			PartPose.offset(0, 1, 0)
		);

		PartDefinition chime2 = bar.addOrReplaceChild("chime2", CubeListBuilder.create(), PartPose.offset(0, 1, 5));
		chime2.addOrReplaceChild(
			"chain",
			CubeListBuilder.create()
				.texOffs(1, 5)
				.addBox(0, 0, -1.5F, 0, 1, 3)
				.texOffs(1, 10)
				.addBox(-1.5F, 0, 0, 3, 1, 0),
			PartPose.offsetAndRotation(0, 0, 0, 0, -0.7854F, 0)
		);
		chime2.addOrReplaceChild(
			"tube",
			CubeListBuilder.create()
				.texOffs(24, 18)
				.addBox(-1, 0, -1, 2, 5, 2),
			PartPose.offset(0, 1, 0)
		);

		PartDefinition chime3 = bar.addOrReplaceChild("chime3", CubeListBuilder.create(), PartPose.offset(0, 1, 8));
		chime3.addOrReplaceChild(
			"chain",
			CubeListBuilder.create()
				.texOffs(1, 10)
				.addBox(-1.5F, 0, 0, 3, 1, 0)
				.texOffs(1, 5)
				.addBox(0, 0, -1.5F, 0, 1, 3),
			PartPose.offsetAndRotation(0, 0, 0, 0, -0.7854F, 0)
		);
		chime3.addOrReplaceChild(
			"tube",
			CubeListBuilder.create()
				.texOffs(16, 18)
				.addBox(-1, 0, -1, 2, 7, 2),
			PartPose.offset(0, 1, 0)
		);

		PartDefinition chime4 = bar.addOrReplaceChild("chime4", CubeListBuilder.create(), PartPose.offset(0, 1, 11));
		chime4.addOrReplaceChild(
			"chain",
			CubeListBuilder.create()
				.texOffs(1, 10)
				.addBox(-1.5F, 0, 0, 3, 1, 0)
				.texOffs(1, 5)
				.addBox(0, 0, -1.5F, 0, 1, 3),
			PartPose.offsetAndRotation(0, 0, 0, 0, -0.7854F, 0)
		);
		chime4.addOrReplaceChild(
			"tube",
			CubeListBuilder.create()
				.texOffs(8, 18)
				.addBox(-1, 0, -1, 2, 9, 2),
			PartPose.offset(0, 1, 0)
		);

		PartDefinition chime5 = bar.addOrReplaceChild("chime5", CubeListBuilder.create(), PartPose.offset(0, 1, 14));
		chime5.addOrReplaceChild(
			"chain",
			CubeListBuilder.create()
				.texOffs(1, 10)
				.addBox(-1.5F, 0, 0, 3, 1, 0)
				.texOffs(1, 5)
				.addBox(0, 0, -1.5F, 0, 1, 3),
			PartPose.offsetAndRotation(0, 0, 0, 0, -0.7854F, 0)
		);
		chime5.addOrReplaceChild(
			"tube",
			CubeListBuilder.create()
				.texOffs(0, 18)
				.addBox(-1, 0, -1, 2, 4, 2),
			PartPose.offset(0, 1, 0)
		);

		return LayerDefinition.create(meshdefinition, 48, 48);
	}

	@Override
	public void setupAnim(@NotNull ChimeRenderState renderState) {
		super.setupAnim(renderState);

		final Vec3 movement = renderState.chimeMovement.yRot(renderState.blockYRot);
		final Vec3 chimeMovement = movement.scale(0.5D);

		if (renderState.hanging) {
			final Pair<Float, Float> supportRot = getRotationForMovement(0F, renderState.ageInTicks, chimeMovement);
			this.support.xRot += supportRot.getFirst();
			this.support.zRot -= supportRot.getSecond();
		} else {
			this.supportChain.skipDraw = true;
		}

		float movementOffset = 0F;
		for (Pair<ModelPart, ModelPart> pair : this.chimes) {
			movementOffset += 1F;
			final ModelPart chime = pair.getFirst();
			final ModelPart tube = pair.getSecond();

			final Pair<Float, Float> chimeRotA = getRotationForMovement(movementOffset, renderState.ageInTicks, chimeMovement);
			chime.xRot += chimeRotA.getFirst();
			chime.zRot -= chimeRotA.getSecond();

			final Pair<Float, Float> chimeRotB = getRotationForMovement(movementOffset + 0.5F, renderState.ageInTicks, chimeMovement);
			tube.xRot += chimeRotB.getFirst();
			tube.zRot -= chimeRotB.getSecond();
		}

	}

	@Contract("_, _, _ -> null")
	private static @NotNull Pair<Float, Float> getRotationForMovement(float partIndex, float ageInTicks, @NotNull Vec3 movement) {
		ageInTicks *= 0.15F;
		final float xRot = Mth.cos(ageInTicks - partIndex) + 0.25F;
		final float zRot = Mth.cos(ageInTicks - (partIndex + 2F)) + 0.25F;
		return Pair.of(xRot * (float)movement.x, zRot * (float)movement.z);
	}
}
