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

import java.io.File;
//? if >=26.1 {
/*import com.evandev.modulation.Constants;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Screenshot;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
*///?}

public final class PanoramaScreenshot {

    //? if >=26.1 {
    /*public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "main"));
    private static final int MAX_SUPERSAMPLED_SIZE = 4096;
    private static final int MAX_DOWNSCALE = 4;
    *///?} else {
    public static final String CATEGORY = "key.categories.modulation";
    //?}

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
            key = new KeyMapping("key.modulation.panorama_screenshot", InputConstants.KEY_F4, CATEGORY);
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
            //? if >=26.1 {
            /*Component result = grab(minecraft, minecraft.gameDirectory, resolution);
            *///?} else {
            Component result = minecraft.grabPanoramixScreenshot(minecraft.gameDirectory, resolution, resolution);
            //?}
            //? if >=26.1 {
            /*minecraft.gui.getChat().addClientSystemMessage(isFailure(result) ? result : successMessage(minecraft.gameDirectory));
            *///?} else {
            minecraft.gui.getChat().addMessage(isFailure(result) ? result : successMessage(minecraft.gameDirectory));
            //?}
        }
    }

    //? if >=26.1 {
    /*private static Component grab(Minecraft minecraft, File gameDirectory, int resolution) {
        int downscale = Math.max(1, Math.min(MAX_DOWNSCALE, MAX_SUPERSAMPLED_SIZE / resolution));
        int size = resolution * downscale;
        Window window = minecraft.getWindow();
        int originalWidth = window.getWidth();
        int originalHeight = window.getHeight();
        //? if >=26.2 {
        /^RenderTarget target = minecraft.gameRenderer.mainRenderTarget();
        ^///?} else {
        RenderTarget target = minecraft.getMainRenderTarget();
        //?}
        LocalPlayer player = minecraft.player;
        float xRot = player.getXRot();
        float yRot = player.getYRot();
        float xRotO = player.xRotO;
        float yRotO = player.yRotO;
        //? if >=26.2 {
        /^Camera camera = minecraft.gameRenderer.mainCamera();
        ^///?} else {
        Camera camera = minecraft.gameRenderer.getMainCamera();
        //?}
        minecraft.gameRenderer.setRenderBlockOutline(false);

        try {
            camera.enablePanoramicMode();
            window.setWidth(size);
            window.setHeight(size);
            target.resize(size, size);

            for (int face = 0; face < 6; face++) {
                switch (face) {
                    case 0 -> {
                        player.setYRot(yRot);
                        player.setXRot(0.0F);
                    }
                    case 1 -> {
                        player.setYRot((yRot + 90.0F) % 360.0F);
                        player.setXRot(0.0F);
                    }
                    case 2 -> {
                        player.setYRot((yRot + 180.0F) % 360.0F);
                        player.setXRot(0.0F);
                    }
                    case 3 -> {
                        player.setYRot((yRot - 90.0F) % 360.0F);
                        player.setXRot(0.0F);
                    }
                    case 4 -> {
                        player.setYRot(yRot);
                        player.setXRot(-90.0F);
                    }
                    default -> {
                        player.setYRot(yRot);
                        player.setXRot(90.0F);
                    }
                }

                player.yRotO = player.getYRot();
                player.xRotO = player.getXRot();
                //? if >=26.2 {
                /^minecraft.gameRenderer.update(DeltaTracker.ONE);
                ^///?} else {
                minecraft.gameRenderer.update(DeltaTracker.ONE, true);
                //?}
                minecraft.gameRenderer.extract(DeltaTracker.ONE, true);
                //? if >=26.3 {
                /^minecraft.gameRenderer.renderLevel();
                ^///?} else {
                minecraft.gameRenderer.renderLevel(DeltaTracker.ONE);
                //?}
                Screenshot.grab(gameDirectory, "panorama_" + face + ".png", target, downscale, message -> {
                });
            }

            return Component.translatable("screenshot.success", Component.literal(gameDirectory.getName()));
        } catch (Exception exception) {
            Constants.LOG.error("Couldn't save panorama", exception);
            return Component.translatable(FAILURE_KEY, exception.getMessage());
        } finally {
            player.setXRot(xRot);
            player.setYRot(yRot);
            player.xRotO = xRotO;
            player.yRotO = yRotO;
            minecraft.gameRenderer.setRenderBlockOutline(true);
            window.setWidth(originalWidth);
            window.setHeight(originalHeight);
            target.resize(originalWidth, originalHeight);
            camera.disablePanoramicMode();
        }
    }
    *///?}

    private static boolean isFailure(Component result) {
        return result.getContents() instanceof TranslatableContents contents && FAILURE_KEY.equals(contents.getKey());
    }

    private static Component successMessage(File gameDirectory) {
        String screenshotDir = new File(gameDirectory, "screenshots").getAbsolutePath();
        Component link = Component.literal(PANORAMA_NAMES)
                .withStyle(ChatFormatting.UNDERLINE)
                //? if >=26.1 {
                /*.withStyle(style -> style.withClickEvent(new ClickEvent.OpenFile(screenshotDir)));
                *///?} else {
                .withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, screenshotDir)));
                //?}
        return Component.translatable("screenshot.success", link);
    }
}
