package com.evandev.modulation.mixin.vanilla.client;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaAnvilModule;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AnvilScreen.class, priority = 1500)
public class AnvilScreenMixin {

    @ModifyExpressionValue(method = "renderLabels", at = @At(value = "CONSTANT", args = "intValue=40"), require = 0)
    private int modulation$hideTooExpensiveText(int constant) {
        if (ModuleManager.isEnabled("vanilla_anvil", VanillaAnvilModule.class, VanillaAnvilModule::isRemoveAnvilLimitEnabled)) {
            return Integer.MAX_VALUE;
        }
        return constant;
    }
}