package com.evandev.modulation.mixin.minecraft.gameplay;

//? if <26.3 {
import com.evandev.modulation.mixin.minecraft.accessor.AxeItemAccessor;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import net.minecraft.tags.BlockTags;
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

import java.util.Optional;

@IfMinecraftVersion(maxVersion = "26.3", maxInclusive = false)
@Mixin(AxeItem.class)
public class AxeItemMixin {

    @Unique
    private static boolean modulation$isStrippable(BlockState state) {
        return AxeItemAccessor.modulation$getStrippables().containsKey(state.getBlock())
                || state.is(BlockTags.LOGS)
                //? if >=26.2 {
                /*|| state.is(net.minecraft.tags.BlockItemTags.LOGS_THAT_BURN.block());
                *///?} else {
                || state.is(BlockTags.LOGS_THAT_BURN);
                //?}
    }

    @Unique
    private static boolean modulation$isCopperScrapable(BlockState state) {
        if (WeatheringCopper.getPrevious(state).isPresent()) {
            return true;
        }
        return HoneycombItem.WAX_OFF_BY_BLOCK.get().containsKey(state.getBlock());
    }

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void modulation$handleAxeUseOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());

        if (VanillaGameplayModule.DISABLE_AXE_STRIPPING.on()) {
            if (modulation$isStrippable(state)) {
                cir.setReturnValue(InteractionResult.PASS);
                return;
            }
        }

        if (VanillaGameplayModule.DISABLE_COPPER_SCRAPING.on()) {
            if (modulation$isCopperScrapable(state)) {
                cir.setReturnValue(InteractionResult.PASS);
            }
        }
    }

    @Inject(method = "getStripped", at = @At("HEAD"), cancellable = true)
    private void modulation$disableStripping(BlockState unstrippedState, CallbackInfoReturnable<Optional<BlockState>> cir) {
        if (VanillaGameplayModule.DISABLE_AXE_STRIPPING.on()) {
            cir.setReturnValue(Optional.empty());
        }
    }
}
//?} else {
/*import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import org.spongepowered.asm.mixin.Mixin;

@IfMinecraftVersion(maxVersion = "26.3", maxInclusive = false)
@Mixin(targets = "net.minecraft.world.item.AxeItem")
public class AxeItemMixin {
}
*///?}
