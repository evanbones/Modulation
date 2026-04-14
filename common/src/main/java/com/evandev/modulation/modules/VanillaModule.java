package com.evandev.modulation.modules;

import com.evandev.modulation.api.AbstractTweak;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.tweaks.BooleanTweak;

import java.util.List;

public class VanillaModule implements IModule {

    private final BooleanTweak fixFocusBug = new BooleanTweak("fix_focus_bug", true);
    private final BooleanTweak attackSleepingVillagers = new BooleanTweak("attack_sleeping_villagers", true);
    private final BooleanTweak fixExperienceLoss = new BooleanTweak("fix_experience_loss", true);
    private final BooleanTweak fixResourceFilterLeak = new BooleanTweak("fix_resource_filter_leak", true);
    private final BooleanTweak fixDensityMemoization = new BooleanTweak("fix_density_memoization", true);

    private final BooleanTweak removeAnvilLimit = new BooleanTweak("remove_anvil_limit", true);
    private final BooleanTweak noAnvilEnchantCost = new BooleanTweak("no_anvil_enchant_cost", false);
    private final BooleanTweak noAnvilRepairCost = new BooleanTweak("no_anvil_repair_cost", false);
    private final BooleanTweak noAnvilRenameCost = new BooleanTweak("no_anvil_rename_cost", false);

    @Override
    public String getId() {
        return "vanilla";
    }

    @Override
    public boolean shouldLoad() {
        return true;
    }

    @Override
    public List<AbstractTweak<?>> getTweaks() {
        return List.of(
                fixFocusBug, attackSleepingVillagers, fixExperienceLoss, fixResourceFilterLeak,
                fixDensityMemoization, removeAnvilLimit, noAnvilEnchantCost, noAnvilRepairCost, noAnvilRenameCost
        );
    }

    @Override
    public void initialize() {
    }

    public boolean isFixDensityMemoizationEnabled() {
        return fixDensityMemoization.getValue();
    }

    public boolean isFixFocusBugEnabled() {
        return fixFocusBug.getValue();
    }

    public boolean isAttackSleepingVillagersEnabled() {
        return attackSleepingVillagers.getValue();
    }

    public boolean isFixExperienceLossEnabled() {
        return fixExperienceLoss.getValue();
    }

    public boolean isFixResourceFilterLeakEnabled() {
        return fixResourceFilterLeak.getValue();
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