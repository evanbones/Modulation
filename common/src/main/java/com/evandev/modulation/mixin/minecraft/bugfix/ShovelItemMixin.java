package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModAbsent("pathunderfencegates")
@Mixin(ShovelItem.class)
public class ShovelItemMixin {

    @WrapOperation(
            method = "useOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isAir()Z")
    )
    private boolean modulation$flattenUnderPartialBlocks(BlockState above, Operation<Boolean> original, @Local(ordinal = 0) Level level, @Local(ordinal = 0) BlockPos pos) {
        if (VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixPathUnderBlocksEnabled)) {
            return !above.isFaceSturdy(level, pos.above(), Direction.DOWN, SupportType.FULL);
        }
        return original.call(above);
    }
}
