package com.evandev.modulation.mixin.vanilla.client;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.mixin.vanilla.accessor.MinecraftAccessor;
import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BreakingItemParticle.class)
public abstract class BreakingItemParticleMixin extends TextureSheetParticle {

    protected BreakingItemParticleMixin(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/multiplayer/ClientLevel;DDDLnet/minecraft/world/item/ItemStack;)V", at = @At("TAIL"))
    private void modulation$coloredCrackParticles(ClientLevel level, double x, double y, double z, ItemStack stack, CallbackInfo ci) {
        if (ModuleManager.isEnabled("vanilla_bugfixes", VanillaBugfixesModule.class, VanillaBugfixesModule::isColoredCrackParticlesEnabled)) {
            if (stack.getItem() instanceof PotionItem) return;
            ItemColors itemColors = ((MinecraftAccessor) Minecraft.getInstance()).modulation$getItemColors();
            if (itemColors != null) {
                int c = itemColors.getColor(stack, 0);
                this.setColor(((c >> 16) & 0xFF) / 255.0F, ((c >> 8) & 0xFF) / 255.0F, (c & 0xFF) / 255.0F);
            }
        }
    }
}
