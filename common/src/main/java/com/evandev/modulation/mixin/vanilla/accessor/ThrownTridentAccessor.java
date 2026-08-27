package com.evandev.modulation.mixin.vanilla.accessor;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.projectile.ThrownTrident;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ThrownTrident.class)
public interface ThrownTridentAccessor {
    @Accessor("ID_LOYALTY")
    static EntityDataAccessor<Byte> modulation$getIdLoyalty() {
        throw new AssertionError();
    }

    @Accessor("dealtDamage")
    void modulation$setDealtDamage(boolean dealtDamage);
}
