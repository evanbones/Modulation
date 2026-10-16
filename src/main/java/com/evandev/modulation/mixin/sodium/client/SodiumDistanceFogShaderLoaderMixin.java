package com.evandev.modulation.mixin.sodium.client;

//? if >=26.1 && <26.2 {
/*import com.evandev.modulation.Constants;
import com.evandev.modulation.client.DistanceFog;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@IfModLoaded("sodium")
@IfMinecraftVersion(minVersion = "26.1", maxVersion = "26.2", maxInclusive = false)
@Mixin(value = ShaderLoader.class, remap = false)
public abstract class SodiumDistanceFogShaderLoaderMixin {

    @Unique
    private static final ResourceLocation modulation$CHUNK_FRAGMENT = ResourceLocation.fromNamespaceAndPath("sodium", "blocks/block_layer_opaque.fsh");

    @Unique
    private static final String modulation$REPLACEMENT = "/assets/modulation/shaders/sodium/block_layer_opaque.fsh";

    @Unique
    private static boolean modulation$replacementFailed;

    @Shadow
    public static String getShaderSource(ResourceLocation name) {
        throw new AssertionError();
    }

    @ModifyReturnValue(method = "getShaderSource", at = @At("RETURN"))
    private static String modulation$replaceChunkFragment(String source, ResourceLocation name) {
        if (modulation$replacementFailed || !modulation$CHUNK_FRAGMENT.equals(name)) {
            return source;
        }

        try (InputStream in = DistanceFog.class.getResourceAsStream(modulation$REPLACEMENT)) {
            if (in == null) {
                Constants.LOG.warn("Missing Sodium distance fog shader, distance fog will not apply to Sodium terrain");
                return source;
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            Constants.LOG.warn("Failed to read Sodium distance fog shader", exception);
            return source;
        }
    }

    @WrapOperation(method = "loadShader", at = @At(value = "NEW", target = "net/caffeinemc/mods/sodium/client/gl/shader/GlShader"))
    private static GlShader modulation$fallBackToSodiumChunkShader(ShaderType type, ResourceLocation name, ShaderParser.ParsedShader parsedShader, Operation<GlShader> original, @Local(argsOnly = true) ShaderConstants constants) {
        if (modulation$replacementFailed || !modulation$CHUNK_FRAGMENT.equals(name)) {
            return original.call(type, name, parsedShader);
        }

        try {
            return original.call(type, name, parsedShader);
        } catch (RuntimeException exception) {
            Constants.LOG.warn("Sodium distance fog shader failed to compile, falling back to the original", exception);
            modulation$replacementFailed = true;
            return original.call(type, name, ShaderParser.parseShader(getShaderSource(name), constants));
        }
    }
}
*///?} else {
import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import org.spongepowered.asm.mixin.Mixin;

@IfMinecraftVersion(minVersion = "26.1", maxVersion = "26.2", maxInclusive = false)
@Mixin(targets = "net.caffeinemc.mods.sodium.client.gl.shader.ShaderLoader", remap = false)
public abstract class SodiumDistanceFogShaderLoaderMixin {
}
//?}
