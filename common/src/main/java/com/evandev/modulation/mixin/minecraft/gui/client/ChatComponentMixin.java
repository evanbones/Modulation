package com.evandev.modulation.mixin.minecraft.gui.client;

import com.evandev.modulation.modules.vanilla.VanillaGuiModule;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {

    @ModifyConstant(method = "getTimeFactor", constant = @Constant(doubleValue = 200.0D))
    private static double modulation$modifyFadeDuration(double duration) {
        return VanillaGuiModule.CHAT_MESSAGE_DURATION.get();
    }

    @ModifyConstant(method = "render", constant = @Constant(intValue = 200))
    private int modulation$modifyChatDuration(int duration) {
        return VanillaGuiModule.CHAT_MESSAGE_DURATION.get();
    }
}
