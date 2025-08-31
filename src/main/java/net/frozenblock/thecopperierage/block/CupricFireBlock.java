package net.frozenblock.thecopperierage.block;

import com.mojang.serialization.MapCodec;
import net.frozenblock.thecopperierage.tag.TCABlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class CupricFireBlock extends BaseFireBlock {
    public static final MapCodec<CupricFireBlock> CODEC = simpleCodec(CupricFireBlock::new);

    public CupricFireBlock(BlockBehaviour.Properties properties) {
        super(properties, 1.0f);
    }

    @Override
    public MapCodec<CupricFireBlock> codec() {
        return CODEC;
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        return this.canSurvive(state, world, pos) ? this.defaultBlockState() : Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return canSurviveOnBlock(world.getBlockState(pos.below()));
    }

    public static boolean canSurviveOnBlock(BlockState blockState) {
        return blockState.is(BlockTags.COPPER);
    }

    @Override
    protected boolean canBurn(BlockState blockState) {
        return true;
    }

    @Override
    protected void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier) {
        super.entityInside(blockState, level, blockPos, entity, insideBlockEffectApplier);
        if (!level.isClientSide() && entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 200));
        }
    }
}
