package com.evandev.modulation.items.impl;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.WeatheringCopper;

public interface ItemOxidizationCacheInterface {
    void modulation$setWeatherState(WeatheringCopper.WeatherState weatherState);

    void modulation$setWaxed(boolean waxed);

    void modulation$setBaseItem(Item item);

    WeatheringCopper.WeatherState modulation$weatherState();

    boolean modulation$waxed();

    Item modulation$baseItem();

    void modulation$clearOxidizationCache();
}