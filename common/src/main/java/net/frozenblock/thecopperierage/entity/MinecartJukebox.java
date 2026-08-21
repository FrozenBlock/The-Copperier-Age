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

package net.frozenblock.thecopperierage.entity;

import java.util.Optional;
import net.frozenblock.thecopperierage.mod_compat.audioplayer.AudioPlayerIntegration;
import net.frozenblock.thecopperierage.registry.TCAEntityTypes;
import net.frozenblock.thecopperierage.registry.TCAItems;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecartContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class MinecartJukebox extends AbstractMinecartContainer {
	public static final String TICKS_SINCE_SONG_STARTED_TAG_ID = "ticks_since_song_started";
	private static final long PLAY_EVENT_INTERVAL_TICKS = 20L;
	private static final EntityDataAccessor<ItemStack> DATA_CLIENT_RECORD_ITEM = SynchedEntityData.defineId(MinecartJukebox.class, EntityDataSerializers.ITEM_STACK);
	private static final EntityDataAccessor<Byte> DATA_PLAYING = SynchedEntityData.defineId(MinecartJukebox.class, EntityDataSerializers.BYTE);
	private static final int CONTAINER_SIZE = 1;
	private long ticksSinceSongStarted;
	private boolean customAudio;
	@Nullable
	private ServerPlayer insertingPlayer;

	public MinecartJukebox(EntityType<? extends MinecartJukebox> type, Level level) {
		super(type, level);
		this.clearItemStacks();
	}

	public MinecartJukebox(Level level, double x, double y, double z) {
		this(TCAEntityTypes.JUKEBOX_MINECART.get(), level);
		this.setPos(x, y, z);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder entityData) {
		super.defineSynchedData(entityData);
		entityData.define(DATA_CLIENT_RECORD_ITEM, ItemStack.EMPTY);
		entityData.define(DATA_PLAYING, (byte) 0);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);
		if (DATA_CLIENT_RECORD_ITEM.equals(key)) this.updateDisplayState();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.level().isClientSide() || !this.isSongPlaying()) return;

		final Optional<Holder<JukeboxSong>> song = this.getSong();
		if (song.isEmpty()) {
			this.stopPlaying();
			return;
		}

		final boolean finished = this.customAudio
			? AudioPlayerIntegration.isStopped(this)
			: this.ticksSinceSongStarted >= song.get().value().lengthInTicks();
		if (finished) {
			this.stopPlaying();
			return;
		}

		if (this.ticksSinceSongStarted % PLAY_EVENT_INTERVAL_TICKS == 0L && this.level() instanceof ServerLevel serverLevel) {
			this.level().gameEvent(GameEvent.JUKEBOX_PLAY, this.position(), GameEvent.Context.of(this));
			final float noteOffset = this.getRandom().nextInt(4) / 24F;
			serverLevel.sendParticles(ParticleTypes.NOTE, this.getX(), this.getY() + this.getBlockTopOffset() + 0.2D, this.getZ(), 0, noteOffset, 0D, 0D, 1D);
		}

		this.ticksSinceSongStarted++;
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand, Vec3 vec3) {
		if (!(this.level().isClientSide() ? this.getClientRecordItem() : this.getItem(0)).isEmpty()) {
			if (this.level() instanceof ServerLevel serverLevel) this.ejectRecord(serverLevel);
			return InteractionResult.SUCCESS;
		}

		final ItemStack heldStack = player.getItemInHand(hand);
		final boolean canInsertSong = heldStack.has(DataComponents.JUKEBOX_PLAYABLE);

		if (this.level().isClientSide()) {
			return canInsertSong ? InteractionResult.SUCCESS : InteractionResult.PASS;
		}

		if (canInsertSong) {
			final ItemStack insertStack = heldStack.consumeAndReturn(1, player);
			this.insertingPlayer = player instanceof ServerPlayer serverPlayer ? serverPlayer : null;
			try {
				this.setItem(0, insertStack);
			} finally {
				this.insertingPlayer = null;
			}
			player.awardStat(Stats.PLAY_RECORD);
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	@Override
	public void activateMinecart(ServerLevel level, int x, int y, int z, boolean powered) {
		if (!powered) return;
		if (this.level() instanceof ServerLevel serverLevel) this.ejectRecord(serverLevel);
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		if (this.isSongPlaying()) output.putLong(TICKS_SINCE_SONG_STARTED_TAG_ID, this.ticksSinceSongStarted);
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		final ItemStack item = this.getItem(0);
		this.setClientRecordItem(item);
		input.getLong(TICKS_SINCE_SONG_STARTED_TAG_ID)
			.ifPresent(ticks -> JukeboxSong.fromStack(item)
				.ifPresent(song -> this.setSongWithoutPlaying(song, ticks))
			);
	}

	@Override
	protected Item getDropItem() {
		return TCAItems.JUKEBOX_MINECART.get();
	}

	@Override
	public ItemStack getPickResult() {
		return new ItemStack(TCAItems.JUKEBOX_MINECART);
	}

	@Override
	public BlockState getDefaultDisplayBlockState() {
		return Blocks.JUKEBOX.defaultBlockState().setValue(JukeboxBlock.HAS_RECORD, false);
	}

	public double getBlockTopOffset() {
		return 0.75D + (this.getDisplayOffset() * 0.75D / 16D);
	}

	public Optional<Holder<JukeboxSong>> getSong() {
		return JukeboxSong.fromStack(this.getClientRecordItem());
	}

	public int getComparatorOutput() {
		return this.getSong().map(Holder::value).map(JukeboxSong::comparatorOutput).orElse(0);
	}

	public boolean isSongPlaying() {
		return (this.getEntityData().get(DATA_PLAYING) & 1) != 0;
	}

	public boolean isSongSilent() {
		return (this.getEntityData().get(DATA_PLAYING) & 2) != 0;
	}

	public void setSongWithoutPlaying(Holder<JukeboxSong> song, long ticksSinceSongStarted) {
		if (song.value().hasFinished(ticksSinceSongStarted)) return;
		this.ticksSinceSongStarted = ticksSinceSongStarted;
		this.getEntityData().set(DATA_PLAYING, (byte) 3);
	}

	private void startPlaying() {
		if (this.getSong().isEmpty()) {
			this.stopPlaying();
			return;
		}

		this.ticksSinceSongStarted = 0L;
		this.customAudio = this.level() instanceof ServerLevel serverLevel
			&& AudioPlayerIntegration.startMusicDisc(serverLevel, this, this.getItem(0), this.insertingPlayer);
		this.getEntityData().set(DATA_PLAYING, (byte) (this.customAudio ? 3 : 1));
	}

	private void stopPlaying() {
		if (!this.isSongPlaying()) return;

		if (this.customAudio) {
			AudioPlayerIntegration.stop(this);
			this.customAudio = false;
		}
		this.ticksSinceSongStarted = 0L;
		this.getEntityData().set(DATA_PLAYING, (byte) 0);
		this.level().gameEvent(GameEvent.JUKEBOX_STOP_PLAY, this.position(), GameEvent.Context.of(this));
	}

	private void ejectRecord(ServerLevel level) {
		final ItemStack removedRecord = this.getItem(0).copyAndClear();
		if (!removedRecord.isEmpty()) this.spawnAtLocation(level, removedRecord, (float) (this.getBlockTopOffset() + 0.01F));
		this.setClientRecordItem(ItemStack.EMPTY);
	}

	private ItemStack getClientRecordItem() {
		return this.getEntityData().get(DATA_CLIENT_RECORD_ITEM);
	}

	private void setClientRecordItem(ItemStack recordItem) {
		this.getEntityData().set(DATA_CLIENT_RECORD_ITEM, recordItem.copyWithCount(Math.min(recordItem.getCount(), 1)));
		this.updateDisplayState();
		this.setChanged();
	}

	private void updateDisplayState() {
		final BlockState state = this.getDefaultDisplayBlockState().setValue(JukeboxBlock.HAS_RECORD, !this.getClientRecordItem().isEmpty());
		this.setCustomDisplayBlockState(Optional.of(state));
	}

	@Override
	public int getContainerSize() {
		return CONTAINER_SIZE;
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		final ItemStack removed = super.removeItem(slot, amount);
		final ItemStack remaining = this.getItem(slot);
		this.setClientRecordItem(remaining);
		if (remaining.isEmpty()) this.stopPlaying();
		return removed;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		super.setItem(slot, stack);
		this.setClientRecordItem(stack.copy());
		if (!stack.has(DataComponents.JUKEBOX_PLAYABLE)) return;
		this.startPlaying();
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return stack.has(DataComponents.JUKEBOX_PLAYABLE) && this.getItem(slot).isEmpty();
	}

	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
		return null;
	}

	@Nullable
	@Override
	protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
		return null;
	}
}
