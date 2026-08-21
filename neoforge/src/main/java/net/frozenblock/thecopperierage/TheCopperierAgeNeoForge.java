package net.frozenblock.thecopperierage;

import java.util.function.Consumer;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.item.api.OxidizableItemHelper;
import net.frozenblock.thecopperierage.item.impl.ItemOxidizationCacheInterface;
import net.frozenblock.thecopperierage.tag.TCAItemTags;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.WeatheringCopper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.tooltip.TooltipLocation;
import net.neoforged.neoforge.event.RegisterTooltipAppendersEvent;
import org.jetbrains.annotations.Nullable;

@Mod(TCAConstants.MOD_ID)
public class TheCopperierAgeNeoForge {

	public TheCopperierAgeNeoForge(IEventBus modBus) {
		TheCopperierAge.init();

		modBus.addListener(FMLCommonSetupEvent.class, event -> {
			TheCopperierAge.setup();
		});

		modBus.addListener(RegisterTooltipAppendersEvent.class, event ->
			event.registerAppender(TooltipLocation.POST_CUSTOM, TheCopperierAgeNeoForge::addWeatheringAndWaxedTooltips)
		);
	}

	private static void addWeatheringAndWaxedTooltips(
		ItemStack stack, Item.TooltipContext context, TooltipDisplay display, @Nullable Player player, TooltipFlag tooltipFlag, Consumer<Component> builder
	) {
		if (stack.is(TCAItemTags.OXIDIZABLE_EQUIPMENT)) {
			addWeatherStateTooltip(
				builder,
				OxidizableItemHelper.getValueForOxidization(
					stack,
					WeatheringCopper.WeatherState.UNAFFECTED,
					WeatheringCopper.WeatherState.EXPOSED,
					WeatheringCopper.WeatherState.WEATHERED,
					WeatheringCopper.WeatherState.OXIDIZED
				)
			);
		}
		if (OxidizableItemHelper.hasWaxedComponent(stack)) builder.accept(OxidizableItemHelper.WAXED_TOOLTIP);

		if (!TCAConfig.BETTER_COPPER_TOOLTIPS.get()) return;
		if (!(stack.getItem() instanceof ItemOxidizationCacheInterface oxidizationCache)) return;

		final WeatheringCopper.WeatherState weatherState = oxidizationCache.theCopperierAge$weatherState();
		if (weatherState != null) addWeatherStateTooltip(builder, weatherState);
		if (oxidizationCache.theCopperierAge$waxed()) builder.accept(OxidizableItemHelper.WAXED_TOOLTIP);
	}

	private static void addWeatherStateTooltip(Consumer<Component> consumer, WeatheringCopper.WeatherState weatherState) {
		if (weatherState == WeatheringCopper.WeatherState.UNAFFECTED) return;
		consumer.accept(OxidizableItemHelper.getWeatheringStateName(weatherState));
	}
}
