package com.evandev.modulation.mixin.vanilla.passablefoliage;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.PassableFoliageModule;
import com.evandev.modulation.modules.vanillabackport.EntityLeafDrag;
import com.evandev.modulation.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class LeavesBlockMixin {

    @Inject(
            method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void modulation$getCollisionShape(BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        BlockState state = (BlockState) (Object) this;
        if (state.is(ModTags.PASSABLE_LEAVES)) {
            PassableFoliageModule module = ModuleManager.getModule("passable_foliage", PassableFoliageModule.class);
            if (module != null && module.isPassableFoliageEnabled()) {
                if (module.isScaffoldingModeEnabled() && context.isAbove(Shapes.block(), pos, true) && !context.isDescending()) {
                    return;
                }
                if (!(context instanceof EntityCollisionContext entityContext && entityContext.getEntity() == null)) {
                    cir.setReturnValue(Shapes.empty());
                }
            }
        }
    }

    @Inject(
            method = "getVisualShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void modulation$getVisualShape(BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        BlockState state = (BlockState) (Object) this;
        if (state.is(ModTags.PASSABLE_LEAVES)) {
            PassableFoliageModule module = ModuleManager.getModule("passable_foliage", PassableFoliageModule.class);
            if (module != null && module.isPassableFoliageEnabled()) {
                if (module.isScaffoldingModeEnabled() && context.isAbove(Shapes.block(), pos, true) && !context.isDescending()) {
                    return;
                }
                cir.setReturnValue(Shapes.empty());
            }
        }
    }

    @Inject(method = "entityInside", at = @At("HEAD"))
    private void modulation$entityInside(Level level, BlockPos pos, Entity entity, CallbackInfo ci) {
        BlockState state = (BlockState) (Object) this;
        if (state.is(ModTags.PASSABLE_LEAVES)) {
            PassableFoliageModule module = ModuleManager.getModule("passable_foliage", PassableFoliageModule.class);
            if (module != null && module.isPassableFoliageEnabled()) {
                if (entity instanceof EntityLeafDrag drag) {
                    drag.modulation$applyLeafDrag(state, level, pos);
                }
            }
        }
    }
}