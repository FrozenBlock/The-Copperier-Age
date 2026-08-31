package net.frozenblock.thecopperierage.registry;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.golem.CopperGolem;

public final class TCAMemoryModuleTypes {
	private static final DeferredRegister<MemoryModuleType<?>> REGISTER = DeferredRegister.create(Registries.MEMORY_MODULE_TYPE, TCAConstants.MOD_ID);

	public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Set<GlobalPos>>> UNREACHABLE_BUTTON_PRESS_BLOCK_POSITIONS = register(
		"unreachable_button_press_block_positions",
		GlobalPos.CODEC.listOf().xmap(Sets::newHashSet, Lists::newArrayList)
	);
	public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<GlobalPos>> TARGETED_BUTTON = register("targeted_button");
	public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<List<CopperGolem>>> NEARBY_COPPER_GOLEMS = register("nearby_copper_golems");
	public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Integer>> BUTTON_PRESS_COOLDOWN_TICKS = register("button_press_cooldown_ticks", Codec.INT);
	public static final DeferredHolder<MemoryModuleType<?>, MemoryModuleType<Integer>> NEARBY_BUTTON_SEARCH_TICKS = register("nearby_button_search_ticks", Codec.INT);

	static {
		REGISTER.register();
	}

	public static void init() {}

	private static <U> DeferredHolder<MemoryModuleType<?>, MemoryModuleType<U>> register(String name, Codec<U> codec) {
		return REGISTER.register(name, () -> new MemoryModuleType<>(Optional.of(codec)));
	}

	private static <U> DeferredHolder<MemoryModuleType<?>, MemoryModuleType<U>> register(String name) {
		return REGISTER.register(name, () -> new MemoryModuleType<>(Optional.empty()));
	}
}
