package com.evandev.modulation.modules.brainierbees.tasks;

import com.evandev.modulation.modules.brainierbees.BrainierBeesModule;
import com.evandev.modulation.registry.ModMemoryTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.pathfinder.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FindFlowerTask extends Behavior<Bee> {

    private static final UniformInt SEARCH_FAILURE_COOLDOWN = UniformInt.of(120, 240);

    private BlockPos targetFlower;

    public FindFlowerTask() {
        super(Map.of(ModMemoryTypes.BEE_POLLINATING_COOLDOWN, MemoryStatus.VALUE_ABSENT));
    }

    private static boolean shouldLookForFlowers(Bee bee) {
        return !bee.hasNectar()
                && bee.getBrain().getMemory(ModMemoryTypes.BEE_FLOWER_POS).isEmpty()
                && bee.getBrain().getMemory(ModMemoryTypes.BEE_POLLINATING_COOLDOWN).isEmpty()
                && !bee.getBrain().getMemory(ModMemoryTypes.BEE_WANTS_HIVE).orElse(false);
    }

    private static boolean isFlower(BlockState state) {
        return state.is(BlockTags.FLOWERS) && !state.hasProperty(BlockStateProperties.WATERLOGGED);
    }

    private static BlockPos findFlower(Bee bee, ServerLevel level) {
        int radius = BrainierBeesModule.flowerLocateRange();
        BlockPos origin = bee.blockPosition();

        List<BlockPos> candidates = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -radius; y <= radius; y++) {
                    BlockPos pos = origin.offset(x, y, z);
                    if (isFlower(level.getBlockState(pos))) {
                        candidates.add(pos);
                    }
                }
            }
        }

        if (candidates.isEmpty()) {
            bee.getBrain().setMemory(ModMemoryTypes.BEE_POLLINATING_COOLDOWN, SEARCH_FAILURE_COOLDOWN.sample(level.getRandom()));
            return null;
        }
        return candidates.get(bee.getRandom().nextInt(candidates.size()));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Bee bee) {
        return shouldLookForFlowers(bee);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Bee bee, long gameTime) {
        return shouldLookForFlowers(bee);
    }

    @Override
    protected void start(ServerLevel level, Bee bee, long gameTime) {
        BlockPos flowerPos = findFlower(bee, level);
        if (flowerPos != null && bee.getBrain().getMemory(ModMemoryTypes.BEE_FLOWER_POS).isEmpty()) {
            this.targetFlower = flowerPos;
        }
    }

    @Override
    protected void tick(ServerLevel level, Bee bee, long gameTime) {
        if (this.targetFlower == null) {
            return;
        }

        BlockPos flowerPos = this.targetFlower;
        BehaviorUtils.setWalkAndLookTargetMemories(bee, flowerPos, 0.4F, 1);

        Path path = bee.getNavigation().createPath(flowerPos, 1);
        if (path == null || !path.canReach()) {
            bee.getBrain().eraseMemory(ModMemoryTypes.BEE_FLOWER_POS);
            this.targetFlower = null;
            bee.getBrain().setMemory(ModMemoryTypes.BEE_POLLINATING_COOLDOWN, SEARCH_FAILURE_COOLDOWN.sample(level.getRandom()));
            return;
        }

        bee.getNavigation().moveTo(path, 0.6);
        if (bee.blockPosition().closerThan(flowerPos, 2) && isFlower(level.getBlockState(flowerPos))) {
            bee.getBrain().setMemory(ModMemoryTypes.BEE_FLOWER_POS, GlobalPos.of(level.dimension(), flowerPos));
        }
    }

    @Override
    protected void stop(ServerLevel level, Bee bee, long gameTime) {
        if (bee.getBrain().getMemory(ModMemoryTypes.BEE_POLLINATING_COOLDOWN).isEmpty() && bee.hasNectar()) {
            bee.getBrain().setMemory(ModMemoryTypes.BEE_POLLINATING_COOLDOWN, 100);
        }
    }
}
