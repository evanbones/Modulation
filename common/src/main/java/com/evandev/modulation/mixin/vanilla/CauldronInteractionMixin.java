package com.evandev.modulation.mixin.vanilla;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CauldronInteraction.class)
public interface CauldronInteractionMixin {

    @Inject(method = "emptyBucket", at = @At("HEAD"), cancellable = true)
    private static void modulation$netherCauldron(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack filledStack, BlockState state, SoundEvent emptySound, CallbackInfoReturnable<ItemInteractionResult> cir) {
        if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isNetherCauldronEnabled)) {
            if (state.is(Blocks.WATER_CAULDRON) && level.dimensionType().ultraWarm()) {
                if (!level.isClientSide) {
                    Item item = filledStack.getItem();
                    player.setItemInHand(hand, ItemUtils.createFilledResult(filledStack, player, new ItemStack(Items.BUCKET)));
                    player.awardStat(Stats.FILL_CAULDRON);
                    player.awardStat(Stats.ITEM_USED.get(item));
                    level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 8, 0.2, 0.2, 0.2, 0.0);
                    }
                }
                cir.setReturnValue(ItemInteractionResult.sidedSuccess(level.isClientSide));
            }
        }
    }
}
