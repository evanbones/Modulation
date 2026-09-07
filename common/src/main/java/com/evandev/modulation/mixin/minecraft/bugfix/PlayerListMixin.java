package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserBanList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

    @Shadow
    @Final
    private List<ServerPlayer> players;

    @Shadow
    public abstract void sendPlayerPermissionLevel(ServerPlayer player);

    @WrapOperation(
            method = "canPlayerLogin",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/UserBanList;isBanned(Lcom/mojang/authlib/GameProfile;)Z")
    )
    private boolean modulation$dropExpiredBansBeforeCheck(UserBanList bans, GameProfile profile, Operation<Boolean> original) {
        if (VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixExpiredBanLoginEnabled)) {
            bans.get(profile);
        }
        return original.call(bans, profile);
    }

    @Inject(method = "reloadResources", at = @At("RETURN"))
    private void modulation$resendCommandSuggestions(CallbackInfo ci) {
        if (VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixReloadCommandSuggestionsEnabled)) {
            for (ServerPlayer player : this.players) {
                this.sendPlayerPermissionLevel(player);
            }
        }
    }
}
