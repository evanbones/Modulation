package com.evandev.modulation.mixin.minecraft.visual.client;

import com.evandev.modulation.Constants;
import com.evandev.modulation.items.api.ItemTooltipHelper;
import com.evandev.modulation.modules.vanilla.VanillaVisualModule;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {

    @Unique
    private static final ResourceLocation MODULATION$WAXED_OVERLAY = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "container/slot_waxed_overlay");
    @Unique
    private static final ResourceLocation MODULATION$REDSTONE_OVERLAY = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "container/slot_redstone_overlay");
    @Unique
    private static final ResourceLocation MODULATION$INFESTED_OVERLAY = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "container/slot_infested_overlay");

    @Shadow
    public abstract void blitSprite(ResourceLocation sprite, int x, int y, int width, int height);

    @Shadow
    public abstract PoseStack pose();

    @Inject(
            method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
            at = @At("TAIL")
    )
    private void modulation$blitOverlays(Font font, ItemStack stack, int x, int y, @Nullable String text, CallbackInfo ci) {
        if (stack.isEmpty()) return;

        boolean drawWaxed = VanillaVisualModule.WAXED_ITEM_ICON_OVERLAY.on() && ItemTooltipHelper.isWaxed(stack);
        boolean drawExtra = VanillaVisualModule.EXTRA_ITEM_ICON_OVERLAYS.on();
        boolean drawInfested = drawExtra && ItemTooltipHelper.isInfested(stack);
        boolean drawTrapped = drawExtra && ItemTooltipHelper.isTrapped(stack);

        if (!drawWaxed && !drawInfested && !drawTrapped) return;

        this.pose().pushPose();
        this.pose().translate(0.0F, 0.0F, 200.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        if (drawWaxed) {
            this.blitSprite(MODULATION$WAXED_OVERLAY, x - 3, y - 3, 24, 24);
        }

        if (drawInfested) {
            this.blitSprite(MODULATION$INFESTED_OVERLAY, x - 3, y - 3, 24, 24);
        }

        if (drawTrapped) {
            this.blitSprite(MODULATION$REDSTONE_OVERLAY, x - 3, y - 3, 24, 24);
        }

        RenderSystem.disableBlend();
        this.pose().popPose();
    }
}
