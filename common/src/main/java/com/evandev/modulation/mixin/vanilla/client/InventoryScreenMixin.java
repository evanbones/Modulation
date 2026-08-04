package com.evandev.modulation.mixin.vanilla.client;

import com.evandev.modulation.Constants;
import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.mixin.vanilla.accessor.AbstractContainerScreenAccessor;
import com.evandev.modulation.modules.vanilla.VanillaGuiModule;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends Screen {
    @Unique
    private ImageButton modulation$clearButton;

    protected InventoryScreenMixin(Component title) {
        super(title);
    }

    @WrapOperation(
            method = "containerTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasInfiniteItems()Z"))
    private boolean modulation$wrapTick(MultiPlayerGameMode instance, Operation<Boolean> original) {
        if (ModuleManager.isEnabled("vanilla_gui", VanillaGuiModule.class, VanillaGuiModule::isDisableCreativeInventoryEnabled))
            return false;
        return original.call(instance);
    }

    @WrapOperation(
            method = "init",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasInfiniteItems()Z"))
    private boolean modulation$wrapInit(MultiPlayerGameMode instance, Operation<Boolean> original) {
        if (ModuleManager.isEnabled("vanilla_gui", VanillaGuiModule.class, VanillaGuiModule::isDisableCreativeInventoryEnabled))
            return false;
        return original.call(instance);
    }

    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void modulation$onSlotClicked(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode == null || mc.player == null) return;
        if (!mc.gameMode.hasInfiniteItems()) return;

        VanillaGuiModule module = ModuleManager.getModule("vanilla_gui", VanillaGuiModule.class);
        if (module == null || !module.isDisableCreativeInventoryEnabled()) return;

        ci.cancel();

        type = slotId == -999 && type == ClickType.PICKUP ? ClickType.THROW : type;

        if (slot == null && type != ClickType.QUICK_CRAFT) {
            if (!mc.player.inventoryMenu.getCarried().isEmpty()) {
                if (mouseButton == 0) {
                    mc.player.drop(mc.player.inventoryMenu.getCarried(), true);
                    mc.gameMode.handleCreativeModeItemDrop(mc.player.inventoryMenu.getCarried());
                    mc.player.inventoryMenu.setCarried(ItemStack.EMPTY);
                } else if (mouseButton == 1) {
                    ItemStack split = mc.player.inventoryMenu.getCarried().split(1);
                    mc.player.drop(split, true);
                    mc.gameMode.handleCreativeModeItemDrop(split);
                }
            }
        } else if (slot != null && !slot.mayPickup(mc.player)) {
            return;
        } else if (type == ClickType.THROW && slot.hasItem()) {
            ItemStack itemstack = slot.remove(mouseButton == 0 ? 1 : slot.getItem().getMaxStackSize());
            ItemStack itemstack1 = slot.getItem();
            mc.player.drop(itemstack, true);
            mc.gameMode.handleCreativeModeItemDrop(itemstack);
            mc.gameMode.handleCreativeModeItemAdd(itemstack1, slot.index);
        } else if (type == ClickType.THROW && !mc.player.inventoryMenu.getCarried().isEmpty()) {
            mc.player.drop(mc.player.inventoryMenu.getCarried(), true);
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
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void modulation$renderClearButtonTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode == null || !mc.gameMode.hasInfiniteItems()) return;
        VanillaGuiModule module = ModuleManager.getModule("vanilla_gui", VanillaGuiModule.class);
        if (module == null || !module.isDisableCreativeInventoryEnabled()) return;

        if (this.modulation$clearButton != null && this.modulation$clearButton.isHovered()) {
            guiGraphics.renderTooltip(this.font, Component.translatable("inventory.binSlot"), mouseX, mouseY);
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void modulation$addClearButton(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode == null || mc.player == null) return;
        if (!mc.gameMode.hasInfiniteItems()) return;

        VanillaGuiModule module = ModuleManager.getModule("vanilla_gui", VanillaGuiModule.class);
        if (module == null || !module.isDisableCreativeInventoryEnabled()) return;

        InventoryScreen screen = (InventoryScreen) (Object) this;
        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) screen;
        int initialX = accessor.getLeftPos() + module.getClearButtonX();
        int initialY = accessor.getTopPos() + module.getClearButtonY();

        ResourceLocation tabTexture = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/clear_button_tab.png");

        ImageButton clearButton = new ImageButton(
                initialX,
                initialY,
                28,
                29,
                RecipeBookComponent.RECIPE_BUTTON_SPRITES,
                button -> {
                    if (mc.gameMode == null || mc.player == null) return;
                    boolean isShiftPressed = Screen.hasShiftDown();

                    if (isShiftPressed) {
                        for (int i = 0; i < mc.player.inventoryMenu.slots.size(); i++) {
                            mc.gameMode.handleCreativeModeItemAdd(ItemStack.EMPTY, i);
                            mc.player.inventoryMenu.getSlot(i).set(ItemStack.EMPTY);
                        }
                        screen.getMenu().setCarried(ItemStack.EMPTY);
                    } else {
                        screen.getMenu().setCarried(ItemStack.EMPTY);
                    }
                },
                Component.translatable("inventory.binSlot")
        ) {
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                this.setX(accessor.getLeftPos() + module.getClearButtonX());
                this.setY(accessor.getTopPos() + module.getClearButtonY());

                guiGraphics.blit(tabTexture, this.getX(), this.getY(), 0, 0, 28, 29, 28, 29);

                if (this.isHovered()) {
                    guiGraphics.fill(this.getX() + 5, this.getY() + 5, this.getX() + 23, this.getY() + 23, 0x40FFFFFF);
                }
            }
        };

        this.modulation$clearButton = clearButton;
        this.addRenderableWidget(clearButton);
    }
}
