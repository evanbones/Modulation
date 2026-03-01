package com.evandev.modulation.mixin.minecraft;

import com.evandev.modulation.registry.ModRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "pick", at = @At("TAIL"))
    private void onPick(float partialTicks, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        Entity entity = mc.getCameraEntity();

        if (entity != null && mc.player != null && ModRegistry.CHAIN_STAFF != null) {
            if (mc.player.isUsingItem() && mc.player.getUseItem().is(ModRegistry.CHAIN_STAFF)) {
                mc.hitResult = entity.level().clip(new ClipContext(
                        entity.getEyePosition(partialTicks),
                        entity.getEyePosition(partialTicks).add(entity.getViewVector(partialTicks).scale(500.0)),
                        ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity));
                mc.crosshairPickEntity = null;
            }
        }
    }
}