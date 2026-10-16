package com.evandev.modulation.client;

import net.minecraft.client.Minecraft;

public class ClientCommandHelper {
    public static void forward(String command) {
        if (Minecraft.getInstance().getConnection() != null) {
            Minecraft.getInstance().getConnection().sendCommand(command);
        }
    }
}