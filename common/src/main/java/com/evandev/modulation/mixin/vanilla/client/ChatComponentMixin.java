package com.evandev.modulation.mixin.vanilla.client;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.VanillaModule;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {

    @ModifyConstant(method = "render", constant = @Constant(intValue = 200))
    private int modulation$modifyChatDuration(int duration) {
        VanillaModule module = (VanillaModule) ModuleManager.getModule("vanilla");
        if (module == null) return duration;
        return module.getChatMessageDuration();
    }

    @ModifyConstant(method = "getTimeFactor", constant = @Constant(doubleValue = 200.0D))
    private static double modulation$modifyFadeDuration(double duration) {
        VanillaModule module = (VanillaModule) ModuleManager.getModule("vanilla");
        if (module == null) return duration;
        return module.getChatMessageDuration();
    }
}
