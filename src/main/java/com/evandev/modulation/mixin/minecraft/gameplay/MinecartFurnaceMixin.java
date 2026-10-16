package com.evandev.modulation.mixin.minecraft.gameplay;

import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.evandev.modulation.util.EntityActions;
import org.spongepowered.asm.mixin.Unique;
//? if >=26.3 {
/*import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;

import java.util.Optional;
*///?}
//? if >=26.1 {
/*import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.phys.Vec3;
*///?} else {
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
//?}

@Mixin(MinecartFurnace.class)
public abstract class MinecartFurnaceMixin extends AbstractMinecart {

    //? if >=26.1 {
    /*@Shadow
    public Vec3 push;
    *///?} else {
    @Shadow
    public double xPush;
    @Shadow
    public double zPush;
    //?}
    @Shadow
    private int fuel;

    protected MinecartFurnaceMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    //? if >=26.1 {
    /*@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void modulation$acceptAnyFuel(Player player, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir) {
        if (VanillaGameplayModule.FURNACE_MINECART_ANY_FUEL.on()) {
            ItemStack itemstack = player.getItemInHand(hand);
            int burnTime = this.modulation$burnTime(itemstack);
            if (burnTime > 0) {
                int fuelValue = (int) (burnTime * 2.25);
                if (this.fuel + fuelValue <= 32000) {
                    ItemStackTemplate remainder = itemstack.getItem().getCraftingRemainder();
                    itemstack.consume(1, player);
                    if (remainder != null && !player.hasInfiniteMaterials()) {
                        ItemStack remainderStack = remainder.create();
                        if (!player.getInventory().add(remainderStack)) {
                            EntityActions.drop(player, remainderStack, false);
                        }
                    }
                    this.fuel += fuelValue;
                }

                if (this.fuel > 0) {
                    this.push = this.position().subtract(player.position()).horizontal();
                }

                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }

    @Unique
    private int modulation$burnTime(ItemStack stack) {
        //? if >=26.3 {
        /^if (!(this.level() instanceof ServerLevel serverLevel)) {
            return stack.has(DataComponents.COOKING_FUEL) ? 1 : 0;
        }
        FurnaceBlockEntity furnace = new FurnaceBlockEntity(this.blockPosition(), Blocks.FURNACE.defaultBlockState());
        LootContext context = new LootContext.Builder(new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.BLOCK_STATE, furnace.getBlockState())
                .withParameter(LootContextParams.BLOCK_ENTITY, furnace)
                .withParameter(LootContextParams.ORIGIN, this.position())
                .withParameter(LootContextParams.CONTAINER, furnace)
                .create(LootContextParamSets.CONTAINER_PROCESS))
                .create(Optional.empty());
        return ResolvableInt.getFromItem(stack, DataComponents.COOKING_FUEL, CookingFuel::burnTime, context, 0);
        ^///?} else {
        return this.level().fuelValues().burnDuration(stack);
        //?}
    }
    *///?} else {
    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void modulation$acceptAnyFuel(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (VanillaGameplayModule.FURNACE_MINECART_ANY_FUEL.on()) {
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
                            EntityActions.drop(player, remainder, false);
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
    //?}
}
