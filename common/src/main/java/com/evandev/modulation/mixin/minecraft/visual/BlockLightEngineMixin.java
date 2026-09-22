package com.evandev.modulation.mixin.minecraft.visual;

import com.evandev.modulation.modules.vanilla.smoothlight.LightEngineLevelAccess;
import com.evandev.modulation.modules.vanilla.smoothlight.LightTransitions;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.BlockLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockLightEngine.class)
public abstract class BlockLightEngineMixin implements LightEngineLevelAccess {

    @Inject(method = "getEmission", at = @At("RETURN"), cancellable = true)
    private void modulation$transitionEmission(long pos, BlockState state, CallbackInfoReturnable<Integer> cir) {
        int actual = cir.getReturnValueI();
        int value = LightTransitions.emission(this.modulation$lightEngineLevel(), pos, actual);
        if (value != actual) cir.setReturnValue(value);
    }
}
