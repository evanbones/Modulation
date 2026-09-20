package com.evandev.modulation.modules.brainierbees.tasks;

import com.evandev.modulation.modules.brainierbees.BeeBrain;
import com.evandev.modulation.modules.brainierbees.HiveHelper;
import com.evandev.modulation.registry.ModMemoryTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;

public class EnterHiveTask extends Behavior<Bee> {

    private static final double HIVE_CLOSE_ENOUGH_DISTANCE = 2.0;

    public EnterHiveTask() {
        super(Map.of(ModMemoryTypes.BEE_HIVE_POS, MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Bee bee) {
        BlockPos hivePos = HiveHelper.getHivePos(bee);
        if (hivePos == null
                || !BeeBrain.wantsToEnterHive(bee)
                || !hivePos.closerToCenterThan(bee.position(), HIVE_CLOSE_ENOUGH_DISTANCE)) {
            return false;
        }
        BlockEntity blockEntity = level.getBlockEntity(hivePos);
        if (!(blockEntity instanceof BeehiveBlockEntity hive)) {
            return false;
        }
        if (hive.isFull()) {
            bee.getBrain().eraseMemory(ModMemoryTypes.BEE_HIVE_POS);
            return false;
        }
        return true;
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Bee bee, long gameTime) {
        return false;
    }

    @Override
    protected void start(ServerLevel level, Bee bee, long gameTime) {
        BlockPos hivePos = HiveHelper.getHivePos(bee);
        if (hivePos != null && level.getBlockEntity(hivePos) instanceof BeehiveBlockEntity hive) {
            hive.addOccupant(bee);
        }
    }
}
