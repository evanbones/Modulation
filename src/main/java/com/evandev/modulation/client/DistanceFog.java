package com.evandev.modulation.client;

//? if >=26.1 {
/*import com.evandev.modulation.Constants;
import com.evandev.modulation.mixin.minecraft.accessor.RenderPipelinesAccessor;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FogType;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;

import java.util.OptionalDouble;
//? if >=26.3 {
/^import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.renderpearl.api.pipeline.UniformType;
import net.minecraft.client.renderer.BindGroupLayouts;

import java.util.IdentityHashMap;
import java.util.Map;
^///?} else if >=26.2 {
/^import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import net.minecraft.client.renderer.BindGroupLayouts;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
^///?} else {
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.util.ARGB;

import java.util.OptionalInt;
//?}

public final class DistanceFog {

    private static final float DEFAULT_OCCLUSION_START = -1.0F;
    private static final float DEFAULT_OCCLUSION_END = -8.0F;
    private static final float BELOW_MIN_Y_OCCLUSION_START = 15.0F;
    private static final float BELOW_MIN_Y_OCCLUSION_END = 10.0F;
    private static final float UNDERWATER_OCCLUSION_START = 40.0F;
    private static final float UNDERWATER_OCCLUSION_END = 20.0F;

    private static final String SKY_SAMPLER = "ModulationSkySampler";
    private static final String CLOUDS_SAMPLER = "ModulationCloudsSampler";
    private static final String CLOUDS_DEPTH_SAMPLER = "ModulationCloudsDepthSampler";
    private static final ResourceLocation TERRAIN_SHADER = id("core/terrain");
    //? if >=26.2 {
    /^private static final ResourceLocation SODIUM_TERRAIN_SHADER = id("core/sodium_terrain");
    private static final Map<RenderPipeline, RenderPipeline> SODIUM_PIPELINES = new IdentityHashMap<>();
    private static boolean sodiumVariantBound;
    ^///?}
    //? if >=26.3 {
    /^private static final float TRANSLUCENT_ALPHA_CUTOUT = 0.1F;
    private static final BindGroupLayout BACKGROUND_SAMPLERS = BindGroupLayout.builder()
            .withUniform(SKY_SAMPLER, UniformType.COMBINED_IMAGE_SAMPLER)
            .withUniform(CLOUDS_SAMPLER, UniformType.COMBINED_IMAGE_SAMPLER)
            .withUniform(CLOUDS_DEPTH_SAMPLER, UniformType.COMBINED_IMAGE_SAMPLER)
            .build();
    private static final Map<RenderPipeline, RenderPipeline> TERRAIN_PIPELINES = new IdentityHashMap<>();
    ^///?} else if >=26.2 {
    /^private static final float TRANSLUCENT_ALPHA_CUTOUT = 0.1F;
    private static final BindGroupLayout BACKGROUND_SAMPLERS = BindGroupLayout.builder()
            .withSampler(SKY_SAMPLER)
            .withSampler(CLOUDS_SAMPLER)
            .withSampler(CLOUDS_DEPTH_SAMPLER)
            .build();
    ^///?} else {
    private static final float TRANSLUCENT_ALPHA_CUTOUT = 0.01F;
    //?}

    //? if <26.3 {
    private static @Nullable RenderPipeline solidTerrain;
    private static @Nullable RenderPipeline cutoutTerrain;
    private static @Nullable RenderPipeline translucentTerrain;
    //?}
    private static @Nullable RenderPipeline skyOccluder;

    private static @Nullable TextureTarget skyTarget;
    private static @Nullable TextureTarget cloudsTarget;
    private static @Nullable RenderTarget redirect;
    private static boolean active;

    private DistanceFog() {
    }

    public static boolean enabled() {
        return VanillaBugfixesModule.IMPROVED_FOG.on();
    }

    public static boolean isActive() {
        return active;
    }

    public static RenderTarget redirect(RenderTarget original) {
        return redirect != null ? redirect : original;
    }

    public static boolean isCapturing() {
        return redirect != null;
    }

    public static void beginFrame() {
        active = false;
        if (!enabled()) {
            release();
        }
    }

    public static void endFrame() {
        active = false;
    }

    public static RenderTarget mainTarget() {
        //? if >=26.2 {
        /^return Minecraft.getInstance().gameRenderer.mainRenderTarget();
        ^///?} else {
        return Minecraft.getInstance().getMainRenderTarget();
        //?}
    }

    public static void prepareTargets(Vector4f fogColor) {
        RenderTarget main = mainTarget();
        int width = main.width;
        int height = main.height;
        if (skyTarget == null || cloudsTarget == null) {
            skyTarget = createTarget("Modulation distance fog sky", width, height);
            cloudsTarget = createTarget("Modulation distance fog clouds", width, height);
        } else if (skyTarget.width != width || skyTarget.height != height) {
            skyTarget.resize(width, height);
            cloudsTarget.resize(width, height);
        }

        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        //? if >=26.2 {
        /^encoder.clearColorAndDepthTextures(skyTarget.getColorTexture(), new Vector4f(fogColor.x, fogColor.y, fogColor.z, 1.0F), skyTarget.getDepthTexture(), 0.0);
        encoder.clearColorAndDepthTextures(cloudsTarget.getColorTexture(), new Vector4f(0.0F), cloudsTarget.getDepthTexture(), 0.0);
        ^///?} else {
        encoder.clearColorAndDepthTextures(skyTarget.getColorTexture(), ARGB.colorFromFloat(1.0F, fogColor.x, fogColor.y, fogColor.z), skyTarget.getDepthTexture(), 1.0);
        encoder.clearColorAndDepthTextures(cloudsTarget.getColorTexture(), 0, cloudsTarget.getDepthTexture(), 1.0);
        //?}
        active = true;
    }

    private static TextureTarget createTarget(String label, int width, int height) {
        //? if >=26.3 {
        /^return new TextureTarget(label, width, height, GpuFormat.RGBA8_UNORM, GpuFormat.D32_FLOAT);
        ^///?} else if >=26.2 {
        /^return new TextureTarget(label, width, height, true, GpuFormat.RGBA8_UNORM);
        ^///?} else {
        return new TextureTarget(label, width, height, true);
        //?}
    }

    public static @Nullable RenderTarget skyTarget() {
        return skyTarget;
    }

    public static @Nullable RenderTarget cloudsTarget() {
        return cloudsTarget;
    }

    public static boolean hasBackground() {
        return active && skyTarget != null && cloudsTarget != null;
    }

    public static void captureSky(Runnable renderSky) {
        capture(skyTarget, renderSky);
    }

    public static void captureClouds(Runnable renderClouds) {
        capture(cloudsTarget, renderClouds);
    }

    private static void capture(@Nullable RenderTarget target, Runnable render) {
        if (target == null) {
            return;
        }
        redirect = target;
        try {
            render.run();
        } catch (RuntimeException exception) {
            Constants.LOG.error("Failed to capture distance fog background", exception);
        } finally {
            redirect = null;
        }
    }

    //? if >=26.3 {
    /^public static RenderPipeline terrainPipeline(RenderPipeline pipeline) {
        if (!active) {
            return pipeline;
        }
        if (TERRAIN_PIPELINES.isEmpty()) {
            TERRAIN_PIPELINES.put(RenderPipelines.SOLID_TERRAIN, terrainBuilder(RenderPipelinesAccessor.modulation$getTerrainSnippet(), "pipeline/solid_terrain_distance_fog")
                    .withColorTargetState(ColorTargetState.DEFAULT)
                    .build());
            TERRAIN_PIPELINES.put(RenderPipelines.SOLID_TERRAIN_MULTIDRAW, terrainBuilder(RenderPipelinesAccessor.modulation$getMultidrawTerrainSnippet(), "pipeline/solid_terrain_multidraw_distance_fog")
                    .withColorTargetState(ColorTargetState.DEFAULT)
                    .build());
            TERRAIN_PIPELINES.put(RenderPipelines.CUTOUT_TERRAIN, terrainBuilder(RenderPipelinesAccessor.modulation$getTerrainSnippet(), "pipeline/cutout_terrain_distance_fog")
                    .withShaderDefine("ALPHA_CUTOUT", 0.5F)
                    .withColorTargetState(ColorTargetState.DEFAULT)
                    .build());
            TERRAIN_PIPELINES.put(RenderPipelines.CUTOUT_TERRAIN_MULTIDRAW, terrainBuilder(RenderPipelinesAccessor.modulation$getMultidrawTerrainSnippet(), "pipeline/cutout_terrain_multidraw_distance_fog")
                    .withShaderDefine("ALPHA_CUTOUT", 0.5F)
                    .withColorTargetState(ColorTargetState.DEFAULT)
                    .build());
            TERRAIN_PIPELINES.put(RenderPipelines.TRANSLUCENT_TERRAIN, terrainBuilder(RenderPipelinesAccessor.modulation$getTerrainSnippet(), "pipeline/translucent_terrain_distance_fog")
                    .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                    .withShaderDefine("ALPHA_CUTOUT", TRANSLUCENT_ALPHA_CUTOUT)
                    .build());
            TERRAIN_PIPELINES.put(RenderPipelines.TRANSLUCENT_TERRAIN_MULTIDRAW, terrainBuilder(RenderPipelinesAccessor.modulation$getMultidrawTerrainSnippet(), "pipeline/translucent_terrain_multidraw_distance_fog")
                    .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                    .withShaderDefine("ALPHA_CUTOUT", TRANSLUCENT_ALPHA_CUTOUT)
                    .build());
            TERRAIN_PIPELINES.put(RenderPipelines.OIT_TERRAIN.accumulatePipeline(), oitAccumulateBuilder(false).build());
            TERRAIN_PIPELINES.put(RenderPipelines.OIT_TERRAIN_MULTIDRAW.accumulatePipeline(), oitAccumulateBuilder(true).build());
        }
        return TERRAIN_PIPELINES.getOrDefault(pipeline, pipeline);
    }

    private static RenderPipeline.Builder terrainBuilder(RenderPipeline.Snippet snippet, String location) {
        return RenderPipeline.builder(snippet)
                .withLocation(id(location))
                .withFragmentShader(TERRAIN_SHADER)
                .withBindGroupLayout(BACKGROUND_SAMPLERS);
    }

    private static RenderPipeline.Builder oitAccumulateBuilder(boolean multidraw) {
        RenderPipeline.Builder base = RenderPipeline.builder(RenderPipelinesAccessor.modulation$getGenericBlocksSnippet())
                .withVertexShader("core/terrain")
                .withBindGroupLayout(BindGroupLayouts.TERRAIN_INFO)
                .withShaderDefine("ALPHA_CUTOUT", TRANSLUCENT_ALPHA_CUTOUT);
        if (multidraw) {
            base.withVertexBinding(1, DefaultVertexFormat.CHUNK_DATA_INSTANCED).withShaderDefine("MULTIDRAW_TERRAIN");
        } else {
            base.withBindGroupLayout(BindGroupLayouts.CHUNK_SECTION);
        }
        return RenderPipeline.builder(base.buildSnippet(), RenderPipelines.OIT_ACCUMULATE_SNIPPET)
                .withLocation(id(multidraw ? "pipeline/oit_accumulate_terrain_multidraw_distance_fog" : "pipeline/oit_accumulate_terrain_distance_fog"))
                .withFragmentShader(TERRAIN_SHADER)
                .withBindGroupLayout(BindGroupLayouts.SAMPLER2)
                .withBindGroupLayout(BACKGROUND_SAMPLERS);
    }
    ^///?} else {
    public static RenderPipeline terrainPipeline(RenderPipeline pipeline) {
        if (!active) {
            return pipeline;
        }
        if (pipeline == RenderPipelines.SOLID_TERRAIN) {
            if (solidTerrain == null) {
                solidTerrain = terrainBuilder("pipeline/solid_terrain_distance_fog").build();
            }
            return solidTerrain;
        }
        if (pipeline == RenderPipelines.CUTOUT_TERRAIN) {
            if (cutoutTerrain == null) {
                cutoutTerrain = terrainBuilder("pipeline/cutout_terrain_distance_fog")
                        .withShaderDefine("ALPHA_CUTOUT", 0.5F)
                        .build();
            }
            return cutoutTerrain;
        }
        if (pipeline == RenderPipelines.TRANSLUCENT_TERRAIN) {
            if (translucentTerrain == null) {
                translucentTerrain = terrainBuilder("pipeline/translucent_terrain_distance_fog")
                        .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                        .withShaderDefine("ALPHA_CUTOUT", TRANSLUCENT_ALPHA_CUTOUT)
                        .build();
            }
            return translucentTerrain;
        }
        return pipeline;
    }

    private static RenderPipeline.Builder terrainBuilder(String location) {
        //? if >=26.2 {
        /^return RenderPipeline.builder(RenderPipelinesAccessor.modulation$getTerrainSnippet())
                .withLocation(id(location))
                .withFragmentShader(TERRAIN_SHADER)
                .withBindGroupLayout(BACKGROUND_SAMPLERS)
                .withShaderDefine("MODULATION_REVERSED_Z");
        ^///?} else {
        return RenderPipeline.builder(RenderPipelinesAccessor.modulation$getTerrainSnippet())
                .withLocation(id(location))
                .withFragmentShader(TERRAIN_SHADER)
                .withSampler(SKY_SAMPLER)
                .withSampler(CLOUDS_SAMPLER)
                .withSampler(CLOUDS_DEPTH_SAMPLER);
        //?}
    }
    //?}

    //? if >=26.2 {
    /^public static RenderPipeline.Builder sodiumVariant(RenderPipeline.Builder builder, String path) {
        return builder.withLocation(id("sodium/" + path + "_distance_fog"))
                .withFragmentShader(SODIUM_TERRAIN_SHADER)
                .withBindGroupLayout(BACKGROUND_SAMPLERS);
    }

    public static void registerSodiumPipeline(RenderPipeline pipeline, RenderPipeline variant) {
        SODIUM_PIPELINES.put(pipeline, variant);
    }

    public static RenderPipeline sodiumPipeline(RenderPipeline pipeline) {
        RenderPipeline variant = hasBackground() ? SODIUM_PIPELINES.get(pipeline) : null;
        sodiumVariantBound = variant != null;
        return variant != null ? variant : pipeline;
    }

    public static void bindSodiumBackground(RenderPass renderPass) {
        if (sodiumVariantBound) {
            bindBackground(renderPass);
        }
    }
    ^///?}

    public static void bindBackground(RenderPass renderPass) {
        if (!hasBackground()) {
            return;
        }
        GpuSampler sampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST);
        //? if >=26.3 {
        /^renderPass.setUniform(SKY_SAMPLER, skyTarget.getColorTextureView(), sampler);
        renderPass.setUniform(CLOUDS_SAMPLER, cloudsTarget.getColorTextureView(), sampler);
        renderPass.setUniform(CLOUDS_DEPTH_SAMPLER, cloudsTarget.getDepthTextureView(), sampler);
        ^///?} else {
        renderPass.bindTexture(SKY_SAMPLER, skyTarget.getColorTextureView(), sampler);
        renderPass.bindTexture(CLOUDS_SAMPLER, cloudsTarget.getColorTextureView(), sampler);
        renderPass.bindTexture(CLOUDS_DEPTH_SAMPLER, cloudsTarget.getDepthTextureView(), sampler);
        //?}
    }

    private static Vector3f occlusionAngles() {
        Minecraft minecraft = Minecraft.getInstance();
        //? if >=26.2 {
        /^Camera camera = minecraft.gameRenderer.mainCamera();
        ^///?} else {
        Camera camera = minecraft.gameRenderer.getMainCamera();
        //?}
        if (minecraft.level != null && camera.position().y < minecraft.level.getMinY()) {
            return new Vector3f(BELOW_MIN_Y_OCCLUSION_START, BELOW_MIN_Y_OCCLUSION_END, 0.0F);
        }
        if (camera.getFluidInCamera() == FogType.WATER) {
            return new Vector3f(UNDERWATER_OCCLUSION_START, UNDERWATER_OCCLUSION_END, 0.0F);
        }
        return new Vector3f(DEFAULT_OCCLUSION_START, DEFAULT_OCCLUSION_END, 0.0F);
    }

    //? if >=26.3 {
    /^public static void renderSkyOccluder(RenderPass renderPass) {
        if (!enabled()) {
            return;
        }
        if (skyOccluder == null) {
            skyOccluder = RenderPipeline.builder()
                    .withLocation(id("pipeline/sky_occluder"))
                    .withVertexShader(id("core/sky_occluder"))
                    .withFragmentShader(id("core/sky_occluder"))
                    .withBindGroupLayout(BindGroupLayouts.PROJECTION)
                    .withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
                    .withBindGroupLayout(BindGroupLayouts.FOG)
                    .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                    .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
                    .build();
        }
        var transforms = RenderSystem.getDynamicUniforms()
                .writeTransform(RenderSystem.getModelViewMatrixCopy(), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), occlusionAngles(), new Matrix4f());
        renderPass.pushDebugGroup(() -> "Modulation sky occluder");
        renderPass.setPipeline(RenderSystem.getCompiledPipeline(skyOccluder));
        renderPass.setUniform("DynamicTransforms", transforms);
        renderPass.draw(3, 1, 0, 0);
        renderPass.popDebugGroup();
    }
    ^///?} else {
    public static void renderSkyOccluder() {
        renderSkyOccluder(redirect(mainTarget()));
    }

    public static void renderSkyOccluder(RenderTarget target) {
        if (!enabled() || target.getColorTextureView() == null) {
            return;
        }

        if (skyOccluder == null) {
            //? if >=26.2 {
            /^skyOccluder = RenderPipeline.builder()
                    .withLocation(id("pipeline/sky_occluder"))
                    .withVertexShader(id("core/sky_occluder"))
                    .withFragmentShader(id("core/sky_occluder"))
                    .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
                    .withBindGroupLayout(BindGroupLayouts.FOG)
                    .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                    .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
                    .withShaderDefine("MODULATION_REVERSED_Z")
                    .build();
            ^///?} else {
            skyOccluder = RenderPipeline.builder()
                    .withLocation(id("pipeline/sky_occluder"))
                    .withVertexShader(id("core/sky_occluder"))
                    .withFragmentShader(id("core/sky_occluder"))
                    .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
                    .withUniform("Projection", UniformType.UNIFORM_BUFFER)
                    .withUniform("Fog", UniformType.UNIFORM_BUFFER)
                    .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                    .withVertexFormat(DefaultVertexFormat.EMPTY, VertexFormat.Mode.TRIANGLES)
                    .build();
            //?}
        }

        //? if >=26.2 {
        /^var transforms = RenderSystem.getDynamicUniforms()
                .writeTransform(RenderSystem.getModelViewMatrixCopy(), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), occlusionAngles(), new Matrix4f());
        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> "Modulation sky occluder", target.getColorTextureView(), Optional.empty(), target.getDepthTextureView(), OptionalDouble.empty())) {
            renderPass.setPipeline(skyOccluder);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", transforms);
            renderPass.draw(3, 1, 0, 0);
        }
        ^///?} else {
        var transforms = RenderSystem.getDynamicUniforms()
                .writeTransform(RenderSystem.getModelViewMatrix(), new Vector4f(1.0F, 1.0F, 1.0F, 1.0F), occlusionAngles(), new Matrix4f());
        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> "Modulation sky occluder", target.getColorTextureView(), OptionalInt.empty(), target.getDepthTextureView(), OptionalDouble.empty())) {
            renderPass.setPipeline(skyOccluder);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms", transforms);
            renderPass.draw(0, 3);
        }
        //?}
    }
    //?}

    private static void release() {
        if (skyTarget != null) {
            skyTarget.destroyBuffers();
            skyTarget = null;
        }
        if (cloudsTarget != null) {
            cloudsTarget.destroyBuffers();
            cloudsTarget = null;
        }
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
    }
}
*///?}
