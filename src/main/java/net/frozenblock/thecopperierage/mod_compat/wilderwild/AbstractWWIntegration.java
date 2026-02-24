package net.frozenblock.thecopperierage.mod_compat.wilderwild;

import net.frozenblock.lib.integration.api.ModIntegration;
import net.minecraft.sounds.SoundEvent;

public abstract class AbstractWWIntegration extends ModIntegration {

	public AbstractWWIntegration() {
		super("wilderwild");
	}

	@Override
	public void init() {
	}

	abstract public boolean chestBubbling();

	abstract public SoundEvent underwaterOpenChestSound();

	abstract public SoundEvent underwaterCloseChestSound();
}
