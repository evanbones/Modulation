package com.evandev.modulation.mixin.sodium.client;

import com.evandev.modulation.Constants;
import com.evandev.modulation.client.ImprovedFog;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.caffeinemc.mods.sodium.client.gl.shader.GlShader;
import net.caffeinemc.mods.sodium.client.gl.shader.ShaderConstants;
import net.caffeinemc.mods.sodium.client.gl.shader.ShaderLoader;
import net.caffeinemc.mods.sodium.client.gl.shader.ShaderParser;
import net.caffeinemc.mods.sodium.client.gl.shader.ShaderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@IfModLoaded("sodium")
@Mixin(value = ShaderLoader.class, remap = false)
public abstract class SodiumShaderLoaderMixin {

    @Unique
    private static final ResourceLocation modulation$CHUNK_FRAGMENT = ResourceLocation.fromNamespaceAndPath("sodium", "blocks/block_layer_opaque.fsh");

    @Unique
    private static final String modulation$FOG_OVERRIDE = """
            vec4 linear_fog(vec4 inColor, float vertexDistance, float fogStart, float fogEnd, vec4 fogColor) {
                return _linearFog(inColor, vertexDistance, fogColor, fogStart, fogEnd);
            }

            vec4 modulation_linear_fog(vec4 inColor, float vertexDistance, vec4 fogColor, float fogStart, float fogEnd) {
                return modulation_improved_fog(inColor, vertexDistance, fogStart, fogEnd, fogColor);
            }

            #ifdef USE_FOG
            #define _linearFog modulation_linear_fog
            #endif
            """;

    @Unique
    private static boolean modulation$patchFailed;

    @Shadow
    public static String getShaderSource(ResourceLocation name) {
        throw new AssertionError();
    }

    @ModifyReturnValue(method = "getShaderSource", at = @At("RETURN"))
    private static String modulation$patchChunkFog(String source, ResourceLocation name) {
        if (modulation$patchFailed || !modulation$CHUNK_FRAGMENT.equals(name)) {
            return source;
        }

        String patched = source.contains("_linearFog(") ? ImprovedFog.patchBeforeMain(source, modulation$FOG_OVERRIDE) : null;
        if (patched == null) {
            Constants.LOG.warn("Sodium chunk shader has no recognisable fog call, improved fog will not apply to it");
            return source;
        }

        return patched;
    }

    @WrapOperation(method = "loadShader", at = @At(value = "NEW", target = "net/caffeinemc/mods/sodium/client/gl/shader/GlShader"))
    private static GlShader modulation$fallBackToUnpatchedChunkShader(ShaderType type, ResourceLocation name, ShaderParser.ParsedShader parsedShader, Operation<GlShader> original, @Local(argsOnly = true) ShaderConstants constants) {
        if (modulation$patchFailed || !modulation$CHUNK_FRAGMENT.equals(name)) {
            return original.call(type, name, parsedShader);
        }

        try {
            return original.call(type, name, parsedShader);
        } catch (RuntimeException exception) {
            Constants.LOG.warn("Patched Sodium chunk shader failed to compile, falling back to the original", exception);
            modulation$patchFailed = true;
            return original.call(type, name, ShaderParser.parseShader(getShaderSource(name), constants));
        }
    }
}
