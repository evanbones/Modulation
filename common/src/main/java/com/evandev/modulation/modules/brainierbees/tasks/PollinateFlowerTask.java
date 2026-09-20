package com.evandev.modulation.modules.brainierbees.tasks;

import com.evandev.modulation.mixin.minecraft.accessor.BeeAccessor;
import com.evandev.modulation.modules.brainierbees.BeeBrain;
import com.evandev.modulation.registry.ModMemoryTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.Optional;

public class PollinateFlowerTask extends Behavior<Bee> {

    private static final int MAX_POLLINATING_TICKS = 600;
    private static final int POLLINATION_COMPLETE_TICKS = 400;
    private static final int COOLDOWN_BEFORE_LOCATING_NEW_FLOWER = 200;

    private int lastSoundPlayedTick;
    private Vec3 hoverPos;

    public PollinateFlowerTask() {
        super(Map.of(ModMemoryTypes.BEE_FLOWER_POS, MemoryStatus.VALUE_PRESENT));
    }

    private static boolean shouldPollinate(Bee bee) {
        return bee.getBrain().getMemory(ModMemoryTypes.BEE_FLOWER_POS).isPresent()
                && bee.getBrain().getMemory(ModMemoryTypes.BEE_POLLINATING_COOLDOWN).isEmpty()
                && !bee.getBrain().getMemory(ModMemoryTypes.BEE_WANTS_HIVE).orElse(false);
    }

    private static boolean hasPollinatedLongEnough(Bee bee) {
        return bee.getBrain().getMemory(ModMemoryTypes.BEE_SUCCESSFUL_POLLINATING_TICKS).orElse(0) > POLLINATION_COMPLETE_TICKS;
    }

    private static float getOffset(Bee bee) {
        return (bee.getRandom().nextFloat() * 2.0F - 1.0F) * 0.33333334F;
    }

    private static void clearFlowerMemory(Brain<?> brain) {
        brain.eraseMemory(ModMemoryTypes.BEE_FLOWER_POS);
        brain.setMemory(ModMemoryTypes.BEE_POLLINATING_TICKS, 0);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Bee bee) {
        return shouldPollinate(bee);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Bee bee, long gameTime) {
        return shouldPollinate(bee);
    }

    @Override
    protected void start(ServerLevel level, Bee bee, long gameTime) {
        this.lastSoundPlayedTick = 0;
        bee.resetTicksWithoutNectarSinceExitingHive();
    }

    @Override
    protected void stop(ServerLevel level, Bee bee, long gameTime) {
        if (hasPollinatedLongEnough(bee)) {
            ((BeeAccessor) bee).invokeSetHasNectar(true);
            bee.getBrain().eraseMemory(ModMemoryTypes.BEE_FLOWER_POS);
            bee.getBrain().setMemory(ModMemoryTypes.BEE_SUCCESSFUL_POLLINATING_TICKS, 0);
            bee.getBrain().setMemory(ModMemoryTypes.BEE_POLLINATING_COOLDOWN, POLLINATION_COMPLETE_TICKS);
        }

        bee.getNavigation().stop();
        ((BeeAccessor) bee).modulation$setRemainingCooldownBeforeLocatingNewFlower(COOLDOWN_BEFORE_LOCATING_NEW_FLOWER);
    }

    @Override
    protected void tick(ServerLevel level, Bee bee, long gameTime) {
        Brain<?> brain = bee.getBrain();
        BeeBrain.incrementMemory(brain, ModMemoryTypes.BEE_POLLINATING_TICKS);

        if (brain.getMemory(ModMemoryTypes.BEE_POLLINATING_TICKS).orElse(0) > MAX_POLLINATING_TICKS) {
            clearFlowerMemory(brain);
            return;
        }

        Optional<GlobalPos> flowerPosOpt = brain.getMemory(ModMemoryTypes.BEE_FLOWER_POS);
        if (flowerPosOpt.isEmpty()) {
            return;
        }

        BlockPos flowerPos = flowerPosOpt.get().pos();
        if (!level.getBlockState(flowerPos).is(BlockTags.FLOWERS)) {
            clearFlowerMemory(brain);
            return;
        }

        Vec3 flowerCenter = Vec3.atBottomCenterOf(flowerPos).add(0.0, 0.6F, 0.0);
        if (flowerCenter.distanceTo(bee.position()) > 1.0) {
            this.hoverPos = flowerCenter;
            setWantedPos(bee);
            return;
        }

        if (this.hoverPos == null) {
            this.hoverPos = flowerCenter;
        }

        boolean updatePos = true;
        if (bee.position().distanceTo(this.hoverPos) <= 0.1) {
            if (bee.getRandom().nextInt(25) == 0) {
                this.hoverPos = new Vec3(flowerCenter.x() + getOffset(bee), flowerCenter.y(), flowerCenter.z() + getOffset(bee));
                bee.getNavigation().stop();
            } else {
                updatePos = false;
            }
            bee.getLookControl().setLookAt(flowerCenter.x(), flowerCenter.y(), flowerCenter.z());
        }

        if (updatePos) {
            setWantedPos(bee);
        }

        BeeBrain.incrementMemory(brain, ModMemoryTypes.BEE_SUCCESSFUL_POLLINATING_TICKS);
        int successTicks = brain.getMemory(ModMemoryTypes.BEE_SUCCESSFUL_POLLINATING_TICKS).orElse(0);
        if (bee.getRandom().nextFloat() < 0.05F && successTicks > this.lastSoundPlayedTick + 59) {
            this.lastSoundPlayedTick = successTicks;
            bee.playSound(SoundEvents.BEE_POLLINATE, 1.0F, 1.0F);
        }
    }

    private void setWantedPos(Bee bee) {
        bee.getMoveControl().setWantedPosition(this.hoverPos.x(), this.hoverPos.y(), this.hoverPos.z(), 0.35F);
    }
}
