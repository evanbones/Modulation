package com.evandev.modulation.items.api;

import com.evandev.modulation.items.impl.ItemOxidizationCacheInterface;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;

import java.util.Optional;

public final class OxidizableItemHelper {
    public static final Component WAXED_TOOLTIP = Component.translatable("tooltip.modulation.waxed").withStyle(ChatFormatting.GOLD);

    public static final TagKey<Item> TAG_WAXED = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("modulation", "waxed"));
    public static final TagKey<Item> TAG_COMMON_WAXED = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "waxed"));

    private static final MutableComponent[] WEATHERING_NAMES = new MutableComponent[]{
            Component.translatable("tooltip.modulation.weathering.unaffected").withStyle(ChatFormatting.GRAY),
            Component.translatable("tooltip.modulation.weathering.exposed").withStyle(ChatFormatting.GRAY),
            Component.translatable("tooltip.modulation.weathering.weathered").withStyle(ChatFormatting.GRAY),
            Component.translatable("tooltip.modulation.weathering.oxidized").withStyle(ChatFormatting.GRAY),
            Component.translatable("tooltip.modulation.weathering.unknown").withStyle(ChatFormatting.GRAY)
    };

    public static void populateCache(Iterable<Item> items) {
        for (Item item : items) {
            if (!(item instanceof ItemOxidizationCacheInterface oxidizationCache)) continue;

            oxidizationCache.modulation$clearOxidizationCache();

            ResourceLocation itemLoc = BuiltInRegistries.ITEM.getKey(item);
            boolean isWaxedByName = itemLoc.getPath().contains("waxed");

            if (!(item instanceof BlockItem blockItem)) {
                if (isWaxedByName) oxidizationCache.modulation$setWaxed(true);
                continue;
            }

            final Block block = blockItem.getBlock();
            final Optional<Block> baseBlock = getNonWeatheringNonWaxedEquivalent(block);
            baseBlock.ifPresent(base -> oxidizationCache.modulation$setBaseItem(base.asItem()));

            final Optional<Block> nonWaxedBlock = getNonWaxedEquivalent(block);
            Block effectiveBlockForWeathering = nonWaxedBlock.orElse(block);

            if (effectiveBlockForWeathering instanceof WeatheringCopper weatheringCopper) {
                oxidizationCache.modulation$setWeatherState(weatheringCopper.getAge());
            } else {
                String path = itemLoc.getPath();
                if (path.contains("oxidized_"))
                    oxidizationCache.modulation$setWeatherState(WeatheringCopper.WeatherState.OXIDIZED);
                else if (path.contains("weathered_"))
                    oxidizationCache.modulation$setWeatherState(WeatheringCopper.WeatherState.WEATHERED);
                else if (path.contains("exposed_"))
                    oxidizationCache.modulation$setWeatherState(WeatheringCopper.WeatherState.EXPOSED);
            }

            if (nonWaxedBlock.isPresent() || isWaxedByName) {
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
        Block nonWaxedBlock = HoneycombItem.WAX_OFF_BY_BLOCK.get().get(block);
        if (nonWaxedBlock != null) return Optional.of(nonWaxedBlock);

        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(block);
        if (loc.getPath().startsWith("waxed_")) {
            String baseName = loc.getPath().substring("waxed_".length());
            return findBlockByPath(loc.getNamespace(), baseName);
        }
        return Optional.empty();
    }

    public static Optional<Block> getNonWeatheringEquivalent(Block block) {
        Block current = block;
        Block previous = WeatheringCopper.PREVIOUS_BY_BLOCK.get().get(current);
        while (previous != null) {
            current = previous;
            previous = WeatheringCopper.PREVIOUS_BY_BLOCK.get().get(current);
        }
        if (current != block) return Optional.of(current);

        ResourceLocation loc = BuiltInRegistries.BLOCK.getKey(block);
        String path = loc.getPath();
        String[] prefixes = {"exposed_", "weathered_", "oxidized_"};

        for (String prefix : prefixes) {
            if (path.startsWith(prefix)) {
                String baseName = path.substring(prefix.length());
                return findBlockByPath(loc.getNamespace(), baseName);
            }
        }

        return Optional.empty();
    }

    private static Optional<Block> findBlockByPath(String originalNamespace, String baseName) {
        ResourceLocation exactLoc = ResourceLocation.fromNamespaceAndPath(originalNamespace, baseName);
        if (BuiltInRegistries.BLOCK.containsKey(exactLoc)) {
            return Optional.of(BuiltInRegistries.BLOCK.get(exactLoc));
        }

        ResourceLocation vanillaLoc = ResourceLocation.withDefaultNamespace(baseName);
        if (BuiltInRegistries.BLOCK.containsKey(vanillaLoc)) {
            return Optional.of(BuiltInRegistries.BLOCK.get(vanillaLoc));
        }

        for (ResourceLocation registryKey : BuiltInRegistries.BLOCK.keySet()) {
            if (registryKey.getPath().equals(baseName)) {
                return Optional.of(BuiltInRegistries.BLOCK.get(registryKey));
            }
        }

        return Optional.empty();
    }

    public static MutableComponent getWeatheringStateName(WeatheringCopper.WeatherState weatherState) {
        if (weatherState == WeatheringCopper.WeatherState.UNAFFECTED) return WEATHERING_NAMES[0];
        if (weatherState == WeatheringCopper.WeatherState.EXPOSED) return WEATHERING_NAMES[1];
        if (weatherState == WeatheringCopper.WeatherState.WEATHERED) return WEATHERING_NAMES[2];
        if (weatherState == WeatheringCopper.WeatherState.OXIDIZED) return WEATHERING_NAMES[3];
        return WEATHERING_NAMES[4];
    }

    public static boolean isWaxed(ItemStack stack) {
        if (stack.is(TAG_WAXED) || stack.is(TAG_COMMON_WAXED)) return true;

        if (stack.getItem() instanceof ItemOxidizationCacheInterface cache) {
            return cache.modulation$waxed();
        }
        return false;
    }
}