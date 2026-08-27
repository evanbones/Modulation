package com.evandev.modulation.mixin.vanilla;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin() {
        super(null, null);
    }

    @Shadow
    public abstract InteractionResult interactOn(Entity entityToInteractOn, InteractionHand hand);

    @Inject(method = "getProjectile", at = @At("RETURN"), cancellable = true)
    private void modulation$infinityBowNoArrow(ItemStack shootable, CallbackInfoReturnable<ItemStack> cir) {
        if (cir.getReturnValue().isEmpty() && shootable.getItem() instanceof BowItem) {
            if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isInfibowsEnabled)) {
                var infinityHolder = this.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.INFINITY);
                if (infinityHolder.isPresent() && EnchantmentHelper.getItemEnchantmentLevel(infinityHolder.get(), shootable) > 0) {
                    cir.setReturnValue(new ItemStack(Items.ARROW));
                }
            }
        }
    }

    @Inject(method = "interactOn", at = @At("RETURN"), cancellable = true)
    private void modulation$fireAspectInteractEntity(Entity entityToInteractOn, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isFireAspectIsFlintAndSteelEnabled)
                && cir.getReturnValue() == InteractionResult.PASS) {
            Player self = (Player) (Object) this;
            ItemStack stack = self.getItemInHand(hand);
            if (!stack.isEmpty()) {
                var fireAspectHolder = self.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.FIRE_ASPECT);
                if (fireAspectHolder.isPresent() && EnchantmentHelper.getItemEnchantmentLevel(fireAspectHolder.get(), stack) > 0) {
                    ItemStack flintAndSteel = new ItemStack(Items.FLINT_AND_STEEL);
                    try {
                        self.setItemInHand(hand, flintAndSteel);
                        InteractionResult ar = this.interactOn(entityToInteractOn, hand);
                        if (ar.consumesAction()) {
                            self.swing(hand, true);
                            self.level().playSound(null, entityToInteractOn.getX(), entityToInteractOn.getY(), entityToInteractOn.getZ(),
                                    SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, self.level().getRandom().nextFloat() * 0.4F + 0.8F);
                            if (flintAndSteel.getDamageValue() > 0) {
                                stack.hurtAndBreak(flintAndSteel.getDamageValue(), self, LivingEntity.getSlotForHand(hand));
                            }
                            cir.setReturnValue(ar);
                        }
                    } finally {
                        self.setItemInHand(hand, stack);
                    }
                }
            }
        }
    }
}
