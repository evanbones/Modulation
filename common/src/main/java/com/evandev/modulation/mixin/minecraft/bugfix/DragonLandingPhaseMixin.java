package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonLandingPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModAbsent("debugify")
@Mixin(DragonLandingPhase.class)
public class DragonLandingPhaseMixin {

    @ModifyExpressionValue(
            method = "doServerTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getHeightmapPos(Lnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/BlockPos;")
    )
    private BlockPos modulation$dragonLandsOnPortalHeight(BlockPos pos) {
        if (pos.getY() == 0 && VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixDragonLandingInVoidEnabled)) {
            return pos.atY(65);
        }
        return pos;
    }
}
