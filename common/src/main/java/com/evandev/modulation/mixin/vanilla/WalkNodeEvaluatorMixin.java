package com.evandev.modulation.mixin.vanilla;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(WalkNodeEvaluator.class)
public class WalkNodeEvaluatorMixin {

    @Inject(method = "getPathTypeWithinMobBB", at = @At("RETURN"))
    private void modulation$allowMobsCrossRails(PathfindingContext context, int x, int y, int z, CallbackInfoReturnable<Set<PathType>> cir) {
        if (ModuleManager.isEnabled("vanilla_bugfixes", VanillaBugfixesModule.class, VanillaBugfixesModule::isFixMobsCrossingRailsEnabled)) {
            Set<PathType> set = cir.getReturnValue();
            if (set != null && set.contains(PathType.UNPASSABLE_RAIL)) {
                set.remove(PathType.UNPASSABLE_RAIL);
                set.add(PathType.RAIL);
            }
        }
    }
}
