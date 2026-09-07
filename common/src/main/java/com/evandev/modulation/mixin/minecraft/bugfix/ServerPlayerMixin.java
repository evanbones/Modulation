package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(method = "setServerLevel", at = @At("RETURN"))
    private void modulation$onSetServerLevel(ServerLevel level, CallbackInfo ci) {
        if (ModuleManager.isEnabled("vanilla_bugfixes", VanillaBugfixesModule.class, VanillaBugfixesModule::isFixExperienceLossEnabled)) {
            ServerPlayer player = (ServerPlayer) (Object) this;

            if (player.connection != null) {
                player.connection.send(new ClientboundSetExperiencePacket(player.experienceProgress, player.totalExperience, player.experienceLevel));
            }
        }
    }

    @IfModAbsent("debugify")
    @Inject(method = "setGameMode", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;stopRiding()V"))
    private void modulation$onBecomeSpectator(CallbackInfoReturnable<Boolean> cir) {
        ServerPlayer player = (ServerPlayer) (Object) this;

        if (ModuleManager.isEnabled("vanilla_bugfixes", VanillaBugfixesModule.class, VanillaBugfixesModule::isFixSpectatorItemUseEnabled)) {
            player.releaseUsingItem();
        }

        if (ModuleManager.isEnabled("vanilla_bugfixes", VanillaBugfixesModule.class, VanillaBugfixesModule::isFixSpectatorFreezingEnabled)) {
            player.setTicksFrozen(0);
        }
    }

    @IfModAbsent("debugify")
    @Inject(method = "die", at = @At("RETURN"))
    private void modulation$stopUsingItemOnDeath(DamageSource source, CallbackInfo ci) {
        if (VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixDeathScreenItemUseEnabled)) {
            ((ServerPlayer) (Object) this).stopUsingItem();
        }
    }

}
