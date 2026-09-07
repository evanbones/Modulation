package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.decoration.ItemFrame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModAbsent("debugify")
@Mixin(ItemFrame.class)
public class ItemFrameMixin {

    @WrapOperation(
            method = "setItem(Lnet/minecraft/world/item/ItemStack;Z)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/ItemFrame;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V")
    )
    private void modulation$silentItemFrameLoad(ItemFrame frame, SoundEvent sound, float volume, float pitch, Operation<Void> original, @Local(argsOnly = true, ordinal = 0) boolean updateNeighbours) {
        if (updateNeighbours || !ModuleManager.isEnabled("vanilla_bugfixes", VanillaBugfixesModule.class, VanillaBugfixesModule::isFixItemFrameLoadSoundEnabled)) {
            original.call(frame, sound, volume, pitch);
        }
    }
}
