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

package net.frozenblock.thecopperierage.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.frozenblock.thecopperierage.block.entity.CopperCrateBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
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
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class CopperCrateBlock extends BaseEntityBlock {
	public static final MapCodec<CopperCrateBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
			WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(CopperCrateBlock::getWeatherState),
			propertiesCodec()
		).apply(instance, CopperCrateBlock::new)
	);
	public static final ResourceLocation CONTENTS = ResourceLocation.withDefaultNamespace("contents");
	public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	private final WeatheringCopper.WeatherState weatherState;
	private final SoundEvent openSound;
	private final SoundEvent closeSound;

	@Override
	public MapCodec<CopperCrateBlock> codec() {
		return CODEC;
	}

	public CopperCrateBlock(WeatheringCopper.WeatherState weatherState, Properties properties) {
		super(properties);
		this.weatherState = weatherState;
		this.openSound = getOpenSound(weatherState);
		this.closeSound = getCloseSound(weatherState);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false));
	}

	// TODO: sounds
	@Contract(pure = true)
	private static SoundEvent getOpenSound(WeatheringCopper.WeatherState weatherState) {
		return SoundEvents.BARREL_OPEN;
	}

	@Contract(pure = true)
	private static SoundEvent getCloseSound(WeatheringCopper.WeatherState weatherState) {
		return SoundEvents.BARREL_CLOSE;
	}

	public SoundEvent getOpenSound() {
		return this.openSound;
	}

	public SoundEvent getCloseSound() {
		return this.closeSound;
	}

	public WeatheringCopper.WeatherState getWeatherState() {
		return this.weatherState;
	}

	public static boolean veryStackForPlacement(ItemStack stack, Container container) {
		if (stack.isEmpty()) return false;
		if (!stack.getComponents().getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY).isEmpty()) return false;
		if (stack.getComponents().getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyStream().findAny().isPresent()) return false;

		final Item item = stack.getItem();
		return !container.hasAnyMatching(containerStack -> !containerStack.isEmpty() && !containerStack.is(item));
	}

	// TODO: custom stat
	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level instanceof ServerLevel serverLevel && level.getBlockEntity(pos) instanceof CopperCrateBlockEntity copperCrate) {
			player.openMenu(copperCrate);
			player.awardStat(Stats.OPEN_BARREL);
			PiglinAi.angerNearbyPiglins(serverLevel, player, true);
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (level instanceof ServerLevel serverLevel && level.getBlockEntity(pos) instanceof CopperCrateBlockEntity copperCrate) {
			player.openMenu(copperCrate);
			player.awardStat(Stats.OPEN_BARREL);
			PiglinAi.angerNearbyPiglins(serverLevel, player, true);
		}
		return InteractionResult.TRY_WITH_EMPTY_HAND;
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean pushedByPiston) {
		Containers.updateNeighboursAfterDestroy(state, level, pos);
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (level.getBlockEntity(pos) instanceof CopperCrateBlockEntity copperCrate) copperCrate.recheckOpen();
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CopperCrateBlockEntity(pos, state);
	}

	@Nullable
	@Override
	protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		return super.getMenuProvider(state, level, pos);
		//return null;
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
			// TODO: config
			if (true) break saveItemsToDrop;
			if (!(level.getBlockEntity(pos) instanceof CopperCrateBlockEntity crate)) break saveItemsToDrop;

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
		if (builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof CopperCrateBlockEntity crate) {
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
}
