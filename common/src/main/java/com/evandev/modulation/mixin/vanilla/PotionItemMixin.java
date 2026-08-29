package com.evandev.modulation.mixin.vanilla;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.mixin.vanilla.accessor.ConcretePowderBlockAccessor;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionItem.class)
public class PotionItemMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void modulation$solidifyConcrete(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isWaterBottlesOnConcreteEnabled)) {
            Level level = context.getLevel();
            BlockPos blockpos = context.getClickedPos();
            Player player = context.getPlayer();
            ItemStack itemstack = context.getItemInHand();
            PotionContents potioncontents = itemstack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            BlockState blockstate = level.getBlockState(blockpos);
            if (context.getClickedFace() != Direction.DOWN && blockstate.getBlock() instanceof ConcretePowderBlock powder && potioncontents.is(Potions.WATER)) {
                level.playSound(null, blockpos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (player != null) {
                    player.setItemInHand(context.getHand(), ItemUtils.createFilledResult(itemstack, player, new ItemStack(Items.GLASS_BOTTLE)));
                    player.awardStat(Stats.ITEM_USED.get(itemstack.getItem()));
                }
                if (!level.isClientSide()) {
                    ServerLevel serverlevel = (ServerLevel) level;
                    for (int i = 0; i < 5; i++) {
                        serverlevel.sendParticles(
                                ParticleTypes.SPLASH,
                                (double) blockpos.getX() + level.random.nextDouble(),
                                blockpos.getY() + 1,
                                (double) blockpos.getZ() + level.random.nextDouble(),
                                1,
                                0.0,
                                0.0,
                                0.0,
                                1.0
                        );
                    }
                }

                level.playSound(null, blockpos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PLACE, blockpos);
                Block concreteBlock = ((ConcretePowderBlockAccessor) powder).modulation$getConcrete();
                level.setBlockAndUpdate(blockpos, concreteBlock.defaultBlockState());
                cir.setReturnValue(InteractionResult.sidedSuccess(level.isClientSide()));
            }
        }
    }
}
