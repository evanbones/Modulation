package com.evandev.modulation.mixin.vanilla;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.mixin.vanilla.accessor.ConcretePowderBlockAccessor;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {

    @Inject(method = "getDispenseMethod", at = @At("HEAD"), cancellable = true)
    private void modulation$dispenseWaterBottleOnConcrete(Level level, ItemStack item, CallbackInfoReturnable<DispenseItemBehavior> cir) {
        if (item.is(Items.POTION) && ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isWaterBottlesOnConcreteEnabled)) {
            DispenseItemBehavior originalBehavior = DispenserBlock.DISPENSER_REGISTRY.get(item.getItem());
            cir.setReturnValue(new DefaultDispenseItemBehavior() {
                @Override
                public ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                    PotionContents potioncontents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
                    if (potioncontents.is(Potions.WATER)) {
                        ServerLevel serverlevel = blockSource.level();
                        BlockPos blockpos = blockSource.pos();
                        BlockPos targetPos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
                        BlockState targetState = serverlevel.getBlockState(targetPos);
                        if (targetState.getBlock() instanceof ConcretePowderBlock powder) {
                            if (!serverlevel.isClientSide()) {
                                for (int i = 0; i < 5; i++) {
                                    serverlevel.sendParticles(
                                            ParticleTypes.SPLASH,
                                            (double) blockpos.getX() + serverlevel.random.nextDouble(),
                                            blockpos.getY() + 1,
                                            (double) blockpos.getZ() + serverlevel.random.nextDouble(),
                                            1,
                                            0.0,
                                            0.0,
                                            0.0,
                                            1.0
                                    );
                                }
                            }

                            serverlevel.playSound(null, blockpos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                            serverlevel.gameEvent(null, GameEvent.FLUID_PLACE, blockpos);
                            Block concreteBlock = ((ConcretePowderBlockAccessor) powder).modulation$getConcrete();
                            serverlevel.setBlockAndUpdate(targetPos, concreteBlock.defaultBlockState());
                            return this.consumeWithRemainder(blockSource, itemStack, new ItemStack(Items.GLASS_BOTTLE));
                        }
                    }
                    return originalBehavior != null ? originalBehavior.dispense(blockSource, itemStack) : super.execute(blockSource, itemStack);
                }
            });
        }
    }
}
