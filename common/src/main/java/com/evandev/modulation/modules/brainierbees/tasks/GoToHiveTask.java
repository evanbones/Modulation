package com.evandev.modulation.modules.brainierbees.tasks;

import com.evandev.modulation.modules.brainierbees.BrainierBeesModule;
import com.evandev.modulation.modules.brainierbees.HiveHelper;
import com.evandev.modulation.registry.ModMemoryTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.pathfinder.Path;

import java.util.Map;
import java.util.Optional;

public class GoToHiveTask extends Behavior<Bee> {

    private static final int TICKS_PER_WANDER_BLOCK = 78;
    private static final int MAX_STUCK_TICKS = 600;

    public GoToHiveTask() {
        super(Map.of(
                ModMemoryTypes.BEE_HIVE_POS, MemoryStatus.VALUE_PRESENT,
                ModMemoryTypes.BEE_COOLDOWN_LOCATE_HIVE, MemoryStatus.VALUE_ABSENT
        ));
    }

    private static boolean canGoHome(ServerLevel level, Bee bee) {
        return HiveHelper.hasHive(bee)
                && bee.getBrain().getMemory(ModMemoryTypes.BEE_WANTS_HIVE).orElse(false)
                && !HiveHelper.isHiveNearFire(level, bee);
    }

    private static boolean pathfindDirectlyTowards(BlockPos pos, Bee bee) {
        bee.getNavigation().setMaxVisitedNodesMultiplier(10.0F);
        bee.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), 1.0);
        return bee.getNavigation().getPath() != null && bee.getNavigation().getPath().canReach();
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Bee bee) {
        return canGoHome(level, bee);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Bee bee, long gameTime) {
        return canGoHome(level, bee);
    }

    @Override
    protected void start(ServerLevel level, Bee bee, long gameTime) {
        bee.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
        bee.resetLove();
    }

    @Override
    protected void tick(ServerLevel level, Bee bee, long gameTime) {
        Brain<?> brain = bee.getBrain();
        BlockPos hivePos = HiveHelper.getHivePos(bee);
        if (hivePos == null) {
            return;
        }

        int travellingTicks = brain.getMemory(ModMemoryTypes.BEE_TRAVELLING_TICKS).orElse(0) + 1;
        brain.setMemory(ModMemoryTypes.BEE_TRAVELLING_TICKS, travellingTicks);

        if (travellingTicks > TICKS_PER_WANDER_BLOCK * BrainierBeesModule.maxWanderRadius()) {
            HiveHelper.dropAndBlacklistHive(bee);
            return;
        }

        if (!bee.getNavigation().isInProgress() && !pathfindDirectlyTowards(hivePos, bee)) {
            HiveHelper.dropAndBlacklistHive(bee);
            return;
        }

        Path currentPath = bee.getNavigation().getPath();
        if (currentPath == null) {
            return;
        }

        Optional<Path> lastPath = brain.getMemory(ModMemoryTypes.BEE_LAST_PATH);
        if (lastPath.isPresent() && currentPath.sameAs(lastPath.get())) {
            int stuckTicks = brain.getMemory(ModMemoryTypes.BEE_STUCK_TICKS).orElse(0) + 1;
            brain.setMemory(ModMemoryTypes.BEE_STUCK_TICKS, stuckTicks);
            if (stuckTicks > MAX_STUCK_TICKS) {
                HiveHelper.dropAndBlacklistHive(bee);
            }
        } else {
            brain.setMemory(ModMemoryTypes.BEE_LAST_PATH, currentPath);
            brain.setMemory(ModMemoryTypes.BEE_STUCK_TICKS, 0);
        }
    }

    @Override
    protected void stop(ServerLevel level, Bee bee, long gameTime) {
        bee.getBrain().setMemory(ModMemoryTypes.BEE_TRAVELLING_TICKS, 0);
        bee.getBrain().setMemory(ModMemoryTypes.BEE_STUCK_TICKS, 0);
        bee.getBrain().eraseMemory(ModMemoryTypes.BEE_LAST_PATH);
    }
}
