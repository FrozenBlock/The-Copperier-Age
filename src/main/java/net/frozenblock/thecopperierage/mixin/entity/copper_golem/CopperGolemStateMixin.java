package net.frozenblock.thecopperierage.mixin.entity.copper_golem;

import net.frozenblock.thecopperierage.entity.impl.TCACopperGolemStates;
import net.minecraft.world.entity.animal.coppergolem.CopperGolemState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(value = CopperGolemState.class, priority = 430)
public class CopperGolemStateMixin {
	//CREDIT TO nyuppo/fabric-boat-example ON GITHUB

	@SuppressWarnings("ShadowTarget")
	@Final
	@Shadow
	@Mutable
	private static CopperGolemState[] $VALUES;

	@SuppressWarnings("InvokerTarget")
	@Invoker("<init>")
	private static CopperGolemState theCopperierAge$newCopperGolemState(String internalName, int internalId, String name, int id) {
		throw new AssertionError("Mixin injection failed - The Copperier Age CopperGolemStateMixin.");
	}

	@Inject(
		method = "<clinit>",
		at = @At(value = "FIELD",
			opcode = Opcodes.PUTSTATIC,
			target = "Lnet/minecraft/world/entity/animal/coppergolem/CopperGolemState;$VALUES:[Lnet/minecraft/world/entity/animal/coppergolem/CopperGolemState;",
			shift = At.Shift.AFTER
		)
	)
	private static void theCopperierAge$addCustomCopperGolemState(CallbackInfo info) {
		final List<CopperGolemState> blocks = new ArrayList<>(Arrays.asList($VALUES));
		final CopperGolemState last = blocks.get(blocks.size() - 1);

		final CopperGolemState pressButton = theCopperierAge$newCopperGolemState(
			"THECOPPERIERAGEPRESSINGBUTTON",
			last.ordinal() + 1,
			"thecopperierage_pressing_button",
			last.id() + 1
		);
		TCACopperGolemStates.PRESSING_BUTTON = pressButton;
		blocks.add(pressButton);

		$VALUES = blocks.toArray(new CopperGolemState[0]);
	}
}
