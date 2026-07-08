package com.evandev.modulation.mixin.vanilla;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.items.api.ItemTooltipHelper;
import com.evandev.modulation.items.api.OxidizableItemHelper;
import com.evandev.modulation.items.impl.ItemOxidizationCacheInterface;
import com.evandev.modulation.modules.VanillaModule;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.WeatheringCopper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "getTooltipLines", at = @At("RETURN"))
    public void modulation$addWeatheringAndWaxedTooltips(Item.TooltipContext context, @Nullable Player player, TooltipFlag flag, CallbackInfoReturnable<List<Component>> cir) {
        final ItemStack stack = ItemStack.class.cast(this);

        VanillaModule module = (VanillaModule) ModuleManager.getModule("vanilla");
        if (module == null) return;

        List<Component> tooltip = cir.getReturnValue();
        int insertIndex = Math.min(1, tooltip.size());

        if (module.isBetterCopperTooltipsEnabled() && stack.getItem() instanceof ItemOxidizationCacheInterface oxidizationCache) {
            if (ItemTooltipHelper.isWaxed(stack)) {
                tooltip.add(insertIndex, OxidizableItemHelper.WAXED_TOOLTIP);
            }

            final WeatheringCopper.WeatherState weatherState = oxidizationCache.modulation$weatherState();
            if (weatherState != null) {
                modulation$addWeatherStateTooltip(tooltip, insertIndex, weatherState);
            }
        }

        if (module.isExtraItemIconOverlaysEnabled()) {
            if (ItemTooltipHelper.isInfested(stack)) {
                tooltip.add(insertIndex, Component.translatable("tag.item.modulation.infested").withStyle(ChatFormatting.GRAY));
            }
            if (ItemTooltipHelper.isTrapped(stack)) {
                tooltip.add(insertIndex, Component.translatable("tag.item.modulation.trapped").withStyle(ChatFormatting.GRAY));
            }
        }
    }

    @Unique
    private void modulation$addWeatherStateTooltip(List<Component> tooltip, int index, WeatheringCopper.WeatherState weatherState) {
        if (weatherState == WeatheringCopper.WeatherState.UNAFFECTED) return;
        tooltip.add(index, OxidizableItemHelper.getWeatheringStateName(weatherState));
    }
}