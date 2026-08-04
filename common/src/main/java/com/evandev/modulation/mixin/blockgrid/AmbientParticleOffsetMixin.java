package com.evandev.modulation.mixin.blockgrid;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.blockgrid.BlockGridModule;
import com.evandev.modulation.modules.blockgrid.RenderParticleOffset;
import com.evandev.modulation.modules.blockgrid.SupportOffsets;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientLevel.class)
public class AmbientParticleOffsetMixin {
    @WrapOperation(method = "doAnimateTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;animateTick(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V"))
    private void modulation$alignAmbientParticles(Block block, BlockState state, Level level, BlockPos pos, RandomSource random, Operation<Void> original) {
        if (modulation$particleOffsetsEnabled()) {
            RenderParticleOffset.begin(SupportOffsets.offsetFor(state, level, pos));
        }
        try {
            original.call(block, state, level, pos, random);
        } finally {
            RenderParticleOffset.end();
        }
    }

    @Unique
    private static boolean modulation$particleOffsetsEnabled() {
        BlockGridModule module = ModuleManager.getModule("block_grid", BlockGridModule.class);
        return module != null && module.isEnableBlockOffsets() && module.isEnableParticleOffsets();
    }
}
