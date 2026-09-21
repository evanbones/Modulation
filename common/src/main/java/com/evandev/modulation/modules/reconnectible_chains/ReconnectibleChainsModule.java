package com.evandev.modulation.modules.reconnectible_chains;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.ModuleDef;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.evandev.modulation.api.tweaks.IntTweak;
import com.evandev.modulation.platform.Services;
import com.google.auto.service.AutoService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;

@AutoService(IModule.class)
public class ReconnectibleChainsModule extends AbstractModule {

    private static final ModuleDef DEF = ModuleDef.of("reconnectible_chains");

    public static final BooleanTweak ENABLED = DEF.bool("enabled", false);
    public static final IntTweak CHARGE_UP_TICKS = DEF.integer("charge_up_ticks", 10);
    public static final BooleanTweak CONSUME_DURABILITY = DEF.bool("consume_durability", true);
    public static final BooleanTweak CONSUME_CHAINS = DEF.bool("consume_chains", true);

    public ReconnectibleChainsModule() {
        super(DEF);
    }

    @Override
    public boolean shouldLoad() {
        return Services.PLATFORM.isModLoaded("connectiblechains");
    }

    public void onServerTick() {
        PostPlacementManager.INSTANCE.tick();
    }

    public static boolean handlePostPlacement(ServerPlayer player, BlockPos pos, Direction clickedFace) {
        if (!ENABLED.on()) return false;
        return PostPlacementManager.INSTANCE.handlePostPlacement(player, pos, clickedFace);
    }
}