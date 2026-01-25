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

package net.frozenblock.thecopperierage.mixin.datafix;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.AddNewChoices;
import net.minecraft.util.datafix.fixes.References;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(DataFixers.class)
public class DataFixersMixin {

	@ModifyExpressionValue(
		method = "addFixers",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/datafixers/DataFixerBuilder;addSchema(ILjava/util/function/BiFunction;)Lcom/mojang/datafixers/schemas/Schema;",
			ordinal = 0,
			remap = false
		),
		slice = @Slice(
			from = @At(
				value = "CONSTANT",
				args = "intValue=4533"
			)
		)
	)
	private static Schema theCopperierAge$addFixers4533(Schema schema, DataFixerBuilder builder) {
		builder.addFixer(new AddNewChoices(schema, "Added Chime (The Copperier Age)", References.BLOCK_ENTITY));
		builder.addFixer(new AddNewChoices(schema, "Added Copper Crate (The Copperier Age)", References.BLOCK_ENTITY));
		return schema;
	}

}
