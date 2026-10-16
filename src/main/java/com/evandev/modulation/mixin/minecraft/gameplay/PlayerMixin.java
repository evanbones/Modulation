package com.evandev.modulation.mixin.minecraft.gameplay;

import com.evandev.modulation.util.EntityActions;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import com.evandev.modulation.util.HandSlots;
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
//? if >=26.1
//import net.minecraft.world.phys.Vec3;
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

    //? if >=26.1 {
    /*@Shadow
    public abstract InteractionResult interactOn(Entity entityToInteractOn, InteractionHand hand, Vec3 location);
    *///?} else {
    @Shadow
    public abstract InteractionResult interactOn(Entity entityToInteractOn, InteractionHand hand);
    //?}

    @Inject(method = "getProjectile", at = @At("RETURN"), cancellable = true)
    private void modulation$infinityBowNoArrow(ItemStack shootable, CallbackInfoReturnable<ItemStack> cir) {
        if (cir.getReturnValue().isEmpty() && shootable.getItem() instanceof BowItem) {
            if (VanillaGameplayModule.INFIBOWS.on()) {
                var infinityHolder = this.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.INFINITY);
                if (infinityHolder.isPresent() && EnchantmentHelper.getItemEnchantmentLevel(infinityHolder.get(), shootable) > 0) {
                    cir.setReturnValue(new ItemStack(Items.ARROW));
                }
            }
        }
    }

    @Inject(method = "interactOn", at = @At("RETURN"), cancellable = true)
    //? if >=26.1 {
    /*private void modulation$fireAspectInteractEntity(Entity entityToInteractOn, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir) {
    *///?} else {
    private void modulation$fireAspectInteractEntity(Entity entityToInteractOn, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
    //?}
        if (VanillaGameplayModule.FIRE_ASPECT_IS_FLINT_AND_STEEL.on()
                && cir.getReturnValue() == InteractionResult.PASS) {
            Player self = (Player) (Object) this;
            ItemStack stack = self.getItemInHand(hand);
            if (!stack.isEmpty()) {
                var fireAspectHolder = self.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.FIRE_ASPECT);
                if (fireAspectHolder.isPresent() && EnchantmentHelper.getItemEnchantmentLevel(fireAspectHolder.get(), stack) > 0) {
                    ItemStack flintAndSteel = new ItemStack(Items.FLINT_AND_STEEL);
                    try {
                        self.setItemInHand(hand, flintAndSteel);
                        //? if >=26.1 {
                        /*InteractionResult ar = this.interactOn(entityToInteractOn, hand, location);
                        *///?} else {
                        InteractionResult ar = this.interactOn(entityToInteractOn, hand);
                        //?}
                        if (ar.consumesAction()) {
                            EntityActions.swing(self, hand, true);
                            self.level().playSound(null, entityToInteractOn.getX(), entityToInteractOn.getY(), entityToInteractOn.getZ(),
                                    SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, self.level().getRandom().nextFloat() * 0.4F + 0.8F);
                            if (flintAndSteel.getDamageValue() > 0) {
                                stack.hurtAndBreak(flintAndSteel.getDamageValue(), self, HandSlots.of(hand));
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
