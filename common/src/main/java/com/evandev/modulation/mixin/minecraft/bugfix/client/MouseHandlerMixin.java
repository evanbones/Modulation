package com.evandev.modulation.mixin.minecraft.bugfix.client;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private static GuiEventListener modulation$getFocusedWidget(Screen screen) {
        ComponentPath path = screen.getCurrentFocusPath();
        if (path == null) {
            return null;
        }
        while (path instanceof ComponentPath.Path p) {
            path = p.childPath();
        }
        return path.component();
    }

    @Inject(
            method = "onPress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            )
    )
    private void modulation$afterMouseClicked(long window, int button, int action, int mods, CallbackInfo ci) {
        if (this.minecraft.screen != null && ModuleManager.isEnabled("vanilla_bugfixes", VanillaBugfixesModule.class, VanillaBugfixesModule::isFixFocusBugEnabled)) {
            if (modulation$getFocusedWidget(this.minecraft.screen) instanceof AbstractButton) {
                this.minecraft.screen.clearFocus();
            }
        }
    }

    @Inject(
            method = "onPress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    private void modulation$afterMouseReleased(long window, int button, int action, int mods, CallbackInfo ci) {
        if (this.minecraft.screen != null && ModuleManager.isEnabled("vanilla_bugfixes", VanillaBugfixesModule.class, VanillaBugfixesModule::isFixFocusBugEnabled)) {
            if (modulation$getFocusedWidget(this.minecraft.screen) instanceof AbstractSliderButton) {
                this.minecraft.screen.clearFocus();
            }
        }
    }
}
