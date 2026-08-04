package com.evandev.modulation.mixin.blockgrid;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.blockgrid.BlockGridModule;
import com.evandev.modulation.modules.blockgrid.SupportOffsets;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderOffsetMixin {
    @Inject(method = "setupAndRender", at = @At("HEAD"))
    private static void modulation$beginOffsetPose(BlockEntityRenderer<?> renderer, BlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo ci) {
        poseStack.pushPose();
        BlockGridModule module = ModuleManager.getModule("block_grid", BlockGridModule.class);
        if (module == null || !module.isEnableBlockOffsets() || !module.isEnableBlockEntityOffsets()) {
            return;
        }
        Level level = blockEntity.getLevel();
        if (level == null) {
            return;
        }
        Vec3 offset = SupportOffsets.offsetFor(blockEntity.getBlockState(), level, blockEntity.getBlockPos());
        if (offset != Vec3.ZERO) {
            poseStack.translate(offset.x, offset.y, offset.z);
        }
    }

    @Inject(method = "setupAndRender", at = @At("RETURN"))
    private static void modulation$endOffsetPose(BlockEntityRenderer<?> renderer, BlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo ci) {
        poseStack.popPose();
    }
}
