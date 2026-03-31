package com.evandev.modulation.mixin.minecraft;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.VanillaModule;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(method = "setServerLevel", at = @At("RETURN"))
    private void modulation$onSetServerLevel(ServerLevel level, CallbackInfo ci) {
        VanillaModule module = (VanillaModule) ModuleManager.getModule("vanilla");
        if (module != null && module.isFixExperienceLossEnabled()) {
            ServerPlayer player = (ServerPlayer) (Object) this;

            if (player.connection != null) {
                player.connection.send(new ClientboundSetExperiencePacket(player.experienceProgress, player.totalExperience, player.experienceLevel));
            }
        }
    }
}