package com.evandev.modulation.mixin.vanilla;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {

    @Shadow
    @Final
    protected ServerPlayer player;

    @Shadow
    public abstract InteractionResult useItemOn(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hitResult);

    @Inject(method = "incrementDestroyProgress", at = @At("HEAD"))
    private void modulation$cactusPunchingHurts(BlockState state, BlockPos pos, int startTick, CallbackInfoReturnable<Float> cir) {
        if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isCactusPunchingHurtsEnabled)) {
            if (state.is(Blocks.CACTUS)) {
                this.player.hurt(this.player.damageSources().cactus(), 1.0F);
            }
        }
    }

    @Inject(method = "useItemOn", at = @At("RETURN"), cancellable = true)
    private void modulation$fireAspectUseOnBlock(ServerPlayer player, Level level, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isFireAspectIsFlintAndSteelEnabled)
                && cir.getReturnValue() == InteractionResult.PASS && !stack.isEmpty()) {
            var fireAspectHolder = player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.FIRE_ASPECT);
            if (fireAspectHolder.isPresent() && EnchantmentHelper.getItemEnchantmentLevel(fireAspectHolder.get(), stack) > 0) {
                ItemStack flintAndSteel = new ItemStack(Items.FLINT_AND_STEEL);
                try {
                    player.setItemInHand(hand, flintAndSteel);
                    InteractionResult ar = this.useItemOn(player, level, flintAndSteel, hand, hitResult);
                    if (ar.consumesAction()) {
                        player.swing(hand, true);
                        level.playSound(null, hitResult.getBlockPos(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                        if (flintAndSteel.getDamageValue() > 0) {
                            stack.hurtAndBreak(flintAndSteel.getDamageValue(), player, LivingEntity.getSlotForHand(hand));
                        }
                        cir.setReturnValue(ar);
                    }
                } finally {
                    player.setItemInHand(hand, stack);
                }
            }
        }
    }
}
