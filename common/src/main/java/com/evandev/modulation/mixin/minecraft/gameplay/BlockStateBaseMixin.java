package com.evandev.modulation.mixin.minecraft.gameplay;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {

    @Inject(
            method = "getBlockSupportShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void modulation$leavesSupportBlocks(BlockGetter level, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        if (modulation$leavesSupportEnabled()) {
            cir.setReturnValue(Shapes.block());
        }
    }

    @Inject(
            method = "isFaceSturdy(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/world/level/block/SupportType;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void modulation$leavesAreSturdy(BlockGetter level, BlockPos pos, Direction face, SupportType supportType, CallbackInfoReturnable<Boolean> cir) {
        if (modulation$leavesSupportEnabled()) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private boolean modulation$leavesSupportEnabled() {
        if (!ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isLeavesSupportBlocksEnabled)) {
            return false;
        }
        BlockState state = (BlockState) (Object) this;
        return state.is(BlockTags.LEAVES);
    }
}
