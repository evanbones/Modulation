package com.evandev.modulation.mixin.vanilla.client;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaGuiModule;
import com.evandev.modulation.util.Markdown;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {

    @ModifyConstant(method = "getTimeFactor", constant = @Constant(doubleValue = 200.0D))
    private static double modulation$modifyFadeDuration(double duration) {
        VanillaGuiModule module = ModuleManager.getModule("vanilla_gui", VanillaGuiModule.class);
        if (module == null) return duration;
        return module.getChatMessageDuration();
    }

    @ModifyConstant(method = "render", constant = @Constant(intValue = 200))
    private int modulation$modifyChatDuration(int duration) {
        VanillaGuiModule module = ModuleManager.getModule("vanilla_gui", VanillaGuiModule.class);
        if (module == null) return duration;
        return module.getChatMessageDuration();
    }

    @ModifyVariable(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("HEAD"), argsOnly = true)
    private Component modulation$applyChatMarkdown(Component message) {
        if (message != null && ModuleManager.isEnabled("vanilla_gui", VanillaGuiModule.class, VanillaGuiModule::isChatMarkdownEnabled)) {
            if (message.getContents() instanceof TranslatableContents translatable && "chat.type.text".equals(translatable.getKey())) {
                Object[] args = translatable.getArgs();
                boolean anyMatch = false;
                for (int i = 1; i < args.length; i++) {
                    if (args[i] instanceof Component comp) {
                        String astr = comp.getString();
                        String bstr = Markdown.convert(astr);
                        if (!astr.equals(bstr)) {
                            args[i] = Component.literal(bstr);
                            anyMatch = true;
                        }
                    } else if (args[i] instanceof String str) {
                        String bstr = Markdown.convert(str);
                        if (!str.equals(bstr)) {
                            args[i] = Component.literal(bstr);
                            anyMatch = true;
                        }
                    }
                }
                if (anyMatch) {
                    return Component.translatable(translatable.getKey(), args);
                }
            }
        }
        return message;
    }
}
