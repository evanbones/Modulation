package com.evandev.modulation.mixin.vanilla.client;

import com.evandev.modulation.client.cursor.CursorFeedbackManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftCursorMixin {

    @Inject(method = "setScreen", at = @At("TAIL"))
    private void modulation$resetCursorOnScreenClose(Screen screen, CallbackInfo ci) {
        if (screen == null) {
            CursorFeedbackManager.reset();
        }
    }
}
