package com.evandev.modulation.modules.brainierbees;

import com.evandev.modulation.mixin.minecraft.accessor.BeeAccessor;
import com.evandev.modulation.modules.brainierbees.tasks.*;
import com.evandev.modulation.registry.ModMemoryTypes;
import com.evandev.modulation.registry.ModSensorTypes;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
//? if >=26.2 {
/*import net.minecraft.world.entity.EntityTypes;
*///?} else {
import net.minecraft.world.entity.EntityType;
//?}
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;

import java.util.Set;
import java.util.function.Predicate;
//? if >=26.1 {
/*import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.ai.ActivityData;

import java.util.List;
*///?}

public class BeeBrain {

    public static final ImmutableList<SensorType<? extends Sensor<? super Bee>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_ADULT,
            SensorType.HURT_BY,
            ModSensorTypes.BEE_TEMPTATIONS
    );

    public static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.NEAREST_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_PLAYERS,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_ADULT,
            MemoryModuleType.NEAREST_ATTACKABLE,
            MemoryModuleType.HURT_BY,
            MemoryModuleType.HURT_BY_ENTITY,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.BREED_TARGET,
            MemoryModuleType.TEMPTING_PLAYER,
            MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
            //? if <26.3
            MemoryModuleType.IS_TEMPTED,
            MemoryModuleType.IS_PANICKING,
            ModMemoryTypes.BEE_FLOWER_POS,
            ModMemoryTypes.BEE_HIVE_POS,
            ModMemoryTypes.BEE_LAST_PATH,
            ModMemoryTypes.BEE_HIVE_BLACKLIST,
            ModMemoryTypes.BEE_POLLINATING_COOLDOWN,
            ModMemoryTypes.BEE_POLLINATING_TICKS,
            ModMemoryTypes.BEE_SUCCESSFUL_POLLINATING_TICKS,
            ModMemoryTypes.BEE_COOLDOWN_LOCATE_HIVE,
            ModMemoryTypes.BEE_TRAVELLING_TICKS,
            ModMemoryTypes.BEE_STUCK_TICKS,
            ModMemoryTypes.BEE_WANTS_HIVE
    );

    //? if >=26.1 {
    /*@SuppressWarnings("deprecation")
    public static final Brain.Provider<Bee> BRAIN_PROVIDER = Brain.provider(MEMORY_TYPES, SENSOR_TYPES, bee -> createActivities());
    *///?} else {
    public static final Brain.Provider<Bee> BRAIN_PROVIDER = Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    //?}
    private static final UniformInt TIME_BETWEEN_POLLINATING = UniformInt.of(10, 15);
    private static final UniformInt ADULT_FOLLOW_RANGE = UniformInt.of(3, 16);

    public static boolean wantsToEnterHive(Bee bee) {
        BeeAccessor accessor = (BeeAccessor) bee;
        if (accessor.modulation$getStayOutOfHiveCountdown() > 0 || bee.hasStung() || bee.getTarget() != null) {
            return false;
        }
        //? if >=26.1 {
        /*boolean wants = bee.level().environmentAttributes().getValue(EnvironmentAttributes.BEES_STAY_IN_HIVE, bee.position()) || bee.hasNectar();
        *///?} else {
        boolean wants = bee.level().isRaining() || bee.level().isNight() || bee.hasNectar();
        //?}
        return wants && !HiveHelper.isHiveNearFire(bee.level(), bee);
    }

    public static Predicate<ItemStack> getTemptations() {
        return stack -> stack.is(ItemTags.BEE_FOOD);
    }

    //? if >=26.1 {
    /*private static List<ActivityData<Bee>> createActivities() {
        return List.of(
                ActivityData.create(Activity.CORE, 0, coreBehaviors()),
                ActivityData.create(Activity.IDLE, idleBehaviors(), idleConditions()),
                ActivityData.create(Activity.FIGHT, 0, stingBehaviors(), MemoryModuleType.ATTACK_TARGET),
                ActivityData.create(Activity.CELEBRATE, pollinateBehaviors(), pollinateConditions())
        );
    }
    *///?} else {
    public static Brain<Bee> makeBrain(Brain<Bee> brain) {
        brain.addActivity(Activity.CORE, 0, coreBehaviors());
        brain.addActivityWithConditions(Activity.IDLE, idleBehaviors(), idleConditions());
        brain.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 0, stingBehaviors(), MemoryModuleType.ATTACK_TARGET);
        brain.addActivityWithConditions(Activity.CELEBRATE, pollinateBehaviors(), pollinateConditions());
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }
    //?}

    public static void initMemories(Bee bee, RandomSource random) {
        bee.getBrain().setMemory(ModMemoryTypes.BEE_POLLINATING_COOLDOWN, TIME_BETWEEN_POLLINATING.sample(random));
    }

    private static ImmutableList<BehaviorControl<? super Bee>> coreBehaviors() {
        return ImmutableList.of(
                new MoveToTargetSink(),
                new CountDownCooldownTicks(ModMemoryTypes.BEE_POLLINATING_COOLDOWN),
                new CountDownCooldownTicks(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)
        );
    }

    private static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Bee>>> idleBehaviors() {
        return ImmutableList.<Pair<Integer, ? extends BehaviorControl<? super Bee>>>of(
                Pair.of(0, new GoToHiveTask()),
                Pair.of(1, BabyFollowAdult.create(ADULT_FOLLOW_RANGE, 1.25F)),
                Pair.of(2, new EnterHiveTask()),
                //? if >=26.2 {
                /*Pair.of(3, new AnimalMakeLove(EntityTypes.BEE)),
                *///?} else {
                Pair.of(3, new AnimalMakeLove(EntityType.BEE)),
                //?}
                Pair.of(4, new BetterFollowTemptation(entity -> 0.6F)),
                Pair.of(5, new LocateHiveTask()),
                Pair.of(9, new FloatTask()),
                Pair.of(9, new GrowCropTask()),
                Pair.of(9, new RunOne<>(ImmutableList.of(Pair.of(new BeePathfinding(), 1))))
        );
    }

    private static Set<Pair<MemoryModuleType<?>, MemoryStatus>> idleConditions() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_ABSENT));
    }

    private static ImmutableList<BehaviorControl<? super Bee>> stingBehaviors() {
        return ImmutableList.<BehaviorControl<? super Bee>>of(
                StopAttackingIfTargetInvalid.create(),
                SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.0F),
                MeleeAttack.create(20),
                EraseMemoryIf.create(Predicate.not(Bee::isAngry), MemoryModuleType.ATTACK_TARGET)
        );
    }

    private static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Bee>>> pollinateBehaviors() {
        return ImmutableList.<Pair<Integer, ? extends BehaviorControl<? super Bee>>>of(
                Pair.of(0, BabyFollowAdult.create(ADULT_FOLLOW_RANGE, 1.25F)),
                Pair.of(0, new FindFlowerTask()),
                Pair.of(1, new PollinateFlowerTask()),
                Pair.of(2, EraseMemoryIf.create(Bee::hasNectar, ModMemoryTypes.BEE_FLOWER_POS)),
                Pair.of(3, EraseMemoryIf.create(BeeBrain::wantsToEnterHive, ModMemoryTypes.BEE_FLOWER_POS))
        );
    }

    private static Set<Pair<MemoryModuleType<?>, MemoryStatus>> pollinateConditions() {
        return ImmutableSet.of(
                Pair.of(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_ABSENT),
                Pair.of(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT),
                Pair.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT),
                Pair.of(ModMemoryTypes.BEE_WANTS_HIVE, MemoryStatus.VALUE_ABSENT),
                Pair.of(ModMemoryTypes.BEE_POLLINATING_COOLDOWN, MemoryStatus.VALUE_ABSENT)
        );
    }

    public static void updateActivity(Bee bee) {
        bee.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.CELEBRATE, Activity.IDLE));
    }

    public static void incrementMemory(Brain<?> brain, MemoryModuleType<Integer> type) {
        brain.setMemory(type, brain.getMemory(type).orElse(0) + 1);
    }
}
