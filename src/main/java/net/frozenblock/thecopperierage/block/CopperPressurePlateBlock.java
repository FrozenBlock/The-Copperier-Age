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

package net.frozenblock.thecopperierage.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class CopperPressurePlateBlock extends BasePressurePlateBlock {
	public static final MapCodec<CopperPressurePlateBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
			WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(copperPressurePlateBlock -> copperPressurePlateBlock.weatherState),
			propertiesCodec()
		).apply(instance, CopperPressurePlateBlock::new)
	);
	public static final IntegerProperty POWER = BlockStateProperties.POWER;
	protected WeatheringCopper.WeatherState weatherState;
	private final int slotsPerPower;

	public CopperPressurePlateBlock(WeatheringCopper.WeatherState weatherState, Properties properties) {
		super(properties, BlockSetType.COPPER);
		this.weatherState = weatherState;
		this.slotsPerPower = getSlotsPerPower(weatherState);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWER, 0));
	}

	@Override
	public @NotNull MapCodec<? extends CopperPressurePlateBlock> codec() {
		return CODEC;
	}

	@Override
	protected int getSignalStrength(@NotNull Level level, @NotNull BlockPos pos) {
		final List<Entity> entities = level.getEntitiesOfClass(Entity.class, TOUCH_AABB.move(pos));
		if (entities.isEmpty()) return 0;

		int totalOccupiedSlots = 0;
		for (Entity entity : entities) totalOccupiedSlots += getEntityOccupiedSlots(entity);
		if (totalOccupiedSlots == 0) return 0;

		return Math.min(15, totalOccupiedSlots / this.slotsPerPower);
	}

	private int getEntityOccupiedSlots(Entity entity) {
		int occupiedSlots = 0;

		if (entity instanceof LivingEntity livingEntity) {
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				if (!livingEntity.getItemBySlot(slot).isEmpty()) occupiedSlots += 1;
			}

			if (entity instanceof Player player) {
				for (ItemStack stack : player.getInventory().getNonEquipmentItems()) if (!stack.isEmpty()) occupiedSlots += 1;
			}

			if (entity instanceof AbstractHorse horse) {
				for (ItemStack stack : horse.inventory) if (!stack.isEmpty()) occupiedSlots += 1;
			}

			if (entity instanceof InventoryCarrier inventoryCarrier) {
				for (ItemStack stack : inventoryCarrier.getInventory()) if (!stack.isEmpty()) occupiedSlots += 1;
			}
		}

		if (entity instanceof ContainerEntity containerEntity) {
			for (ItemStack stack : containerEntity) if (!stack.isEmpty()) occupiedSlots += 1;
		}

		return occupiedSlots;
	}

	@Override
	protected int getSignalForState(@NotNull BlockState state) {
		return state.getValue(POWER);
	}

	@Override
	protected @NotNull BlockState setSignalForState(@NotNull BlockState state, int signal) {
		return state.setValue(POWER, signal);
	}

	@Contract(pure = true)
	private int getSlotsPerPower(@NotNull WeatheringCopper.WeatherState weatherState) {
		return switch (weatherState) {
			case UNAFFECTED -> 1;
			case EXPOSED -> 2;
			case WEATHERED -> 4;
			case OXIDIZED -> 8;
		};
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
		builder.add(POWER);
	}
}
