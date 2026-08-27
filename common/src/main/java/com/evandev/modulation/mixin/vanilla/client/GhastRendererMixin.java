package com.evandev.modulation.mixin.vanilla.client;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.client.GhastAttackTimeAccess;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.GhastRenderer;
import net.minecraft.world.entity.monster.Ghast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GhastRenderer.class)
public class GhastRendererMixin {

    @Inject(method = "scale(Lnet/minecraft/world/entity/monster/Ghast;Lcom/mojang/blaze3d/vertex/PoseStack;F)V", at = @At("HEAD"), cancellable = true)
    protected void modulation$scaleGhastCharging(Ghast ghast, PoseStack poseStack, float partialTickTime, CallbackInfo ci) {
        if (!ModuleManager.isEnabled("vanilla_bugfixes", VanillaBugfixesModule.class, VanillaBugfixesModule::isGhastChargingEnabled)) {
            return;
        }

        ci.cancel();
        float base = 4.5F;
        float hScale = base;
        float vScale = base;

        if (ghast.isCharging()) {
            int attackTime = ((GhastAttackTimeAccess) ghast).modulation$getAttackTime();
            float a = (((attackTime + (ghast.isAlive() ? partialTickTime : 0)) + 10) / 20.0F);
            if (a > 1.0F) a = 1.0F;
            if (a < 0.0F) a = 0.0F;
            a = 1.0F / (a * a * a * a * a * 2.0F + 1.0F);
            hScale = (8.0F + 1.0F / a) / 2.0F;
            vScale = (8.0F + a) / 2.0F;
        }

        poseStack.scale(hScale, vScale, hScale);
    }
}
