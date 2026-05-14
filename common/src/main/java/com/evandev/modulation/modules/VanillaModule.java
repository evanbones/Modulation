package com.evandev.modulation.modules;

import com.evandev.modulation.api.AbstractTweak;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.evandev.modulation.mixin.minecraft.accessor.MapColorAccessor;
import net.minecraft.world.level.material.MapColor;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class VanillaModule implements IModule {

    private static Map<MapColor, Integer> VANILLA_COLORS = null;
    private static Map<MapColor, Integer> NICER_COLORS = null;
    private final BooleanTweak fixFocusBug = new BooleanTweak("fix_focus_bug", true);
    private final BooleanTweak attackSleepingVillagers = new BooleanTweak("attack_sleeping_villagers", true);
    private final BooleanTweak fixExperienceLoss = new BooleanTweak("fix_experience_loss", true);
    private final BooleanTweak fixResourceFilterLeak = new BooleanTweak("fix_resource_filter_leak", true);
    private final BooleanTweak fixDensityMemoization = new BooleanTweak("fix_density_memoization", true);
    private final BooleanTweak removeAnvilLimit = new BooleanTweak("remove_anvil_limit", true);
    private final BooleanTweak noAnvilEnchantCost = new BooleanTweak("no_anvil_enchant_cost", false);
    private final BooleanTweak noAnvilRepairCost = new BooleanTweak("no_anvil_repair_cost", false);
    private final BooleanTweak noAnvilRenameCost = new BooleanTweak("no_anvil_rename_cost", false);
    private final BooleanTweak nicerMapColors = new BooleanTweak("nicer_map_colors", true) {
        @Override
        public void onApply() {
            applyNicerMapColors(getValue());
        }
    };

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
                fixDensityMemoization, removeAnvilLimit, noAnvilEnchantCost, noAnvilRepairCost,
                noAnvilRenameCost, nicerMapColors
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

    private void applyNicerMapColors(boolean nicer) {
        if (NICER_COLORS == null) {
            NICER_COLORS = new IdentityHashMap<>();
            VANILLA_COLORS = new IdentityHashMap<>();

            NICER_COLORS.put(MapColor.GRASS, 0x8EB971);
            NICER_COLORS.put(MapColor.SAND, 0xF7E9E3);
            NICER_COLORS.put(MapColor.WOOL, 0xFFFFFF);
            NICER_COLORS.put(MapColor.FIRE, 0xFF0000);
            NICER_COLORS.put(MapColor.ICE, 0xA0A0FF);
            NICER_COLORS.put(MapColor.METAL, 0xA7A7D7);
            NICER_COLORS.put(MapColor.PLANT, 0x507736);
            NICER_COLORS.put(MapColor.SNOW, 0xFFFFFF);
            NICER_COLORS.put(MapColor.CLAY, 0xA4A8B8);
            NICER_COLORS.put(MapColor.DIRT, 0xB6855B);
            NICER_COLORS.put(MapColor.STONE, 0x707070);
            NICER_COLORS.put(MapColor.WATER, 0x3F76E4);
            NICER_COLORS.put(MapColor.WOOD, 0xC0A361);
            NICER_COLORS.put(MapColor.QUARTZ, 0xFFFDF5);
            NICER_COLORS.put(MapColor.COLOR_ORANGE, 0xD87F33);
            NICER_COLORS.put(MapColor.COLOR_MAGENTA, 0xB24CD8);
            NICER_COLORS.put(MapColor.COLOR_LIGHT_BLUE, 0x6699D8);
            NICER_COLORS.put(MapColor.COLOR_YELLOW, 0xE5E533);
            NICER_COLORS.put(MapColor.COLOR_LIGHT_GREEN, 0x7FCC19);
            NICER_COLORS.put(MapColor.COLOR_PINK, 0xF27FA5);
            NICER_COLORS.put(MapColor.COLOR_GRAY, 0x4C4C4C);
            NICER_COLORS.put(MapColor.COLOR_LIGHT_GRAY, 0x999999);
            NICER_COLORS.put(MapColor.COLOR_CYAN, 0x4C7F99);
            NICER_COLORS.put(MapColor.COLOR_PURPLE, 0x7F3FB2);
            NICER_COLORS.put(MapColor.COLOR_BLUE, 0x334CB2);
            NICER_COLORS.put(MapColor.COLOR_BROWN, 0x664C33);
            NICER_COLORS.put(MapColor.COLOR_GREEN, 0x667F33);
            NICER_COLORS.put(MapColor.COLOR_RED, 0x993333);
            NICER_COLORS.put(MapColor.COLOR_BLACK, 0x191919);
            NICER_COLORS.put(MapColor.GOLD, 0xFAEE4D);
            NICER_COLORS.put(MapColor.DIAMOND, 0x5CD8D5);
            NICER_COLORS.put(MapColor.LAPIS, 0x4A80FF);
            NICER_COLORS.put(MapColor.EMERALD, 0x00D93A);
            NICER_COLORS.put(MapColor.PODZOL, 0x815631);
            NICER_COLORS.put(MapColor.NETHER, 0x700200);
            NICER_COLORS.put(MapColor.TERRACOTTA_WHITE, 0xD1B1A1);
            NICER_COLORS.put(MapColor.TERRACOTTA_ORANGE, 0x9F5224);
            NICER_COLORS.put(MapColor.TERRACOTTA_MAGENTA, 0x95576C);
            NICER_COLORS.put(MapColor.TERRACOTTA_LIGHT_BLUE, 0x706C8A);
            NICER_COLORS.put(MapColor.TERRACOTTA_YELLOW, 0xBA8524);
            NICER_COLORS.put(MapColor.TERRACOTTA_LIGHT_GREEN, 0x677535);
            NICER_COLORS.put(MapColor.TERRACOTTA_PINK, 0xA04D4E);
            NICER_COLORS.put(MapColor.TERRACOTTA_GRAY, 0x392923);
            NICER_COLORS.put(MapColor.TERRACOTTA_LIGHT_GRAY, 0x876A62);
            NICER_COLORS.put(MapColor.TERRACOTTA_CYAN, 0x575C5C);
            NICER_COLORS.put(MapColor.TERRACOTTA_PURPLE, 0x7A4958);
            NICER_COLORS.put(MapColor.TERRACOTTA_BLUE, 0x4C3E7C);
            NICER_COLORS.put(MapColor.TERRACOTTA_BROWN, 0x4C3223);
            NICER_COLORS.put(MapColor.TERRACOTTA_GREEN, 0x4C522A);
            NICER_COLORS.put(MapColor.TERRACOTTA_RED, 0x8E3C2E);
            NICER_COLORS.put(MapColor.TERRACOTTA_BLACK, 0x251610);
            NICER_COLORS.put(MapColor.CRIMSON_NYLIUM, 0xBD3031);
            NICER_COLORS.put(MapColor.CRIMSON_STEM, 0x5C191D);
            NICER_COLORS.put(MapColor.CRIMSON_HYPHAE, 0x5C191D);
            NICER_COLORS.put(MapColor.WARPED_NYLIUM, 0x167E86);
            NICER_COLORS.put(MapColor.WARPED_STEM, 0x3A8E8C);
            NICER_COLORS.put(MapColor.WARPED_HYPHAE, 0x14B485);
            NICER_COLORS.put(MapColor.WARPED_WART_BLOCK, 0x167E86);
            NICER_COLORS.put(MapColor.DEEPSLATE, 0x646464);
            NICER_COLORS.put(MapColor.RAW_IRON, 0xD8AF93);
            NICER_COLORS.put(MapColor.GLOW_LICHEN, 0x7FA796);

            for (MapColor mc : NICER_COLORS.keySet()) {
                VANILLA_COLORS.put(mc, ((MapColorAccessor) mc).modulation$getCol());
            }
        }

        for (Map.Entry<MapColor, Integer> entry : NICER_COLORS.entrySet()) {
            int targetCol = nicer ? entry.getValue() : VANILLA_COLORS.get(entry.getKey());
            ((MapColorAccessor) entry.getKey()).modulation$setCol(targetCol);
        }
    }
}