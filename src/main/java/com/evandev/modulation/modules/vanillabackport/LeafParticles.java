package com.evandev.modulation.modules.vanillabackport;

import com.evandev.modulation.modules.vanilla.PassableFoliageModule;
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
//? if >=26.1 {
/*import com.evandev.modulation.mixin.minecraft.accessor.UntintedParticleLeavesBlockAccessor;
import net.minecraft.world.level.block.TintedParticleLeavesBlock;
*///?} else {
import com.blackgear.vanillabackport.client.registries.ModParticles;
import com.blackgear.vanillabackport.common.registries.ModBlocks;
import com.blackgear.vanillabackport.core.data.tags.ModBlockTags;
import com.evandev.modulation.platform.Services;
//?}

public final class LeafParticles {

    private LeafParticles() {
    }

    public static ParticleOptions resolve(BlockState state, ServerLevel level, BlockPos pos) {
        if (!PassableFoliageModule.ENABLE_PASSABLE_FOLIAGE.on()) {
            return null;
        }

        if (state.is(Blocks.CHERRY_LEAVES) && PassableFoliageModule.ENABLE_CHERRY_LEAVES.on()) {
            return ParticleTypes.CHERRY_LEAVES;
        }

        //? if >=26.1 {
        /*if (state.is(Blocks.PALE_OAK_LEAVES)) {
            return PassableFoliageModule.ENABLE_PALE_OAK_LEAVES.on() ? ParticleTypes.PALE_OAK_LEAVES : null;
        }
        if (state.getBlock() instanceof UntintedParticleLeavesBlockAccessor untinted) {
            ParticleOptions particle = untinted.modulation$getLeafParticle();
            if (particle.getType() == ParticleTypes.CHERRY_LEAVES) {
                return PassableFoliageModule.ENABLE_CHERRY_LEAVES.on() ? particle : null;
            }
            if (particle.getType() == ParticleTypes.PALE_OAK_LEAVES) {
                return PassableFoliageModule.ENABLE_PALE_OAK_LEAVES.on() ? particle : null;
            }
            return PassableFoliageModule.ENABLE_TINTED_LEAVES.on() ? particle : null;
        }
        if (state.getBlock() instanceof TintedParticleLeavesBlock && PassableFoliageModule.ENABLE_TINTED_LEAVES.on()) {
            return ColorParticleOption.create(ParticleTypes.TINTED_LEAVES, getLeafColor(state, level, pos));
        }
        return null;
        *///?} else {
        if (Services.PLATFORM.isModLoaded("vanillabackport")) {
            return resolveVanillaBackport(state, level, pos);
        }

        return null;
        //?}
    }

    //? if <26.1 {
    private static ParticleOptions resolveVanillaBackport(BlockState state, ServerLevel level, BlockPos pos) {
        if (PassableFoliageModule.ENABLE_PALE_OAK_LEAVES.on() && state.is(ModBlocks.PALE_OAK_LEAVES.get())) {
            return ModParticles.PALE_OAK_LEAVES.get();
        }
        if (PassableFoliageModule.ENABLE_TINTED_LEAVES.on() && state.is(ModBlockTags.SPAWN_FALLING_LEAVES)) {
            return ColorParticleOption.create(ModParticles.TINTED_LEAVES.get(), getLeafColor(state, level, pos));
        }
        if (PassableFoliageModule.ENABLE_TINTED_NEEDLES.on() && state.is(ModBlockTags.SPAWN_FALLING_NEEDLES)) {
            return ColorParticleOption.create(ModParticles.TINTED_NEEDLES.get(), getLeafColor(state, level, pos));
        }
        return null;
    }
    //?}

    private static int getLeafColor(BlockState state, ServerLevel level, BlockPos pos) {
        //? if >=26.1 {
        /*if (state.is(Blocks.BIRCH_LEAVES)) {
            return FoliageColor.FOLIAGE_BIRCH;
        }
        if (state.is(Blocks.SPRUCE_LEAVES)) {
            return FoliageColor.FOLIAGE_EVERGREEN;
        }
        if (state.is(Blocks.MANGROVE_LEAVES)) {
            return FoliageColor.FOLIAGE_MANGROVE;
        }
        *///?} else {
        if (state.is(Blocks.BIRCH_LEAVES)) {
            return FoliageColor.getBirchColor();
        }
        if (state.is(Blocks.SPRUCE_LEAVES)) {
            return FoliageColor.getEvergreenColor();
        }
        //?}
        Holder<Biome> biome = level.getBiome(pos);
        return biome.value().getFoliageColor();
    }
}
