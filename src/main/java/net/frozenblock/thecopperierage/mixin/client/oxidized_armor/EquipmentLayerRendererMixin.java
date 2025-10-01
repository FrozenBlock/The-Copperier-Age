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

package net.frozenblock.thecopperierage.mixin.client.oxidized_armor;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.item.api.OxidizableItemHelper;
import net.frozenblock.thecopperierage.tag.TCAItemTags;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.EquipmentLayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(EquipmentLayerRenderer.class)
public class EquipmentLayerRendererMixin {

	@Unique
	private static final ResourceLocation THE_COPPERIER_AGE$EXPOSED_COPPER_LEGGINGS = TCAConstants.id("textures/entity/equipment/humanoid_leggings/copper_exposed.png");
	@Unique
	private static final ResourceLocation THE_COPPERIER_AGE$WEATHERED_COPPER_LEGGINGS = TCAConstants.id("textures/entity/equipment/humanoid_leggings/copper_weathered.png");
	@Unique
	private static final ResourceLocation THE_COPPERIER_AGE$OXIDIZED_COPPER_LEGGINGS = TCAConstants.id("textures/entity/equipment/humanoid_leggings/copper_oxidized.png");

	@Unique
	private static final ResourceLocation THE_COPPERIER_AGE$EXPOSED_COPPER = TCAConstants.id("textures/entity/equipment/humanoid/copper_exposed.png");
	@Unique
	private static final ResourceLocation THE_COPPERIER_AGE$WEATHERED_COPPER = TCAConstants.id("textures/entity/equipment/humanoid/copper_weathered.png");
	@Unique
	private static final ResourceLocation THE_COPPERIER_AGE$OXIDIZED_COPPER = TCAConstants.id("textures/entity/equipment/humanoid/copper_oxidized.png");

	@WrapOperation(
		method = "renderLayers(Lnet/minecraft/client/resources/model/EquipmentClientInfo$LayerType;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/world/item/ItemStack;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/resources/ResourceLocation;II)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/RenderType;armorCutoutNoCull(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"
		)
	)
	public RenderType theCopperierAge$submitOxidizedArmor(
		ResourceLocation resourceLocation, Operation<RenderType> original,
		@Local(argsOnly = true) ItemStack stack
	) {
		if (!TCAConfig.OXIDIZABLE_COPPER_EQUIPMENT || !resourceLocation.getPath().contains("humanoid") || !stack.is(TCAItemTags.OXIDIZABLE_EQUIPMENT)) return original.call(resourceLocation);
		if (stack.is(ItemTags.LEG_ARMOR)) return original.call(
			OxidizableItemHelper.getValueForOxidization(
				stack,
				resourceLocation,
				THE_COPPERIER_AGE$EXPOSED_COPPER_LEGGINGS,
				THE_COPPERIER_AGE$WEATHERED_COPPER_LEGGINGS,
				THE_COPPERIER_AGE$OXIDIZED_COPPER_LEGGINGS
			)
		);
		return original.call(
			OxidizableItemHelper.getValueForOxidization(
				stack,
				resourceLocation,
				THE_COPPERIER_AGE$EXPOSED_COPPER,
				THE_COPPERIER_AGE$WEATHERED_COPPER,
				THE_COPPERIER_AGE$OXIDIZED_COPPER
			)
		);
	}

}
