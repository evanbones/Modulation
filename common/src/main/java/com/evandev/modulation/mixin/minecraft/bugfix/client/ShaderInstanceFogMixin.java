package com.evandev.modulation.mixin.minecraft.bugfix.client;

import com.evandev.modulation.Constants;
import com.evandev.modulation.client.ImprovedFog;
import com.evandev.modulation.client.ImprovedFogUniforms;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Program;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.regex.Pattern;

@Mixin(ShaderInstance.class)
public abstract class ShaderInstanceFogMixin {

    @Unique
    private static final Set<String> modulation$TERRAIN_PROGRAMS = Set.of(
            "rendertype_solid",
            "rendertype_cutout",
            "rendertype_cutout_mipped",
            "rendertype_translucent",
            "rendertype_tripwire"
    );

    @Unique
    private static final Pattern modulation$FOG_CALL = Pattern.compile("\\blinear_fog\\(\\s*(\\w+)\\s*,\\s*vertexDistance\\s*,\\s*FogStart\\s*,\\s*FogEnd\\s*,\\s*FogColor\\s*\\)");

    @Shadow
    @Final
    private Program fragmentProgram;

    @Shadow
    public abstract int getId();

    @Unique
    private final ImprovedFogUniforms modulation$uniforms = new ImprovedFogUniforms();

    @WrapOperation(
            method = "getOrCreate",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/shaders/Program;compileShader(Lcom/mojang/blaze3d/shaders/Program$Type;Ljava/lang/String;Ljava/io/InputStream;Ljava/lang/String;Lcom/mojang/blaze3d/preprocessor/GlslPreprocessor;)Lcom/mojang/blaze3d/shaders/Program;"
            )
    )
    private static Program modulation$patchTerrainFog(Program.Type type, String name, InputStream shaderData, String sourceName, GlslPreprocessor preprocessor, Operation<Program> original) throws IOException {
        if (type != Program.Type.FRAGMENT || !modulation$TERRAIN_PROGRAMS.contains(modulation$stripNamespace(name))) {
            return original.call(type, name, shaderData, sourceName, preprocessor);
        }

        byte[] source = shaderData.readAllBytes();
        String patched = ImprovedFog.patchFogCall(new String(source, StandardCharsets.UTF_8), modulation$FOG_CALL, "modulation_improved_fog($1, vertexDistance, FogStart, FogEnd, FogColor)", "");
        if (patched == null) {
            Constants.LOG.warn("Terrain shader {} has no recognisable fog call, improved fog will not apply to it", name);
            return original.call(type, name, new ByteArrayInputStream(source), sourceName, preprocessor);
        }

        try {
            Program program = original.call(type, name, new ByteArrayInputStream(patched.getBytes(StandardCharsets.UTF_8)), sourceName, preprocessor);
            ImprovedFog.markPatched(program.getName());
            return program;
        } catch (Exception exception) {
            Constants.LOG.warn("Patched terrain shader {} failed to compile, falling back to the original", name, exception);
            return original.call(type, name, new ByteArrayInputStream(source), sourceName, preprocessor);
        }
    }

    @Unique
    private static String modulation$stripNamespace(String name) {
        return name.startsWith("minecraft:") ? name.substring("minecraft:".length()) : name;
    }

    @Inject(method = "apply", at = @At("TAIL"))
    private void modulation$applyImprovedFog(CallbackInfo ci) {
        if (!ImprovedFog.isPatched(this.fragmentProgram.getName())) {
            return;
        }

        this.modulation$uniforms.upload(this.getId());
    }
}
