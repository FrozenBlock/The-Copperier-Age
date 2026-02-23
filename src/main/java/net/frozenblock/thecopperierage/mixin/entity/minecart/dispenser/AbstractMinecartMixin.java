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

package net.frozenblock.thecopperierage.mixin.entity.minecart.dispenser;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.Optional;
import net.frozenblock.thecopperierage.entity.AbstractMinecartDispenser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.NewMinecartBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractMinecart.class)
public class AbstractMinecartMixin {

	@Inject(
		method = "createMinecart",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/EntityType;createDefaultStackConfig(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Ljava/util/function/Consumer;"
		)
	)
	private static <T extends AbstractMinecart> void theCopperierAge$rotateMinecartBlocks(
		Level level,
		double x, double y, double z,
		EntityType<T> type,
		EntitySpawnReason reason,
		ItemStack stack,
		@Nullable Player player,
		CallbackInfoReturnable<T> infoReturnable,
		@Local T minecart
	) {
		if (!(minecart instanceof AbstractMinecartDispenser minecartDispenser) || player == null) return;
		if (!(minecart.getBehavior() instanceof NewMinecartBehavior)) return;

		Direction facing = Direction.orderedByNearest(player)[0].getOpposite();
		fixDirection: {
			if (facing.getAxis() == Direction.Axis.Y) break fixDirection;

			final BlockPos pos = minecart.blockPosition();
			final BlockState state = level.getBlockState(pos);
			if (!state.is(BlockTags.RAILS)) return;

			final RailShape railShape = state.getValueOrElse(
				BlockStateProperties.RAIL_SHAPE,
				state.getValueOrElse(BlockStateProperties.RAIL_SHAPE_STRAIGHT, RailShape.NORTH_SOUTH)
			);

			if (railShape == RailShape.SOUTH_EAST || railShape == RailShape.EAST_WEST) facing = facing.getClockWise();
			if (railShape == RailShape.SOUTH_WEST) facing = facing.getOpposite();
		}

		minecartDispenser.setCustomDisplayBlockState(
			Optional.of(minecartDispenser.getDefaultDisplayBlockState().trySetValue(DispenserBlock.FACING, facing))
		);
	}

}
