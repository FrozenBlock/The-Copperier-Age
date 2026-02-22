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
import net.minecraft.world.Container;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.frozenblock.thecopperierage.registry.TCAEntityTypes;
import net.frozenblock.thecopperierage.registry.TCAItems;

public class JukeboxMinecart extends AbstractMinecart implements Container {
	public static final String SONG_ITEM_TAG_ID = "RecordItem";
	public static final String TICKS_SINCE_SONG_STARTED_TAG_ID = "ticks_since_song_started";
	private static final long PLAY_EVENT_INTERVAL_TICKS = 20L;
	private static final EntityDataAccessor<ItemStack> DATA_RECORD_ITEM = SynchedEntityData.defineId(JukeboxMinecart.class, EntityDataSerializers.ITEM_STACK);
	private static final EntityDataAccessor<Boolean> DATA_IS_PLAYING = SynchedEntityData.defineId(JukeboxMinecart.class, EntityDataSerializers.BOOLEAN);
	private static final int CONTAINER_SIZE = 1;
	private long ticksSinceSongStarted;

	public JukeboxMinecart(EntityType<? extends JukeboxMinecart> entityType, Level level) {
		super(entityType, level);
	}

	public JukeboxMinecart(Level level, double x, double y, double z) {
		this(TCAEntityTypes.JUKEBOX_MINECART, level);
		this.setPos(x, y, z);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_RECORD_ITEM, ItemStack.EMPTY);
		builder.define(DATA_IS_PLAYING, false);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		super.onSyncedDataUpdated(key);
		if (DATA_RECORD_ITEM.equals(key)) {
			this.updateDisplayState();
		}
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

		if (this.ticksSinceSongStarted >= song.get().value().lengthInTicks()) {
			this.stopPlaying();
			return;
		}

		if (this.ticksSinceSongStarted % PLAY_EVENT_INTERVAL_TICKS == 0L && this.level() instanceof ServerLevel serverLevel) {
			this.level().gameEvent(GameEvent.JUKEBOX_PLAY, this.position(), GameEvent.Context.of(this));
			final float noteOffset = this.level().random.nextInt(4) / 24.0F;
			serverLevel.sendParticles(ParticleTypes.NOTE, this.getX(), this.getY() + 1.2D, this.getZ(), 0, noteOffset, 0.0D, 0.0D, 1.0D);
		}

		this.ticksSinceSongStarted++;
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		final ItemStack heldStack = player.getItemInHand(hand);
		final boolean canInsertSong = this.getRecordItem().isEmpty() && heldStack.has(DataComponents.JUKEBOX_PLAYABLE);
		final boolean canEjectSong = !this.getRecordItem().isEmpty() && (player.isSecondaryUseActive() || heldStack.isEmpty());

		if (this.level().isClientSide()) {
			return canInsertSong || canEjectSong ? InteractionResult.SUCCESS : InteractionResult.PASS;
		}

		if (canInsertSong) {
			final Optional<Holder<JukeboxSong>> song = JukeboxSong.fromStack(this.level().registryAccess(), heldStack);
			if (song.isEmpty()) {
				return InteractionResult.PASS;
			}

			this.setRecordItem(heldStack.copyWithCount(1));
			this.startPlaying();
			if (!player.getAbilities().instabuild) {
				heldStack.shrink(1);
			}
			return InteractionResult.SUCCESS;
		}

		if (canEjectSong && this.level() instanceof ServerLevel serverLevel) {
			this.ejectRecord(serverLevel, true);
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	@Override
	protected void destroy(ServerLevel level, DamageSource source) {
		final ItemStack removedRecord = this.removeRecordItem();
		super.destroy(level, source);
		if (!removedRecord.isEmpty() && level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
			this.spawnAtLocation(level, removedRecord);
		}
	}

	@Override
	public void activateMinecart(int x, int y, int z, boolean powered) {
		if (!powered) return;
		if (this.level() instanceof ServerLevel serverLevel) {
			this.ejectRecord(serverLevel, true);
		}
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		if (!this.getRecordItem().isEmpty()) {
			output.store(SONG_ITEM_TAG_ID, ItemStack.CODEC, this.getRecordItem());
		}

		if (this.isSongPlaying()) {
			output.putLong(TICKS_SINCE_SONG_STARTED_TAG_ID, this.ticksSinceSongStarted);
		}
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		this.setRecordItem(input.read(SONG_ITEM_TAG_ID, ItemStack.CODEC).orElse(ItemStack.EMPTY));
		this.ticksSinceSongStarted = 0L;
		this.getEntityData().set(DATA_IS_PLAYING, false);
	}

	@Override
	protected Item getDropItem() {
		return TCAItems.JUKEBOX_MINECART;
	}

	@Override
	public ItemStack getPickResult() {
		return new ItemStack(TCAItems.JUKEBOX_MINECART);
	}

	@Override
	public BlockState getDefaultDisplayBlockState() {
		return Blocks.JUKEBOX.defaultBlockState().setValue(JukeboxBlock.HAS_RECORD, false);
	}

	@Override
	public int getDefaultDisplayOffset() {
		return 6;
	}

	public Optional<Holder<JukeboxSong>> getSong() {
		return JukeboxSong.fromStack(this.level().registryAccess(), this.getRecordItem());
	}

	public int getComparatorOutput() {
		return this.getSong().map(Holder::value).map(JukeboxSong::comparatorOutput).orElse(0);
	}

	public boolean isSongPlaying() {
		return this.getEntityData().get(DATA_IS_PLAYING);
	}

	private void startPlaying() {
		if (this.getSong().isEmpty()) {
			this.stopPlaying();
			return;
		}

		this.ticksSinceSongStarted = 0L;
		this.getEntityData().set(DATA_IS_PLAYING, true);
	}

	private void stopPlaying() {
		if (!this.isSongPlaying()) return;

		this.ticksSinceSongStarted = 0L;
		this.getEntityData().set(DATA_IS_PLAYING, false);
		this.level().gameEvent(GameEvent.JUKEBOX_STOP_PLAY, this.position(), GameEvent.Context.of(this));
	}

	private ItemStack removeRecordItem() {
		final ItemStack record = this.getRecordItem();
		if (record.isEmpty()) return ItemStack.EMPTY;

		this.setRecordItem(ItemStack.EMPTY);
		this.stopPlaying();
		return record;
	}

	private void ejectRecord(ServerLevel serverLevel, boolean dropInWorld) {
		final ItemStack removedRecord = this.removeRecordItem();
		if (dropInWorld && !removedRecord.isEmpty()) {
			this.spawnAtLocation(serverLevel, removedRecord);
		}
	}

	private ItemStack getRecordItem() {
		return this.getEntityData().get(DATA_RECORD_ITEM);
	}

	private void setRecordItem(ItemStack recordItem) {
		this.getEntityData().set(DATA_RECORD_ITEM, recordItem.copyWithCount(Math.min(recordItem.getCount(), 1)));
		this.updateDisplayState();
		this.setChanged();
	}

	private void updateDisplayState() {
		final BlockState state = this.getDefaultDisplayBlockState().setValue(JukeboxBlock.HAS_RECORD, !this.getRecordItem().isEmpty());
		this.setCustomDisplayBlockState(Optional.of(state));
	}

	@Override
	public int getContainerSize() {
		return CONTAINER_SIZE;
	}

	@Override
	public boolean isEmpty() {
		return this.getRecordItem().isEmpty();
	}

	@Override
	public ItemStack getItem(int slot) {
		return slot == 0 ? this.getRecordItem() : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		if (slot != 0) return ItemStack.EMPTY;

		final ItemStack record = this.getRecordItem();
		if (record.isEmpty() || amount <= 0) {
			return ItemStack.EMPTY;
		}

		final int removedCount = Math.min(amount, record.getCount());
		final ItemStack removed = record.copyWithCount(removedCount);
		final ItemStack remaining = record.copy();
		remaining.shrink(removedCount);
		this.setRecordItem(remaining);
		if (remaining.isEmpty()) {
			this.stopPlaying();
		}

		return removed;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		if (slot != 0) return ItemStack.EMPTY;
		return this.removeRecordItem();
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		if (slot != 0) return;

		if (stack.isEmpty()) {
			this.removeRecordItem();
			return;
		}

		if (!stack.has(DataComponents.JUKEBOX_PLAYABLE)) {
			return;
		}

		this.setRecordItem(stack.copyWithCount(1));
		this.startPlaying();
	}

	@Override
	public boolean stillValid(Player player) {
		return !this.isRemoved() && player.distanceToSqr(this) <= 64.0D;
	}

	@Override
	public void setChanged() {
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return slot == 0 && this.getRecordItem().isEmpty() && stack.has(DataComponents.JUKEBOX_PLAYABLE);
	}

	@Override
	public void clearContent() {
		this.removeRecordItem();
	}
}