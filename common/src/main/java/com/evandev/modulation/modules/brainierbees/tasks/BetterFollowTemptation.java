package com.evandev.modulation.modules.brainierbees.tasks;

import com.evandev.modulation.mixin.minecraft.accessor.FollowTemptationAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.function.Function;

public class BetterFollowTemptation extends FollowTemptation {

    public BetterFollowTemptation(Function<LivingEntity, Float> speedModifier) {
        super(speedModifier);
    }

    @Override
    protected void tick(ServerLevel level, PathfinderMob mob, long gameTime) {
        FollowTemptationAccessor accessor = (FollowTemptationAccessor) this;
        Optional<Player> temptingPlayer = accessor.invokeGetTemptingPlayer(mob);
        if (temptingPlayer.isEmpty()) {
            return;
        }

        Player player = temptingPlayer.get();
        Brain<?> brain = mob.getBrain();
        brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(player, true));

        BlockPos playerPos = player.blockPosition();
        double closeEnough = accessor.modulation$getCloseEnoughDistance().apply(mob);
        boolean blockedAbove = !level.getBlockState(playerPos.above()).isAir();

        if (mob.distanceToSqr(player) < Mth.square(closeEnough) || blockedAbove) {
            brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        } else {
            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(playerPos.above(2), this.getSpeedModifier(mob), 1));
        }
    }
}
