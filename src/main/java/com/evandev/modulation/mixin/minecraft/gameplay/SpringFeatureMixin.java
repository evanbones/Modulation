package com.evandev.modulation.mixin.minecraft.gameplay;

import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.levelgen.feature.SpringFeature;
//? if >=26.3 {
/*import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
*///?} else {
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpringFeature.class)
public class SpringFeatureMixin {

    //? if >=26.3 {
    /*@Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void modulation$noDinnerlava(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin, CallbackInfoReturnable<Boolean> cir) {
        if (VanillaGameplayModule.NO_DINNERLAVA.on()) {
            SpringFeature feature = (SpringFeature) (Object) this;
            if (feature.holeCount() == 0 && feature.state().is(FluidTags.LAVA)) {
                cir.setReturnValue(false);
            }
        }
    }
    *///?} else {
    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void modulation$noDinnerlava(FeaturePlaceContext<SpringConfiguration> context, CallbackInfoReturnable<Boolean> cir) {
        if (VanillaGameplayModule.NO_DINNERLAVA.on()) {
            SpringConfiguration config = context.config();
            if (config.holeCount == 0 && config.state.is(FluidTags.LAVA)) {
                cir.setReturnValue(false);
            }
        }
    }
    //?}
}
