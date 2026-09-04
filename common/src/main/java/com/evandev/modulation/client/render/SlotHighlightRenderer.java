package com.evandev.modulation.client.render;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaGuiModule;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;

public final class SlotHighlightRenderer {
    private static final ResourceLocation BACK_SPRITE = ResourceLocation.withDefaultNamespace("container/slot_highlight_back");
    private static final ResourceLocation FRONT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot_highlight_front");
    private static final int FRONT_BLIT_OFFSET = 300;

    private SlotHighlightRenderer() {
    }

    public static boolean isEnabled() {
        return ModuleManager.isEnabled("vanilla_gui", VanillaGuiModule.class, VanillaGuiModule::isSlotHighlightBehindItemEnabled);
    }

    public static void renderBack(GuiGraphics guiGraphics, Slot slot) {
        blit(guiGraphics, BACK_SPRITE, slot.x, slot.y);
    }

    public static void renderFront(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, FRONT_BLIT_OFFSET);
        blit(guiGraphics, FRONT_SPRITE, x, y);
        guiGraphics.pose().popPose();
    }

    private static void blit(GuiGraphics guiGraphics, ResourceLocation sprite, int x, int y) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blitSprite(sprite, x - 4, y - 4, 24, 24);
        RenderSystem.disableBlend();
    }
}
