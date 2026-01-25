package net.frozenblock.thecopperierage.mixin.entity.copper_golem;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import net.frozenblock.thecopperierage.entity.ai.coppergolem.CopperGolemPressButton;
import net.frozenblock.thecopperierage.registry.TCAMemoryModuleTypes;
import net.frozenblock.thecopperierage.registry.TCASensorTypes;
import net.frozenblock.thecopperierage.tag.TCABlockTags;
import net.minecraft.world.entity.ai.behavior.CountDownCooldownTicks;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.coppergolem.CopperGolem;
import net.minecraft.world.entity.animal.coppergolem.CopperGolemAi;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CopperGolemAi.class, priority = 997)
public class CopperGolemAiMixin {

	@Shadow
	@Final
	@Mutable
	private static ImmutableList<MemoryModuleType<?>> MEMORY_TYPES;

	@Shadow
	@Final
	@Mutable
	private static ImmutableList<SensorType<? extends Sensor<? super CopperGolem>>> SENSOR_TYPES;

	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void theCopperierAge$appendMemoryAndSensorTypes(CallbackInfo info) {
		final ArrayList<MemoryModuleType<?>> memoryTypes = new ArrayList<>(MEMORY_TYPES);
		memoryTypes.add(TCAMemoryModuleTypes.UNREACHABLE_BUTTON_PRESS_BLOCK_POSITIONS);
		memoryTypes.add(TCAMemoryModuleTypes.BUTTON_PRESS_COOLDOWN_TICKS);
		memoryTypes.add(TCAMemoryModuleTypes.TARGETED_BUTTON);
		memoryTypes.add(TCAMemoryModuleTypes.NEARBY_COPPER_GOLEMS);
		MEMORY_TYPES = ImmutableList.copyOf(memoryTypes);

		final ArrayList<SensorType<? extends Sensor<? super CopperGolem>>> sensorTypes = new ArrayList<>(SENSOR_TYPES);
		sensorTypes.add(TCASensorTypes.COPPER_GOLEM_SPECIFIC_SENSOR);
		SENSOR_TYPES = ImmutableList.copyOf(sensorTypes);
	}

	@ModifyExpressionValue(
		method = "initCoreActivity",
		at = @At(
			value = "INVOKE",
			target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;"
		)
	)
	private static ImmutableList theCopperierAge$addPressButtonCountCooldown(ImmutableList original) {
		final ArrayList behaviors = new ArrayList<>(original);
		behaviors.add(new CountDownCooldownTicks(TCAMemoryModuleTypes.BUTTON_PRESS_COOLDOWN_TICKS));
		return ImmutableList.copyOf(behaviors);
	}

	@ModifyExpressionValue(
		method = "initIdleActivity",
		at = @At(
			value = "INVOKE",
			target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;",
			ordinal = 0
		)
	)
	private static ImmutableList theCopperierAge$addPressButton(ImmutableList original) {
		final ArrayList behaviors = new ArrayList<>(original);
		behaviors.add(
			Pair.of(
				1,
				new CopperGolemPressButton(
					1F,
					state -> state.is(TCABlockTags.COPPER_BUTTONS),
					32,
					8
				)
			)
		);
		return ImmutableList.copyOf(behaviors);
	}

}
