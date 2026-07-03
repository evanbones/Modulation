package com.evandev.modulation.mixin.vanilla;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.items.impl.ItemOxidizationCacheInterface;
import com.evandev.modulation.modules.VanillaModule;
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
        VanillaModule module = (VanillaModule) ModuleManager.getModule("vanilla");
        if (module == null || !module.isBetterCopperTooltipsEnabled()) return;
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