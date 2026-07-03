package com.evandev.modulation.mixin.vanilla.accessor;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Display;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Display.class)
public interface DisplayAccessor {

    @Accessor("DATA_TRANSFORMATION_INTERPOLATION_DURATION_ID")
    static EntityDataAccessor<Integer> getInterpolationDurationId() {
        throw new UnsupportedOperationException();
    }

    @Accessor("DATA_TRANSFORMATION_INTERPOLATION_START_DELTA_TICKS_ID")
    static EntityDataAccessor<Integer> getInterpolationDelayId() {
        throw new UnsupportedOperationException();
    }

    @Accessor("DATA_TRANSLATION_ID")
    static EntityDataAccessor<Vector3f> getTranslationId() {
        throw new UnsupportedOperationException();
    }

    @Accessor("DATA_SCALE_ID")
    static EntityDataAccessor<Vector3f> getScaleId() {
        throw new UnsupportedOperationException();
    }

    @Accessor("DATA_LEFT_ROTATION_ID")
    static EntityDataAccessor<Quaternionf> getLeftRotationId() {
        throw new UnsupportedOperationException();
    }

    @Accessor("DATA_RIGHT_ROTATION_ID")
    static EntityDataAccessor<Quaternionf> getRightRotationId() {
        throw new UnsupportedOperationException();
    }
}