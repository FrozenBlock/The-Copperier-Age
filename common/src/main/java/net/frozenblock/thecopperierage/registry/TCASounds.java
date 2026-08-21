package net.frozenblock.thecopperierage.registry;

import net.frozenblock.lib.platform.api.registry.DeferredRegister;
import net.frozenblock.lib.platform.api.registry.DeferredHolder;
import net.frozenblock.thecopperierage.TCAConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;

public final class TCASounds {
	private static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(
		Registries.SOUND_EVENT,
		TCAConstants.MOD_ID
	);

	// BLOCK
	public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_GEARBOX_ON = register("block.gearbox.on");
	public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_GEARBOX_OFF = register("block.gearbox.off");
	public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_GEARBOX_IDLE = register("block.gearbox.idle");
	public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_CHIME_AMBIENT = register("block.chime.ambient");
	public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_CHIME_DISTURB = register("block.chime.disturb");
	public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_CHIME_BREAK = register("block.chime.break");
	public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_CHIME_STEP = register("block.chime.step");
	public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_CHIME_PLACE = register("block.chime.place");
	public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_CHIME_HIT = register("block.chime.hit");
	public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_CHIME_FALL = register("block.chime.fall");

	public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_COPPER_FAN_ON = register("block.copper_fan.on");
	public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_COPPER_FAN_OFF = register("block.copper_fan.off");
	public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_COPPER_FAN_IDLE_BLOW = register("block.copper_fan.idle_blow");
	public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_COPPER_FAN_IDLE_HUM = register("block.copper_fan.idle_hum");

	public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_CRATE_OPEN = register("block.crate.open");
	public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_CRATE_CLOSE = register("block.crate.close");
	public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_CRATE_EJECT = register("block.crate.eject");

	// ITEM
	public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_COPPER_HORN_SAX = register("item.copper_horn.saxophone");
	public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_COPPER_HORN_TUBA = register("item.copper_horn.tuba");
	public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_COPPER_HORN_RECORDER = register("item.copper_horn.recorder");
	public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_COPPER_HORN_FLUTE = register("item.copper_horn.flute");
	public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_COPPER_HORN_OBOE = register("item.copper_horn.oboe");
	public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_COPPER_HORN_CLARINET = register("item.copper_horn.clarinet");
	public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_COPPER_HORN_TRUMPET = register("item.copper_horn.trumpet");
	public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_COPPER_HORN_TROMBONE = register("item.copper_horn.trombone");
	public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_WRENCH_USE = register("item.wrench.use");
	public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_MINECART_PLACE = register("entity.minecart.place");
	public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_MINECART_BREAK = register("entity.minecart.break");
	public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_MINECART_COUPLE = register("entity.minecart.couple");
	public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_MINECART_UNCOUPLE = register("entity.minecart.uncouple");
	public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_MINECART_COUPLE_BREAK = register("entity.minecart.couple_break");

	// ENTITY
	public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_COPPER_GOLEM_BUTTON_PRESS = register("entity.copper_golem.button_press");

	// UI
	public static final DeferredHolder<SoundEvent, SoundEvent> UI_CRATE_CLICK_FAIL = register("ui.crate.click_fail");

	static {
		REGISTER.register();
	}

	public static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
		return REGISTER.register(name, SoundEvent::createVariableRangeEvent);
	}

	public static SoundType chimeSoundType() {
		return new SoundType(
			1F,
			1F,
			BLOCK_CHIME_BREAK.get(),
			BLOCK_CHIME_STEP.get(),
			BLOCK_CHIME_PLACE.get(),
			BLOCK_CHIME_HIT.get(),
			BLOCK_CHIME_FALL.get()
		);
	}

	public static void init() {}

	public static void register() {
		REGISTER.register();
	}
}
