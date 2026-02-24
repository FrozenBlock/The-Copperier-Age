package net.frozenblock.thecopperierage.mod_compat.wilderwild;

import net.frozenblock.wilderwild.config.WWBlockConfig;
import net.frozenblock.wilderwild.registry.WWSounds;
import net.minecraft.sounds.SoundEvent;

public class WWIntegration extends AbstractWWIntegration {
	@Override
	public boolean chestBubbling() {
		return WWBlockConfig.get().chestBubbling;
	}

	@Override
	public SoundEvent underwaterOpenChestSound() {
		return WWSounds.BLOCK_CHEST_OPEN_UNDERWATER;
	}

	@Override
	public SoundEvent underwaterCloseChestSound() {
		return WWSounds.BLOCK_CHEST_CLOSE_UNDERWATER;
	}
}
