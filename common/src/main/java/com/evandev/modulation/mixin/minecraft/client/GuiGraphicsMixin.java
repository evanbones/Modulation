package com.evandev.modulation.mixin.minecraft.client;

import com.evandev.modulation.Constants;
import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.items.api.OxidizableItemHelper;
import com.evandev.modulation.modules.VanillaModule;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Reference2ByteMap;
import it.unimi.dsi.fastutil.objects.Reference2ByteOpenHashMap;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
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
    @Unique
    private static final byte MODULATION$OVERLAY_FLAG_INFESTED = 1;
    @Unique
    private static final byte MODULATION$OVERLAY_FLAG_REDSTONE = 2;
    @Unique
    private static final byte MODULATION$OVERLAY_FLAGS_NOT_CACHED = -1;
    @Unique
    private static final Reference2ByteMap<Item> MODULATION$OVERLAY_FLAGS_CACHE = new Reference2ByteOpenHashMap<>();

    static {
        MODULATION$OVERLAY_FLAGS_CACHE.defaultReturnValue(MODULATION$OVERLAY_FLAGS_NOT_CACHED);
    }

    @Unique
    private static byte modulation$getOverlayFlags(Item item) {
        byte overlayFlags = MODULATION$OVERLAY_FLAGS_CACHE.getByte(item);
        if (overlayFlags != MODULATION$OVERLAY_FLAGS_NOT_CACHED) {
            return overlayFlags;
        }

        overlayFlags = 0;
        if (item instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof InfestedBlock) {
                overlayFlags |= MODULATION$OVERLAY_FLAG_INFESTED;
            }
            if (blockItem.getBlock() instanceof TrappedChestBlock) {
                overlayFlags |= MODULATION$OVERLAY_FLAG_REDSTONE;
            }
        }

        MODULATION$OVERLAY_FLAGS_CACHE.put(item, overlayFlags);
        return overlayFlags;
    }

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

        VanillaModule module = (VanillaModule) ModuleManager.getModule("vanilla");
        if (module == null) return;

        boolean drawWaxed = module.isWaxedItemIconOverlayEnabled() && OxidizableItemHelper.isWaxed(stack);
        boolean drawExtra = module.isExtraItemIconOverlaysEnabled();

        if (drawWaxed || drawExtra) {
            this.pose().pushPose();
            this.pose().translate(0.0F, 0.0F, 200.0F);

            if (drawWaxed) {
                this.blitSprite(MODULATION$WAXED_OVERLAY, x - 3, y - 3, 24, 24);
            }

            if (drawExtra) {
                final byte overlayFlags = modulation$getOverlayFlags(stack.getItem());
                if ((overlayFlags & MODULATION$OVERLAY_FLAG_INFESTED) != 0) {
                    this.blitSprite(MODULATION$INFESTED_OVERLAY, x - 3, y - 3, 24, 24);
                }
                if ((overlayFlags & MODULATION$OVERLAY_FLAG_REDSTONE) != 0) {
                    this.blitSprite(MODULATION$REDSTONE_OVERLAY, x - 3, y - 3, 24, 24);
                }
            }

            this.pose().popPose();
        }
    }
}