package com.evandev.modulation.mixin.minecraft;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.VanillaModule;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ContainerEventHandler.class)
public interface ContainerEventHandlerMixin extends ContainerEventHandler {

    /**
     * @author evandev
     * @reason The Annotation Processor blocks @Inject inside interfaces, so we have to @Overwrite the default method instead.
     */
    @Overwrite
    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (GuiEventListener listener : this.children()) {
            if (listener.mouseClicked(mouseX, mouseY, button)) {
                this.setFocused(listener);

                VanillaModule module = (VanillaModule) ModuleManager.getModule("vanilla");
                if (module != null && module.isFixFocusBugEnabled()) {
                    if (this.getFocused() instanceof AbstractButton) {
                        this.setFocused(null);
                    }
                }

                if (button == 0) {
                    this.setDragging(true);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * @author evandev
     * @reason The Annotation Processor blocks @Inject inside interfaces, so we have to @Overwrite the default method instead.
     */
    @Overwrite
    default boolean mouseReleased(double mouseX, double mouseY, int button) {
        VanillaModule module = (VanillaModule) ModuleManager.getModule("vanilla");
        if (module != null && module.isFixFocusBugEnabled()) {
            if (this.getFocused() instanceof AbstractSliderButton) {
                this.setFocused(null);
            }
        }

        this.setDragging(false);
        return this.getChildAt(mouseX, mouseY).filter(listener -> listener.mouseReleased(mouseX, mouseY, button)).isPresent();
    }
}