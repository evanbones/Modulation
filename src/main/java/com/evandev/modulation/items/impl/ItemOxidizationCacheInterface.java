/*
 * Copyright 2025-2026 FrozenBlock
 * This file contains code from The Copperier Age <https://github.com/FrozenBlock/The-Copperier-Age>.
 *
 * This file is licensed under version 1 of the FrozenBlock Modding Oasis License
 * as published by FrozenBlock Modding Oasis, and is not covered by Modulation's MIT license.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * FrozenBlock Modding Oasis License for more details.
 *
 * You should have received a copy of the FrozenBlock Modding Oasis License
 * along with this program in LICENSE-COPPERIER-AGE; if not, see <https://github.com/FrozenBlock/Licenses>.
 */

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