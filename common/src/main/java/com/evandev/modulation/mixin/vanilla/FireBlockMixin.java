package com.evandev.modulation.mixin.vanilla;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.VanillaModule;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireBlock.class)
public class FireBlockMixin {

    @Inject(method = "getIgniteOdds*", at = @At("HEAD"), cancellable = true)
    private void modulation$flammableCobwebsIgniteOdds(BlockState state, CallbackInfoReturnable<Integer> cir) {
        VanillaModule module = (VanillaModule) ModuleManager.getModule("vanilla");
        if (module != null && module.isFlammableCobwebsEnabled() && state.is(Blocks.COBWEB)) {
            cir.setReturnValue(60);
        }
    }

    @Inject(method = "getBurnOdds", at = @At("HEAD"), cancellable = true)
    private void modulation$flammableCobwebsBurnOdds(BlockState state, CallbackInfoReturnable<Integer> cir) {
        VanillaModule module = (VanillaModule) ModuleManager.getModule("vanilla");
        if (module != null && module.isFlammableCobwebsEnabled() && state.is(Blocks.COBWEB)) {
            cir.setReturnValue(100);
        }
    }
}