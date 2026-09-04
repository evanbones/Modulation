package com.evandev.modulation.mixin.vanilla.accessor;

import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FogRenderer.class)
public interface FogRendererAccessor {

    @Accessor("fogRed")
    static float modulation$getFogRed() {
        throw new AssertionError();
    }

    @Accessor("fogGreen")
    static float modulation$getFogGreen() {
        throw new AssertionError();
    }

    @Accessor("fogBlue")
    static float modulation$getFogBlue() {
        throw new AssertionError();
    }
}
