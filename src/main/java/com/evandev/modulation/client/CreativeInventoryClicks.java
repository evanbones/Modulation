package com.evandev.modulation.client;

import com.evandev.modulation.util.EntityActions;
import com.evandev.modulation.modules.vanilla.VanillaGuiModule;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class CreativeInventoryClicks {

    private CreativeInventoryClicks() {
    }

    public static boolean handle(Slot slot, int slotId, int mouseButton, ClickType type) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode == null || mc.player == null) return false;
        if (!mc.player.hasInfiniteMaterials()) return false;

        if (!VanillaGuiModule.DISABLE_CREATIVE_INVENTORY.on()) return false;

        type = slotId == -999 && type == ClickType.PICKUP ? ClickType.THROW : type;

        if (slot == null && type != ClickType.QUICK_CRAFT) {
            if (!mc.player.inventoryMenu.getCarried().isEmpty()) {
                if (mouseButton == 0) {
                    EntityActions.drop(mc.player, mc.player.inventoryMenu.getCarried(), true);
                    mc.gameMode.handleCreativeModeItemDrop(mc.player.inventoryMenu.getCarried());
                    mc.player.inventoryMenu.setCarried(ItemStack.EMPTY);
                } else if (mouseButton == 1) {
                    ItemStack split = mc.player.inventoryMenu.getCarried().split(1);
                    EntityActions.drop(mc.player, split, true);
                    mc.gameMode.handleCreativeModeItemDrop(split);
                }
            }
        } else if (slot != null && !slot.mayPickup(mc.player)) {
            return true;
        } else if (type == ClickType.THROW && slot.hasItem()) {
            ItemStack itemstack = slot.remove(mouseButton == 0 ? 1 : slot.getItem().getMaxStackSize());
            ItemStack itemstack1 = slot.getItem();
            EntityActions.drop(mc.player, itemstack, true);
            mc.gameMode.handleCreativeModeItemDrop(itemstack);
            mc.gameMode.handleCreativeModeItemAdd(itemstack1, slot.index);
        } else if (type == ClickType.THROW && !mc.player.inventoryMenu.getCarried().isEmpty()) {
            EntityActions.drop(mc.player, mc.player.inventoryMenu.getCarried(), true);
            mc.gameMode.handleCreativeModeItemDrop(mc.player.inventoryMenu.getCarried());
            mc.player.inventoryMenu.setCarried(ItemStack.EMPTY);
        } else {
            int index = slot == null ? slotId : slot.index;
            mc.player.inventoryMenu.clicked(index, mouseButton, type, mc.player);
            mc.player.inventoryMenu.broadcastChanges();

            for (int i = 0; i < mc.player.inventoryMenu.slots.size(); i++) {
                mc.gameMode.handleCreativeModeItemAdd(mc.player.inventoryMenu.getSlot(i).getItem(), i);
            }
        }
        return true;
    }
}
