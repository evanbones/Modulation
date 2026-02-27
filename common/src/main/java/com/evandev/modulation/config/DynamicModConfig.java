package com.evandev.modulation.config;

import com.evandev.modulation.Constants;
import com.evandev.modulation.api.AbstractTweak;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.evandev.modulation.platform.Services;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class DynamicModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = Services.PLATFORM.getConfigDirectory().resolve("modulation.json").toFile();

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            save();
            return;
        }
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            if (root == null) return;

            for (IModule module : ModuleManager.getModules()) {
                if (root.has(module.getId())) {
                    JsonObject moduleJson = root.getAsJsonObject(module.getId());
                    for (AbstractTweak<?> tweak : module.getTweaks()) {
                        if (moduleJson.has(tweak.getId())) {
                            parseValue(tweak, moduleJson);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Constants.LOG.error("Failed to load modulation.json", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static void parseValue(AbstractTweak<?> tweak, JsonObject json) {
        if (tweak instanceof BooleanTweak) {
            ((AbstractTweak<Boolean>) tweak).setValue(json.get(tweak.getId()).getAsBoolean());
        }
        // TODO: Add parsing for IntTweak, DoubleTweak, StringTweak...
        tweak.onApply();
    }

    public static void save() {
        JsonObject root = new JsonObject();
        for (IModule module : ModuleManager.getModules()) {
            JsonObject moduleJson = new JsonObject();
            for (AbstractTweak<?> tweak : module.getTweaks()) {
                if (tweak instanceof BooleanTweak booleanTweak) {
                    moduleJson.addProperty(tweak.getId(), booleanTweak.getValue());
                }
                // TODO: Add saving for IntTweak, etc...
            }
            root.add(module.getId(), moduleJson);
        }

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(root, writer);
        } catch (Exception e) {
            Constants.LOG.error("Failed to save modulation.json", e);
        }
    }
}