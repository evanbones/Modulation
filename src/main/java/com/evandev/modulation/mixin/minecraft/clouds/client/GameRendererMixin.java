package com.evandev.modulation.mixin.minecraft.clouds.client;

//? if <26.1 {
import com.evandev.modulation.modules.vanilla.ExtendedCloudsModule;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @ModifyReturnValue(method = "getDepthFar", at = @At("RETURN"))
    private float modulation$extendDepthFar(float original) {
        if (!ExtendedCloudsModule.EXTEND_FRUSTUM.on()) {
            return original;
        }
        return Math.max(original, ExtendedCloudsModule.CLOUD_RANGE.get() * 16.0F);
    }
}
//?}
