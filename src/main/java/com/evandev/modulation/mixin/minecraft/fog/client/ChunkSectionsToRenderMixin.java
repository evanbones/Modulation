package com.evandev.modulation.mixin.minecraft.fog.client;

//? if >=26.1 {
/*import com.evandev.modulation.client.DistanceFog;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//? if >=26.3 {
/^import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.pipeline.IndexType;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
^///?}

@Mixin(ChunkSectionsToRender.class)
public abstract class ChunkSectionsToRenderMixin {

    //? if >=26.3 {
    /^@WrapOperation(
            method = "renderLayers",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;render(Lnet/minecraft/client/renderer/chunk/ChunkSectionLayer;Lcom/mojang/renderpearl/api/commands/RenderPass;Lcom/mojang/renderpearl/api/buffers/GpuBuffer;Lcom/mojang/renderpearl/api/pipeline/IndexType;Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;)V")
    )
    private void modulation$useDistanceFogPipeline(ChunkSectionsToRender instance, ChunkSectionLayer layer, RenderPass renderPass, GpuBuffer indexBuffer, IndexType indexType,
                                                   RenderPipeline override, RenderPipeline multidrawOverride, Operation<Void> original) {
        if (!DistanceFog.isActive()) {
            original.call(instance, layer, renderPass, indexBuffer, indexType, override, multidrawOverride);
            return;
        }
        RenderPipeline single = DistanceFog.terrainPipeline(override != null ? override : layer.pipeline(false));
        RenderPipeline multidraw = DistanceFog.terrainPipeline(multidrawOverride != null ? multidrawOverride : layer.pipeline(true));
        DistanceFog.bindBackground(renderPass);
        original.call(instance, layer, renderPass, indexBuffer, indexType, single, multidraw);
    }
    ^///?} else {
    @WrapOperation(
            method = "renderGroup",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;setPipeline(Lcom/mojang/blaze3d/pipeline/RenderPipeline;)V")
    )
    private void modulation$useDistanceFogPipeline(RenderPass renderPass, RenderPipeline pipeline, Operation<Void> original) {
        RenderPipeline replacement = DistanceFog.terrainPipeline(pipeline);
        original.call(renderPass, replacement);
        if (replacement != pipeline) {
            DistanceFog.bindBackground(renderPass);
        }
    }
    //?}
}
*///?}
