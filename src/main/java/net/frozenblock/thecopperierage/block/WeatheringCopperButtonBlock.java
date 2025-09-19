package net.frozenblock.thecopperierage.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class WeatheringCopperButtonBlock extends CopperButtonBlock implements WeatheringCopper {
	public static final MapCodec<WeatheringCopperButtonBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
			WeatherState.CODEC.fieldOf("weathering_state").forGetter(WeatheringCopperButtonBlock::getAge),
			propertiesCodec()
		).apply(instance, (weatherState, properties) -> new WeatheringCopperButtonBlock(weatherState, properties))
	);
	private final WeatheringCopper.WeatherState weatherState;

	public WeatheringCopperButtonBlock(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties properties) {
		super(weatherState, properties);
		this.weatherState = weatherState;
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		this.changeOverTime(state, level, pos, random);
	}

	@Override
	protected boolean isRandomlyTicking(@NotNull BlockState blockState) {
		return WeatheringCopper.getNext(blockState.getBlock()).isPresent();
	}

	@Override
	public WeatheringCopper.@NotNull WeatherState getAge() {
		return this.weatherState;
	}
}
