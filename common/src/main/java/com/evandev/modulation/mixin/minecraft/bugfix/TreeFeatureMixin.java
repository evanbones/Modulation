package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@IfModAbsent("debugify")
@IfModAbsent("flwr-8187")
@Mixin(TreeFeature.class)
public class TreeFeatureMixin {

    @Unique
    private static int modulation$startOffset(int start, int trunkHeight, TreeConfiguration config) {
        if (start == -1 && config.minimumSize.getSizeAtHeight(trunkHeight, 0) == 1
                && VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixTwoByTwoSaplingsEnabled)) {
            return 0;
        }
        return start;
    }

    @ModifyVariable(method = "getMaxFreeTreeHeight", at = @At("STORE"), ordinal = 3)
    private int modulation$twoByTwoSaplingsX(int start, LevelSimulatedReader level, int trunkHeight, BlockPos topPosition, TreeConfiguration config) {
        return modulation$startOffset(start, trunkHeight, config);
    }

    @ModifyVariable(method = "getMaxFreeTreeHeight", at = @At("STORE"), ordinal = 4)
    private int modulation$twoByTwoSaplingsZ(int start, LevelSimulatedReader level, int trunkHeight, BlockPos topPosition, TreeConfiguration config) {
        return modulation$startOffset(start, trunkHeight, config);
    }
}
