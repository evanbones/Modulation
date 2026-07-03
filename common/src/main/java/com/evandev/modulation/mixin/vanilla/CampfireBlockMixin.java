package com.evandev.modulation.mixin.vanilla;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.VanillaModule;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CampfireBlock.class)
public class CampfireBlockMixin {

    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
    private void modulation$placeCampfiresUnlit(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        VanillaModule module = (VanillaModule) ModuleManager.getModule("vanilla");
        if (module != null && module.isCampfiresPlaceUnlitEnabled()) {
            BlockState state = cir.getReturnValue();
            if (state != null && state.hasProperty(CampfireBlock.LIT)) {
                cir.setReturnValue(state.setValue(CampfireBlock.LIT, false));
            }
        }
    }
}