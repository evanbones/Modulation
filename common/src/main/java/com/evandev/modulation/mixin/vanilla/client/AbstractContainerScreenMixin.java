package com.evandev.modulation.mixin.vanilla.client;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.client.cursor.CursorFeedbackManager;
import com.evandev.modulation.client.render.SlotHighlightRenderer;
import com.evandev.modulation.modules.vanilla.VanillaGuiModule;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
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

    @Shadow
    protected abstract boolean isHovering(Slot slot, double mouseX, double mouseY);

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

    @Inject(method = "render", at = @At("TAIL"))
    private void modulation$updateCursor(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        Screen screen = this;
        if (screen == this.minecraft.screen) {
            CursorFeedbackManager.update(screen, mouseX, mouseY);
        }
    }

    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void modulation$onSlotClicked(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
        if (ModuleManager.isEnabled("vanilla_gui", VanillaGuiModule.class, VanillaGuiModule::isCtrlDragToCraftingGridEnabled)) {
            if (Screen.hasControlDown() && mouseButton == 0 && type == ClickType.PICKUP) {
                if (slot != null && slot.container instanceof Inventory && slot.hasItem()) {
                    modulation$processedDragSlots.clear();
                    modulation$processedDragSlots.add(slot);
                    modulation$transferSlotToCraftingGrid(slot);
                    ci.cancel();
                }
            }
        }
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void modulation$onMouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY, CallbackInfoReturnable<Boolean> cir) {
        if (ModuleManager.isEnabled("vanilla_gui", VanillaGuiModule.class, VanillaGuiModule::isCtrlDragToCraftingGridEnabled)) {
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
            } else if (ItemStack.isSameItemSameComponents(sourceStack, targetStack)) {
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
}
