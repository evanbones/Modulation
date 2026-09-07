package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSetCommandMinecartPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModAbsent("debugify")
@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {

    @WrapOperation(
            method = "handleSetCommandMinecart",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;sendSystemMessage(Lnet/minecraft/network/chat/Component;)V", ordinal = 2)
    )
    private void modulation$noEmptyCommandMinecartMessage(ServerPlayer player, Component message, Operation<Void> original, @Local(argsOnly = true) ServerboundSetCommandMinecartPacket packet) {
        String command = packet.getCommand();
        if (!command.isEmpty() || !VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixEmptyCommandMinecartMessageEnabled)) {
            original.call(player, message);
        }
    }
}
