package net.frozenblock.thecopperierage.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CopperPressurePlateBlock extends BasePressurePlateBlock implements WeatheringCopper {
	public static final IntegerProperty POWER = BlockStateProperties.POWER;
	private final WeatherState weatherState;

	public static final MapCodec<CopperPressurePlateBlock> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					WeatherState.CODEC.fieldOf("weathering_state").forGetter(CopperPressurePlateBlock::getAge),
					propertiesCodec()
			).apply(instance, CopperPressurePlateBlock::new)
	);

	public CopperPressurePlateBlock(WeatherState weatherState, Properties properties) {
		super(properties, BlockSetType.COPPER);
		this.weatherState = weatherState;
		this.registerDefaultState(this.stateDefinition.any().setValue(POWER, 0));
	}

	@Override
	public @NotNull MapCodec<? extends CopperPressurePlateBlock> codec() {
		return CODEC;
	}

	@Override
	public WeatherState getAge() {
		return this.weatherState;
	}

	protected static final AABB SENSING_AABB = new AABB(0.125, 0.0, 0.125, 0.875, 0.25, 0.875);

	@Override
	protected int getSignalStrength(Level level, BlockPos pos) {
		List<Entity> entities = level.getEntitiesOfClass(Entity.class, SENSING_AABB.move(pos));
		if (entities.isEmpty()) {
			return 0;
		}

		int totalOccupiedSlots = 0;
		for (Entity entity : entities) {
			totalOccupiedSlots += getEntityOccupiedSlots(entity);
		}

		if (totalOccupiedSlots == 0) {
			return 0;
		}

		totalOccupiedSlots += 1;

		int slotsPerPower = getSlotsPerPower(this.weatherState);
		if (slotsPerPower == 0) return 0;
		return Math.min(15, (totalOccupiedSlots - 1) / slotsPerPower + 1);
	}

	private int getEntityOccupiedSlots(Entity entity) {
		int occupiedSlots = 0;

		if (entity instanceof Player player) {
			for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
				if (!player.getInventory().getItem(i).isEmpty()) {
					occupiedSlots++;
				}
			}
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR) {
					if (!player.getItemBySlot(slot).isEmpty()) {
						occupiedSlots++;
					}
				}
			}
			if (!player.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
				occupiedSlots++;
			}
			if (!player.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty()) {
				occupiedSlots++;
			}
		}
		else if (entity instanceof LivingEntity livingEntity) {
			for (EquipmentSlot slot : EquipmentSlot.values()) {
				if (!livingEntity.getItemBySlot(slot).isEmpty()) {
					occupiedSlots++;
				}
			}
		}
		else if (entity instanceof ContainerEntity containerEntity) {
			for (int i = 0; i < containerEntity.getContainerSize(); i++) {
				if (!containerEntity.getItem(i).isEmpty()) {
					occupiedSlots++;
				}
			}
		}

		return occupiedSlots;
	}

	@Override
	protected int getSignalForState(BlockState state) {
		return state.getValue(POWER);
	}

	@Override
	protected BlockState setSignalForState(BlockState state, int signal) {
		return state.setValue(POWER, signal);
	}

	private int getSlotsPerPower(WeatherState weatherState) {
		return switch (weatherState) {
			case UNAFFECTED -> 1;
			case EXPOSED -> 2;
			case WEATHERED -> 4;
			case OXIDIZED -> 8;
		};
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(POWER);
	}

	public static class Waxed extends CopperPressurePlateBlock {
		private final WeatherState weatherState;

		public Waxed(WeatherState weatherState, Properties properties) {
			super(weatherState, properties);
			this.weatherState = weatherState;
		}

		public Waxed(Properties properties) {
			this(WeatherState.UNAFFECTED, properties);
		}

		public WeatherState getAge() {
			return this.weatherState;
		}
	}
}
