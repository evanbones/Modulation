package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.npc.CatSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfModAbsent("neoforge")
@Mixin(CatSpawner.class)
public class CatSpawnerMixin {

    @Inject(
            method = "spawnCat",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Cat;finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;)Lnet/minecraft/world/entity/SpawnGroupData;")
    )
    private void modulation$positionCatBeforeSpawn(BlockPos pos, ServerLevel serverLevel, CallbackInfoReturnable<Integer> cir, @Local Cat cat) {
        if (VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixWitchHutCatsEnabled)) {
            cat.moveTo(pos, 0.0F, 0.0F);
        }
    }
}
