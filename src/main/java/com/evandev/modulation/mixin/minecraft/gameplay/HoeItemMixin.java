package com.evandev.modulation.mixin.minecraft.gameplay;

//? if <26.3 {
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfMinecraftVersion(maxVersion = "26.3", maxInclusive = false)
@Mixin(HoeItem.class)
public class HoeItemMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void modulation$disableFarmlandCreation(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (VanillaGameplayModule.DISABLE_FARMLAND_CREATION.on()) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }
}
//?} else {
/*import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import org.spongepowered.asm.mixin.Mixin;

@IfMinecraftVersion(maxVersion = "26.3", maxInclusive = false)
@Mixin(targets = "net.minecraft.world.item.HoeItem")
public class HoeItemMixin {
}
*///?}
