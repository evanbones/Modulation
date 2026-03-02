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

    private final BooleanTweak enabled = new BooleanTweak("enabled", true);
    private final IntTweak chargeUpTicks = new IntTweak("charge_up_ticks", 10);

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

    public void onServerTick() {
        PostPlacementManager.INSTANCE.tick();
    }

    public void handlePostPlacement(ServerPlayer player, BlockPos pos, Direction clickedFace) {
        if (!isEnabled()) return;
        PostPlacementManager.INSTANCE.handlePostPlacement(player, pos, clickedFace);
    }
}