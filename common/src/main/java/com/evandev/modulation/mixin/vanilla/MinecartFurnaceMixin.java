package com.evandev.modulation.mixin.vanilla;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecartFurnace.class)
public abstract class MinecartFurnaceMixin extends AbstractMinecart {

    @Shadow
    public double xPush;
    @Shadow
    public double zPush;
    @Shadow
    private int fuel;

    protected MinecartFurnaceMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void modulation$acceptAnyFuel(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isFurnaceMinecartAnyFuelEnabled)) {
            ItemStack itemstack = player.getItemInHand(hand);
            int burnTime = AbstractFurnaceBlockEntity.getFuel().getOrDefault(itemstack.getItem(), 0);
            if (burnTime > 0) {
                int fuelValue = (int) (burnTime * 2.25);
                if (this.fuel + fuelValue <= 32000) {
                    Item remainderItem = itemstack.getItem().hasCraftingRemainingItem() ? itemstack.getItem().getCraftingRemainingItem() : null;
                    itemstack.consume(1, player);
                    if (remainderItem != null && !player.hasInfiniteMaterials()) {
                        ItemStack remainder = new ItemStack(remainderItem);
                        if (!player.getInventory().add(remainder)) {
                            player.drop(remainder, false);
                        }
                    }
                    this.fuel += fuelValue;
                }

                if (this.fuel > 0) {
                    this.xPush = this.getX() - player.getX();
                    this.zPush = this.getZ() - player.getZ();
                }

                cir.setReturnValue(InteractionResult.sidedSuccess(this.level().isClientSide));
            }
        }
    }
}
