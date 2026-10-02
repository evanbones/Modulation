package com.evandev.modulation.mixin.minecraft.clouds.client;

import com.evandev.modulation.Constants;
import com.evandev.modulation.client.ExtendedCloudMesher;
import com.evandev.modulation.modules.vanilla.ExtendedCloudsModule;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererCloudsMixin {

    @Unique
    private static final ExecutorService modulation$CLOUD_MESHER = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "Modulation Cloud Mesher");
        thread.setDaemon(true);
        return thread;
    });

    @Unique
    private final Tesselator modulation$cloudTesselator = new Tesselator(786432);

    @Shadow
    private ClientLevel level;

    @Shadow
    private int ticks;

    @Shadow
    private boolean generateClouds;

    @Shadow
    private VertexBuffer cloudBuffer;

    @Shadow
    private CloudStatus prevCloudsType;

    @Unique
    private int modulation$meshRange = Integer.MIN_VALUE;

    @Unique
    private Future<MeshData> modulation$cloudBuildTask;

    @Unique
    private boolean modulation$cloudStateValid;

    @Unique
    private double modulation$cloudX;

    @Unique
    private double modulation$cloudY;

    @Unique
    private double modulation$cloudZ;

    @Unique
    private Vec3 modulation$cloudColor = Vec3.ZERO;

    @Unique
    private int modulation$cellX;

    @Unique
    private int modulation$cellY;

    @Unique
    private int modulation$cellZ;

    @Unique
    private int modulation$meshCellX;

    @Unique
    private int modulation$meshCellY;

    @Unique
    private int modulation$meshCellZ;

    @Unique
    private int modulation$pendingCellX;

    @Unique
    private int modulation$pendingCellY;

    @Unique
    private int modulation$pendingCellZ;

    @Unique
    private float modulation$previousFogEnd;

    @Unique
    private static int modulation$cloudRange() {
        return ExtendedCloudsModule.ENABLE_EXTENDED_CLOUDS.on() ? ExtendedCloudsModule.CLOUD_RANGE.get() : -1;
    }

    @Shadow
    protected abstract MeshData buildClouds(Tesselator tesselator, double x, double y, double z, Vec3 cloudColor);

    @Unique
    private MeshData modulation$buildCloudMesh(Tesselator tesselator, double x, double y, double z, Vec3 color, CloudStatus status, int range) {
        if (range < 0) {
            return this.buildClouds(tesselator, x, y, z, color);
        }
        return ExtendedCloudMesher.build(tesselator, x, y, z, color, status, ExtendedCloudMesher.radiusCells(range));
    }

    @Inject(method = "onResourceManagerReload", at = @At("TAIL"))
    private void modulation$reloadCloudCells(ResourceManager resourceManager, CallbackInfo ci) {
        ExtendedCloudMesher.reload(resourceManager);
        this.generateClouds = true;
    }

    @Inject(method = "renderClouds", at = @At("HEAD"))
    private void modulation$captureCloudOrigin(PoseStack poseStack, Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        this.modulation$cloudStateValid = false;
        if (this.level == null) {
            return;
        }

        int range = modulation$cloudRange();
        if (range >= 0) {
            ExtendedCloudMesher.ensureLoaded();
        }
        if (range != this.modulation$meshRange) {
            this.modulation$meshRange = range;
            this.generateClouds = true;
        }

        float cloudHeight = this.level.effects().getCloudHeight();
        if (Float.isNaN(cloudHeight)) {
            return;
        }

        double drift = ((float) this.ticks + partialTick) * 0.03F;
        double x = (camX + drift) / 12.0;
        double y = cloudHeight - (float) camY + 0.33F;
        double z = camZ / 12.0 + 0.33F;
        x -= Mth.floor(x / 2048.0) * 2048;
        z -= Mth.floor(z / 2048.0) * 2048;

        this.modulation$cloudX = x;
        this.modulation$cloudY = y;
        this.modulation$cloudZ = z;
        this.modulation$cloudColor = this.level.getCloudColor(partialTick);
        this.modulation$cellX = (int) Math.floor(x);
        this.modulation$cellY = (int) Math.floor(y / 4.0);
        this.modulation$cellZ = (int) Math.floor(z);
        this.modulation$cloudStateValid = true;
    }

    @WrapOperation(
            method = "renderClouds",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;generateClouds:Z",
                    opcode = Opcodes.GETFIELD
            )
    )
    private boolean modulation$buildCloudsAsync(LevelRenderer instance, Operation<Boolean> original) {
        boolean dirty = original.call(instance);
        if (!ExtendedCloudsModule.ASYNC_CLOUD_MESHING.on() || !this.modulation$cloudStateValid) {
            this.modulation$discardPendingMesh();
            return dirty;
        }

        if (dirty && this.modulation$cloudBuildTask == null) {
            this.generateClouds = false;
            this.modulation$pendingCellX = this.modulation$cellX;
            this.modulation$pendingCellY = this.modulation$cellY;
            this.modulation$pendingCellZ = this.modulation$cellZ;

            double x = this.modulation$cloudX;
            double y = this.modulation$cloudY;
            double z = this.modulation$cloudZ;
            Vec3 color = this.modulation$cloudColor;
            CloudStatus status = this.prevCloudsType;
            int range = this.modulation$meshRange;
            this.modulation$cloudBuildTask = modulation$CLOUD_MESHER.submit(() -> {
                this.modulation$cloudTesselator.clear();
                return this.modulation$buildCloudMesh(this.modulation$cloudTesselator, x, y, z, color, status, range);
            });
        }

        if (this.modulation$cloudBuildTask != null && this.modulation$cloudBuildTask.isDone()) {
            MeshData mesh = this.modulation$takeMesh();
            if (mesh != null) {
                if (this.cloudBuffer != null) {
                    this.cloudBuffer.close();
                }

                this.cloudBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
                this.cloudBuffer.bind();
                this.cloudBuffer.upload(mesh);
                VertexBuffer.unbind();
                this.modulation$meshCellX = this.modulation$pendingCellX;
                this.modulation$meshCellY = this.modulation$pendingCellY;
                this.modulation$meshCellZ = this.modulation$pendingCellZ;
            }
        }

        return false;
    }

    @Unique
    private MeshData modulation$takeMesh() {
        Future<MeshData> task = this.modulation$cloudBuildTask;
        this.modulation$cloudBuildTask = null;
        try {
            return task.get();
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException | RuntimeException failure) {
            Constants.LOG.error("Failed to build extended cloud mesh", failure);
        }
        return null;
    }

    @Unique
    private void modulation$discardPendingMesh() {
        if (this.modulation$cloudBuildTask == null) {
            return;
        }

        if (!this.modulation$cloudBuildTask.isDone()) {
            this.modulation$cloudBuildTask.cancel(false);
            this.modulation$cloudBuildTask = null;
            return;
        }

        MeshData mesh = this.modulation$takeMesh();
        if (mesh != null) {
            mesh.close();
        }
    }

    @WrapOperation(
            method = "renderClouds",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;buildClouds(Lcom/mojang/blaze3d/vertex/Tesselator;DDDLnet/minecraft/world/phys/Vec3;)Lcom/mojang/blaze3d/vertex/MeshData;")
    )
    private MeshData modulation$buildExtendedClouds(LevelRenderer instance, Tesselator tesselator, double x, double y, double z, Vec3 color, Operation<MeshData> original) {
        if (this.modulation$meshRange < 0) {
            return original.call(instance, tesselator, x, y, z, color);
        }
        return ExtendedCloudMesher.build(tesselator, x, y, z, color, this.prevCloudsType, ExtendedCloudMesher.radiusCells(this.modulation$meshRange));
    }

    @WrapOperation(
            method = "renderClouds",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V")
    )
    private void modulation$offsetStaleClouds(PoseStack poseStack, float x, float y, float z, Operation<Void> original) {
        if (!ExtendedCloudsModule.ASYNC_CLOUD_MESHING.on() || !this.modulation$cloudStateValid) {
            original.call(poseStack, x, y, z);
            return;
        }

        original.call(poseStack,
                x + (this.modulation$meshCellX - this.modulation$cellX),
                y - (this.modulation$meshCellY - this.modulation$cellY) * 4.0F,
                z + (this.modulation$meshCellZ - this.modulation$cellZ));
    }

    @Inject(
            method = "renderClouds",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V")
    )
    private void modulation$extendCloudFog(PoseStack poseStack, Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        this.modulation$previousFogEnd = RenderSystem.getShaderFogEnd();
        int range = modulation$cloudRange();
        float terrainFogEnd = Math.max(Minecraft.getInstance().gameRenderer.getRenderDistance(), 32.0F);
        if (range >= 0 && this.modulation$previousFogEnd >= terrainFogEnd) {
            RenderSystem.setShaderFogEnd(Math.max(this.modulation$previousFogEnd, range * 16.0F));
        }
    }

    @Inject(
            method = "renderClouds",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V")
    )
    private void modulation$restoreCloudFog(PoseStack poseStack, Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        RenderSystem.setShaderFogEnd(this.modulation$previousFogEnd);
    }
}
