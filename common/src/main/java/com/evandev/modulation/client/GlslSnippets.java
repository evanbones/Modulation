package com.evandev.modulation.client;

import com.evandev.modulation.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class GlslSnippets {

    private static final String ROOT = "assets/modulation/shaders/";

    private static final Map<String, String> CACHE = new ConcurrentHashMap<>();

    private GlslSnippets() {
    }

    public static String load(String name) {
        return CACHE.computeIfAbsent(name, GlslSnippets::read);
    }

    private static String read(String name) {
        String path = ROOT + name;

        try (InputStream stream = GlslSnippets.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                Constants.LOG.error("Missing bundled shader snippet {}", path);
                return "";
            }

            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            Constants.LOG.error("Failed to read bundled shader snippet {}", path, exception);
            return "";
        }
    }
}
