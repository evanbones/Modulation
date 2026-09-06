package com.evandev.modulation.compat.dyedflames;

import fuzs.dyedflames.init.ModRegistry;
import fuzs.dyedflames.world.level.block.FireType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DyedFlamesCompat {

    public static void onCampfireInside(Entity entity, BlockState state) {
        if (entity.isOnFire() || entity.getRemainingFireTicks() > 0) {
            Block sourceBlock = getFireSourceBlock(state);
            ModRegistry.LAST_FIRE_SOURCE_ATTACHMENT_TYPE.set(entity, sourceBlock);
            if (entity instanceof Player) {
                ModRegistry.WAS_PLAYER_ON_FIRE_ATTACHMENT_TYPE.set(entity, true);
            }
        }
    }

    public static Block getFireSourceBlock(BlockState state) {
        Block block = state.getBlock();
        if (FireType.getFireType(block).isPresent()) {
            return block;
        }
        if (state.is(Blocks.SOUL_CAMPFIRE) || block.getDescriptionId().contains("soul_campfire")) {
            return Blocks.SOUL_FIRE;
        }
        return Blocks.FIRE;
    }
}
