package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.raid.Raid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@IfModAbsent("debugify")
@Mixin(Raid.class)
public class RaidMixin {

    @ModifyArg(
            method = "playSound",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundSoundPacket;<init>(Lnet/minecraft/core/Holder;Lnet/minecraft/sounds/SoundSource;DDDFFJ)V")
    )
    private SoundSource modulation$raidHornSoundSource(SoundSource source) {
        return VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixRaidHornSoundEnabled) ? SoundSource.HOSTILE : source;
    }
}
