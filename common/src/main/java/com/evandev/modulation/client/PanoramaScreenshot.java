package com.evandev.modulation.client;

import com.evandev.modulation.modules.vanilla.VanillaGuiModule;
import com.evandev.modulation.platform.Services;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public final class PanoramaScreenshot {

    public static final String CATEGORY = "key.categories.modulation";

    private static final String CONFLICTING_MOD = "panorama_screenshot";
    private static final String PANORAMA_NAMES = "panorama_0.png - panorama_5.png";
    private static final String FAILURE_KEY = "screenshot.failure";

    private static KeyMapping key;

    private PanoramaScreenshot() {
    }

    public static boolean isAvailable() {
        return !Services.PLATFORM.isModLoaded(CONFLICTING_MOD);
    }

    public static KeyMapping key() {
        if (key == null) {
            key = new KeyMapping("key.modulation.panorama_screenshot", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F4, CATEGORY);
        }
        return key;
    }

    public static void onClientTick(Minecraft minecraft) {
        if (key == null) {
            return;
        }
        while (key.consumeClick()) {
            if (!VanillaGuiModule.PANORAMA_SCREENSHOT.on()) {
                continue;
            }
            if (minecraft.player == null || minecraft.level == null || minecraft.screen != null) {
                continue;
            }
            int resolution = VanillaGuiModule.PANORAMA_RESOLUTION.get();
            Component result = minecraft.grabPanoramixScreenshot(minecraft.gameDirectory, resolution, resolution);
            minecraft.gui.getChat().addMessage(isFailure(result) ? result : successMessage(minecraft.gameDirectory));
        }
    }

    private static boolean isFailure(Component result) {
        return result.getContents() instanceof TranslatableContents contents && FAILURE_KEY.equals(contents.getKey());
    }

    private static Component successMessage(File gameDirectory) {
        String screenshotDir = new File(gameDirectory, "screenshots").getAbsolutePath();
        Component link = Component.literal(PANORAMA_NAMES)
                .withStyle(ChatFormatting.UNDERLINE)
                .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, screenshotDir)));
        return Component.translatable("screenshot.success", link);
    }
}
