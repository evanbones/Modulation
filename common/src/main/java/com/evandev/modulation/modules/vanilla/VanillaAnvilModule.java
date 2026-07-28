package com.evandev.modulation.modules.vanilla;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.google.auto.service.AutoService;

@AutoService(IModule.class)
public class VanillaAnvilModule extends AbstractModule {

    private final BooleanTweak removeAnvilLimit = tweak(new BooleanTweak("remove_anvil_limit", true));
    private final BooleanTweak noAnvilEnchantCost = tweak(new BooleanTweak("no_anvil_enchant_cost", false));
    private final BooleanTweak noAnvilRepairCost = tweak(new BooleanTweak("no_anvil_repair_cost", false));
    private final BooleanTweak noAnvilRenameCost = tweak(new BooleanTweak("no_anvil_rename_cost", false));

    public VanillaAnvilModule() {
        super("vanilla_anvil");
    }

    public boolean isRemoveAnvilLimitEnabled() {
        return removeAnvilLimit.getValue();
    }

    public boolean isNoAnvilEnchantCostEnabled() {
        return noAnvilEnchantCost.getValue();
    }

    public boolean isNoAnvilRepairCostEnabled() {
        return noAnvilRepairCost.getValue();
    }

    public boolean isNoAnvilRenameCostEnabled() {
        return noAnvilRenameCost.getValue();
    }
}
