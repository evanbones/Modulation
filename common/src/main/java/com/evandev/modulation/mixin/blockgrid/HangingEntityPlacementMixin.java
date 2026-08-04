package com.evandev.modulation.mixin.blockgrid;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.blockgrid.BlockGridModule;
import com.evandev.modulation.modules.blockgrid.SurfaceSnapping;
import com.evandev.modulation.modules.blockgrid.storage.SurfaceOffsetHolder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HangingEntityItem.class)
public class HangingEntityPlacementMixin {
    @WrapOperation(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/HangingEntity;survives()Z"))
    private boolean modulation$applySurfaceMount(HangingEntity entity, Operation<Boolean> original, @Local(argsOnly = true) UseOnContext context) {
        BlockGridModule module = ModuleManager.getModule("block_grid", BlockGridModule.class);
        boolean enabled = module != null && module.isEnableBlockOffsets() && module.isEnableHangingEntityOffsets();
        if (enabled && entity instanceof SurfaceOffsetHolder holder) {
            holder.modulation$setSurfaceOffset(SurfaceSnapping.mountOffsetFor(context));
        }
        return original.call(entity);
    }
}
