/*
 * Copyright 2025-2026 FrozenBlock
 * This file is part of The Copperier Age.
 *
 * This program is free software; you can modify it under
 * the terms of version 1 of the FrozenBlock Modding Oasis License
 * as published by FrozenBlock Modding Oasis.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * FrozenBlock Modding Oasis License for more details.
 *
 * You should have received a copy of the FrozenBlock Modding Oasis License
 * along with this program; if not, see <https://github.com/FrozenBlock/Licenses>.
 */

package net.frozenblock.thecopperierage.item;

import java.util.Optional;
import net.frozenblock.lib.sound.impl.networking.FrozenLibSoundPackets;
import net.frozenblock.thecopperierage.mod_compat.FrozenLibIntegration;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class CopperHornItem extends InstrumentItem {

	public CopperHornItem(Properties properties) {
		super(properties);
	}

	private static void playSound(Instrument instrument, Player player, Level level) {
		if (!level.isClientSide()) {
			final SoundEvent sound = instrument.soundEvent().value();
			final float range = instrument.range() / 16F;
			final int note = (int) ((-player.getXRot() + 90) / 7.5D);

			final float soundPitch = !player.isShiftKeyDown() ?
				(float) Math.pow(2D, (note - 12D) / 12D) :
				(float) Math.pow(2D, 0.01111D * -player.getXRot());
			FrozenLibSoundPackets.createAndSendMovingRestrictionLoopingSound(
				level,
				player,
				BuiltInRegistries.SOUND_EVENT.get(sound.location()).orElseThrow(),
				SoundSource.RECORDS,
				range,
				soundPitch,
				FrozenLibIntegration.INSTRUMENT_SOUND_PREDICATE,
				true
			);
		}

		level.gameEvent(GameEvent.INSTRUMENT_PLAY, player.position(), GameEvent.Context.of(player));
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		final ItemStack stack = player.getItemInHand(hand);
		final Optional<? extends Holder<Instrument>> optional = this.getInstrument(stack);
		if (optional.isEmpty()) return InteractionResult.FAIL;

		player.startUsingItem(hand);
		playSound(optional.get().value(), player, level);
		player.awardStat(Stats.ITEM_USED.get(this));
		return InteractionResult.CONSUME;
	}
}
