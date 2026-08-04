package com.evandev.modulation.mixin.vanilla.passablefoliage;

import com.evandev.modulation.api.ModuleManager;
import com.evandev.modulation.modules.vanilla.PassableFoliageModule;
import com.evandev.modulation.modules.vanillabackport.EntityLeafDrag;
import com.evandev.modulation.modules.vanillabackport.LeafParticles;
import com.evandev.modulation.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public abstract class EntityLeafDragMixin implements EntityLeafDrag {

    @Unique
    private static final double MODULATION$DRAG_PER_SPEED = 0.15;
    @Unique
    private static final double MODULATION$BURIED_MULTIPLIER = 1.8;
    @Unique
    private static final double MODULATION$MAX_DRAG_BURIED = 0.90;
    @Unique
    private static final double MODULATION$DIVE_DRAG_MULTIPLIER = 0.30;
    @Unique
    private static final double MODULATION$MIN_EFFECT_SPEED = 0.02;
    @Unique
    private static final double MODULATION$CRASH_SPEED = 0.5;
    @Unique
    private static final double MODULATION$COUNT_LINEAR = 2.0;
    @Unique
    private static final double MODULATION$COUNT_QUADRATIC = 6.0;
    @Unique
    private static final int MODULATION$MAX_PARTICLES = 16;
    @Unique
    private static final double MODULATION$FLING_LOW_SLOPE = 1.4;
    @Unique
    private static final double MODULATION$FLING_HIGH_BASE = 0.15;
    @Unique
    private static final double MODULATION$FLING_HIGH_SLOPE = 0.18;
    @Unique
    private static final double MODULATION$FLING_CAP = 0.7;

    @Unique
    private int modulation$lastLeafDragTick = Integer.MIN_VALUE;

    @Override
    public void modulation$applyLeafDrag(BlockState state, Level level, BlockPos pos) {
        PassableFoliageModule module = ModuleManager.getModule("passable_foliage", PassableFoliageModule.class);
        if (module == null || !module.isPassableFoliageEnabled()) {
            return;
        }

        Entity self = (Entity) (Object) this;
        if (self.tickCount == this.modulation$lastLeafDragTick) {
            return;
        }

        Vec3 delta = self.getDeltaMovement();
        Vec3 movement = new Vec3(self.getX() - self.xo, self.getY() - self.yo, self.getZ() - self.zo);
        double speed = movement.length();
        if (speed < 1.0E-4) {
            return;
        }

        boolean entering = self.tickCount != this.modulation$lastLeafDragTick + 1;
        this.modulation$lastLeafDragTick = self.tickCount;

        boolean diving = self.isShiftKeyDown() && movement.y < 0.0 && !self.onGround();
        boolean buried = level.getBlockState(self.blockPosition()).is(ModTags.PASSABLE_LEAVES)
                && level.getBlockState(BlockPos.containing(self.getEyePosition())).is(ModTags.PASSABLE_LEAVES);

        double drag = module.getBaseDrag() + MODULATION$DRAG_PER_SPEED * speed;
        if (buried) {
            drag *= MODULATION$BURIED_MULTIPLIER;
        }
        drag = Math.min(buried ? MODULATION$MAX_DRAG_BURIED : module.getMaxDrag(), drag);
        if (diving) {
            drag *= MODULATION$DIVE_DRAG_MULTIPLIER;
        }
        self.setDeltaMovement(delta.scale(1.0 - drag));

        if (!diving && movement.y < 0.0 && self.fallDistance > 0.0) {
            self.fallDistance = (float) (self.fallDistance * (1.0 - drag));
        }

        if (level instanceof ServerLevel serverLevel && speed > MODULATION$MIN_EFFECT_SPEED) {
            if (module.isLeafSoundsEnabled()) {
                SoundType sound = state.getSoundType();
                if (entering && speed >= MODULATION$CRASH_SPEED) {
                    float volume = (float) Math.min(1.0, 0.4 + speed * 0.2);
                    float pitch = sound.getPitch() * (0.9F + self.getRandom().nextFloat() * 0.2F);
                    level.playSound(null, pos, sound.getBreakSound(), SoundSource.BLOCKS, volume, pitch);
                } else if (self.getRandom().nextFloat() < (float) Math.min(1.0, speed)) {
                    float volume = (float) Math.min(0.55, 0.1 + speed * 0.35);
                    float pitch = sound.getPitch() * (0.9F + self.getRandom().nextFloat() * 0.2F);
                    level.playSound(null, pos, sound.getStepSound(), SoundSource.BLOCKS, volume, pitch);
                }
            }

            double expected = speed * (MODULATION$COUNT_LINEAR + MODULATION$COUNT_QUADRATIC * speed);
            int count = (int) expected;
            if (self.getRandom().nextFloat() < expected - count) {
                count++;
            }
            if (count > 0) {
                ParticleOptions leafParticle = LeafParticles.resolve(state, serverLevel, pos);
                if (leafParticle != null) {
                    double sideSpread = Math.min(speed * 2.0, 1.6);
                    double flingBase = Math.min(speed * MODULATION$FLING_LOW_SLOPE,
                            MODULATION$FLING_HIGH_BASE + speed * MODULATION$FLING_HIGH_SLOPE);
                    AABB box = self.getBoundingBox();
                    for (int i = 0; i < Math.min(count, MODULATION$MAX_PARTICLES); i++) {
                        double px = Math.max(pos.getX(), Math.min(pos.getX() + 1.0,
                                box.minX + self.getRandom().nextDouble() * (box.maxX - box.minX)));
                        double py = Math.max(pos.getY(), Math.min(pos.getY() + 1.0,
                                box.minY + self.getRandom().nextDouble() * (box.maxY - box.minY)));
                        double pz = Math.max(pos.getZ(), Math.min(pos.getZ() + 1.0,
                                box.minZ + self.getRandom().nextDouble() * (box.maxZ - box.minZ)));
                        double vy = Math.min(MODULATION$FLING_CAP, flingBase * (0.8 + self.getRandom().nextDouble() * 0.5));
                        double maxSide = vy * 0.7;
                        double vx = Math.max(-maxSide, Math.min(maxSide,
                                movement.x * 0.4 + (self.getRandom().nextDouble() - 0.5) * sideSpread));
                        double vz = Math.max(-maxSide, Math.min(maxSide,
                                movement.z * 0.4 + (self.getRandom().nextDouble() - 0.5) * sideSpread));
                        serverLevel.sendParticles(leafParticle, px, py, pz, 0, vx, vy, vz, 1.0);
                    }
                }
            }
        }
    }
}
