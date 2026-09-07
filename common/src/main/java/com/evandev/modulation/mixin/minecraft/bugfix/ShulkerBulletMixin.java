package com.evandev.modulation.mixin.minecraft.bugfix;

import com.evandev.modulation.modules.vanilla.VanillaBugfixesModule;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBullet.class)
public class ShulkerBulletMixin {

    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ShulkerBullet;setPos(DDD)V")
    )
    private void modulation$shulkerBulletBubbles(CallbackInfo ci) {
        ShulkerBullet bullet = (ShulkerBullet) (Object) this;
        if (!bullet.isInWater() || !VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixShulkerBulletBubblesEnabled)) {
            return;
        }

        Vec3 position = bullet.position();
        Vec3 movement = bullet.getDeltaMovement();

        for (int i = 0; i < 4; i++) {
            bullet.level()
                    .addParticle(
                            ParticleTypes.BUBBLE,
                            position.x - movement.x * 0.25,
                            position.y - movement.y * 0.25,
                            position.z - movement.z * 0.25,
                            movement.x,
                            movement.y,
                            movement.z
                    );
        }
    }

    @Inject(
            method = "onHitEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/EntityHitResult;getEntity()Lnet/minecraft/world/entity/Entity;")
    )
    private void modulation$shulkerBulletImpactEffects(EntityHitResult result, CallbackInfo ci) {
        ShulkerBullet bullet = (ShulkerBullet) (Object) this;
        if (bullet.level() instanceof ServerLevel serverLevel && VanillaBugfixesModule.enabled(VanillaBugfixesModule::isFixShulkerBulletImpactEnabled)) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, bullet.getX(), bullet.getY(), bullet.getZ(), 2, 0.2, 0.2, 0.2, 0.0);
            bullet.playSound(SoundEvents.SHULKER_BULLET_HIT, 1.0F, 1.0F);
        }
    }
}
