package com.evandev.modulation.client;

import com.evandev.modulation.Constants;
import com.evandev.modulation.mixin.minecraft.accessor.FogRendererAccessor;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Uniform;
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
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
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
    private static final Pattern MAIN_FUNCTION = Pattern.compile("^[ \\t]*void\\s+main\\s*\\(", Pattern.MULTILINE);

    private static final Set<String> PATCHED_PROGRAMS = ConcurrentHashMap.newKeySet();

    private static RenderTarget skyTarget;
    private static RenderTarget cloudsTarget;
    private static boolean active;
    private static boolean capturingClouds;
    private static boolean cloudsCaptured;
    private static boolean cloudsReplaced;
    private static boolean expectingCloudCall;
    private static boolean cloudCallReached;
    private static int sceneFramebuffer;
    private static int fabulousCloudsFramebuffer = -1;
    private static int compositeProgram;
    private static int compositeVao;
    private static boolean compositeFailed;
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
        return withImprovedFog(replaced, replaced.length(), prelude);
    }

    public static String patchBeforeMain(String source, String prelude) {
        Matcher main = MAIN_FUNCTION.matcher(source);
        if (!main.find()) {
            return null;
        }

        return withImprovedFog(source, main.start(), prelude);
    }

    private static String withImprovedFog(String source, int preludeIndex, String prelude) {
        Matcher versionLine = VERSION_LINE.matcher(source);
        if (!versionLine.find() || preludeIndex < versionLine.end()) {
            return null;
        }

        String uniforms = GlslSnippets.load("improved_fog_uniforms.glsl");
        String body = GlslSnippets.load("improved_fog.glsl");
        if (uniforms.isEmpty() || body.isEmpty()) {
            return null;
        }

        return source.substring(0, versionLine.end())
                + "\n" + uniforms
                + source.substring(versionLine.end(), preludeIndex)
                + "\n" + prelude
                + "\n" + source.substring(preludeIndex)
                + "\n" + body;
    }

    public static void captureBackground(LevelRenderer levelRenderer, Camera camera, Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick) {
        active = false;
        if (!enabled()) {
            release();
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        sceneFramebuffer = GlStateManager.getBoundFramebuffer();
        int width = GlStateManager.Viewport.width();
        int height = GlStateManager.Viewport.height();
        ensureTargets(width, height);

        GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, sceneFramebuffer);
        GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, skyTarget.frameBufferId);
        GlStateManager._glBlitFrameBuffer(0, 0, width, height, 0, 0, skyTarget.width, skyTarget.height, GL30.GL_COLOR_BUFFER_BIT, GL30.GL_NEAREST);

        cloudsTarget.clear(Minecraft.ON_OSX);
        cloudsCaptured = false;
        cloudCallReached = false;
        expectingCloudCall = minecraft.options.getCloudsType() != CloudStatus.OFF;
        if (expectingCloudCall && !cloudsReplaced) {
            Vec3 pos = camera.getPosition();
            RenderTarget fabulousClouds = levelRenderer.getCloudsTarget();
            fabulousCloudsFramebuffer = fabulousClouds != null ? fabulousClouds.frameBufferId : -1;
            cloudsTarget.bindWrite(false);
            Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushMatrix();
            modelViewStack.mul(frustumMatrix);
            RenderSystem.applyModelViewMatrix();
            capturingClouds = true;
            try {
                levelRenderer.renderClouds(new PoseStack(), frustumMatrix, projectionMatrix, partialTick, pos.x, pos.y, pos.z);
                cloudsCaptured = true;
            } finally {
                capturingClouds = false;
                modelViewStack.popMatrix();
                RenderSystem.applyModelViewMatrix();
            }
        }

        bindSceneFramebuffer();

        float farPlane = Math.max(minecraft.gameRenderer.getRenderDistance(), 32.0F);
        renderFogStart = farPlane - Mth.clamp(farPlane / 10.0F, 4.0F, 64.0F);
        renderFogEnd = farPlane;
        active = true;
    }

    public static int redirectFramebuffer(int target, int framebuffer) {
        if (!capturingClouds || target == GL30.GL_READ_FRAMEBUFFER) {
            return framebuffer;
        }
        if (framebuffer == sceneFramebuffer || framebuffer == fabulousCloudsFramebuffer) {
            return cloudsTarget.frameBufferId;
        }
        return framebuffer;
    }

    public static boolean interceptCloudCall(LevelRenderer levelRenderer) {
        if (!active || capturingClouds) {
            return false;
        }

        cloudCallReached = true;
        if (!cloudsCaptured) {
            return false;
        }

        cloudsCaptured = false;
        return compositeCapturedClouds(levelRenderer);
    }

    private static boolean compositeCapturedClouds(LevelRenderer levelRenderer) {
        RenderTarget fabulousClouds = levelRenderer.getCloudsTarget();
        if (fabulousClouds != null) {
            GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, cloudsTarget.frameBufferId);
            GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fabulousClouds.frameBufferId);
            GlStateManager._glBlitFrameBuffer(0, 0, cloudsTarget.width, cloudsTarget.height, 0, 0, fabulousClouds.width, fabulousClouds.height, GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT, GL30.GL_NEAREST);
            bindSceneFramebuffer();
            return true;
        }

        if (!ensureCompositeProgram()) {
            return false;
        }

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);
        RenderSystem.depthMask(true);

        GlStateManager._glUseProgram(compositeProgram);
        Uniform.uploadInteger(Uniform.glGetUniformLocation(compositeProgram, "ModulationCloudsSampler"), CLOUDS_UNIT);
        Uniform.uploadInteger(Uniform.glGetUniformLocation(compositeProgram, "ModulationCloudsDepthSampler"), CLOUDS_DEPTH_UNIT);
        bindBackgroundTextures();
        GlStateManager._glBindVertexArray(compositeVao);
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
        GlStateManager._glBindVertexArray(0);
        BufferUploader.invalidate();
        GlStateManager._glUseProgram(0);

        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        return true;
    }

    private static void bindSceneFramebuffer() {
        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, sceneFramebuffer);
    }

    private static boolean ensureCompositeProgram() {
        if (compositeProgram != 0) {
            return true;
        }
        if (compositeFailed) {
            return false;
        }

        compositeFailed = true;
        int vertex = compileShader(GL20.GL_VERTEX_SHADER, "improved_fog_clouds.vsh");
        int fragment = compileShader(GL20.GL_FRAGMENT_SHADER, "improved_fog_clouds.fsh");
        if (vertex == 0 || fragment == 0) {
            GL20.glDeleteShader(vertex);
            GL20.glDeleteShader(fragment);
            return false;
        }

        int program = GL20.glCreateProgram();
        GL20.glAttachShader(program, vertex);
        GL20.glAttachShader(program, fragment);
        GL20.glLinkProgram(program);
        GL20.glDeleteShader(vertex);
        GL20.glDeleteShader(fragment);
        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            Constants.LOG.warn("Improved fog cloud composite failed to link, clouds will be drawn twice: {}", GL20.glGetProgramInfoLog(program));
            GL20.glDeleteProgram(program);
            return false;
        }

        compositeProgram = program;
        compositeVao = GlStateManager._glGenVertexArrays();
        compositeFailed = false;
        return true;
    }

    private static int compileShader(int type, String name) {
        String source = GlslSnippets.load(name);
        if (source.isEmpty()) {
            return 0;
        }

        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);
        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            Constants.LOG.warn("Improved fog shader {} failed to compile, clouds will be drawn twice: {}", name, GL20.glGetShaderInfoLog(shader));
            GL20.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }

    public static void endFrame() {
        if (expectingCloudCall) {
            cloudsReplaced = !cloudCallReached;
            expectingCloudCall = false;
        }

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
        cloudsReplaced = false;
        expectingCloudCall = false;
        if (skyTarget != null) {
            skyTarget.destroyBuffers();
            cloudsTarget.destroyBuffers();
            skyTarget = null;
            cloudsTarget = null;
        }
    }
}
