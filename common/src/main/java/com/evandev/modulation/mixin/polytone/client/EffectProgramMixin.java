package com.evandev.modulation.mixin.polytone.client;

import com.evandev.modulation.Constants;
import com.evandev.modulation.client.GlslSnippets;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.EffectProgram;
import com.mojang.blaze3d.shaders.Program;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@IfModLoaded("polytone")
@Mixin(EffectProgram.class)
public abstract class EffectProgramMixin {

    @Unique
    private static final String modulation$GODRAYS_SHADER = "sunbathing:godrays";

    @Unique
    private static final Pattern modulation$SKY_TEST = Pattern.compile("(?<!-\\s{0,8})step\\(\\s*0\\.9{4,}\\s*,\\s*depth\\s*\\)");

    @Unique
    private static final Pattern modulation$VERSION_LINE = Pattern.compile("^\\s*#version[^\\r\\n]*", Pattern.MULTILINE);

    @WrapOperation(
            method = "compileShader",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/shaders/EffectProgram;compileShaderInternal(Lcom/mojang/blaze3d/shaders/Program$Type;Ljava/lang/String;Ljava/io/InputStream;Ljava/lang/String;Lcom/mojang/blaze3d/preprocessor/GlslPreprocessor;)I"
            )
    )
    private static int modulation$softenSunbathingSkyMask(Program.Type type, String name, InputStream shaderData, String sourceName, GlslPreprocessor preprocessor, Operation<Integer> original) {
        if (!modulation$GODRAYS_SHADER.equals(name)
                || !VanillaBugfixesModule.enabled(VanillaBugfixesModule::isPatchSunbathingGodraysEnabled)) {
            return original.call(type, name, shaderData, sourceName, preprocessor);
        }

        byte[] source;
        try {
            source = shaderData.readAllBytes();
        } catch (IOException exception) {
            return original.call(type, name, shaderData, sourceName, preprocessor);
        }

        String patched = modulation$patch(new String(source, StandardCharsets.UTF_8));
        if (patched == null) {
            Constants.LOG.warn("Sunbathing godrays shader has no recognisable sky depth test, leaving {} untouched", name);
            return original.call(type, name, new ByteArrayInputStream(source), sourceName, preprocessor);
        }

        try {
            return original.call(type, name, new ByteArrayInputStream(patched.getBytes(StandardCharsets.UTF_8)), sourceName, preprocessor);
        } catch (Exception exception) {
            Constants.LOG.warn("Patched Sunbathing godrays shader failed to compile, falling back to the original", exception);
            return original.call(type, name, new ByteArrayInputStream(source), sourceName, preprocessor);
        }
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

        String uniforms = GlslSnippets.load("godrays_uniforms.glsl");
        String skyness = GlslSnippets.load("godrays_skyness.glsl");
        if (uniforms.isEmpty() || skyness.isEmpty()) {
            return null;
        }

        return replaced.substring(0, versionLine.end())
                + "\n" + uniforms
                + replaced.substring(versionLine.end())
                + "\n" + skyness;
    }
}
