package com.evandev.modulation.modules.vanilla;

import com.evandev.modulation.api.AbstractModule;
import com.evandev.modulation.api.IModule;
import com.evandev.modulation.api.tweaks.BooleanTweak;
import com.evandev.modulation.api.tweaks.DoubleTweak;
import com.google.auto.service.AutoService;

@AutoService(IModule.class)
public class ExtendedCloudsModule extends AbstractModule {

    private static final double MIN_MULTIPLIER = 0.1;
    private static final double MAX_MULTIPLIER = 16.0;

    private final BooleanTweak enableExtendedClouds = tweak(new BooleanTweak("enable_extended_clouds", true));
    private final DoubleTweak cloudDistanceMultiplier = tweak(new DoubleTweak("cloud_distance_multiplier", 4.0));
    private final BooleanTweak extendFrustum = tweak(new BooleanTweak("extend_frustum", true));
    private final BooleanTweak asyncCloudMeshing = tweak(new BooleanTweak("async_cloud_meshing", true));

    public ExtendedCloudsModule() {
        super("extended_clouds");
    }

    public boolean isExtendedCloudsEnabled() {
        return enableExtendedClouds.getValue();
    }

    public boolean isExtendFrustumEnabled() {
        return enableExtendedClouds.getValue() && extendFrustum.getValue();
    }

    public boolean isAsyncCloudMeshingEnabled() {
        return enableExtendedClouds.getValue() && asyncCloudMeshing.getValue();
    }

    public double getCloudDistanceMultiplier() {
        double value = cloudDistanceMultiplier.getValue();
        if (Double.isNaN(value)) {
            return 1.0;
        }
        return Math.max(MIN_MULTIPLIER, Math.min(MAX_MULTIPLIER, value));
    }
}
