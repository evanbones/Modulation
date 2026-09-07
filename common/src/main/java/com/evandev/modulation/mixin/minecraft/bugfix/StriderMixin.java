package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.monster.Strider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModAbsent("debugify")
@Mixin(Strider.class)
public class StriderMixin {

    @ModifyExpressionValue(
            method = "finalizeSpawn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I", ordinal = 0)
    )
    private int modulation$noPeacefulStriderSaddles(int roll) {
        if (roll == 0 && ((Strider) (Object) this).level().getDifficulty() == Difficulty.PEACEFUL) {
            return VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixPeacefulStriderSaddlesEnabled) ? 1 : roll;
        }
        return roll;
    }
}
