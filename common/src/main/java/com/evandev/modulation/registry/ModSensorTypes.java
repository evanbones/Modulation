package com.evandev.modulation.registry;

import com.evandev.modulation.mixin.minecraft.accessor.SensorTypeInvoker;
import com.evandev.modulation.modules.brainierbees.BeeBrain;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.sensing.TemptingSensor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModSensorTypes {

    private static final Map<String, SensorType<?>> TO_REGISTER = new LinkedHashMap<>();

    public static final SensorType<TemptingSensor> BEE_TEMPTATIONS = register("bee_temptations", () -> new TemptingSensor(BeeBrain.getTemptations()));

    private static <U extends Sensor<?>> SensorType<U> register(String id, Supplier<U> factory) {
        SensorType<U> type = SensorTypeInvoker.invokeInit(factory);
        TO_REGISTER.put(id, type);
        return type;
    }

    public static Map<String, SensorType<?>> all() {
        return Collections.unmodifiableMap(TO_REGISTER);
    }
}
