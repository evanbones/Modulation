package com.evandev.modulation.mixin.minecraft.accessor;

//? if >=26.1 {
/*import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemStackRenderState.class)
public interface ItemStackRenderStateAccessor {

    @Accessor("layers")
    ItemStackRenderState.LayerRenderState[] modulation$getLayers();

    @Accessor("activeLayerCount")
    int modulation$getActiveLayerCount();
}
*///?}
