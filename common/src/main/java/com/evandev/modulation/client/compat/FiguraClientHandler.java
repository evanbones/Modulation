package com.evandev.modulation.client.compat;

import com.evandev.modulation.Constants;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.lang.reflect.Method;

// TODO: Just use a mixin plugin with MixinSquared or something
public class FiguraClientHandler {

    public static void loadSkin(String skin) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.connection.sendCommand("figura load " + skin);
        }
    }

    public static void clearSkin() {
        try {
            Class<?> avatarManagerClass = Class.forName("org.figuramc.figura.avatar.AvatarManager");
            Method loadLocalAvatar = avatarManagerClass.getMethod("loadLocalAvatar", File.class);
            loadLocalAvatar.invoke(null, (File) null);
        } catch (Exception e) {
            Constants.LOG.error("Failed to clear Figura avatar", e);
        }
    }
}