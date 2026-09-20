package com.evandev.modulation.modules.brainierbees.tasks;

import com.evandev.modulation.modules.brainierbees.HiveHelper;
import com.evandev.modulation.registry.ModMemoryTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class LocateHiveTask extends Behavior<Bee> {

    private static final int HIVE_SEARCH_DISTANCE = 20;

    public LocateHiveTask() {
        super(Map.of(ModMemoryTypes.BEE_COOLDOWN_LOCATE_HIVE, MemoryStatus.VALUE_ABSENT));
    }

    private static boolean doesHiveHaveSpace(ServerLevel level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof BeehiveBlockEntity hive && !hive.isFull();
    }

    private static List<BlockPos> findNearbyHivesWithSpace(ServerLevel level, Bee bee) {
        BlockPos origin = bee.blockPosition();
        PoiManager poiManager = level.getPoiManager();
        return poiManager.getInRange(holder -> holder.is(PoiTypeTags.BEE_HOME), origin, HIVE_SEARCH_DISTANCE, PoiManager.Occupancy.ANY)
                .map(PoiRecord::getPos)
                .filter(pos -> doesHiveHaveSpace(level, pos))
                .sorted(Comparator.comparingDouble(pos -> pos.distSqr(origin)))
                .toList();
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Bee bee) {
        return !HiveHelper.hasHive(bee);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Bee bee, long gameTime) {
        return false;
    }

    @Override
    protected void start(ServerLevel level, Bee bee, long gameTime) {
        bee.getBrain().setMemory(ModMemoryTypes.BEE_COOLDOWN_LOCATE_HIVE, HiveHelper.RELOCATE_COOLDOWN);
        for (BlockPos hivePos : findNearbyHivesWithSpace(level, bee)) {
            if (!HiveHelper.isBlacklisted(bee, hivePos)) {
                HiveHelper.setHivePos(bee, hivePos);
                return;
            }
        }
    }
}
