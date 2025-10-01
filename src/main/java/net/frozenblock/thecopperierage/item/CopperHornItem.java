/*
 * Copyright 2025 FrozenBlock
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
import org.jetbrains.annotations.NotNull;

public class CopperHornItem extends InstrumentItem {

	public CopperHornItem(@NotNull Properties settings) {
		super(settings);
	}

	private static void playSound(@NotNull Instrument instrument, @NotNull Player player, @NotNull Level level) {
		final SoundEvent soundEvent = instrument.soundEvent().value();
		final float range = instrument.range() / 16F;
		final int note = (int) ((-player.getXRot() + 90) / 7.5D);

		if (!level.isClientSide()) {
			final float soundPitch = !player.isShiftKeyDown() ?
				(float) Math.pow(2D, (note - 12F) / 12D) :
				(float) Math.pow(2D, 0.01111F * -player.getXRot());
			FrozenLibSoundPackets.createAndSendMovingRestrictionLoopingSound(
				level,
				player,
				BuiltInRegistries.SOUND_EVENT.get(soundEvent.location()).orElseThrow(),
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
	@NotNull
	public InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand interactionHand) {
		final ItemStack stack = player.getItemInHand(interactionHand);
		final Optional<? extends Holder<Instrument>> optional = this.getInstrument(stack, level.registryAccess());
		if (optional.isEmpty()) return InteractionResult.FAIL;

		player.startUsingItem(interactionHand);
		playSound(optional.get().value(), player, level);
		player.awardStat(Stats.ITEM_USED.get(this));
		return InteractionResult.CONSUME;
	}
}
