package com.evandev.modulation.mixin.minecraft.bees;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.mixin.minecraft.accessor.NodeEvaluatorAccessor;
import com.evandev.modulation.modules.brainierbees.BrainierBeesModule;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModAbsent("brainierbees")
@Mixin(FlyNodeEvaluator.class)
public class FlyNodeEvaluatorMixin {

    @Inject(method = "getPathType", at = @At("RETURN"), cancellable = true)
    private void modulation$beesAvoidLadders(PathfindingContext context, int x, int y, int z, CallbackInfoReturnable<PathType> cir) {
        if (!(((NodeEvaluatorAccessor) this).modulation$getMob() instanceof Bee)) {
            return;
        }
        if (!ModuleManager.isEnabled("brainier_bees", BrainierBeesModule.class, BrainierBeesModule::isBeesAvoidLaddersEnabled)) {
            return;
        }
        if (context.getBlockState(new BlockPos(x, y, z)).is(Blocks.LADDER)) {
            cir.setReturnValue(PathType.TRAPDOOR);
        }
    }
}
