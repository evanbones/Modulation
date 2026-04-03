package com.evandev.modulation.mixin.minecraft;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.VanillaModule;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = AnvilScreen.class, priority = 1500)
public class AnvilScreenMixin {

    @ModifyConstant(method = "renderLabels", constant = @Constant(intValue = 40), require = 0)
    private int modulation$hideTooExpensiveText(int constant) {
        VanillaModule module = (VanillaModule) ModuleManager.getModule("vanilla");
        if (module != null && module.isRemoveAnvilLimitEnabled()) {
            return Integer.MAX_VALUE;
        }
        return constant;
    }
}