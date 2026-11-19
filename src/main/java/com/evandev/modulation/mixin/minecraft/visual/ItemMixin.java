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

package com.evandev.modulation.mixin.minecraft.visual;

import com.evandev.modulation.items.impl.ItemOxidizationCacheInterface;
import com.evandev.modulation.modules.vanilla.VanillaVisualModule;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.WeatheringCopper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin implements ItemOxidizationCacheInterface {

    @Unique
    @Nullable
    private WeatheringCopper.WeatherState modulation$weatherState = null;

    @Unique
    private boolean modulation$waxed = false;

    @Unique
    @Nullable
    private Item modulation$baseItem = null;

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    public void modulation$getNonWeatheringNonWaxedName(ItemStack stack, CallbackInfoReturnable<Component> cir) {
        if (!VanillaVisualModule.BETTER_COPPER_TOOLTIPS.on())
            return;
        final Item baseItem = this.modulation$baseItem();
        if (baseItem == null || baseItem == (Object) this) return;
        cir.setReturnValue(baseItem.getName(stack.transmuteCopy(baseItem)));
    }

    @Unique
    @Override
    public void modulation$setWeatherState(WeatheringCopper.WeatherState weatherState) {
        this.modulation$weatherState = weatherState;
    }

    @Unique
    @Override
    public void modulation$setWaxed(boolean waxed) {
        this.modulation$waxed = waxed;
    }

    @Unique
    @Override
    public void modulation$setBaseItem(Item item) {
        this.modulation$baseItem = item;
    }

    @Unique
    @Override
    public WeatheringCopper.WeatherState modulation$weatherState() {
        return this.modulation$weatherState;
    }

    @Unique
    @Override
    public boolean modulation$waxed() {
        return this.modulation$waxed;
    }

    @Unique
    @Override
    public Item modulation$baseItem() {
        return this.modulation$baseItem;
    }

    @Unique
    @Override
    public void modulation$clearOxidizationCache() {
        this.modulation$weatherState = null;
        this.modulation$waxed = false;
        this.modulation$baseItem = null;
    }
}