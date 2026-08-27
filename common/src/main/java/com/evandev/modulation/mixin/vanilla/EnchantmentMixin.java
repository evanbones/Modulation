package com.evandev.modulation.mixin.vanilla;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {

    @Inject(method = "areCompatible", at = @At("HEAD"), cancellable = true)
    private static void modulation$sharpnessImpalingIncompatible(Holder<Enchantment> first, Holder<Enchantment> second, CallbackInfoReturnable<Boolean> cir) {
        if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isTridentsAcceptSharpnessEnabled)) {
            if ((first.is(Enchantments.SHARPNESS) && second.is(Enchantments.IMPALING)) || (first.is(Enchantments.IMPALING) && second.is(Enchantments.SHARPNESS))) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true)
    private void modulation$tridentCanEnchant(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isTridentsAcceptSharpnessEnabled)) {
            if (stack.is(Items.TRIDENT)) {
                Enchantment self = (Enchantment) (Object) this;
                if (self.effects().has(EnchantmentEffectComponents.DAMAGE)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "isSupportedItem", at = @At("HEAD"), cancellable = true)
    private void modulation$tridentIsSupportedItem(ItemStack item, CallbackInfoReturnable<Boolean> cir) {
        if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isTridentsAcceptSharpnessEnabled)) {
            if (item.is(Items.TRIDENT)) {
                Enchantment self = (Enchantment) (Object) this;
                if (self.effects().has(EnchantmentEffectComponents.DAMAGE)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
