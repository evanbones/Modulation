package com.evandev.modulation.mixin.vanilla;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Inject(method = "modifyDamage", at = @At("RETURN"), cancellable = true)
    private static void modulation$bedrockImpaling(ServerLevel level, ItemStack tool, Entity entity, DamageSource damageSource, float damage, CallbackInfoReturnable<Float> cir) {
        if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isBedrockImpalingEnabled)) {
            if (entity.isInWaterOrRain()) {
                var impalingHolder = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.IMPALING);
                if (impalingHolder.isPresent()) {
                    int impalingLevel = EnchantmentHelper.getItemEnchantmentLevel(impalingHolder.get(), tool);
                    if (impalingLevel > 0 && !entity.getType().is(EntityTypeTags.SENSITIVE_TO_IMPALING)) {
                        cir.setReturnValue(cir.getReturnValue() + (2.5F * impalingLevel));
                    }
                }
            }
        }
    }
}
