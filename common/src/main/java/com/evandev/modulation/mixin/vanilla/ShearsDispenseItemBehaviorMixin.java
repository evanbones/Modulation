package com.evandev.modulation.mixin.vanilla;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShearsDispenseItemBehavior.class)
public abstract class ShearsDispenseItemBehaviorMixin extends OptionalDispenseItemBehavior {

    @Inject(method = "execute", at = @At("RETURN"))
    private void modulation$shearPumpkin(BlockSource blockSource, ItemStack item, CallbackInfoReturnable<ItemStack> cir) {
        if (!this.isSuccess() && ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isDispenserShearsPumpkinsEnabled)) {
            ServerLevel serverLevel = blockSource.level();
            if (!serverLevel.isClientSide()) {
                Direction facing = blockSource.state().getValue(DispenserBlock.FACING);
                BlockPos targetPos = blockSource.pos().relative(facing);
                BlockState state = serverLevel.getBlockState(targetPos);
                if (state.is(Blocks.PUMPKIN)) {
                    Direction carvedFacing = facing.getAxis().isHorizontal() ? facing.getOpposite() : Direction.NORTH;
                    serverLevel.playSound(null, targetPos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    serverLevel.setBlock(targetPos, Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, carvedFacing), 11);
                    ItemEntity itemEntity = new ItemEntity(
                            serverLevel,
                            (double) targetPos.getX() + 0.5 + (double) carvedFacing.getStepX() * 0.65,
                            (double) targetPos.getY() + 0.1,
                            (double) targetPos.getZ() + 0.5 + (double) carvedFacing.getStepZ() * 0.65,
                            new ItemStack(Items.PUMPKIN_SEEDS, 4)
                    );
                    itemEntity.setDeltaMovement(
                            0.05 * (double) carvedFacing.getStepX() + serverLevel.random.nextDouble() * 0.02,
                            0.05,
                            0.05 * (double) carvedFacing.getStepZ() + serverLevel.random.nextDouble() * 0.02
                    );
                    serverLevel.addFreshEntity(itemEntity);
                    item.hurtAndBreak(1, serverLevel, null, p -> {});
                    serverLevel.gameEvent(null, GameEvent.SHEAR, targetPos);
                    this.setSuccess(true);
                }
            }
        }
    }
}
