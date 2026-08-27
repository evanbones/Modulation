package com.evandev.modulation.mixin.farmersdelight;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.farmersdelight.FarmersDelightModule;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.AbstractStoveBlock;

@Mixin(AbstractStoveBlock.class)
public class AbstractStoveBlockMixin {

    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
    private void modulation$placeStovesUnlit(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        if (ModuleManager.isEnabled("farmers_delight", FarmersDelightModule.class, FarmersDelightModule::isStovesPlaceUnlitEnabled)) {
            BlockState state = cir.getReturnValue();
            if (state != null && state.hasProperty(AbstractStoveBlock.LIT)) {
                cir.setReturnValue(state.setValue(AbstractStoveBlock.LIT, false));
            }
        }
    }
}
