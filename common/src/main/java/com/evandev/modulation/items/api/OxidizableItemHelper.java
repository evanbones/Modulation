package com.evandev.modulation.items.api;

import com.evandev.modulation.items.impl.ItemOxidizationCacheInterface;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;

import java.util.Optional;

public final class OxidizableItemHelper {
    public static final Component WAXED_TOOLTIP = Component.translatable("tooltip.modulation.waxed").withStyle(ChatFormatting.GOLD);

    private static final MutableComponent[] WEATHERING_NAMES = new MutableComponent[]{
            Component.translatable("tooltip.modulation.weathering.unaffected").withStyle(ChatFormatting.GRAY),
            Component.translatable("tooltip.modulation.weathering.exposed").withStyle(ChatFormatting.GRAY),
            Component.translatable("tooltip.modulation.weathering.weathered").withStyle(ChatFormatting.GRAY),
            Component.translatable("tooltip.modulation.weathering.oxidized").withStyle(ChatFormatting.GRAY),
            Component.translatable("tooltip.modulation.weathering.unknown").withStyle(ChatFormatting.GRAY)
    };

    /**
     * Call this from a common setup phase or reload listener across your loaders.
     * Pass in BuiltInRegistries.ITEM to populate the cache.
     */
    public static void populateCache(Iterable<Item> items) {
        for (Item item : items) {
            if (!(item instanceof ItemOxidizationCacheInterface oxidizationCache)) continue;

            oxidizationCache.modulation$clearOxidizationCache();

            if (!(item instanceof BlockItem blockItem)) continue;

            final Block block = blockItem.getBlock();
            final Optional<Block> baseBlock = getNonWeatheringNonWaxedEquivalent(block);
            baseBlock.ifPresent(base -> oxidizationCache.modulation$setBaseItem(base.asItem()));

            final Optional<Block> nonWaxedBlock = getNonWaxedEquivalent(block);
            if (nonWaxedBlock.orElse(block) instanceof WeatheringCopper weatheringCopper) {
                oxidizationCache.modulation$setWeatherState(weatheringCopper.getAge());
            }

            if (nonWaxedBlock.isPresent()) {
                oxidizationCache.modulation$setWaxed(true);
            }
        }
    }

    public static Optional<Block> getNonWeatheringNonWaxedEquivalent(Block block) {
        final Block nonWaxed = getNonWaxedEquivalent(block).orElse(block);
        final Block nonWeatheringNonWaxed = getNonWeatheringEquivalent(nonWaxed).orElse(nonWaxed);
        if (block == nonWeatheringNonWaxed) return Optional.empty();
        return Optional.of(nonWeatheringNonWaxed);
    }

    public static Optional<Block> getNonWaxedEquivalent(Block block) {
        final Block nonWaxedBlock = HoneycombItem.WAX_OFF_BY_BLOCK.get().get(block);
        if (nonWaxedBlock == null) return Optional.empty();
        return Optional.of(nonWaxedBlock);
    }

    public static Optional<Block> getNonWeatheringEquivalent(Block block) {
        final Block nonWeatheringBlock = WeatheringCopper.getFirst(block);
        if (nonWeatheringBlock == block) return Optional.empty();
        return Optional.of(nonWeatheringBlock);
    }

    public static MutableComponent getWeatheringStateName(WeatheringCopper.WeatherState weatherState) {
        if (weatherState == WeatheringCopper.WeatherState.UNAFFECTED) return WEATHERING_NAMES[0];
        if (weatherState == WeatheringCopper.WeatherState.EXPOSED) return WEATHERING_NAMES[1];
        if (weatherState == WeatheringCopper.WeatherState.WEATHERED) return WEATHERING_NAMES[2];
        if (weatherState == WeatheringCopper.WeatherState.OXIDIZED) return WEATHERING_NAMES[3];
        return WEATHERING_NAMES[4];
    }

    public static boolean isWaxed(ItemStack stack) {
        if (stack.getItem() instanceof ItemOxidizationCacheInterface cache) {
            return cache.modulation$waxed();
        }
        return false;
    }
}