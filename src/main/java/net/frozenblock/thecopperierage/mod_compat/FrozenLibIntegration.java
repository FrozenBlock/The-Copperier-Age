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

package net.frozenblock.thecopperierage.mod_compat;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.integration.api.ModIntegration;
import net.frozenblock.lib.sound.api.predicate.SoundPredicate;
import net.frozenblock.lib.wind.api.WindDisturbance;
import net.frozenblock.lib.wind.api.WindDisturbanceLogic;
import net.frozenblock.lib.worldgen.structure.api.BlockStateRespectingProcessorRule;
import net.frozenblock.lib.worldgen.structure.api.BlockStateRespectingRuleProcessor;
import net.frozenblock.lib.worldgen.structure.api.StructureProcessorApi;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.block.CopperFanBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;

public class FrozenLibIntegration extends ModIntegration {
	public static final ResourceLocation INSTRUMENT_SOUND_PREDICATE = TCAConstants.id("instrument");
	public static final ResourceLocation COPPER_FAN_WIND_DISTURBANCE = TCAConstants.id("copper_fan");
	public static final ResourceLocation COPPER_FAN_WIND_DISTURBANCE_REVERSE = TCAConstants.id("copper_fan_reverse");

	public FrozenLibIntegration() {
		super("frozenlib");
	}

	@Override
	public void initPreFreeze() {
		SoundPredicate.register(INSTRUMENT_SOUND_PREDICATE, () -> new SoundPredicate.LoopPredicate<LivingEntity>() {

			private boolean firstCheck = true;
			private ItemStack lastStack;

			@Override
			public Boolean firstTickTest(LivingEntity entity) {
				return true;
			}

			@Override
			public boolean test(LivingEntity entity) {
				if (firstCheck) {
					firstCheck = false;
					InteractionHand hand = !entity.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() ? InteractionHand.MAIN_HAND : !entity.getItemInHand(InteractionHand.OFF_HAND).isEmpty() ? InteractionHand.OFF_HAND : null;
					if (hand == null) return false;

					ItemStack stack = entity.getItemInHand(hand);
					if (stack.getItem() instanceof InstrumentItem) {
						this.lastStack = stack;
						return true;
					}
					return false;
				}
				var stack = entity.getUseItem();
				if (stack.getItem() instanceof InstrumentItem) {
					if (this.lastStack == null || ItemStack.matches(this.lastStack, stack)) {
						this.lastStack = stack;
						return true;
					}
					this.firstCheck = true;
				}
				return false;
			}
		});

		WindDisturbanceLogic.register(
			COPPER_FAN_WIND_DISTURBANCE,
			(WindDisturbanceLogic.DisturbanceLogic<CopperFanBlock>) (source, level, windOrigin, affectedArea, windTarget) -> {
				return getCopperFanDisturbanceResult(source, level, windOrigin, windTarget, false, CopperFanBlock.FAN_DISTANCE, CopperFanBlock.BASE_WIND_INTENSITY);
			}
		);

		WindDisturbanceLogic.register(
			COPPER_FAN_WIND_DISTURBANCE_REVERSE,
			(WindDisturbanceLogic.DisturbanceLogic<CopperFanBlock>) (source, level, windOrigin, affectedArea, windTarget) -> {
				return getCopperFanDisturbanceResult(source, level, windOrigin, windTarget, true, CopperFanBlock.FAN_DISTANCE_REVERSE, CopperFanBlock.BASE_WIND_INTENSITY_REVERSE);
			}
		);
	}

	private static WindDisturbance.@Nullable DisturbanceResult getCopperFanDisturbanceResult(
		@NotNull Optional<CopperFanBlock> source,
		Level level,
		Vec3 windOrigin,
		Vec3 windTarget,
		boolean reverse,
		double fanDistance,
		double windIntensity
	) {
		if (source.isPresent()) {
			final BlockPos pos = BlockPos.containing(windOrigin);
			final BlockState state = level.getBlockState(pos);

			if (state.getBlock() instanceof CopperFanBlock) {
				final Direction direction = state.getValue(CopperFanBlock.FACING);
				Vec3 movement = Vec3.atLowerCornerOf(direction.getUnitVec3i());
				double strength = fanDistance - Math.min(windTarget.distanceTo(windOrigin), fanDistance);
				double intensity = strength / fanDistance;
				return new WindDisturbance.DisturbanceResult(
					Mth.clamp(intensity * 1.5D, 0D, 1D) * (reverse ? 0.5D : 1D),
					strength * 1.5D * (reverse ? 0.5D : 1D),
					movement.scale(intensity * windIntensity).scale(20D * (reverse ? 0.5D : 1D))
				);
			}
		}
		return null;
	}

	@Override
	public void init() {
		// TODO: Config
		//if (WWWorldgenConfig.get().structure.newDesertVillages) {
			StructureProcessorApi.addProcessor(
				BuiltinStructures.TRIAL_CHAMBERS.location(),
				new BlockStateRespectingRuleProcessor(
					ImmutableList.of(
						new BlockStateRespectingProcessorRule(new BlockMatchTest(Blocks.CHEST), AlwaysTrueTest.INSTANCE, Blocks.WAXED_COPPER_CHEST)
					)
				)
			);
		//}

	}

	@Environment(EnvType.CLIENT)
	@Override
	public void clientInit() {
	}
}
