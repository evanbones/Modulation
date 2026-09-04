package com.evandev.modulation.mixin.polytone.client;

import com.evandev.modulation.Constants;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.shaders.EffectProgram;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(EffectProgram.class)
public abstract class EffectProgramMixin {

    @Unique
    private static final String modulation$SUNBATHING_PREFIX = "sunbathing:";

    @Unique
    private static final Pattern modulation$SKY_TEST = Pattern.compile("step\\(\\s*0\\.9{4,}\\s*,\\s*depth\\s*\\)");

    @Unique
    private static final Pattern modulation$VERSION_LINE = Pattern.compile("^\\s*#version[^\\r\\n]*", Pattern.MULTILINE);

    @Unique
    private static final String modulation$HELPER_DECLARATION = """
            
            uniform vec2 ModulationFogRange;
            uniform float ModulationSkyVisibility;
            uniform float ModulationSkyHidden;
            float modulation_skyness(float d);
            """;

    @Unique
    private static final String modulation$HELPER_DEFINITION = """
            
            float modulation_skyness(float d) {
                float visible = 1.0 - clamp(ModulationSkyHidden, 0.0, 1.0);
                if (visible <= 0.0) {
                    return 0.0;
                }
                float raw = step(0.999999, d);
                float fogStart = ModulationFogRange.x;
                float fogEnd = ModulationFogRange.y;
                if (ModulationSkyVisibility <= 0.0 || fogEnd <= fogStart || fogEnd <= 0.0) {
                    return visible * raw;
                }
                float a = PolyProjMat[2][2];
                float b = PolyProjMat[3][2];
                float viewZ = b / ((d * 2.0 - 1.0) + a);
                vec2 ndc = texCoord * 2.0 - 1.0;
                float tx = ndc.x / PolyProjMat[0][0];
                float ty = ndc.y / PolyProjMat[1][1];
                float dist = viewZ * sqrt(1.0 + tx * tx + ty * ty);
                return visible * max(raw, ModulationSkyVisibility * smoothstep(fogStart, fogEnd, dist));
            }
            """;

    @ModifyVariable(method = "compileShader", at = @At("HEAD"), argsOnly = true)
    private static InputStream modulation$softenSunbathingSkyMask(InputStream original, @Local(argsOnly = true, ordinal = 0) String name) {
        if (name == null || !name.startsWith(modulation$SUNBATHING_PREFIX)) {
            return original;
        }

        String source;
        try {
            source = new String(original.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            return original;
        }

        String patched = modulation$patch(source);
        if (patched == null) {
            Constants.LOG.info("No sky depth test found in shader {}, leaving it untouched", name);
            return new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8));
        }

        return new ByteArrayInputStream(patched.getBytes(StandardCharsets.UTF_8));
    }

    @Unique
    private static String modulation$patch(String source) {
        if (!source.contains("PolyProjMat")) {
            return null;
        }

        Matcher skyTest = modulation$SKY_TEST.matcher(source);
        if (!skyTest.find()) {
            return null;
        }

        String replaced = skyTest.replaceAll("modulation_skyness(depth)");

        Matcher versionLine = modulation$VERSION_LINE.matcher(replaced);
        if (!versionLine.find()) {
            return null;
        }

        return replaced.substring(0, versionLine.end())
                + modulation$HELPER_DECLARATION
                + replaced.substring(versionLine.end())
                + modulation$HELPER_DEFINITION;
    }
}
