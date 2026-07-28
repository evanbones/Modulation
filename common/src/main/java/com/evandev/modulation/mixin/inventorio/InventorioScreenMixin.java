package com.evandev.modulation.mixin.inventorio;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaGuiModule;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.rubixdev.inventorio.client.ui.InventorioScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InventorioScreen.class)
public class InventorioScreenMixin {

    @WrapOperation(
            method = "containerTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasInfiniteItems()Z"))
    private boolean modulation$wrapTick(MultiPlayerGameMode instance, Operation<Boolean> original) {
        if (ModuleManager.isEnabled("vanilla_gui", VanillaGuiModule.class, VanillaGuiModule::isDisableCreativeInventoryEnabled)) return false;
        return original.call(instance);
    }

    @WrapOperation(
            method = "init",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasInfiniteItems()Z"))
    private boolean modulation$wrapInit(MultiPlayerGameMode instance, Operation<Boolean> original) {
        if (ModuleManager.isEnabled("vanilla_gui", VanillaGuiModule.class, VanillaGuiModule::isDisableCreativeInventoryEnabled)) return false;
        return original.call(instance);
    }
}
