package com.evandev.modulation.mixin.sodium.client;

//? if >=26.2 {
/*import com.evandev.modulation.client.DistanceFog;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.caffeinemc.mods.sodium.client.render.chunk.ShaderChunkRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//? if >=26.3
/^import net.minecraft.client.renderer.oit.OitPipelineSet;^/

@IfModLoaded("sodium")
@IfMinecraftVersion(minVersion = "26.2")
@Mixin(value = ShaderChunkRenderer.class, remap = false)
public abstract class SodiumDistanceFogPipelineMixin {

    //? if >=26.3 {
    /^@WrapOperation(method = "createShader", at = @At(value = "INVOKE", target = "Lcom/mojang/renderpearl/api/pipeline/RenderPipeline$Builder;build()Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;"))
    ^///?} else {
    @WrapOperation(method = "createShader", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderPipeline$Builder;build()Lcom/mojang/blaze3d/pipeline/RenderPipeline;"))
    //?}
    private RenderPipeline modulation$buildDistanceFogVariant(RenderPipeline.Builder builder, Operation<RenderPipeline> original, @Local(argsOnly = true) TerrainRenderPass pass) {
        RenderPipeline pipeline = original.call(builder);
        DistanceFog.registerSodiumPipeline(pipeline, original.call(DistanceFog.sodiumVariant(builder, pass.getPipeline().getLocation().getPath())));
        return pipeline;
    }

    //? if >=26.3 {
    /^@WrapOperation(method = "createOITShader", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/oit/OitPipelineSet$Builder;build()Lnet/minecraft/client/renderer/oit/OitPipelineSet;"))
    private OitPipelineSet modulation$buildDistanceFogOitVariant(OitPipelineSet.Builder builder, Operation<OitPipelineSet> original, @Local(argsOnly = true) TerrainRenderPass pass) {
        OitPipelineSet pipelines = original.call(builder);
        String path = pass.getPipeline().getLocation().getPath() + "_oit_accumulate";
        OitPipelineSet variants = original.call(builder.withAccumulateModifier(accumulate -> DistanceFog.sodiumVariant(accumulate, path)));
        DistanceFog.registerSodiumPipeline(pipelines.accumulatePipeline(), variants.accumulatePipeline());
        return pipelines;
    }
    ^///?}

    @ModifyReturnValue(method = "compileProgram", at = @At("RETURN"))
    private RenderPipeline modulation$useDistanceFogVariant(RenderPipeline pipeline) {
        return DistanceFog.sodiumPipeline(pipeline);
    }
}
*///?} else {
import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import org.spongepowered.asm.mixin.Mixin;

@IfMinecraftVersion(minVersion = "26.2")
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.ShaderChunkRenderer", remap = false)
public abstract class SodiumDistanceFogPipelineMixin {
}
//?}
