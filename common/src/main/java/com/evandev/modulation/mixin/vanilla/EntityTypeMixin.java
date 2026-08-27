package com.evandev.modulation.mixin.vanilla;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.VanillaGameplayModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.class)
public class EntityTypeMixin {

    @Inject(method = "spawn(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/MobSpawnType;ZZ)Lnet/minecraft/world/entity/Entity;", at = @At("RETURN"))
    private void modulation$crackingSpawnEggs(ServerLevel serverLevel, ItemStack stack, Player player, BlockPos pos, MobSpawnType spawnType, boolean shouldOffsetY, boolean shouldOffsetYMore, CallbackInfoReturnable<Entity> cir) {
        if (ModuleManager.isEnabled("vanilla_gameplay", VanillaGameplayModule.class, VanillaGameplayModule::isCrackingSpawnEggsEnabled) && stack != null) {
            if (stack.getItem() instanceof SpawnEggItem) {
                Entity entity = cir.getReturnValue();
                if (entity != null) {
                    AABB box = entity.getBoundingBox();
                    Vec3 center = box.getCenter();
                    serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack.copy()), center.x, center.y, center.z, 20, box.getXsize() / 2.0, box.getYsize() / 2.0, box.getZsize() / 2.0, 0.05);
                    serverLevel.playSound(null, center.x, center.y, center.z, SoundEvents.TURTLE_EGG_CRACK, SoundSource.NEUTRAL, 1.0F, 1.0F);
                }
            }
        }
    }
}
