package com.evandev.modulation.client.cursor;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.mixin.vanilla.accessor.AbstractContainerScreenAccessor;
import com.evandev.modulation.mixin.vanilla.accessor.AbstractSelectionListAccessor;
import com.evandev.modulation.mixin.vanilla.accessor.CreativeModeInventoryScreenAccessor;
import com.evandev.modulation.mixin.vanilla.accessor.DeathScreenAccessor;
import com.evandev.modulation.modules.vanilla.VanillaGuiModule;
import com.evandev.modulation.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

public final class CursorFeedbackManager {

    private static final long[] HANDLES = new long[Shape.values().length];
    private static Shape current = Shape.DEFAULT;
    private CursorFeedbackManager() {
    }

    public static void update(Screen screen, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (!ModuleManager.isEnabled("vanilla_gui", VanillaGuiModule.class, VanillaGuiModule::isCursorFeedbackEnabled)
                || screen == null || mc.mouseHandler.isMouseGrabbed()) {
            reset();
            return;
        }

        apply(resolve(screen, mouseX, mouseY));
    }

    public static void reset() {
        apply(Shape.DEFAULT);
    }

    private static Shape resolve(Screen screen, int mouseX, int mouseY) {
        Style linkStyle = hoveredLinkStyle(screen, mouseX, mouseY);
        if (linkStyle != null && (linkStyle.getClickEvent() != null || linkStyle.getInsertion() != null)) {
            return Shape.POINTER;
        }

        if (screen instanceof CreativeModeInventoryScreen creativeScreen && hoveredCreativeTab(creativeScreen, mouseX, mouseY)) {
            return Shape.POINTER;
        }

        if (Services.PLATFORM.isModLoaded("emi")) {
            if (EmiCursorSupport.isHoveringSearchField(mouseX, mouseY)) {
                return Shape.TEXT;
            }
            AbstractWidget hoveredButton = EmiCursorSupport.hoveredButton(mouseX, mouseY);
            if (hoveredButton != null) {
                return hoveredButton.isActive() ? Shape.POINTER : Shape.NOT_ALLOWED;
            }
        }

        GuiEventListener hovered = findHovered(screen, mouseX, mouseY);
        return classify(hovered, mouseX);
    }

    private static boolean hoveredCreativeTab(CreativeModeInventoryScreen screen, int mouseX, int mouseY) {
        AbstractContainerScreenAccessor containerAccessor = (AbstractContainerScreenAccessor) screen;
        CreativeModeInventoryScreenAccessor tabAccessor = (CreativeModeInventoryScreenAccessor) screen;
        double relativeMouseX = mouseX - containerAccessor.getLeftPos();
        double relativeMouseY = mouseY - containerAccessor.getTopPos();
        for (CreativeModeTab tab : CreativeModeTabs.tabs()) {
            if (tabAccessor.modulation$checkTabClicked(tab, relativeMouseX, relativeMouseY)) {
                return true;
            }
        }
        return false;
    }

    private static Style hoveredLinkStyle(Screen screen, int mouseX, int mouseY) {
        if (screen instanceof ChatScreen) {
            return Minecraft.getInstance().gui.getChat().getClickedComponentStyleAt(mouseX, mouseY);
        } else if (screen instanceof BookViewScreen bookViewScreen) {
            return bookViewScreen.getClickedComponentStyleAt(mouseX, mouseY);
        } else if (screen instanceof DeathScreen deathScreen) {
            return ((DeathScreenAccessor) deathScreen).modulation$getClickedComponentStyleAt(mouseX);
        }
        return null;
    }

    private static GuiEventListener findHovered(ContainerEventHandler root, int mouseX, int mouseY) {
        for (GuiEventListener child : root.children()) {
            boolean over = child instanceof AbstractWidget widget
                    ? widget.visible && mouseX >= widget.getX() && mouseY >= widget.getY()
                    && mouseX < widget.getX() + widget.getWidth() && mouseY < widget.getY() + widget.getHeight()
                    : child.isMouseOver(mouseX, mouseY);
            if (over) {
                if (child instanceof ContainerEventHandler container) {
                    GuiEventListener deeper = findHovered(container, mouseX, mouseY);
                    return deeper != null ? deeper : child;
                }
                return child;
            }
        }
        return null;
    }

    private static Shape classify(GuiEventListener hovered, int mouseX) {
        if (hovered instanceof EditBox) {
            return Shape.TEXT;
        }
        if (hovered instanceof AbstractSelectionList<?> list) {
            AbstractSelectionListAccessor accessor = (AbstractSelectionListAccessor) list;
            int scrollbarX = accessor.modulation$getScrollbarPosition();
            if (accessor.modulation$scrollbarVisible() && mouseX >= scrollbarX && mouseX < scrollbarX + 6) {
                return Shape.VRESIZE;
            }
            return Shape.DEFAULT;
        }
        if (hovered instanceof AbstractWidget widget) {
            return widget.isActive() ? Shape.POINTER : Shape.NOT_ALLOWED;
        }
        return Shape.DEFAULT;
    }

    private static void apply(Shape shape) {
        if (shape == current) {
            return;
        }
        current = shape;
        long window = Minecraft.getInstance().getWindow().getWindow();
        GLFW.glfwSetCursor(window, shape == Shape.DEFAULT ? MemoryUtil.NULL : handle(shape));
    }

    private static long handle(Shape shape) {
        long handle = HANDLES[shape.ordinal()];
        if (handle == 0L) {
            handle = GLFW.glfwCreateStandardCursor(shape.glfwShape);
            HANDLES[shape.ordinal()] = handle;
        }
        return handle;
    }

    private enum Shape {
        DEFAULT(0),
        POINTER(GLFW.GLFW_POINTING_HAND_CURSOR),
        TEXT(GLFW.GLFW_IBEAM_CURSOR),
        NOT_ALLOWED(GLFW.GLFW_NOT_ALLOWED_CURSOR),
        VRESIZE(GLFW.GLFW_VRESIZE_CURSOR);

        final int glfwShape;

        Shape(int glfwShape) {
            this.glfwShape = glfwShape;
        }
    }
}
