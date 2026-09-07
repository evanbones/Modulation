package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.SupportType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModAbsent("pathunderfencegates")
@Mixin(DirtPathBlock.class)
public class DirtPathBlockMixin {

    @ModifyReturnValue(method = "canSurvive", at = @At("RETURN"))
    private boolean modulation$pathUnderPartialBlocks(boolean original, @Local(argsOnly = true) LevelReader level, @Local(argsOnly = true) BlockPos pos) {
        if (original || !VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixPathUnderBlocksEnabled)) {
            return original;
        }
        BlockPos above = pos.above();
        return !level.getBlockState(above).isFaceSturdy(level, above, Direction.DOWN, SupportType.FULL);
    }
}
