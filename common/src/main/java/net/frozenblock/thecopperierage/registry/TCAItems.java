package net.frozenblock.thecopperierage.registry;

import net.frozenblock.lib.platform.api.registry.DeferredItem;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.item.CopperHornItem;
import net.frozenblock.thecopperierage.item.WrenchItem;
import net.frozenblock.thecopperierage.references.TCABlockItemIds;
import net.frozenblock.thecopperierage.references.TCAItemIds;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.component.InstrumentComponent;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.WeatheringCopperCollection;

public final class TCAItems {
	private static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(TCAConstants.MOD_ID);

	// BLOCK ITEMS
	public static final DeferredItem<BlockItem> COPPER_CAMPFIRE = REGISTER.registerSimpleBlockItem(TCABlockItemIds.COPPER_CAMPFIRE, TCABlocks.COPPER_CAMPFIRE);
	public static final DeferredItem<BlockItem> CUPRIC_LANTERN = REGISTER.registerSimpleBlockItem(TCABlockItemIds.CUPRIC_LANTERN, TCABlocks.CUPRIC_LANTERN);
	public static final DeferredItem<BlockItem> COPPER_JACK_O_LANTERN = REGISTER.registerSimpleBlockItem(TCABlockItemIds.COPPER_JACK_O_LANTERN, TCABlocks.COPPER_JACK_O_LANTERN);
	public static final DeferredItem<BlockItem> REDSTONE_JACK_O_LANTERN = REGISTER.registerSimpleBlockItem(TCABlockItemIds.REDSTONE_JACK_O_LANTERN, TCABlocks.REDSTONE_JACK_O_LANTERN);
	public static final DeferredItem<BlockItem> REDSTONE_GRIT = REGISTER.registerSimpleBlockItem(TCABlockItemIds.REDSTONE_GRIT, TCABlocks.REDSTONE_GRIT);
	public static final WeatheringCopperCollection<DeferredItem<BlockItem>> GEARBOX = WeatheringCopperCollection.zipMap(
		TCABlockItemIds.GEARBOX, TCABlocks.GEARBOX, REGISTER::registerSimpleBlockItem
	);
	public static final WeatheringCopperCollection<DeferredItem<BlockItem>> STICKY_GEARBOX = WeatheringCopperCollection.zipMap(
		TCABlockItemIds.STICKY_GEARBOX, TCABlocks.STICKY_GEARBOX, REGISTER::registerSimpleBlockItem
	);
	public static final WeatheringCopperCollection<DeferredItem<BlockItem>> COPPER_FAN = WeatheringCopperCollection.zipMap(
		TCABlockItemIds.COPPER_FAN, TCABlocks.COPPER_FAN, REGISTER::registerSimpleBlockItem
	);
	public static final WeatheringCopperCollection<DeferredItem<BlockItem>> CHIME = WeatheringCopperCollection.zipMap(
		TCABlockItemIds.CHIME, TCABlocks.CHIME, REGISTER::registerSimpleBlockItem
	);
	public static final DeferredItem<BlockItem> CRATE = REGISTER.registerSimpleBlockItem(TCABlockItemIds.CRATE, TCABlocks.CRATE,
		() -> new Item.Properties().component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
	);
	public static final DeferredItem<BlockItem> KILN = REGISTER.registerSimpleBlockItem(TCABlockItemIds.KILN, TCABlocks.KILN);
	public static final WeatheringCopperCollection<DeferredItem<BlockItem>> COPPER_BUTTON = WeatheringCopperCollection.zipMap(
		TCABlockItemIds.COPPER_BUTTON, TCABlocks.COPPER_BUTTON, REGISTER::registerSimpleBlockItem
	);
	public static final WeatheringCopperCollection<DeferredItem<BlockItem>> WEIGHTED_PRESSURE_PLATE = WeatheringCopperCollection.zipMap(
		TCABlockItemIds.WEIGHTED_PRESSURE_PLATE, TCABlocks.WEIGHTED_PRESSURE_PLATE, REGISTER::registerSimpleBlockItem
	);
	public static final WeatheringCopperCollection<DeferredItem<BlockItem>> COPPER_RAIL = WeatheringCopperCollection.zipMap(
		TCABlockItemIds.COPPER_RAIL, TCABlocks.COPPER_RAIL, REGISTER::registerSimpleBlockItem
	);
	public static final DeferredItem<BlockItem> CROSS_RAIL = REGISTER.registerSimpleBlockItem(TCABlockItemIds.CROSS_RAIL, TCABlocks.CROSS_RAIL);
	public static final DeferredItem<BlockItem> RELAYOR_RAIL = REGISTER.registerSimpleBlockItem(TCABlockItemIds.RELAYOR_RAIL, TCABlocks.RELAYOR_RAIL);

	// ITEMS
	public static final DeferredItem<WrenchItem> WRENCH = REGISTER.registerItem(TCAItemIds.WRENCH,
		WrenchItem::new,
		() -> new Item.Properties().stacksTo(1).durability(128)
	);
	public static final DeferredItem<CopperHornItem> COPPER_HORN = REGISTER.registerItem(TCAItemIds.COPPER_HORN,
		CopperHornItem::new,
		() -> new Item.Properties()
			.stacksTo(1)
			.delayedComponent(DataComponents.INSTRUMENT, context -> new InstrumentComponent(context.getOrThrow(TCAInstruments.SAX_COPPER_HORN)))
	);

	// MINECART ITEMS
	public static final DeferredItem<Item> MINECART_COUPLING = REGISTER.registerItem(TCAItemIds.MINECART_COUPLING,
		Item::new,
		() -> new Item.Properties().stacksTo(16)
	);
	public static final DeferredItem<MinecartItem> CRATE_MINECART = REGISTER.registerItem(TCAItemIds.CRATE_MINECART,
		properties -> new MinecartItem(TCAEntityTypes.CRATE_MINECART.get(), properties),
		() -> new Item.Properties().stacksTo(1)
	);
	public static final DeferredItem<MinecartItem> COPPER_GOLEM_STATUE_MINECART = REGISTER.registerItem(TCAItemIds.COPPER_GOLEM_STATUE_MINECART,
		properties -> new MinecartItem(TCAEntityTypes.COPPER_GOLEM_STATUE_MINECART.get(), properties),
		() -> new Item.Properties().stacksTo(1)
	);
	public static final DeferredItem<MinecartItem> DISPENSER_MINECART = REGISTER.registerItem(TCAItemIds.DISPENSER_MINECART,
		properties -> new MinecartItem(TCAEntityTypes.DISPENSER_MINECART.get(), properties),
		() -> new Item.Properties().stacksTo(1)
	);
	public static final DeferredItem<MinecartItem> DROPPER_MINECART = REGISTER.registerItem(TCAItemIds.DROPPER_MINECART,
		properties -> new MinecartItem(TCAEntityTypes.DROPPER_MINECART.get(), properties),
		() -> new Item.Properties().stacksTo(1)
	);
	public static final DeferredItem<MinecartItem> JUKEBOX_MINECART = REGISTER.registerItem(TCAItemIds.JUKEBOX_MINECART,
		properties -> new MinecartItem(TCAEntityTypes.JUKEBOX_MINECART.get(), properties),
		() -> new Item.Properties().stacksTo(1)
	);

	static {
		REGISTER.register();
	}

	public static void init() {}
}
