package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.FishingRodItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FishingRodItem.class)
public class FishingRodItemMixin {

    @ModifyArg(
            method = "use",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V")
    )
    private SoundSource modulation$fishingBobberSoundSource(SoundSource source) {
        return VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixFishingBobberSoundEnabled) ? SoundSource.PLAYERS : source;
    }
}
