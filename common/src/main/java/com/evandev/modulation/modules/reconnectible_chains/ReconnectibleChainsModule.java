package com.evandev.modulation.modules.reconnectible_chains;

import com.evandev.modulation.api.AbstractTweak;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.evandev.modulation.api.tweaks.IntTweak;
import com.evandev.modulation.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class ReconnectibleChainsModule implements IModule {

    private final BooleanTweak enabled = new BooleanTweak("enabled", false);
    private final IntTweak chargeUpTicks = new IntTweak("charge_up_ticks", 10);
    private final BooleanTweak consumeDurability = new BooleanTweak("consume_durability", true);
    private final BooleanTweak consumeChains = new BooleanTweak("consume_chains", true);

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
        return List.of(enabled, chargeUpTicks, consumeDurability, consumeChains);
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

    public boolean isConsumeDurabilityEnabled() {
        return consumeDurability.getValue();
    }

    public boolean isConsumeChainsEnabled() {
        return consumeChains.getValue();
    }

    public void onServerTick() {
        PostPlacementManager.INSTANCE.tick();
    }

    public boolean handlePostPlacement(ServerPlayer player, BlockPos pos, Direction clickedFace) {
        if (!isEnabled()) return false;
        return PostPlacementManager.INSTANCE.handlePostPlacement(player, pos, clickedFace);
    }
}