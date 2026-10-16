package com.evandev.modulation.mixin.minecraft.gameplay;

//? if <26.3 {
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@IfMinecraftVersion(maxVersion = "26.3", maxInclusive = false)
@Mixin(ShovelItem.class)
public class ShovelItemMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void modulation$handleShovelUseOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (VanillaGameplayModule.DISABLE_PATH_CREATION.on()) {
            BlockState state = context.getLevel().getBlockState(context.getClickedPos());
            if (!(state.getBlock() instanceof CampfireBlock)) {
                cir.setReturnValue(InteractionResult.PASS);
            }
        }
    }
}
//?} else {
/*import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import org.spongepowered.asm.mixin.Mixin;

@IfMinecraftVersion(maxVersion = "26.3", maxInclusive = false)
@Mixin(targets = "net.minecraft.world.item.ShovelItem")
public class ShovelItemMixin {
}
*///?}
