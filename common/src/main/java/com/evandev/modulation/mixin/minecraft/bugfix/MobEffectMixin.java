package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModAbsent("debugify")
@Mixin(MobEffect.class)
public class MobEffectMixin {

    @ModifyReturnValue(method = "getCategory", at = @At("RETURN"))
    private MobEffectCategory modulation$harmfulNeutralEffects(MobEffectCategory category) {
        if (category != MobEffectCategory.NEUTRAL) {
            return category;
        }

        Object self = this;
        if (self == MobEffects.BAD_OMEN.value() && VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixBadOmenTooltipEnabled)) {
            return MobEffectCategory.HARMFUL;
        }
        if (self == MobEffects.GLOWING.value() && VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixGlowingTooltipEnabled)) {
            return MobEffectCategory.HARMFUL;
        }
        return category;
    }
}
