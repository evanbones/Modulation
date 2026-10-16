package com.evandev.modulation.mixin.minecraft.accessor;

//? if >=26.1 {
/*import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderPipelines.class)
public interface RenderPipelinesAccessor {

    @Accessor("TERRAIN_SNIPPET")
    static RenderPipeline.Snippet modulation$getTerrainSnippet() {
        throw new AssertionError();
    }

    //? if >=26.3 {
    /^@Accessor("MULTIDRAW_TERRAIN_SNIPPET")
    static RenderPipeline.Snippet modulation$getMultidrawTerrainSnippet() {
        throw new AssertionError();
    }

    @Accessor("GENERIC_BLOCKS_SNIPPET")
    static RenderPipeline.Snippet modulation$getGenericBlocksSnippet() {
        throw new AssertionError();
    }
    ^///?}
}
*///?}
