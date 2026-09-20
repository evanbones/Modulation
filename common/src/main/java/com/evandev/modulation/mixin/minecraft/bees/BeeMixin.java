package com.evandev.modulation.mixin.minecraft.bees;

import com.evandev.modulation.modules.brainierbees.BeeBrain;
import com.evandev.modulation.modules.brainierbees.BrainierBeesModule;
import com.evandev.modulation.modules.brainierbees.HiveHelper;
import com.evandev.modulation.registry.ModMemoryTypes;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import com.moulberry.mixinconstraints.annotations.IfModAbsent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.pathfinder.PathType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@IfModAbsent("brainierbees")
@Mixin(Bee.class)
public abstract class BeeMixin extends Animal {

    @Unique
    private static final int HIVE_VALIDATION_INTERVAL = 20;

    @Unique
    private boolean modulation$brainAi;

    protected BeeMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected Brain.Provider<?> brainProvider() {
        return BrainierBeesModule.enabled() ? BeeBrain.BRAIN_PROVIDER : Brain.provider(ImmutableList.of(), ImmutableList.of());
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        if (!BrainierBeesModule.enabled()) {
            return this.brainProvider().makeBrain(dynamic);
        }
        return BeeBrain.makeBrain(BeeBrain.BRAIN_PROVIDER.makeBrain(dynamic));
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void modulation$adjustPathfindingMalus(EntityType<? extends Bee> entityType, Level level, CallbackInfo ci) {
        if (BrainierBeesModule.enabled()) {
            this.setPathfindingMalus(PathType.DANGER_FIRE, 8.0F);
            this.setPathfindingMalus(PathType.TRAPDOOR, 8.0F);
            this.setPathfindingMalus(PathType.WATER, -3.0F);
        }
    }

    @Inject(method = "registerGoals", at = @At("RETURN"))
    private void modulation$replaceGoalsWithBrain(CallbackInfo ci) {
        if (!BrainierBeesModule.enabled()) {
            return;
        }
        this.modulation$brainAi = true;
        this.removeAllGoals(goal -> true);
        BeeBrain.initMemories((Bee) (Object) this, this.getRandom());
    }

    @Inject(method = "customServerAiStep", at = @At("RETURN"))
    private void modulation$tickBeeBrain(CallbackInfo ci) {
        if (!this.modulation$brainAi || !(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Bee bee = (Bee) (Object) this;
        Brain<Bee> brain = this.modulation$beeBrain();

        LivingEntity target = bee.getTarget();
        if (target != null) {
            brain.setMemory(MemoryModuleType.ATTACK_TARGET, target);
        }

        ProfilerFiller profiler = serverLevel.getProfiler();
        profiler.push("beeBrain");
        brain.tick(serverLevel, bee);
        profiler.pop();
        profiler.push("beeActivityUpdate");
        BeeBrain.updateActivity(bee);
        profiler.pop();

        this.modulation$tickLocateHiveCooldown(brain);
        this.modulation$validateHive(serverLevel, bee);

        if (BeeBrain.wantsToEnterHive(bee)) {
            brain.setMemory(ModMemoryTypes.BEE_WANTS_HIVE, true);
        } else {
            brain.eraseMemory(ModMemoryTypes.BEE_WANTS_HIVE);
        }
    }

    @Inject(method = "setHivePos", at = @At("RETURN"))
    private void modulation$rememberReleasedHive(BlockPos hivePos, CallbackInfo ci) {
        if (this.modulation$brainAi && hivePos != null) {
            HiveHelper.setHivePos((Bee) (Object) this, hivePos);
        }
    }

    @Unique
    private void modulation$tickLocateHiveCooldown(Brain<Bee> brain) {
        int cooldown = brain.getMemory(ModMemoryTypes.BEE_COOLDOWN_LOCATE_HIVE).orElse(0);
        if (cooldown > 1) {
            brain.setMemory(ModMemoryTypes.BEE_COOLDOWN_LOCATE_HIVE, cooldown - 1);
        } else if (cooldown == 1) {
            brain.eraseMemory(ModMemoryTypes.BEE_COOLDOWN_LOCATE_HIVE);
            brain.eraseMemory(ModMemoryTypes.BEE_HIVE_BLACKLIST);
        }
    }

    @Unique
    private void modulation$validateHive(ServerLevel level, Bee bee) {
        BlockPos hivePos = HiveHelper.getHivePos(bee);
        if (hivePos == null || bee.tickCount % HIVE_VALIDATION_INTERVAL != 0 || !level.isLoaded(hivePos)) {
            return;
        }
        if (!(level.getBlockEntity(hivePos) instanceof BeehiveBlockEntity) || HiveHelper.isHiveNearFire(level, bee)) {
            HiveHelper.dropAndBlacklistHive(bee);
        }
    }

    @Unique
    @SuppressWarnings("unchecked")
    private Brain<Bee> modulation$beeBrain() {
        return (Brain<Bee>) this.getBrain();
    }
}
