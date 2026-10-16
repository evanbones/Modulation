package com.evandev.modulation.mixin.minecraft.gui.client;

//? if >=26.1 {
/*import com.evandev.modulation.modules.vanilla.VanillaGuiModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.client.gui.components.ChatComponent$AlphaCalculator")
public interface ChatAlphaCalculatorMixin {

    @ModifyConstant(method = "lambda$timeBased$0", constant = @Constant(doubleValue = 200.0D))
    private static double modulation$modifyFadeDuration(double duration) {
        return VanillaGuiModule.CHAT_MESSAGE_DURATION.get();
    }
}
*///?}
