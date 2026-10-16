package com.evandev.modulation.mixin.minecraft.gui.client;

import com.evandev.modulation.Constants;
import com.evandev.modulation.mixin.minecraft.accessor.AbstractContainerScreenAccessor;
import com.evandev.modulation.modules.vanilla.VanillaGuiModule;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >=26.1 {
/*import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
*///?} else {
import com.evandev.modulation.client.CreativeInventoryClicks;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
//?}

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends Screen {
    @Unique
    private static final WidgetSprites modulation$CLEAR_BUTTON_SPRITES = new WidgetSprites(
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "clear_button"),
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "clear_button_highlighted")
    );

    @Unique
    private ImageButton modulation$clearButton;

    protected InventoryScreenMixin(Component title) {
        super(title);
    }

    //? if >=26.1 {
    /*@WrapOperation(
            method = "containerTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;hasInfiniteMaterials()Z"))
    private boolean modulation$wrapTick(LocalPlayer instance, Operation<Boolean> original) {
        if (VanillaGuiModule.DISABLE_CREATIVE_INVENTORY.on())
            return false;
        return original.call(instance);
    }

    @WrapOperation(
            method = "init",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;hasInfiniteMaterials()Z"))
    private boolean modulation$wrapInit(LocalPlayer instance, Operation<Boolean> original) {
        if (VanillaGuiModule.DISABLE_CREATIVE_INVENTORY.on())
            return false;
        return original.call(instance);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", at = @At("TAIL"))
    private void modulation$renderClearButtonTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (this.modulation$deletionButtonActive() && this.modulation$clearButton != null && this.modulation$clearButton.isHovered()) {
            guiGraphics.setTooltipForNextFrame(Component.translatable("inventory.binSlot"), mouseX, mouseY);
        }
    }
    *///?} else {
    @WrapOperation(
            method = "containerTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasInfiniteItems()Z"))
    private boolean modulation$wrapTick(MultiPlayerGameMode instance, Operation<Boolean> original) {
        if (VanillaGuiModule.DISABLE_CREATIVE_INVENTORY.on())
            return false;
        return original.call(instance);
    }

    @WrapOperation(
            method = "init",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasInfiniteItems()Z"))
    private boolean modulation$wrapInit(MultiPlayerGameMode instance, Operation<Boolean> original) {
        if (VanillaGuiModule.DISABLE_CREATIVE_INVENTORY.on())
            return false;
        return original.call(instance);
    }

    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void modulation$onSlotClicked(Slot slot, int slotId, int mouseButton, ClickType type, CallbackInfo ci) {
        if (CreativeInventoryClicks.handle(slot, slotId, mouseButton, type)) {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void modulation$renderClearButtonTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (this.modulation$deletionButtonActive() && this.modulation$clearButton != null && this.modulation$clearButton.isHovered()) {
            guiGraphics.renderTooltip(this.font, Component.translatable("inventory.binSlot"), mouseX, mouseY);
        }
    }
    //?}

    @Unique
    private boolean modulation$deletionButtonActive() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode == null || mc.player == null || !mc.player.hasInfiniteMaterials()) return false;
        return VanillaGuiModule.DISABLE_CREATIVE_INVENTORY.on() && VanillaGuiModule.CREATIVE_DELETION_BUTTON.on();
    }

    @Unique
    private static boolean modulation$hasShiftDown() {
        //? if >=26.1 {
        /*return Minecraft.getInstance().hasShiftDown();
        *///?} else {
        return Screen.hasShiftDown();
        //?}
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void modulation$addClearButton(CallbackInfo ci) {
        if (!this.modulation$deletionButtonActive()) return;
        Minecraft mc = Minecraft.getInstance();

        InventoryScreen screen = (InventoryScreen) (Object) this;
        AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) screen;
        int initialX = accessor.getLeftPos() + VanillaGuiModule.CLEAR_BUTTON_X.get();
        int initialY = accessor.getTopPos() + VanillaGuiModule.CLEAR_BUTTON_Y.get();

        ResourceLocation tabTexture = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/clear_button_tab.png");

        ImageButton clearButton = new ImageButton(
                initialX,
                initialY,
                28,
                29,
                modulation$CLEAR_BUTTON_SPRITES,
                button -> {
                    if (mc.gameMode == null || mc.player == null) return;

                    if (modulation$hasShiftDown()) {
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
            //? if >=26.1 {
            /*public void extractContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            *///?} else {
            public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            //?}
                this.setX(accessor.getLeftPos() + VanillaGuiModule.CLEAR_BUTTON_X.get());
                this.setY(accessor.getTopPos() + VanillaGuiModule.CLEAR_BUTTON_Y.get());

                //? if >=26.1 {
                /*guiGraphics.blit(RenderPipelines.GUI_TEXTURED, tabTexture, this.getX(), this.getY(), 0, 0, 28, 29, 28, 29);
                *///?} else {
                guiGraphics.blit(tabTexture, this.getX(), this.getY(), 0, 0, 28, 29, 28, 29);
                //?}

                if (this.isHovered()) {
                    guiGraphics.fill(this.getX() + 5, this.getY() + 5, this.getX() + 23, this.getY() + 23, 0x40FFFFFF);
                }
            }
        };

        this.modulation$clearButton = clearButton;
        this.addRenderableWidget(clearButton);
    }
}
