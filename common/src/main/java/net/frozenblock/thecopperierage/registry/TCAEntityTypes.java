package net.frozenblock.thecopperierage.registry;

import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.entity.MinecartCopperGolemStatue;
import net.frozenblock.thecopperierage.entity.MinecartCrate;
import net.frozenblock.thecopperierage.entity.MinecartDispenser;
import net.frozenblock.thecopperierage.entity.MinecartDispenserDropper;
import net.frozenblock.thecopperierage.entity.MinecartJukebox;
import net.frozenblock.thecopperierage.references.TCAEntityTypeIds;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class TCAEntityTypes {
	private static final DeferredRegister.Entities REGISTER = DeferredRegister.createEntities(TCAConstants.MOD_ID);

	public static final DeferredHolder<EntityType<?>, EntityType<MinecartCrate>> CRATE_MINECART = REGISTER.register(
		TCAEntityTypeIds.CRATE_MINECART,
		MinecartCrate::new,
		MobCategory.MISC,
		builder -> { return builder.sized(0.98F, 0.7F).clientTrackingRange(8).updateInterval(3); }
	);

	public static final DeferredHolder<EntityType<?>, EntityType<MinecartCopperGolemStatue>> COPPER_GOLEM_STATUE_MINECART = REGISTER.register(
		TCAEntityTypeIds.COPPER_GOLEM_STATUE_MINECART,
		MinecartCopperGolemStatue::new,
		MobCategory.MISC,
		builder -> { return builder.sized(0.98F, 0.7F).clientTrackingRange(8).updateInterval(3); }
	);

	public static final DeferredHolder<EntityType<?>, EntityType<MinecartDispenser>> DISPENSER_MINECART = REGISTER.register(
		TCAEntityTypeIds.DISPENSER_MINECART,
		MinecartDispenser::new,
		MobCategory.MISC,
		builder -> { return builder.sized(0.98F, 0.7F).clientTrackingRange(8).updateInterval(3); }
	);

	public static final DeferredHolder<EntityType<?>, EntityType<MinecartDispenserDropper>> DROPPER_MINECART = REGISTER.register(
		TCAEntityTypeIds.DROPPER_MINECART,
		MinecartDispenserDropper::new,
		MobCategory.MISC,
		builder -> { return builder.sized(0.98F, 0.7F).clientTrackingRange(8).updateInterval(3); }
	);

	public static final DeferredHolder<EntityType<?>, EntityType<MinecartJukebox>> JUKEBOX_MINECART = REGISTER.register(
		TCAEntityTypeIds.JUKEBOX_MINECART,
		MinecartJukebox::new,
		MobCategory.MISC,
		builder -> { return builder.sized(0.98F, 0.7F).clientTrackingRange(8).updateInterval(3); }
	);

	static {
		REGISTER.register();
	}

	public static void init() {
		TCAConstants.logWithModId("Registering EntityTypes for", TCAConstants.UNSTABLE_LOGGING);
	}

	private TCAEntityTypes() {
		throw new UnsupportedOperationException("TCAEntityTypes contains only static declarations.");
	}
}
