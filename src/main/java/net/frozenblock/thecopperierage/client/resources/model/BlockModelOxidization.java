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

package net.frozenblock.thecopperierage.client.resources.model;

import com.mojang.math.Transformation;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import org.joml.Matrix4fc;

@Environment(EnvType.CLIENT)
public class BlockModelOxidization implements ModelState {
	private final String name;
	private final ModelState modelState;

	BlockModelOxidization(String name, ModelState modelState) {
		this.name = name;
		this.modelState = modelState;
	}

	public static BlockModelOxidization create(int stage, ModelState modelState) {
		return new BlockModelOxidization("oxidation_" + stage, modelState);
	}

	@Override
	public Transformation transformation() {
		return this.modelState.transformation();
	}

	@Override
	public Matrix4fc faceTransformation(Direction direction) {
		return this.modelState.faceTransformation(direction);
	}

	@Override
	public Matrix4fc inverseFaceTransformation(Direction direction) {
		return this.modelState.inverseFaceTransformation(direction);
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj
			|| (obj instanceof BlockModelOxidization oxidization && oxidization.modelState == this.modelState && Objects.equals(oxidization.name, this.name));
	}
}
