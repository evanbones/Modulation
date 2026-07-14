package com.evandev.modulation.mixin.minecraft.client;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.client.ClickAction;
import com.evandev.modulation.modules.VanillaModule;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen {

    @Unique
    private final Set<Slot> modulation$processedDragSlots = new HashSet<>();
    @Shadow
    @Final
    protected T menu;
    @Shadow
    protected Slot hoveredSlot;

    protected AbstractContainerScreenMixin(net.minecraft.network.chat.Component title) {
        super(title);
    }

    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void modulation$onSlotClicked(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
        VanillaModule module = (VanillaModule) ModuleManager.getModule("vanilla");
        if (module != null && module.isCtrlDragToCraftingGridEnabled()) {
            if (Screen.hasControlDown() && mouseButton == 0 && (type == ClickType.PICKUP || type == ClickType.QUICK_MOVE)) {
                if (slot != null && slot.hasItem()) {
                    if (slot.container instanceof Inventory && type == ClickType.PICKUP) {
                        modulation$processedDragSlots.clear();
                        modulation$processedDragSlots.add(slot);
                        modulation$transferSlotToCraftingGrid(slot);
                        ci.cancel();
                    } else if (slot instanceof ResultSlot) {
                        modulation$processedDragSlots.clear();
                        modulation$processedDragSlots.add(slot);
                        modulation$transferResultToCraftingGrid(slot, type == ClickType.QUICK_MOVE);
                        ci.cancel();
                    }
                }
            }
        }
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void modulation$onMouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY, CallbackInfoReturnable<Boolean> cir) {
        VanillaModule module = (VanillaModule) ModuleManager.getModule("vanilla");
        if (module != null && module.isCtrlDragToCraftingGridEnabled()) {
            if (Screen.hasControlDown() && button == 0) {
                Slot slot = this.hoveredSlot;
                if (slot != null && slot.container instanceof Inventory && slot.hasItem()) {
                    if (!modulation$processedDragSlots.contains(slot)) {
                        modulation$processedDragSlots.add(slot);
                        modulation$transferSlotToCraftingGrid(slot);
                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"))
    private void modulation$onMouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        modulation$processedDragSlots.clear();
    }

    @Unique
    private void modulation$transferSlotToCraftingGrid(Slot sourceSlot) {
        if (sourceSlot == null || !sourceSlot.hasItem() || this.minecraft == null) return;
        LocalPlayer player = this.minecraft.player;
        if (player == null || this.minecraft.gameMode == null) return;

        int containerId = this.menu.containerId;
        ItemStack sourceStack = sourceSlot.getItem();

        List<Slot> targetSlots = new ArrayList<>();
        for (Slot slot : this.menu.slots) {
            if (slot.container instanceof CraftingContainer) {
                targetSlots.add(slot);
            }
        }

        if (targetSlots.isEmpty()) return;

        List<Slot> slotsToFill = new ArrayList<>();
        for (Slot targetSlot : targetSlots) {
            ItemStack targetStack = targetSlot.getItem();
            if (targetStack.isEmpty()) {
                slotsToFill.add(targetSlot);
            } else if (ItemStack.isSameItemSameTags(sourceStack, targetStack)) {
                int maxStack = Math.min(targetSlot.getMaxStackSize(), targetStack.getMaxStackSize());
                if (targetStack.getCount() < maxStack) {
                    slotsToFill.add(targetSlot);
                }
            }
        }

        if (slotsToFill.isEmpty()) return;

        this.minecraft.gameMode.handleInventoryMouseClick(containerId, sourceSlot.index, 0, ClickType.PICKUP, player);

        for (Slot targetSlot : slotsToFill) {
            this.minecraft.gameMode.handleInventoryMouseClick(containerId, targetSlot.index, 0, ClickType.PICKUP, player);
        }

        if (!this.menu.getCarried().isEmpty()) {
            this.minecraft.gameMode.handleInventoryMouseClick(containerId, sourceSlot.index, 0, ClickType.PICKUP, player);
        }
    }

    @Unique
    private void modulation$transferResultToCraftingGrid(Slot resultSlot, boolean craftAll) {
        if (resultSlot == null || !resultSlot.hasItem() || this.minecraft == null) return;
        LocalPlayer player = this.minecraft.player;
        if (player == null || this.minecraft.gameMode == null) return;

        int containerId = this.menu.containerId;
        ItemStack resultTemplate = resultSlot.getItem().copy();

        List<Slot> targetSlots = new ArrayList<>();
        List<Slot> inventorySlots = new ArrayList<>();
        for (Slot slot : this.menu.slots) {
            if (slot.container instanceof CraftingContainer) {
                targetSlots.add(slot);
            } else if (slot.container instanceof Inventory) {
                inventorySlots.add(slot);
            }
        }

        if (targetSlots.isEmpty()) return;

        ItemStack[] virtualSlots = new ItemStack[this.menu.slots.size()];
        for (int i = 0; i < virtualSlots.length; i++) {
            virtualSlots[i] = this.menu.slots.get(i).getItem().copy();
        }

        int possibleCrafts = modulation$getMaxCrafts(virtualSlots);
        int maxCrafts = craftAll ? possibleCrafts : 1;
        if (maxCrafts <= 0) return;

        List<ClickAction> actions = new ArrayList<>();
        ItemStack virtualCarried = ItemStack.EMPTY;

        for (int c = 0; c < maxCrafts; c++) {
            actions.add(new ClickAction(resultSlot.index, 0, ClickType.PICKUP));

            ItemStack produced = resultTemplate.copy();
            if (virtualCarried.isEmpty()) {
                virtualCarried = produced;
            } else {
                virtualCarried.grow(produced.getCount());
            }

            for (Slot slot : this.menu.slots) {
                if (slot.container instanceof CraftingContainer) {
                    if (!virtualSlots[slot.index].isEmpty()) {
                        virtualSlots[slot.index].shrink(1);
                    }
                }
            }

            if (virtualCarried.getCount() >= virtualCarried.getMaxStackSize()) {
                modulation$distributeVirtualCarried(virtualCarried, targetSlots, virtualSlots, actions);
                if (!virtualCarried.isEmpty()) {
                    modulation$distributeVirtualCarried(virtualCarried, inventorySlots, virtualSlots, actions);
                }
                if (!virtualCarried.isEmpty()) {
                    break;
                }
            }
        }

        if (!virtualCarried.isEmpty()) {
            modulation$distributeVirtualCarried(virtualCarried, targetSlots, virtualSlots, actions);
            if (!virtualCarried.isEmpty()) {
                modulation$distributeVirtualCarried(virtualCarried, inventorySlots, virtualSlots, actions);
            }
        }

        for (ClickAction action : actions) {
            this.minecraft.gameMode.handleInventoryMouseClick(containerId, action.slotId(), action.buttonNum(), action.clickType(), player);
        }
    }

    @Unique
    private int modulation$getMaxCrafts(ItemStack[] virtualSlots) {
        int possibleCrafts = 999;
        boolean hasIngredients = false;
        for (Slot slot : this.menu.slots) {
            if (slot.container instanceof CraftingContainer) {
                if (!virtualSlots[slot.index].isEmpty()) {
                    possibleCrafts = Math.min(possibleCrafts, virtualSlots[slot.index].getCount());
                    hasIngredients = true;
                }
            }
        }
        return hasIngredients ? possibleCrafts : 0;
    }

    @Unique
    private void modulation$distributeVirtualCarried(ItemStack carried, List<Slot> targetSlots, ItemStack[] virtualSlots, List<ClickAction> actions) {
        if (carried.isEmpty()) return;

        for (Slot slot : targetSlots) {
            ItemStack stack = virtualSlots[slot.index];
            if (!stack.isEmpty() && ItemStack.isSameItemSameTags(carried, stack)) {
                int maxStack = Math.min(slot.getMaxStackSize(), stack.getMaxStackSize());
                int room = maxStack - stack.getCount();
                if (room > 0) {
                    int toPlace = Math.min(carried.getCount(), room);
                    stack.grow(toPlace);
                    carried.shrink(toPlace);
                    actions.add(new ClickAction(slot.index, 0, ClickType.PICKUP));
                    if (carried.isEmpty()) return;
                }
            }
        }

        for (Slot slot : targetSlots) {
            ItemStack stack = virtualSlots[slot.index];
            if (stack.isEmpty()) {
                int maxStack = Math.min(slot.getMaxStackSize(), carried.getMaxStackSize());
                int toPlace = Math.min(carried.getCount(), maxStack);
                ItemStack newStack = carried.copy();
                newStack.setCount(toPlace);
                virtualSlots[slot.index] = newStack;
                carried.shrink(toPlace);
                actions.add(new ClickAction(slot.index, 0, ClickType.PICKUP));
                if (carried.isEmpty()) return;
            }
        }
    }
}
