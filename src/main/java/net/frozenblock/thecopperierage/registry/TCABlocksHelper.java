package net.frozenblock.thecopperierage.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperBlocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.apache.commons.lang3.function.TriFunction;

import java.util.function.BiFunction;
import java.util.function.Function;

public class TCABlocksHelper {
    public static <W extends Block> WeatheringCopperBlocks create(String name, TriFunction<String, Function<BlockBehaviour.Properties, Block>, BlockBehaviour.Properties, Block> triFunction, BiFunction<WeatheringCopper.WeatherState, BlockBehaviour.Properties, W> waxedBlockCreator, BiFunction<WeatheringCopper.WeatherState, BlockBehaviour.Properties, ? extends Block> weatheringBlockCreator, Function<WeatheringCopper.WeatherState, BlockBehaviour.Properties> propertiesCreator) {
        Block unaffected = triFunction.apply(name, (properties) -> weatheringBlockCreator.apply(WeatheringCopper.WeatherState.UNAFFECTED, properties), propertiesCreator.apply(WeatheringCopper.WeatherState.UNAFFECTED));
        Block exposed = triFunction.apply("exposed_" + name, (properties) -> weatheringBlockCreator.apply(WeatheringCopper.WeatherState.EXPOSED, properties), propertiesCreator.apply(WeatheringCopper.WeatherState.EXPOSED));
        Block weathered = triFunction.apply("weathered_" + name, (properties) -> weatheringBlockCreator.apply(WeatheringCopper.WeatherState.WEATHERED, properties), propertiesCreator.apply(WeatheringCopper.WeatherState.WEATHERED));
        Block oxidized = triFunction.apply("oxidized_" + name, (properties) -> weatheringBlockCreator.apply(WeatheringCopper.WeatherState.OXIDIZED, properties), propertiesCreator.apply(WeatheringCopper.WeatherState.OXIDIZED));

        Block waxed = triFunction.apply("waxed_" + name, (properties) -> waxedBlockCreator.apply(WeatheringCopper.WeatherState.UNAFFECTED, properties), propertiesCreator.apply(WeatheringCopper.WeatherState.UNAFFECTED));
        Block waxedExposed = triFunction.apply("waxed_exposed_" + name, (properties) -> waxedBlockCreator.apply(WeatheringCopper.WeatherState.EXPOSED, properties), propertiesCreator.apply(WeatheringCopper.WeatherState.EXPOSED));
        Block waxedWeathered = triFunction.apply("waxed_weathered_" + name, (properties) -> waxedBlockCreator.apply(WeatheringCopper.WeatherState.WEATHERED, properties), propertiesCreator.apply(WeatheringCopper.WeatherState.WEATHERED));
        Block waxedOxidized = triFunction.apply("waxed_oxidized_" + name, (properties) -> waxedBlockCreator.apply(WeatheringCopper.WeatherState.OXIDIZED, properties), propertiesCreator.apply(WeatheringCopper.WeatherState.OXIDIZED));

        return new WeatheringCopperBlocks(unaffected, exposed, weathered, oxidized, waxed, waxedExposed, waxedWeathered, waxedOxidized);
    }
}
