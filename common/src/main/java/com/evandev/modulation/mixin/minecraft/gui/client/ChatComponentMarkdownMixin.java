package com.evandev.modulation.mixin.minecraft.gui.client;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaGuiModule;
import com.evandev.modulation.util.Markdown;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ChatComponent.class, priority = 400)
public class ChatComponentMarkdownMixin {

    @ModifyVariable(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V", at = @At("HEAD"), argsOnly = true)
    private Component modulation$applyChatMarkdown(Component message) {
        if (message == null || !ModuleManager.isEnabled("vanilla_gui", VanillaGuiModule.class, VanillaGuiModule::isChatMarkdownEnabled)) {
            return message;
        }
        if (!(message.getContents() instanceof TranslatableContents translatable) || !"chat.type.text".equals(translatable.getKey())) {
            return message;
        }

        Object[] args = translatable.getArgs().clone();
        boolean anyMatch = false;
        for (int i = 1; i < args.length; i++) {
            String before;
            if (args[i] instanceof Component component) {
                before = component.getString();
            } else if (args[i] instanceof String string) {
                before = string;
            } else {
                continue;
            }
            String after = Markdown.convert(before);
            if (!before.equals(after)) {
                args[i] = Component.literal(after);
                anyMatch = true;
            }
        }

        if (!anyMatch) {
            return message;
        }
        return Component.translatable(translatable.getKey(), args).setStyle(message.getStyle());
    }
}
