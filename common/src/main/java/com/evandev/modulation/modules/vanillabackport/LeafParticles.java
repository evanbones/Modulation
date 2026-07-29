package com.evandev.modulation.modules.vanillabackport;

import com.blackgear.vanillabackport.client.registries.ModParticles;
import com.blackgear.vanillabackport.common.registries.ModBlocks;
import com.blackgear.vanillabackport.core.data.tags.ModBlockTags;
import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.PassableFoliageModule;
import com.evandev.modulation.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class LeafParticles {

    private LeafParticles() {
    }

    public static ParticleOptions resolve(BlockState state, ServerLevel level, BlockPos pos) {
        PassableFoliageModule module = ModuleManager.getModule("passable_foliage", PassableFoliageModule.class);
        if (module == null || !module.isPassableFoliageEnabled()) {
            return null;
        }

        if (state.is(Blocks.CHERRY_LEAVES) && module.isCherryLeavesEnabled()) {
            return ParticleTypes.CHERRY_LEAVES;
        }

        if (Services.PLATFORM.isModLoaded("vanillabackport")) {
            return resolveVanillaBackport(state, level, pos, module);
        }

        return null;
    }

    private static ParticleOptions resolveVanillaBackport(BlockState state, ServerLevel level, BlockPos pos, PassableFoliageModule module) {
        if (module.isPaleOakLeavesEnabled() && state.is(ModBlocks.PALE_OAK_LEAVES.get())) {
            return ModParticles.PALE_OAK_LEAVES.get();
        }
        if (module.isTintedLeavesEnabled() && state.is(ModBlockTags.SPAWN_FALLING_LEAVES)) {
            return ColorParticleOption.create(ModParticles.TINTED_LEAVES.get(), getLeafColor(state, level, pos));
        }
        if (module.isTintedNeedlesEnabled() && state.is(ModBlockTags.SPAWN_FALLING_NEEDLES)) {
            return ColorParticleOption.create(ModParticles.TINTED_NEEDLES.get(), getLeafColor(state, level, pos));
        }
        return null;
    }

    private static int getLeafColor(BlockState state, ServerLevel level, BlockPos pos) {
        if (state.is(Blocks.BIRCH_LEAVES)) {
            return FoliageColor.getBirchColor();
        }
        if (state.is(Blocks.SPRUCE_LEAVES)) {
            return FoliageColor.getEvergreenColor();
        }
        Holder<Biome> biome = level.getBiome(pos);
        return biome.value().getFoliageColor();
    }
}
