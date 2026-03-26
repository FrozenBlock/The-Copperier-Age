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

package net.frozenblock.thecopperierage.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import net.frozenblock.thecopperierage.block.entity.CrateBlockEntity;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.registry.TCABlockEntityTypes;
import net.frozenblock.thecopperierage.registry.TCAStats;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class CrateBlock extends BaseEntityBlock {
	public static final MapCodec<CrateBlock> CODEC = simpleCodec(CrateBlock::new);
	public static final Identifier CONTENTS = Identifier.withDefaultNamespace("contents");
	public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

	@Override
	public MapCodec<CrateBlock> codec() {
		return CODEC;
	}

	public CrateBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false));
	}

	public static SlotResult verifyStackForPlacement(ItemStack stack, Container container) {
		if (stack == null || stack.isEmpty()) return SlotResult.FAILURE_EMPTY_ITEM;
		if (!stack.getComponents().getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY).isEmpty()) return SlotResult.FAILURE_CONTAINER_ITEM;
		if (stack.getComponents().getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyStream().findAny().isPresent()) return SlotResult.FAILURE_CONTAINER_ITEM;

		final Item item = stack.getItem();
		if (container.hasAnyMatching(containerStack -> !containerStack.isEmpty() && !containerStack.is(item))) return SlotResult.FAILURE_MISMATCHING_ITEM;
		return SlotResult.SUCCESS;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level instanceof ServerLevel serverLevel && level.getBlockEntity(pos) instanceof CrateBlockEntity crate) {
			player.openMenu(crate);
			player.awardStat(TCAStats.OPEN_CRATE);
			PiglinAi.angerNearbyPiglins(serverLevel, player, true);
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean pushedByPiston) {
		Containers.updateNeighboursAfterDestroy(state, level, pos);
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (level.getBlockEntity(pos) instanceof CrateBlockEntity crate) crate.recheckOpen();
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CrateBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide()) return null;
		return createTickerHelper(
			type,
			TCABlockEntityTypes.CRATE,
			(levelx, posx, statex, crate) -> crate.serverTick(levelx, posx, statex)
		);
	}

	@Nullable
	@Override
	protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		if (!TCAConfig.get().crateHasMenu) return null;
		return super.getMenuProvider(state, level, pos);
	}

	@Override
	protected boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
		return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, OPEN);
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		saveItemsToDrop:{
			if (!TCAConfig.get().cratesDropWithItems) break saveItemsToDrop;
			if (!(level.getBlockEntity(pos) instanceof CrateBlockEntity crate)) break saveItemsToDrop;

			if (level.isClientSide() || !player.preventsBlockDrops() || crate.isEmpty()) {
				crate.unpackLootTable(player);
				break saveItemsToDrop;
			}

			final ItemStack stack = new ItemStack(this.asItem());
			stack.applyComponents(crate.collectComponents());

			final ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
			itemEntity.setDefaultPickUpDelay();
			level.addFreshEntity(itemEntity);
		}

		return super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		if (builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof CrateBlockEntity crate) {
			builder = builder.withDynamicDrop(CONTENTS, consumer -> {
				for (int i = 0; i < crate.getContainerSize(); i++) consumer.accept(crate.getItem(i));
			});
		}

		return super.getDrops(state, builder);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
	}

	public enum SlotResult {
		SUCCESS(Optional.empty()),
		FAILURE_EMPTY_ITEM(Optional.empty()),
		FAILURE_CONTAINER_ITEM(Optional.of(Component.translatable("gui.thecopperierage.crate_cannot_fit_container_item"))),
		FAILURE_MISMATCHING_ITEM(Optional.of(Component.translatable("gui.thecopperierage.crate_mismatching_item")));
		private final Optional<Component> tooltip;

		SlotResult(Optional<Component> tooltip) {
			this.tooltip = tooltip;
		}

		public boolean isSuccess() {
			return this == SUCCESS;
		}

		public boolean isEmptyItem() {
			return this == FAILURE_EMPTY_ITEM;
		}

		public Optional<Component> getTooltip() {
			return this.tooltip;
		}
	}
}
