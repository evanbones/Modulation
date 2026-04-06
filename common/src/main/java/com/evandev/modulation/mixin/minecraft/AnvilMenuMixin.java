package com.evandev.modulation.mixin.minecraft;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.VanillaModule;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AnvilMenu.class, priority = 1500)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    @Shadow
    @Final
    private DataSlot cost;

    @Shadow
    private String itemName;

    public AnvilMenuMixin(@Nullable MenuType<?> type, int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(type, containerId, inventory, access);
    }

    @ModifyExpressionValue(method = "createResult", at = @At(value = "CONSTANT", args = "intValue=40"), require = 0)
    private int modulation$removeTooExpensiveLimit(int constant) {
        VanillaModule module = (VanillaModule) ModuleManager.getModule("vanilla");
        if (module != null && module.isRemoveAnvilLimitEnabled()) {
            return Integer.MAX_VALUE;
        }
        return constant;
    }

    @WrapOperation(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/DataSlot;set(I)V"))
    private void modulation$modifyIndependentAnvilCosts(DataSlot instance, int originalCost, Operation<Void> original) {
        VanillaModule module = (VanillaModule) ModuleManager.getModule("vanilla");
        if (module == null) {
            original.call(instance, originalCost);
            return;
        }

        ItemStack input1 = this.inputSlots.getItem(0);
        ItemStack input2 = this.inputSlots.getItem(1);

        if (input1.isEmpty()) {
            original.call(instance, originalCost);
            return;
        }

        int baseCost = input1.getBaseRepairCost() + (input2.isEmpty() ? 0 : input2.getBaseRepairCost());
        boolean isRenaming = this.itemName != null && !this.itemName.isEmpty() && !this.itemName.equals(input1.getHoverName().getString());
        int vanillaRenameCost = isRenaming ? 1 : 0;

        int vanillaActionCost = Math.max(0, originalCost - baseCost - vanillaRenameCost);

        boolean isMaterialRepair = input1.isDamageableItem() && input1.getItem().isValidRepairItem(input1, input2);
        boolean isItemCombine = !input2.isEmpty() && input1.getItem() == input2.getItem();

        int finalCost = 0;

        if (isRenaming && !module.isNoAnvilRenameCostEnabled()) {
            finalCost += vanillaRenameCost;
        }

        if (isMaterialRepair) {
            if (!module.isNoAnvilRepairCostEnabled()) {
                finalCost += vanillaActionCost;
            }
        } else if (!input2.isEmpty()) {
            boolean didRepair = input1.isDamageableItem() && input1.getDamageValue() > 0 && isItemCombine;
            int repCost = 0;
            int enchCost = vanillaActionCost;

            if (didRepair && vanillaActionCost >= 2) {
                repCost = 2;
                enchCost = vanillaActionCost - 2;
            }

            if (!module.isNoAnvilRepairCostEnabled()) finalCost += repCost;
            if (!module.isNoAnvilEnchantCostEnabled()) finalCost += enchCost;
        } else {
            finalCost += vanillaActionCost;
        }

        if (finalCost > 0) {
            finalCost += baseCost;
        }

        original.call(instance, finalCost);
    }

    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    private void modulation$allowZeroCostPickup(Player player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        if (this.cost.get() <= 0 && present) {
            cir.setReturnValue(true);
        }
    }
}