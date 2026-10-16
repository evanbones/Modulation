package com.evandev.modulation.mixin.minecraft.gameplay;

//? if >=26.3 {
/*import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@IfMinecraftVersion(minVersion = "26.3")
@Mixin(BlockTransformer.class)
public abstract class BlockTransformerMixin {

    @WrapOperation(
            method = "transformBlock*",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/stateproviders/BlockStateProvider;getOptionalState(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;")
    )
    private BlockState modulation$disableToolTransforms(BlockStateProvider provider, LevelAccessor level, RandomSource random, BlockPos pos,
                                                        Operation<BlockState> original, @Local BlockTransformer.BlockTransformData data) {
        if (modulation$isDisabled(data)) {
            return null;
        }
        return original.call(provider, level, random, pos);
    }

    @Unique
    private static boolean modulation$isDisabled(BlockTransformer.BlockTransformData data) {
        if (VanillaGameplayModule.DISABLE_AXE_STRIPPING.on() && data.sound().is(SoundEvents.AXE_STRIP.key())) {
            return true;
        }
        if (VanillaGameplayModule.DISABLE_COPPER_SCRAPING.on()
                && (data.particle() == BlockTransformer.TransformParticle.SCRAPE || data.particle() == BlockTransformer.TransformParticle.WAX_OFF)) {
            return true;
        }
        if (VanillaGameplayModule.DISABLE_FARMLAND_CREATION.on() && data.sound().is(SoundEvents.HOE_TILL.key())) {
            return true;
        }
        return VanillaGameplayModule.DISABLE_PATH_CREATION.on() && data.sound().is(SoundEvents.SHOVEL_FLATTEN.key());
    }
}
*///?} else {
import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import org.spongepowered.asm.mixin.Mixin;

@IfMinecraftVersion(minVersion = "26.3")
@Mixin(targets = "net.minecraft.core.component.BlockTransformer")
public class BlockTransformerMixin {
}
//?}
