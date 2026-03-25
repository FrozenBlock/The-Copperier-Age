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

package net.frozenblock.thecopperierage.mixin.client.oxidized_items.auto_models;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.client.renderer.item.FakeUnbakedItemModel;
import net.frozenblock.thecopperierage.client.resources.model.BlockModelOxidization;
import net.frozenblock.thecopperierage.datagen.model.TCAModelProvider;
import net.frozenblock.thecopperierage.item.api.OxidizableItemHelper;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(value = BlockModelWrapper.Unbaked.class, priority = 899)
public abstract class BlockModelWrapperUnbakedMixin {

	@Shadow
	public abstract Identifier model();

	@Shadow
	public abstract ItemModel bake(ItemModel.BakingContext bakingContext);

	@Unique
	private boolean theCopperierAge$generatingNewModel = false;
	@Unique
	private Material theCopperierAge$material = null;
	@Unique
	private int theCopperierAge$oxidationStage;

	@ModifyReturnValue(method = "bake", at = @At("RETURN"))
	public ItemModel theCopperierAge$createOxidizingArmors(
		ItemModel original,
		@Local(argsOnly = true) ItemModel.BakingContext context,
		@Local TextureSlots slots
	) {
		if (this.theCopperierAge$generatingNewModel) return original;

		final Identifier id = this.model();
		final String path = id.getPath();
		if (!(path.contains("item/copper_") || path.equals("item/brush") || path.contains("item/brush_brushing_") || path.equals("item/wrench"))) return original;
		if (OxidizableItemHelper.getOxidizingModelSearchTerms().stream().noneMatch(path::contains)) return original;

		final Material layer0Material = slots.getMaterial("layer0");
		if (layer0Material == null) return original;

		final Identifier texture = layer0Material.texture();
		final String texturePath = texture.getPath();
		final ItemModel.Unbaked[] oxidizingModels = new ItemModel.Unbaked[3];

		this.theCopperierAge$generatingNewModel = true;
		for (int i = 0; i < 3; i++) {
			final String suffix = OxidizableItemHelper.OXIDIZING_SUFFIXES.get(i);
			if (path.contains(suffix)) {
				TCAConstants.log("Item model " + id + " already has suffix " + suffix, TCAConstants.UNSTABLE_LOGGING);
				return original;
			}

			this.theCopperierAge$oxidationStage = i;
			this.theCopperierAge$material = new Material(layer0Material.atlasLocation(), TCAConstants.id(texturePath + "_" + suffix));
			oxidizingModels[i] = new FakeUnbakedItemModel(this.bake(context));
		}

		this.theCopperierAge$generatingNewModel = false;
		this.theCopperierAge$oxidationStage = 0;
		this.theCopperierAge$material = null;

		return TCAModelProvider.createOxidizableDispatch(
			new FakeUnbakedItemModel(original),
			oxidizingModels[0],
			oxidizingModels[1],
			oxidizingModels[2]
		).bake(context);
	}

	@ModifyExpressionValue(
		method = "bake",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/resources/model/ResolvedModel;getTopTextureSlots()Lnet/minecraft/client/renderer/block/model/TextureSlots;"
		)
	)
	public TextureSlots theCopperierAge$useOxidizingSlots(TextureSlots original) {
		if (!this.theCopperierAge$generatingNewModel) return original;

		final Map<String, Material> newSlotsMap = new Object2ObjectLinkedOpenHashMap<>();
		newSlotsMap.putAll(original.resolvedValues);
		newSlotsMap.put("layer0", this.theCopperierAge$material);
		newSlotsMap.put("particle", this.theCopperierAge$material);
		return new TextureSlots(ImmutableMap.copyOf(newSlotsMap));
	}

	@WrapOperation(
		method = "bake",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/resources/model/ResolvedModel;bakeTopGeometry(Lnet/minecraft/client/renderer/block/model/TextureSlots;Lnet/minecraft/client/resources/model/ModelBaker;Lnet/minecraft/client/resources/model/ModelState;)Lnet/minecraft/client/resources/model/QuadCollection;"
		)
	)
	public QuadCollection theCopperierAge$useOxidizingModelState(
		ResolvedModel instance, TextureSlots slots, ModelBaker baker, ModelState modelState, Operation<QuadCollection> original
	) {
		if (this.theCopperierAge$generatingNewModel) return original.call(instance, slots, baker, BlockModelOxidization.create(this.theCopperierAge$oxidationStage, modelState));
		return original.call(instance, slots, baker, modelState);
	}

}
