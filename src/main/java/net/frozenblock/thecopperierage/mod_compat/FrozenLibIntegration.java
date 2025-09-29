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
import net.frozenblock.lib.worldgen.structure.api.TemplatePoolApi;
import net.frozenblock.thecopperierage.TCAConstants;
import net.frozenblock.thecopperierage.block.CopperFanBlock;
import net.frozenblock.thecopperierage.config.TCAConfig;
import net.frozenblock.thecopperierage.registry.TCAResources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.ProcessorLists;
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
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
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
				return getCopperFanDisturbanceResult(source, level, windOrigin, windTarget, false, 1D);
			}
		);

		WindDisturbanceLogic.register(
			COPPER_FAN_WIND_DISTURBANCE_REVERSE,
			(WindDisturbanceLogic.DisturbanceLogic<CopperFanBlock>) (source, level, windOrigin, affectedArea, windTarget) -> {
				return getCopperFanDisturbanceResult(source, level, windOrigin, windTarget, true, CopperFanBlock.WIND_INTENSITY_SUCK_SCALE);
			}
		);
	}

	private static WindDisturbance.@Nullable DisturbanceResult getCopperFanDisturbanceResult(
		@NotNull Optional<CopperFanBlock> source,
		Level level,
		Vec3 windOrigin,
		Vec3 windTarget,
		boolean reverse,
		double windIntensityScale
	) {
		if (source.isEmpty()) return null;

		final BlockPos pos = BlockPos.containing(windOrigin);
		final BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof CopperFanBlock copperFanBlock)) return null;

		final double fanDistance = (!reverse ? copperFanBlock.pushBlocks : copperFanBlock.suckBlocks) + 1D;
		final Direction direction = state.getValue(CopperFanBlock.FACING);
		Vec3 movement = Vec3.atLowerCornerOf(direction.getUnitVec3i());
		double strength = fanDistance - Math.min(windTarget.distanceTo(windOrigin), fanDistance);
		double intensity = strength / fanDistance;
		return new WindDisturbance.DisturbanceResult(
			Mth.clamp(intensity * 1.5D, 0D, 1D) * windIntensityScale,
			strength * 1.5D * windIntensityScale,
			movement.scale(intensity * CopperFanBlock.WIND_INTENSITY).scale(20D * windIntensityScale)
		);
	}

	@Override
	public void init() {
		if (TCAConfig.get().copperChestsInTrialChambers) {
			StructureProcessorApi.addProcessor(
				BuiltinStructures.TRIAL_CHAMBERS.location(),
				new BlockStateRespectingRuleProcessor(
					ImmutableList.of(
						new BlockStateRespectingProcessorRule(new BlockMatchTest(Blocks.CHEST), AlwaysTrueTest.INSTANCE, Blocks.WAXED_COPPER_CHEST)
					)
				)
			);
		}

		TemplatePoolApi.ADD_ADDITIONAL_TEMPLATE_POOLS.register((processorLookup, context) -> {
			if (!TCAResources.HAS_TRICKIER_TRIALS_PACK) return;

			final Holder<StructureProcessorList> copperBulbDegradation = processorLookup
				.get(ProcessorLists.TRIAL_CHAMBERS_COPPER_BULB_DEGRADATION)
				.orElseGet(() -> processorLookup.getOrThrow(ProcessorLists.EMPTY));

			final ResourceLocation hallway = ResourceLocation.withDefaultNamespace("trial_chambers/hallway");
			final ResourceLocation end =ResourceLocation.withDefaultNamespace("trial_chambers/chambers/end");

			// Trials
			context.addElement(
				hallway,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/chamber/gated_treasure"), copperBulbDegradation),
				150,
				StructureTemplatePool.Projection.RIGID
			);
			context.addElement(
				hallway,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/chamber/jumping_wind"), copperBulbDegradation),
				150,
				StructureTemplatePool.Projection.RIGID
			);
			context.addElement(
				hallway,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/chamber/mexican_restaurant"), copperBulbDegradation),
				150,
				StructureTemplatePool.Projection.RIGID
			);
			context.addElement(
				hallway,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/chamber/wind_chamber"), copperBulbDegradation),
				150,
				StructureTemplatePool.Projection.RIGID
			);
			context.addElement(
				hallway,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/chamber/wind_trap"), copperBulbDegradation),
				150,
				StructureTemplatePool.Projection.RIGID
			);
			context.addElement(
				hallway,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/chamber/windy_pit"), copperBulbDegradation),
				150,
				StructureTemplatePool.Projection.RIGID
			);

			// Common Encounters
			context.addElement(
				hallway,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/hallway/factory_encounter"), copperBulbDegradation),
				50,
				StructureTemplatePool.Projection.RIGID
			);
			context.addElement(
				hallway,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/hallway/large_fan_encounter"), copperBulbDegradation),
				50,
				StructureTemplatePool.Projection.RIGID
			);

			// Encounters
			context.addElement(
				hallway,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/hallway/fan_encounter_1"), copperBulbDegradation),
				1,
				StructureTemplatePool.Projection.RIGID
			);
			context.addElement(
				hallway,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/hallway/fan_encounter_2"), copperBulbDegradation),
				1,
				StructureTemplatePool.Projection.RIGID
			);
			context.addElement(
				hallway,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/hallway/fan_encounter_3"), copperBulbDegradation),
				1,
				StructureTemplatePool.Projection.RIGID
			);

			// End
			context.addElement(
				end,
				StructurePoolElement.single(TCAConstants.string("trial_chambers/chamber/fan_eruption"), copperBulbDegradation),
				1,
				StructureTemplatePool.Projection.RIGID
			);
		});
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void clientInit() {
	}
}
