package com.evandev.modulation.modules.vanilla;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.evandev.modulation.mixin.vanilla.accessor.MapColorAccessor;
import com.google.auto.service.AutoService;
import net.minecraft.world.level.material.MapColor;

import java.util.IdentityHashMap;
import java.util.Map;

@AutoService(IModule.class)
public class VanillaVisualModule extends AbstractModule {

    private static Map<MapColor, Integer> VANILLA_COLORS = null;
    private static Map<MapColor, Integer> NICER_COLORS = null;

    private final BooleanTweak nicerMapColors = tweak(new BooleanTweak("nicer_map_colors", true) {
        @Override
        public void onApply() {
            applyNicerMapColors(getValue());
        }
    });
    private final BooleanTweak waxedItemIconOverlay = tweak(new BooleanTweak("waxed_item_icon_overlay", true));
    private final BooleanTweak extraItemIconOverlays = tweak(new BooleanTweak("extra_item_icon_overlays", true));
    private final BooleanTweak betterCopperTooltips = tweak(new BooleanTweak("better_copper_tooltips", true));
    private final BooleanTweak legibleSigns = tweak(new BooleanTweak("legible_signs", true));

    public VanillaVisualModule() {
        super("vanilla_visual");
    }

    public boolean isWaxedItemIconOverlayEnabled() {
        return waxedItemIconOverlay.getValue();
    }

    public boolean isExtraItemIconOverlaysEnabled() {
        return extraItemIconOverlays.getValue();
    }

    public boolean isBetterCopperTooltipsEnabled() {
        return betterCopperTooltips.getValue();
    }

    public boolean isLegibleSignsEnabled() {
        return legibleSigns.getValue();
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
