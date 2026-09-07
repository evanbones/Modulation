package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModAbsent("debugify")
@Mixin(MinecartCommandBlock.class)
public class MinecartCommandBlockMixin {

    @Shadow
    private int lastActivated;

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    private void modulation$readCommandCooldown(CompoundTag compound, CallbackInfo ci) {
        if (compound.contains("LastExecuted") && VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixCommandMinecartCooldownEnabled)) {
            lastActivated = compound.getInt("LastExecuted");
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    private void modulation$writeCommandCooldown(CompoundTag compound, CallbackInfo ci) {
        if (VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixCommandMinecartCooldownEnabled)) {
            compound.putInt("LastExecuted", lastActivated);
        }
    }
}
