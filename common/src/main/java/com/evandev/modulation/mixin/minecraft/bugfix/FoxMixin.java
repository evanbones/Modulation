package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModAbsent("debugify")
@Mixin(Fox.class)
public class FoxMixin {

    @ModifyExpressionValue(
            method = {"dropAllDeathLoot", "dropEquipment"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z")
    )
    private boolean modulation$foxLootFollowsGamerule(boolean isEmpty) {
        if (!isEmpty && !((Fox) (Object) this).level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            return VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixFoxMobLootGameruleEnabled);
        }
        return isEmpty;
    }
}
