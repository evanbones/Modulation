package com.evandev.modulation.client.render;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaGuiModule;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

public final class SlotHighlightRenderer {
    private static final ResourceLocation BACK_SPRITE = ResourceLocation.withDefaultNamespace("container/slot_highlight_back");
    private static final ResourceLocation FRONT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot_highlight_front");
    private static final int FRONT_BLIT_OFFSET = 300;
    private static final List<Predicate<Slot>> DEFERRED_BACK_SLOTS = new CopyOnWriteArrayList<>();

    private static Slot deferredBackSlot;

    private SlotHighlightRenderer() {
    }

    public static boolean isEnabled() {
        return ModuleManager.isEnabled("vanilla_gui", VanillaGuiModule.class, VanillaGuiModule::isSlotHighlightBehindItemEnabled);
    }

    public static void deferBackFor(Predicate<Slot> predicate) {
        DEFERRED_BACK_SLOTS.add(predicate);
    }

    public static void renderBack(GuiGraphics guiGraphics, Slot slot) {
        if (isBackDeferred(slot)) {
            deferredBackSlot = slot;
            return;
        }
        blit(guiGraphics, BACK_SPRITE, slot.x, slot.y, 0);
    }

    public static void renderBack(GuiGraphics guiGraphics, int x, int y) {
        renderBack(guiGraphics, x, y, 0);
    }

    public static void renderBack(GuiGraphics guiGraphics, int x, int y, int z) {
        blit(guiGraphics, BACK_SPRITE, x, y, z);
    }

    public static void renderDeferredBack(GuiGraphics guiGraphics, Slot slot) {
        if (deferredBackSlot != slot) return;
        deferredBackSlot = null;
        blit(guiGraphics, BACK_SPRITE, slot.x, slot.y, 0);
    }

    public static void renderFront(GuiGraphics guiGraphics, int x, int y) {
        blit(guiGraphics, FRONT_SPRITE, x, y, FRONT_BLIT_OFFSET);
    }

    private static boolean isBackDeferred(Slot slot) {
        for (Predicate<Slot> predicate : DEFERRED_BACK_SLOTS) {
            if (predicate.test(slot)) return true;
        }
        return false;
    }

    private static void blit(GuiGraphics guiGraphics, ResourceLocation sprite, int x, int y, int z) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, z);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blitSprite(sprite, x - 4, y - 4, 24, 24);
        RenderSystem.disableBlend();
        guiGraphics.pose().popPose();
    }
}
