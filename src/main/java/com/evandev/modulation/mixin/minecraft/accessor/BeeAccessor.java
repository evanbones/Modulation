package com.evandev.modulation.mixin.minecraft.accessor;

import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Bee.class)
public interface BeeAccessor {

    @Accessor("stayOutOfHiveCountdown")
    int modulation$getStayOutOfHiveCountdown();

    @Accessor("remainingCooldownBeforeLocatingNewFlower")
    void modulation$setRemainingCooldownBeforeLocatingNewFlower(int cooldown);

    @Invoker("getCropsGrownSincePollination")
    int invokeGetCropsGrownSincePollination();

    @Invoker("incrementNumCropsGrownSincePollination")
    void invokeIncrementNumCropsGrownSincePollination();

    @Invoker("setHasNectar")
    void invokeSetHasNectar(boolean hasNectar);
}
