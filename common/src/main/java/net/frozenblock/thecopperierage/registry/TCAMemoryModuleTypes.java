package net.frozenblock.thecopperierage.registry;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Set;
import net.frozenblock.lib.platform.api.registry.DeferredMemoryModuleType;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.animal.golem.CopperGolem;

public final class TCAMemoryModuleTypes {
	private static final DeferredRegister.MemoryModuleTypes REGISTER = DeferredRegister.createMemoryModuleTypes(TCAConstants.MOD_ID);

	public static final DeferredMemoryModuleType<Set<GlobalPos>> UNREACHABLE_BUTTON_PRESS_BLOCK_POSITIONS = register(
		"unreachable_button_press_block_positions",
		GlobalPos.CODEC.listOf().xmap(Sets::newHashSet, Lists::newArrayList)
	);
	public static final DeferredMemoryModuleType<GlobalPos> TARGETED_BUTTON = register("targeted_button");
	public static final DeferredMemoryModuleType<List<CopperGolem>> NEARBY_COPPER_GOLEMS = register("nearby_copper_golems");
	public static final DeferredMemoryModuleType<Integer> BUTTON_PRESS_COOLDOWN_TICKS = register("button_press_cooldown_ticks", Codec.INT);
	public static final DeferredMemoryModuleType<Integer> NEARBY_BUTTON_SEARCH_TICKS = register("nearby_button_search_ticks", Codec.INT);

	static {
		REGISTER.register();
	}

	public static void init() {}

	private static <U> DeferredMemoryModuleType<U> register(String name, Codec<U> codec) {
		return REGISTER.register(name, codec);
	}

	private static <U> DeferredMemoryModuleType<U> register(String name) {
		return REGISTER.register(name);
	}

	private TCAMemoryModuleTypes() {}
}
