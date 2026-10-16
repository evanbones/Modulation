package com.evandev.modulation.mixin.minecraft.accessor;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.FollowTemptation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;
import java.util.function.Function;

@Mixin(FollowTemptation.class)
public interface FollowTemptationAccessor {

    @Accessor("closeEnoughDistance")
    Function<LivingEntity, Double> modulation$getCloseEnoughDistance();

    @Invoker("getTemptingPlayer")
    Optional<Player> invokeGetTemptingPlayer(PathfinderMob mob);
}
