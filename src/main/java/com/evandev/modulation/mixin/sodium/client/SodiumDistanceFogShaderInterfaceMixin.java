package com.evandev.modulation.mixin.sodium.client;

//? if >=26.1 && <26.2 {
/*import com.evandev.modulation.client.DistanceFog;
import com.mojang.blaze3d.opengl.GlSampler;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.caffeinemc.mods.sodium.client.gl.shader.uniform.GlUniformInt;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ChunkShaderOptions;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ChunkShaderTextureSlot;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.DefaultShaderInterface;
import net.caffeinemc.mods.sodium.client.render.chunk.shader.ShaderBindingContext;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GL13C;
import org.lwjgl.opengl.GL33C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("sodium")
@IfMinecraftVersion(minVersion = "26.1", maxVersion = "26.2", maxInclusive = false)
@Mixin(value = DefaultShaderInterface.class, remap = false)
public abstract class SodiumDistanceFogShaderInterfaceMixin {

    @Unique
    private static final int modulation$FIRST_UNIT = ChunkShaderTextureSlot.VALUES.length;

    @Unique
    private @Nullable GlUniformInt modulation$enabled;

    @Unique
    private @Nullable GlUniformInt modulation$sky;

    @Unique
    private @Nullable GlUniformInt modulation$clouds;

    @Unique
    private @Nullable GlUniformInt modulation$cloudsDepth;

    @Unique
    private boolean modulation$bound;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void modulation$bindDistanceFogUniforms(ShaderBindingContext context, ChunkShaderOptions options, CallbackInfo ci) {
        this.modulation$enabled = context.bindUniformOptional("ModulationDistanceFog", GlUniformInt::new);
        this.modulation$sky = context.bindUniformOptional("ModulationSkySampler", GlUniformInt::new);
        this.modulation$clouds = context.bindUniformOptional("ModulationCloudsSampler", GlUniformInt::new);
        this.modulation$cloudsDepth = context.bindUniformOptional("ModulationCloudsDepthSampler", GlUniformInt::new);
    }

    @Inject(method = "setupState", at = @At("TAIL"))
    private void modulation$setupDistanceFog(CallbackInfo ci) {
        if (this.modulation$enabled == null) {
            return;
        }

        RenderTarget sky = DistanceFog.skyTarget();
        RenderTarget clouds = DistanceFog.cloudsTarget();
        boolean bind = DistanceFog.hasBackground() && sky != null && clouds != null && clouds.getDepthTexture() != null
                && this.modulation$sky != null && this.modulation$clouds != null && this.modulation$cloudsDepth != null;
        this.modulation$enabled.setInt(bind ? 1 : 0);
        if (!bind) {
            return;
        }

        GlSampler sampler = (GlSampler) RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST);
        modulation$bind(this.modulation$sky, modulation$FIRST_UNIT, sky.getColorTexture(), sampler);
        modulation$bind(this.modulation$clouds, modulation$FIRST_UNIT + 1, clouds.getColorTexture(), sampler);
        modulation$bind(this.modulation$cloudsDepth, modulation$FIRST_UNIT + 2, clouds.getDepthTexture(), sampler);
        this.modulation$bound = true;
    }

    @Inject(method = "resetState", at = @At("TAIL"))
    private void modulation$resetDistanceFog(CallbackInfo ci) {
        if (!this.modulation$bound) {
            return;
        }
        this.modulation$bound = false;
        for (int unit = modulation$FIRST_UNIT; unit < modulation$FIRST_UNIT + 3; unit++) {
            GL33C.glBindSampler(unit, 0);
        }
    }

    @Unique
    private static void modulation$bind(GlUniformInt uniform, int unit, GpuTexture texture, GlSampler sampler) {
        GlStateManager._activeTexture(GL13C.GL_TEXTURE0 + unit);
        GlStateManager._bindTexture(((GlTexture) texture).glId());
        GL33C.glBindSampler(unit, sampler.getId());
        uniform.setInt(unit);
    }
}
*///?} else {
import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import org.spongepowered.asm.mixin.Mixin;

@IfMinecraftVersion(minVersion = "26.1", maxVersion = "26.2", maxInclusive = false)
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.shader.DefaultShaderInterface", remap = false)
public abstract class SodiumDistanceFogShaderInterfaceMixin {
}
//?}
