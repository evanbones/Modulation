package com.evandev.modulation.modules.brainierbees.tasks;

import com.evandev.modulation.mixin.minecraft.accessor.BeeAccessor;
import com.evandev.modulation.mixin.minecraft.accessor.CropBlockInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.Map;

public class GrowCropTask extends Behavior<Bee> {

    private static final int MAX_CROPS_GROWABLE = 10;

    public GrowCropTask() {
        super(Map.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT));
    }

    private static boolean canBeeUse(ServerLevel level, Bee bee) {
        if (((BeeAccessor) bee).invokeGetCropsGrownSincePollination() >= MAX_CROPS_GROWABLE) {
            return false;
        }
        if (level.getRandom().nextFloat() < 0.3F) {
            return false;
        }
        return bee.hasNectar();
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Bee bee) {
        return canBeeUse(level, bee);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Bee bee, long gameTime) {
        return canBeeUse(level, bee);
    }

    @Override
    protected void tick(ServerLevel level, Bee bee, long gameTime) {
        if (bee.getRandom().nextInt(1, 31) != 1) {
            return;
        }

        for (int i = 1; i <= 2; i++) {
            BlockPos pos = bee.blockPosition().below(i);
            BlockState state = level.getBlockState(pos);
            if (!state.is(BlockTags.BEE_GROWABLES)) {
                continue;
            }

            Block block = state.getBlock();
            IntegerProperty ageProperty = null;

            if (block instanceof CropBlock crop) {
                if (!crop.isMaxAge(state)) {
                    ageProperty = ((CropBlockInvoker) crop).invokeGetAgeProperty();
                }
            } else if (block instanceof StemBlock) {
                if (state.getValue(StemBlock.AGE) < 7) {
                    ageProperty = StemBlock.AGE;
                }
            } else if (state.is(Blocks.SWEET_BERRY_BUSH)) {
                if (state.getValue(SweetBerryBushBlock.AGE) < 3) {
                    ageProperty = SweetBerryBushBlock.AGE;
                }
            } else if (state.is(Blocks.CAVE_VINES) || state.is(Blocks.CAVE_VINES_PLANT)) {
                if (block instanceof BonemealableBlock bonemealable) {
                    bonemealable.performBonemeal(level, bee.getRandom(), pos, state);
                }
            }

            if (ageProperty != null) {
                level.levelEvent(2005, pos, 0);
                level.setBlockAndUpdate(pos, state.setValue(ageProperty, state.getValue(ageProperty) + 1));
                ((BeeAccessor) bee).invokeIncrementNumCropsGrownSincePollination();
            }
        }
    }
}
