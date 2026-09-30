package com.evandev.modulation.client;

import com.evandev.modulation.mixin.minecraft.accessor.FogRendererAccessor;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ImprovedFog {

    public static final int SKY_UNIT = 9;
    public static final int CLOUDS_UNIT = 10;
    public static final int CLOUDS_DEPTH_UNIT = 11;

    private static final float DEFAULT_OCCLUSION_START = -1.0F;
    private static final float DEFAULT_OCCLUSION_END = -8.0F;
    private static final float BELOW_MIN_Y_OCCLUSION_START = 15.0F;
    private static final float BELOW_MIN_Y_OCCLUSION_END = 10.0F;
    private static final float UNDERWATER_OCCLUSION_START = 40.0F;
    private static final float UNDERWATER_OCCLUSION_END = 20.0F;
    private static final float OCCLUDER_RADIUS = 100.0F;
    private static final int OCCLUDER_SEGMENTS = 32;
    private static final int OCCLUDER_BANDS = 16;

    private static final Pattern VERSION_LINE = Pattern.compile("^\\s*#version[^\\r\\n]*", Pattern.MULTILINE);

    private static final Set<String> PATCHED_PROGRAMS = ConcurrentHashMap.newKeySet();

    private static RenderTarget skyTarget;
    private static RenderTarget cloudsTarget;
    private static boolean active;
    private static boolean capturingClouds;
    private static float renderFogStart;
    private static float renderFogEnd;

    private ImprovedFog() {
    }

    public static boolean enabled() {
        return VanillaBugfixesModule.IMPROVED_FOG.on();
    }

    public static boolean isActive() {
        return active;
    }

    public static boolean isCapturingClouds() {
        return capturingClouds;
    }

    public static float renderFogStart() {
        return renderFogStart;
    }

    public static float renderFogEnd() {
        return renderFogEnd;
    }

    public static void markPatched(String program) {
        PATCHED_PROGRAMS.add(program);
    }

    public static boolean isPatched(String program) {
        return PATCHED_PROGRAMS.contains(program);
    }

    public static String patchFogCall(String source, Pattern fogCall, String replacement, String prelude) {
        Matcher call = fogCall.matcher(source);
        if (!call.find()) {
            return null;
        }

        String replaced = call.replaceAll(replacement);
        Matcher versionLine = VERSION_LINE.matcher(replaced);
        if (!versionLine.find()) {
            return null;
        }

        String uniforms = GlslSnippets.load("improved_fog_uniforms.glsl");
        String body = GlslSnippets.load("improved_fog.glsl");
        if (uniforms.isEmpty() || body.isEmpty()) {
            return null;
        }

        return replaced.substring(0, versionLine.end())
                + "\n" + uniforms
                + replaced.substring(versionLine.end())
                + "\n" + prelude
                + "\n" + body;
    }

    public static void captureBackground(LevelRenderer levelRenderer, Camera camera, Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick) {
        active = false;
        if (!enabled()) {
            release();
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget main = minecraft.getMainRenderTarget();
        ensureTargets(main.width, main.height);

        GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, main.frameBufferId);
        GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, skyTarget.frameBufferId);
        GlStateManager._glBlitFrameBuffer(0, 0, main.width, main.height, 0, 0, skyTarget.width, skyTarget.height, GL30.GL_COLOR_BUFFER_BIT, GL30.GL_NEAREST);

        cloudsTarget.clear(Minecraft.ON_OSX);
        if (minecraft.options.getCloudsType() != CloudStatus.OFF) {
            Vec3 pos = camera.getPosition();
            cloudsTarget.bindWrite(false);
            capturingClouds = true;
            try {
                levelRenderer.renderClouds(new PoseStack(), frustumMatrix, projectionMatrix, partialTick, pos.x, pos.y, pos.z);
            } finally {
                capturingClouds = false;
            }
        }

        main.bindWrite(false);

        float farPlane = Math.max(minecraft.gameRenderer.getRenderDistance(), 32.0F);
        renderFogStart = farPlane - Mth.clamp(farPlane / 10.0F, 4.0F, 64.0F);
        renderFogEnd = farPlane;
        active = true;
    }

    public static void bindCloudsTarget() {
        if (cloudsTarget != null) {
            cloudsTarget.bindWrite(false);
        }
    }

    public static void endFrame() {
        if (!active) {
            return;
        }

        active = false;
        int previous = GlStateManager._getActiveTexture();
        for (int unit : new int[]{SKY_UNIT, CLOUDS_UNIT, CLOUDS_DEPTH_UNIT}) {
            GlStateManager._activeTexture(GL30.GL_TEXTURE0 + unit);
            GlStateManager._bindTexture(0);
        }
        GlStateManager._activeTexture(previous);
    }

    public static void bindBackgroundTextures() {
        int previous = GlStateManager._getActiveTexture();
        GlStateManager._activeTexture(GL30.GL_TEXTURE0 + SKY_UNIT);
        GlStateManager._bindTexture(skyTarget.getColorTextureId());
        GlStateManager._activeTexture(GL30.GL_TEXTURE0 + CLOUDS_UNIT);
        GlStateManager._bindTexture(cloudsTarget.getColorTextureId());
        GlStateManager._activeTexture(GL30.GL_TEXTURE0 + CLOUDS_DEPTH_UNIT);
        GlStateManager._bindTexture(cloudsTarget.getDepthTextureId());
        GlStateManager._activeTexture(previous);
    }

    public static void renderSkyOccluder(ClientLevel level, Camera camera, Matrix4f frustumMatrix, float partialTick) {
        if (!enabled()) {
            return;
        }

        float start;
        float end;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.player.getEyePosition(partialTick).y < level.getMinBuildHeight()) {
            start = BELOW_MIN_Y_OCCLUSION_START;
            end = BELOW_MIN_Y_OCCLUSION_END;
        } else if (camera.getFluidInCamera() == FogType.WATER) {
            start = UNDERWATER_OCCLUSION_START;
            end = UNDERWATER_OCCLUSION_END;
        } else {
            start = DEFAULT_OCCLUSION_START;
            end = DEFAULT_OCCLUSION_END;
        }

        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (int band = 0; band <= OCCLUDER_BANDS; band++) {
            float upperAngle = bandAngle(band, start, end);
            float lowerAngle = bandAngle(band + 1, start, end);
            float upperAlpha = bandAlpha(band);
            float lowerAlpha = bandAlpha(band + 1);

            for (int segment = 0; segment < OCCLUDER_SEGMENTS; segment++) {
                float left = segment * Mth.TWO_PI / OCCLUDER_SEGMENTS;
                float right = (segment + 1) * Mth.TWO_PI / OCCLUDER_SEGMENTS;
                occluderVertex(builder, frustumMatrix, upperAngle, left, upperAlpha);
                occluderVertex(builder, frustumMatrix, upperAngle, right, upperAlpha);
                occluderVertex(builder, frustumMatrix, lowerAngle, right, lowerAlpha);
                occluderVertex(builder, frustumMatrix, lowerAngle, left, lowerAlpha);
            }
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(FogRendererAccessor.modulation$getFogRed(), FogRendererAccessor.modulation$getFogGreen(), FogRendererAccessor.modulation$getFogBlue(), 1.0F);
        BufferUploader.drawWithShader(builder.buildOrThrow());
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableCull();
    }

    private static float bandAngle(int band, float start, float end) {
        if (band > OCCLUDER_BANDS) {
            return -90.0F;
        }
        return Mth.lerp((float) band / OCCLUDER_BANDS, start, end);
    }

    private static float bandAlpha(int band) {
        float t = Math.min(1.0F, (float) band / OCCLUDER_BANDS);
        return t * t * (3.0F - 2.0F * t);
    }

    private static void occluderVertex(BufferBuilder builder, Matrix4f pose, float elevation, float azimuth, float alpha) {
        float pitch = elevation * Mth.DEG_TO_RAD;
        float horizontal = Mth.cos(pitch) * OCCLUDER_RADIUS;
        builder.addVertex(pose, horizontal * Mth.cos(azimuth), Mth.sin(pitch) * OCCLUDER_RADIUS, horizontal * Mth.sin(azimuth))
                .setColor(1.0F, 1.0F, 1.0F, alpha);
    }

    private static void ensureTargets(int width, int height) {
        if (skyTarget == null) {
            skyTarget = new TextureTarget(width, height, false, Minecraft.ON_OSX);
            cloudsTarget = new TextureTarget(width, height, true, Minecraft.ON_OSX);
            cloudsTarget.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        } else if (skyTarget.width != width || skyTarget.height != height) {
            skyTarget.resize(width, height, Minecraft.ON_OSX);
            cloudsTarget.resize(width, height, Minecraft.ON_OSX);
        }
    }

    private static void release() {
        if (skyTarget != null) {
            skyTarget.destroyBuffers();
            cloudsTarget.destroyBuffers();
            skyTarget = null;
            cloudsTarget = null;
        }
    }
}
