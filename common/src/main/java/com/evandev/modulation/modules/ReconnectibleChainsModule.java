package com.evandev.modulation.modules;

import com.evandev.connectiblechains.CommonClass;
import com.evandev.connectiblechains.entity.ChainKnotEntity;
import com.evandev.connectiblechains.entity.Chainable;
import com.evandev.modulation.api.AbstractTweak;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.evandev.modulation.api.tweaks.IntTweak;
import com.evandev.modulation.platform.Services;
import com.evandev.modulation.registry.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ReconnectibleChainsModule implements IModule {
    private final Map<UUID, BlockPos> firstPostMap = new HashMap<>();

    private final BooleanTweak enabled = new BooleanTweak("enabled", true);
    private final IntTweak chargeUpTicks = new IntTweak("charge_up_ticks", 20);

    @Override
    public String getId() {
        return "reconnectible_chains";
    }

    @Override
    public boolean shouldLoad() {
        return Services.PLATFORM.isModLoaded("connectiblechains");
    }

    @Override
    public List<AbstractTweak<?>> getTweaks() {
        return List.of(enabled, chargeUpTicks);
    }

    @Override
    public void initialize() {
    }

    public boolean isEnabled() {
        return enabled.getValue();
    }

    public int getChargeUpTicks() {
        return chargeUpTicks.getValue();
    }

    public void handlePostPlacement(ServerPlayer player, BlockPos pos) {
        if (!isEnabled()) return;

        ServerLevel level = player.serverLevel();

        level.setBlock(pos, ModRegistry.CAST_POST.defaultBlockState(), 3);
        level.playSound(null, pos, SoundEvents.BAMBOO_WOOD_PLACE, SoundSource.BLOCKS, 1.0f, 0.5f);
        BlockParticleOption particleOption = new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(pos.below()));
        for (int i = 0; i < 15; i++) {
            double offsetX = level.random.nextGaussian() * 0.15;
            double offsetZ = level.random.nextGaussian() * 0.15;
            level.sendParticles(particleOption,
                    pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                    1, offsetX, 0.2, offsetZ, 0.1);
        }

        level.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                5, 0.1, 0.1, 0.1, 0.05);

        if (!firstPostMap.containsKey(player.getUUID())) {
            firstPostMap.put(player.getUUID(), pos);
        } else {
            BlockPos firstPos = firstPostMap.remove(player.getUUID());
            double dist = Math.sqrt(pos.distSqr(firstPos));

            if (dist <= CommonClass.runtimeConfig.getMaxChainRange()) {
                ChainKnotEntity knot1 = ChainKnotEntity.getOrCreate(level, firstPos, Items.CHAIN);
                ChainKnotEntity knot2 = ChainKnotEntity.getOrCreate(level, pos, Items.CHAIN);

                if (!knot1.equals(knot2)) {
                    knot1.attachChain(new Chainable.ChainData(knot2, Items.CHAIN), null, true);
                }
            }
        }
    }
}