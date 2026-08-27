package com.evandev.modulation.mixin.vanilla;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShovelItem.class)
public class ShovelItemMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void modulation$handleShovelUseOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isDisablePathCreationEnabled)) {
            BlockState state = context.getLevel().getBlockState(context.getClickedPos());
            if (!(state.getBlock() instanceof CampfireBlock)) {
                cir.setReturnValue(InteractionResult.PASS);
            }
        }
    }
}
