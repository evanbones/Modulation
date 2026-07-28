// SPDX-License-Identifier: AGPL-3.0-only
// Ported from BetterWalls (https://modrinth.com/mod/betterwalls) by Lemonnik6484 and JX_Snack,
// licensed under AGPL-3.0-only. This file is licensed under AGPL-3.0-only, as an exception
// to the repository's overall MIT license.
package com.evandev.modulation.mixin.vanilla;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaWallsModule;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FenceBlock.class)
public class FenceBlockMixin {

    @ModifyReturnValue(method = "connectsTo", at = @At("RETURN"))
    private boolean modulation$connectsTo(boolean original, @Local(argsOnly = true) BlockState state) {
        if (!ModuleManager.isEnabled("vanilla_walls", VanillaWallsModule.class, VanillaWallsModule::isFencesConnectToWallsAndBarsEnabled)) return original;
        return original
                || state.is(BlockTags.WALLS)
                || state.is(BlockTags.FENCES)
                || state.getBlock() instanceof IronBarsBlock
                || state.is(BlockTags.WALL_SIGNS);
    }
}
