package com.evandev.modulation.mixin.sodium.client;

import com.evandev.modulation.client.ImprovedFogUniforms;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.DefaultShaderInterface;
import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("sodium")
@Mixin(value = DefaultShaderInterface.class, remap = false)
public abstract class SodiumDefaultShaderInterfaceMixin {

    @Unique
    private final ImprovedFogUniforms modulation$uniforms = new ImprovedFogUniforms();

    @Inject(method = "setupState", at = @At("TAIL"))
    private void modulation$applyImprovedFog(CallbackInfo ci) {
        this.modulation$uniforms.upload(GL20.glGetInteger(GL20.GL_CURRENT_PROGRAM));
    }
}
