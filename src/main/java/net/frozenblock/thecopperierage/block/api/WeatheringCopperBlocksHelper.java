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

package net.frozenblock.thecopperierage.block.api;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import org.jetbrains.annotations.NotNull;

public final class WeatheringCopperBlocksHelper {
    private static final Map<Block, WeatheringCopper.WeatherState> WEATHER_STATE_BY_BLOCK = new IdentityHashMap<>();
    private static final Map<Block, Boolean> WAXED_BY_BLOCK = new IdentityHashMap<>();

    private WeatheringCopperBlocksHelper() {
        throw new UnsupportedOperationException("WeatheringCopperBlocksHelper contains only static declarations.");
    }

    public static void registerSet(
        @NotNull Block unaffected,
        @NotNull Block exposed,
        @NotNull Block weathered,
        @NotNull Block oxidized,
        @NotNull Block waxed,
        @NotNull Block waxedExposed,
        @NotNull Block waxedWeathered,
        @NotNull Block waxedOxidized
    ) {
        register(unaffected, WeatheringCopper.WeatherState.UNAFFECTED, false);
        register(exposed, WeatheringCopper.WeatherState.EXPOSED, false);
        register(weathered, WeatheringCopper.WeatherState.WEATHERED, false);
        register(oxidized, WeatheringCopper.WeatherState.OXIDIZED, false);

        register(waxed, WeatheringCopper.WeatherState.UNAFFECTED, true);
        register(waxedExposed, WeatheringCopper.WeatherState.EXPOSED, true);
        register(waxedWeathered, WeatheringCopper.WeatherState.WEATHERED, true);
        register(waxedOxidized, WeatheringCopper.WeatherState.OXIDIZED, true);
    }

    public static boolean isTracked(@NotNull Block block) {
        return WEATHER_STATE_BY_BLOCK.containsKey(block);
    }

    public static boolean isWaxed(@NotNull Block block) {
        return WAXED_BY_BLOCK.getOrDefault(block, false);
    }

    public static Optional<WeatheringCopper.WeatherState> getWeatherState(@NotNull Block block) {
        return Optional.ofNullable(WEATHER_STATE_BY_BLOCK.get(block));
    }

    private static void register(@NotNull Block block, @NotNull WeatheringCopper.WeatherState weatherState, boolean waxed) {
        WEATHER_STATE_BY_BLOCK.put(block, weatherState);
        WAXED_BY_BLOCK.put(block, waxed);
    }
}
