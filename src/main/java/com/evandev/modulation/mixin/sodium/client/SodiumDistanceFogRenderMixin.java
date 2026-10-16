package com.evandev.modulation.mixin.sodium.client;

//? if >=26.2 {
/*import com.evandev.modulation.client.DistanceFog;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderPass;
import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import net.caffeinemc.mods.sodium.client.render.chunk.DefaultChunkRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModLoaded("sodium")
@IfMinecraftVersion(minVersion = "26.2")
@Mixin(value = DefaultChunkRenderer.class, remap = false)
public abstract class SodiumDistanceFogRenderMixin {

    //? if >=26.3 {
    /^@Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/renderpearl/api/commands/RenderPass;setPipeline", shift = At.Shift.AFTER))
    ^///?} else {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;setPipeline", shift = At.Shift.AFTER))
    //?}
    private void modulation$bindDistanceFogBackground(CallbackInfo ci, @Local RenderPass pass) {
        DistanceFog.bindSodiumBackground(pass);
    }
}
*///?} else {
import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import org.spongepowered.asm.mixin.Mixin;

@IfMinecraftVersion(minVersion = "26.2")
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.DefaultChunkRenderer", remap = false)
public abstract class SodiumDistanceFogRenderMixin {
}
//?}
