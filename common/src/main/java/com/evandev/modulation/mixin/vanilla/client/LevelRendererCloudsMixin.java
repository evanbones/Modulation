package com.evandev.modulation.mixin.vanilla.client;

import com.evandev.modulation.Constants;
import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.ExtendedCloudsModule;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererCloudsMixin {

    @Unique
    private static final int MAX_CLOUD_CELLS = 64;

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
    private int lastViewDistance;

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
    private static ExtendedCloudsModule modulation$module() {
        return ModuleManager.getModule("extended_clouds", ExtendedCloudsModule.class);
    }

    @Shadow
    protected abstract MeshData buildClouds(Tesselator tesselator, double x, double y, double z, Vec3 cloudColor);

    @Unique
    private int modulation$cloudCells() {
        ExtendedCloudsModule module = modulation$module();
        if (module == null || !module.isExtendedCloudsEnabled()) {
            return -1;
        }
        int cells = (int) (Math.max(this.lastViewDistance, 2) * module.getCloudDistanceMultiplier());
        return Math.max(1, Math.min(MAX_CLOUD_CELLS, cells));
    }

    @Inject(method = "renderClouds", at = @At("HEAD"))
    private void modulation$captureCloudOrigin(PoseStack poseStack, Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        this.modulation$cloudStateValid = false;
        if (this.level == null) {
            return;
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
        ExtendedCloudsModule module = modulation$module();
        if (module == null || !module.isAsyncCloudMeshingEnabled() || !this.modulation$cloudStateValid) {
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
            this.modulation$cloudBuildTask = modulation$CLOUD_MESHER.submit(() -> {
                this.modulation$cloudTesselator.clear();
                return this.buildClouds(this.modulation$cloudTesselator, x, y, z, color);
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
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V")
    )
    private void modulation$offsetStaleClouds(PoseStack poseStack, float x, float y, float z, Operation<Void> original) {
        ExtendedCloudsModule module = modulation$module();
        if (module == null || !module.isAsyncCloudMeshingEnabled() || !this.modulation$cloudStateValid) {
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
        ExtendedCloudsModule module = modulation$module();
        if (module != null && module.isExtendedCloudsEnabled()) {
            RenderSystem.setShaderFogEnd((float) (this.modulation$previousFogEnd * module.getCloudDistanceMultiplier()));
        }
    }

    @Inject(
            method = "renderClouds",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V")
    )
    private void modulation$restoreCloudFog(PoseStack poseStack, Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        RenderSystem.setShaderFogEnd(this.modulation$previousFogEnd);
    }

    @ModifyConstant(method = "buildClouds", constant = @Constant(intValue = -3))
    private int modulation$fancyCloudsStart(int constant) {
        int cells = this.modulation$cloudCells();
        return cells < 0 ? constant : -(cells - 1);
    }

    @ModifyConstant(method = "buildClouds", constant = @Constant(intValue = 4))
    private int modulation$fancyCloudsEnd(int constant) {
        int cells = this.modulation$cloudCells();
        return cells < 0 ? constant : cells;
    }

    @ModifyConstant(method = "buildClouds", constant = @Constant(intValue = -32))
    private int modulation$fastCloudsStart(int constant) {
        int cells = this.modulation$cloudCells();
        return cells < 0 ? constant : -(cells * 8);
    }

    @ModifyConstant(
            method = "buildClouds",
            constant = {
                    @Constant(intValue = 32, ordinal = 1),
                    @Constant(intValue = 32, ordinal = 2)
            }
    )
    private int modulation$fastCloudsEnd(int constant) {
        int cells = this.modulation$cloudCells();
        return cells < 0 ? constant : cells * 8;
    }
}
