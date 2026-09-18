package net.frozenblock.thecopperierage.registry;

import net.frozenblock.lib.platform.api.registry.DeferredEntityType;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.TCAFeatureFlags;
import net.frozenblock.thecopperierage.entity.vehicle.minecart.MinecartCrate;
import net.frozenblock.thecopperierage.entity.vehicle.minecart.MinecartDispenser;
import net.frozenblock.thecopperierage.entity.vehicle.minecart.MinecartDropper;
import net.frozenblock.thecopperierage.entity.vehicle.minecart.MinecartJukebox;
import net.frozenblock.thecopperierage.references.TCAEntityTypeIds;
import net.minecraft.world.entity.MobCategory;

public final class TCAEntityTypes {
	private static final DeferredRegister.Entities REGISTER = DeferredRegister.createEntities(TCAConstants.MOD_ID).requiredFeatures(TCAFeatureFlags.FEATURE_FLAG);

	public static final DeferredEntityType<MinecartCrate> CRATE_MINECART = REGISTER.register(TCAEntityTypeIds.CRATE_MINECART,
		MinecartCrate::new,
		MobCategory.MISC,
		builder -> builder.sized(0.98F, 0.7F).clientTrackingRange(8).updateInterval(3)
	);

	public static final DeferredEntityType<MinecartDispenser> DISPENSER_MINECART = REGISTER.register(TCAEntityTypeIds.DISPENSER_MINECART,
		MinecartDispenser::new,
		MobCategory.MISC,
		builder -> builder.sized(0.98F, 0.7F).clientTrackingRange(8).updateInterval(3)
	);

	public static final DeferredEntityType<MinecartDropper> DROPPER_MINECART = REGISTER.register(TCAEntityTypeIds.DROPPER_MINECART,
		MinecartDropper::new,
		MobCategory.MISC,
		builder -> builder.sized(0.98F, 0.7F).clientTrackingRange(8).updateInterval(3)
	);

	public static final DeferredEntityType<MinecartJukebox> JUKEBOX_MINECART = REGISTER.register(TCAEntityTypeIds.JUKEBOX_MINECART,
		MinecartJukebox::new,
		MobCategory.MISC,
		builder -> builder.sized(0.98F, 0.7F).clientTrackingRange(8).updateInterval(3)
	);

	static {
		REGISTER.register();
	}

	public static void init() {}

	private TCAEntityTypes() {}
}
