package com.evandev.modulation.mixin.minecraft.accessor;

//? if >=26.3 {
/*import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.geometry.ItemQuads;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@IfMinecraftVersion(minVersion = "26.3")
@Mixin(ItemStackRenderState.LayerRenderState.class)
public interface ItemLayerRenderStateAccessor {

    @Accessor("quads")
    ItemQuads modulation$getQuads();
}
*///?} else {
import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import org.spongepowered.asm.mixin.Mixin;

@IfMinecraftVersion(minVersion = "26.3")
@Mixin(targets = "net.minecraft.client.renderer.item.ItemStackRenderState$LayerRenderState")
public interface ItemLayerRenderStateAccessor {
}
//?}
