package com.evandev.modulation.mixin.minecraft.bugfix;

//? if <26.1 {
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CampfireBlock.class)
public class CampfireBlockMixin {

    @IfModAbsent("neoforge")
    @WrapOperation(
            method = "isSmokeyPos",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;", ordinal = 0)
    )
    private static BlockState modulation$captureSmokeCheckPos(Level level, BlockPos pos, Operation<BlockState> original, @Share("smokePos") LocalRef<BlockPos> smokePos) {
        smokePos.set(pos);
        return original.call(level, pos);
    }

    @IfModAbsent("neoforge")
    @ModifyArg(
            method = "isSmokeyPos",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"),
            index = 1
    )
    private static BlockPos modulation$campfireSmokeCollisionPos(BlockPos pos, @Share("smokePos") LocalRef<BlockPos> smokePos) {
        if (smokePos.get() != null && VanillaBugfixesModule.FIX_CAMPFIRE_SMOKE_POSITION.on()) {
            return smokePos.get();
        }
        return pos;
    }
}
//?}
