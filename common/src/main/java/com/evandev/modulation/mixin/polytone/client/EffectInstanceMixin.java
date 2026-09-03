package com.evandev.modulation.mixin.polytone.client;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.client.HorizonFogState;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.EffectInstance;
import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EffectInstance.class)
public abstract class EffectInstanceMixin {

    @Unique
    private static final String modulation$SUNBATHING_PREFIX = "sunbathing:";

    @Unique
    private static final String modulation$FOG_UNIFORM = "ModulationFogRange";

    @Unique
    private static final String modulation$SKY_UNIFORM = "ModulationSkyVisibility";

    @Unique
    private boolean modulation$locationsResolved;

    @Unique
    private int modulation$fogLocation;

    @Unique
    private int modulation$skyLocation;

    @Shadow
    public abstract String getName();

    @Shadow
    public abstract int getId();

    @Inject(method = "apply", at = @At("TAIL"))
    private void modulation$uploadFogRange(CallbackInfo ci) {
        String name = this.getName();
        if (name == null || !name.startsWith(modulation$SUNBATHING_PREFIX)) {
            return;
        }

        if (!this.modulation$locationsResolved) {
            this.modulation$fogLocation = Uniform.glGetUniformLocation(this.getId(), modulation$FOG_UNIFORM);
            this.modulation$skyLocation = Uniform.glGetUniformLocation(this.getId(), modulation$SKY_UNIFORM);
            this.modulation$locationsResolved = true;
        }

        if (this.modulation$fogLocation < 0 && this.modulation$skyLocation < 0) {
            return;
        }

        boolean enabled = ModuleManager.isEnabled("vanilla_bugfixes", VanillaBugfixesModule.class, VanillaBugfixesModule::isFixHorizonLineEnabled);

        if (this.modulation$fogLocation >= 0) {
            if (enabled) {
                GL20.glUniform2f(this.modulation$fogLocation, HorizonFogState.getStart(), HorizonFogState.getEnd());
            } else {
                GL20.glUniform2f(this.modulation$fogLocation, -1.0F, -1.0F);
            }
        }

        if (this.modulation$skyLocation >= 0) {
            GL20.glUniform1f(this.modulation$skyLocation, enabled ? HorizonFogState.getSkyVisibility() : 0.0F);
        }
    }
}
