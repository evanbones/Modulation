package com.evandev.modulation.items.api;

import it.unimi.dsi.fastutil.objects.Reference2ByteMap;
import it.unimi.dsi.fastutil.objects.Reference2ByteOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.TrappedChestBlock;

public final class ItemTooltipHelper {

    public static final TagKey<Item> TAG_INFESTED = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("modulation", "infested"));
    public static final TagKey<Item> TAG_TRAPPED = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("modulation", "trapped"));

    public static final TagKey<Item> TAG_WAXED_BLACKLIST = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("modulation", "waxed_blacklist"));
    public static final TagKey<Item> TAG_INFESTED_BLACKLIST = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("modulation", "infested_blacklist"));
    public static final TagKey<Item> TAG_TRAPPED_BLACKLIST = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("modulation", "trapped_blacklist"));

    private static final byte OVERLAY_FLAG_INFESTED = 1;
    private static final byte OVERLAY_FLAG_REDSTONE = 2;
    private static final byte OVERLAY_FLAGS_NOT_CACHED = -1;
    private static final Reference2ByteMap<Item> OVERLAY_FLAGS_CACHE = new Reference2ByteOpenHashMap<>();

    static {
        OVERLAY_FLAGS_CACHE.defaultReturnValue(OVERLAY_FLAGS_NOT_CACHED);
    }

    private static synchronized byte getOverlayFlags(Item item) {
        byte overlayFlags = OVERLAY_FLAGS_CACHE.getByte(item);
        if (overlayFlags != OVERLAY_FLAGS_NOT_CACHED) {
            return overlayFlags;
        }

        overlayFlags = 0;
        ResourceLocation loc = BuiltInRegistries.ITEM.getKey(item);
        String path = loc.getPath();

        if (item instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof InfestedBlock || path.contains("infested")) {
                overlayFlags |= OVERLAY_FLAG_INFESTED;
            }
            if (block instanceof TrappedChestBlock || path.contains("trapped")) {
                overlayFlags |= OVERLAY_FLAG_REDSTONE;
            }
        } else {
            if (path.contains("infested")) overlayFlags |= OVERLAY_FLAG_INFESTED;
            if (path.contains("trapped")) overlayFlags |= OVERLAY_FLAG_REDSTONE;
        }

        OVERLAY_FLAGS_CACHE.put(item, overlayFlags);
        return overlayFlags;
    }

    public static boolean isWaxed(ItemStack stack) {
        if (stack.is(TAG_WAXED_BLACKLIST)) {
            return false;
        }
        return OxidizableItemHelper.isWaxed(stack);
    }

    public static boolean isInfested(ItemStack stack) {
        if (stack.is(TAG_INFESTED_BLACKLIST)) {
            return false;
        }
        if (stack.is(TAG_INFESTED)) {
            return true;
        }
        return (getOverlayFlags(stack.getItem()) & OVERLAY_FLAG_INFESTED) != 0;
    }

    public static boolean isTrapped(ItemStack stack) {
        if (stack.is(TAG_TRAPPED_BLACKLIST)) {
            return false;
        }
        if (stack.is(TAG_TRAPPED)) {
            return true;
        }
        return (getOverlayFlags(stack.getItem()) & OVERLAY_FLAG_REDSTONE) != 0;
    }
}
