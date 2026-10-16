package com.evandev.modulation.mixin.minecraft.gui.client;

//? if <26.1 {
import com.evandev.modulation.client.cursor.CursorFeedbackManager;
//?}
//? if <26.1 {
import com.evandev.modulation.client.render.SlotHighlightRenderer;
//?}
//? if >=26.1 {
/*import com.evandev.modulation.client.CreativeInventoryClicks;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
*///?}
import com.evandev.modulation.modules.vanilla.VanillaGuiModule;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
//? if >=26.1
//import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.Slot;
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

    protected AbstractContainerScreenMixin(Component title) {
        super(title);
    }

    //? if <26.1 {
    @Shadow
    protected abstract boolean isHovering(Slot slot, double mouseX, double mouseY);
    //?}

    //? if <26.1 {
    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderSlot(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/inventory/Slot;)V"))
    private void modulation$renderSlotHighlightBack(AbstractContainerScreen<T> instance, GuiGraphics guiGraphics, Slot slot, Operation<Void> original,
                                                    @Local(argsOnly = true, ordinal = 0) int mouseX, @Local(argsOnly = true, ordinal = 1) int mouseY) {
        if (SlotHighlightRenderer.isEnabled() && slot.isHighlightable() && this.isHovering(slot, mouseX, mouseY)) {
            SlotHighlightRenderer.renderBack(guiGraphics, slot);
        }
        original.call(instance, guiGraphics, slot);
    }
    //?}

    //? if <26.1 {
    @Inject(method = "render", at = @At("TAIL"))
    private void modulation$updateCursor(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        Screen screen = this;
        if (screen == this.minecraft.screen) {
            CursorFeedbackManager.update(screen, mouseX, mouseY);
        }
    }
    //?}

    //? if >=26.1 {
    /*@Inject(method = "slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ClickType;)V", at = @At("HEAD"), cancellable = true)
    private void modulation$creativeInventoryClick(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
        if ((Object) this instanceof InventoryScreen && CreativeInventoryClicks.handle(slot, slotId, mouseButton, type)) {
            ci.cancel();
        }
    }
    *///?}

    @Inject(method = "slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ClickType;)V", at = @At("HEAD"), cancellable = true)
    private void modulation$onSlotClicked(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
        if (VanillaGuiModule.CTRL_DRAG_TO_CRAFTING_GRID.on()) {
            if (modulation$hasControlDown() && mouseButton == 0 && type == ClickType.PICKUP) {
                if (slot != null && slot.container instanceof Inventory && slot.hasItem()) {
                    modulation$processedDragSlots.clear();
                    modulation$processedDragSlots.add(slot);
                    modulation$transferSlotToCraftingGrid(slot);
                    ci.cancel();
                }
            }
        }
    }

    //? if >=26.1 {
    /*@Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void modulation$onMouseDragged(MouseButtonEvent event, double dragX, double dragY, CallbackInfoReturnable<Boolean> cir) {
        int button = event.button();
    *///?} else {
    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void modulation$onMouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY, CallbackInfoReturnable<Boolean> cir) {
    //?}
        if (VanillaGuiModule.CTRL_DRAG_TO_CRAFTING_GRID.on()) {
            if (modulation$hasControlDown() && button == InputConstants.MOUSE_BUTTON_LEFT) {
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

    //? if >=26.1 {
    /*@Inject(method = "mouseReleased", at = @At("HEAD"))
    private void modulation$onMouseReleased(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        modulation$processedDragSlots.clear();
    }
    *///?} else {
    @Inject(method = "mouseReleased", at = @At("HEAD"))
    private void modulation$onMouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        modulation$processedDragSlots.clear();
    }
    //?}

    @Unique
    private boolean modulation$hasControlDown() {
        //? if >=26.1 {
        /*return this.minecraft.hasControlDown();
        *///?} else {
        return Screen.hasControlDown();
        //?}
    }

    @Unique
    private void modulation$click(int containerId, int slotIndex, LocalPlayer player) {
        //? if >=26.1 {
        /*this.minecraft.gameMode.handleContainerInput(containerId, slotIndex, 0, ClickType.PICKUP, player);
        *///?} else {
        this.minecraft.gameMode.handleInventoryMouseClick(containerId, slotIndex, 0, ClickType.PICKUP, player);
        //?}
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
            } else if (ItemStack.isSameItemSameComponents(sourceStack, targetStack)) {
                int maxStack = Math.min(targetSlot.getMaxStackSize(), targetStack.getMaxStackSize());
                if (targetStack.getCount() < maxStack) {
                    slotsToFill.add(targetSlot);
                }
            }
        }

        if (slotsToFill.isEmpty()) return;

        modulation$click(containerId, sourceSlot.index, player);

        for (Slot targetSlot : slotsToFill) {
            modulation$click(containerId, targetSlot.index, player);
        }

        if (!this.menu.getCarried().isEmpty()) {
            modulation$click(containerId, sourceSlot.index, player);
        }
    }
}
