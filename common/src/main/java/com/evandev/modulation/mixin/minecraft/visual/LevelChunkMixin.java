package com.evandev.modulation.mixin.minecraft.visual;

import com.evandev.modulation.modules.vanilla.VanillaVisualModule;
import com.evandev.modulation.modules.vanilla.smoothlight.LightTransitions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {

    @Inject(method = "setBlockState", at = @At("HEAD"))
    private void modulation$recordLightTransition(BlockPos pos, BlockState state, boolean isMoving, CallbackInfoReturnable<BlockState> cir) {
        if (!VanillaVisualModule.SMOOTH_LIGHT.on()) return;
        LevelChunk chunk = (LevelChunk) (Object) this;
        LightTransitions.onBlockChanged(chunk.getLevel(), pos.immutable(), chunk.getBlockState(pos), state);
    }
}
