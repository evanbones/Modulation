package com.evandev.modulation.client.compat;

import com.evandev.modulation.Constants;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.avatar.local.LocalAvatarFetcher;

import java.nio.file.Path;

public class FiguraClientHandler {

    public static void loadSkin(String skin) {
        try {
            Path p = LocalAvatarFetcher.getLocalAvatarDirectory().resolve(Path.of(skin));
            AvatarManager.loadLocalAvatar(p);
        } catch (Exception e) {
            Constants.LOG.error("Failed to load Figura avatar", e);
        }
    }

    public static void clearSkin() {
        try {
            AvatarManager.loadLocalAvatar(null);
        } catch (Exception e) {
            Constants.LOG.error("Failed to clear Figura avatar", e);
        }
    }
}