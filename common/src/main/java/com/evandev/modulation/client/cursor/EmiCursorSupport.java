package com.evandev.modulation.client.cursor;

import com.evandev.modulation.mixin.emi.accessor.EmiScreenManagerAccessor;
import dev.emi.emi.screen.EmiScreenBase;
import dev.emi.emi.screen.EmiScreenManager;
import net.minecraft.client.gui.components.AbstractWidget;

final class EmiCursorSupport {

    private EmiCursorSupport() {
    }

    static boolean isHoveringSearchField(int mouseX, int mouseY) {
        if (!isEmiActive()) {
            return false;
        }
        return isOverWidget(EmiScreenManager.search, mouseX, mouseY);
    }

    static AbstractWidget hoveredButton(int mouseX, int mouseY) {
        if (!isEmiActive()) {
            return null;
        }

        if (isOverWidget(EmiScreenManager.emi, mouseX, mouseY)) {
            return EmiScreenManager.emi;
        }
        if (isOverWidget(EmiScreenManager.tree, mouseX, mouseY)) {
            return EmiScreenManager.tree;
        }

        for (EmiScreenManager.SidebarPanel panel : EmiScreenManagerAccessor.modulation$getPanels()) {
            if (isOverWidget(panel.cycle, mouseX, mouseY)) {
                return panel.cycle;
            }
            if (isOverWidget(panel.pageLeft, mouseX, mouseY)) {
                return panel.pageLeft;
            }
            if (isOverWidget(panel.pageRight, mouseX, mouseY)) {
                return panel.pageRight;
            }
        }

        return null;
    }

    private static boolean isEmiActive() {
        return !EmiScreenManager.isDisabled() && !EmiScreenBase.getCurrent().isEmpty();
    }

    private static boolean isOverWidget(AbstractWidget widget, int mouseX, int mouseY) {
        return widget.visible && mouseX >= widget.getX() && mouseY >= widget.getY()
                && mouseX < widget.getX() + widget.getWidth() && mouseY < widget.getY() + widget.getHeight();
    }
}
