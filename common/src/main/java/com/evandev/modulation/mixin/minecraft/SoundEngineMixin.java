package com.evandev.modulation.mixin.minecraft;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.MusicModule;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {

    @Inject(method = "calculateVolume(Lnet/minecraft/client/resources/sounds/SoundInstance;)F", at = @At("RETURN"), cancellable = true)
    private void onCalculateVolume(SoundInstance sound, CallbackInfoReturnable<Float> cir) {
        if (sound.getSource() == SoundSource.MUSIC || sound.getSource() == SoundSource.RECORDS) {
            MusicModule module = (MusicModule) ModuleManager.getModule("music");
            if (module != null && module.shouldLoad()) {
                if (!module.isCombatSound(sound)) {
                    cir.setReturnValue(cir.getReturnValueF() * module.getVanillaMusicMultiplier());
                }
            }
        }
    }
}