package com.evandev.modulation.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class TargetHelper {

    public static boolean isLootContainerBlocked(Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof RandomizableContainer container) || container.getLootTable() == null) {
            return false;
        }
        return isPlayerTargeted(level, player);
    }

    public static boolean isPlayerTargeted(Level level, Player player) {
        if (player.isCreative() || player.isSpectator()) {
            return false;
        }
        double range = 16.0;
        AABB box = player.getBoundingBox().inflate(range);
        List<Mob> mobs = level.getEntitiesOfClass(Mob.class, box, mob -> mob.isAlive() && mob.getTarget() == player);
        return !mobs.isEmpty();
    }
}
