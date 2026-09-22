package com.evandev.modulation.mixin.minecraft.visual.client;

import com.evandev.modulation.modules.vanilla.smoothlight.LightTransitions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacketData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Shadow
    private ClientLevel level;

    @Inject(method = "applyLightData", at = @At("RETURN"))
    private void modulation$refreshLightTransitions(int x, int z, ClientboundLightUpdatePacketData data, CallbackInfo ci) {
        if (this.level != null) {
            LightTransitions.refresh(this.level, x, z);
        }
    }
}
