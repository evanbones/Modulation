package com.evandev.modulation.mixin.vanilla;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.BiConsumer;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {

    @WrapOperation(
            method = "onTake",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ContainerLevelAccess;execute(Ljava/util/function/BiConsumer;)V")
    )
    private void modulation$preventAnvilDamage(ContainerLevelAccess instance, BiConsumer<Level, BlockPos> biConsumer, Operation<Void> original) {
        if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isDisableAnvilDamageEnabled)) {
            instance.execute((level, pos) -> level.levelEvent(1030, pos, 0));
            return;
        }
        original.call(instance, biConsumer);
    }
}
