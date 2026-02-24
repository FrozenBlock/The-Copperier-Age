package net.frozenblock.thecopperierage.mod_compat.wilderwild;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class NoOpWWIntegration extends AbstractWWIntegration {
	@Override
	public boolean chestBubbling() {
		return false;
	}

	@Override
	public SoundEvent underwaterOpenChestSound() {
		return SoundEvents.CHEST_OPEN;
	}

	@Override
	public SoundEvent underwaterCloseChestSound() {
		return SoundEvents.CHEST_CLOSE;
	}
}
