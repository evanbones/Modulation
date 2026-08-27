package com.evandev.modulation.mixin.vanilla;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.mixin.vanilla.accessor.AxeItemAccessor;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxeItem.class)
public class AxeItemMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void modulation$handleAxeUseOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());

        if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isDisableAxeStrippingEnabled)) {
            if (modulation$isStrippable(state)) {
                cir.setReturnValue(InteractionResult.PASS);
                return;
            }
        }

        if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isDisableCopperScrapingEnabled)) {
            if (modulation$isCopperScrapable(state)) {
                cir.setReturnValue(InteractionResult.PASS);
            }
        }
    }

    @Unique
    private static boolean modulation$isStrippable(BlockState state) {
        return AxeItemAccessor.modulation$getStrippables().containsKey(state.getBlock());
    }

    @Unique
    private static boolean modulation$isCopperScrapable(BlockState state) {
        if (WeatheringCopper.getPrevious(state).isPresent()) {
            return true;
        }
        return HoneycombItem.WAX_OFF_BY_BLOCK.get().containsKey(state.getBlock());
    }
}