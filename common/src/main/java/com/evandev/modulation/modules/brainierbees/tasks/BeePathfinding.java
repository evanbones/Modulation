package com.evandev.modulation.modules.brainierbees.tasks;

import com.evandev.modulation.modules.brainierbees.BrainierBeesModule;
import com.evandev.modulation.modules.brainierbees.HiveHelper;
import com.evandev.modulation.registry.ModMemoryTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.Path;

import java.util.Map;

public class BeePathfinding extends Behavior<Bee> {

    private static final int MAX_PATH_AGE = 50;
    private static final int MAX_ATTEMPTS = 11;
    private static final int NECTAR_POLLINATING_COOLDOWN = 400;

    private final CachedPathHolder cachedPath = new CachedPathHolder();

    public BeePathfinding() {
        super(Map.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
    }

    private static void wander(Bee bee, CachedPathHolder holder) {
        if (!needsNewPath(bee, holder)) {
            bee.getNavigation().moveTo(holder.cachedPath, 1.0);
            holder.pathTimer++;
            return;
        }

        Level level = bee.level();
        BlockPos.MutableBlockPos target = new BlockPos.MutableBlockPos().set(bee.blockPosition());
        LevelChunk chunk = level.getChunkAt(target);
        int surface = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, target.getX(), target.getZ()) + 1;
        BlockPos home = HiveHelper.getHivePos(bee);
        int wanderRadius = BrainierBeesModule.MAX_WANDER_RADIUS.get();

        for (int attempt = 0; attempt < MAX_ATTEMPTS && bee.blockPosition().distManhattan(target) <= 5; attempt++) {
            boolean lowOrEnclosed = level.dimensionType().hasCeiling() || bee.getBlockY() <= surface + 3;
            target.set(bee.blockPosition()).move(
                    bee.getRandom().nextInt(21) - 10,
                    lowOrEnclosed ? bee.getRandom().nextInt(6) - 2 : bee.getRandom().nextInt(6) - 5,
                    bee.getRandom().nextInt(21) - 10
            );

            boolean openBelow = level.getBlockState(target.below(2)).isAir();
            boolean withinRange = home == null || target.closerThan(home, wanderRadius);
            if (!openBelow || !withinRange) {
                target.set(bee.blockPosition());
            }
        }

        Path newPath = bee.getNavigation().createPath(target, 1);
        bee.getNavigation().moveTo(newPath, 1.0);
        holder.cachedPath = newPath;
        holder.pathTimer = 0;
    }

    private static boolean needsNewPath(Bee bee, CachedPathHolder holder) {
        return holder.cachedPath == null
                || holder.pathTimer > MAX_PATH_AGE
                || (bee.getDeltaMovement().length() <= 0.05 && holder.pathTimer > 5)
                || bee.blockPosition().distManhattan(holder.cachedPath.getTarget()) <= 4;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Bee bee) {
        return (bee.getNavigation().isDone() && bee.getRandom().nextInt(10) == 0) || !HiveHelper.hasHive(bee);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Bee bee, long gameTime) {
        return bee.getNavigation().isInProgress() || !HiveHelper.hasHive(bee);
    }

    @Override
    protected void start(ServerLevel level, Bee bee, long gameTime) {
        wander(bee, this.cachedPath);
    }

    @Override
    protected void tick(ServerLevel level, Bee bee, long gameTime) {
        if (bee.hasNectar()) {
            bee.getBrain().setMemory(ModMemoryTypes.BEE_POLLINATING_COOLDOWN, NECTAR_POLLINATING_COOLDOWN);
        }
    }

    private static class CachedPathHolder {
        private Path cachedPath;
        private int pathTimer;
    }
}
