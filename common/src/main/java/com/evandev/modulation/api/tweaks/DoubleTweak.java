package com.evandev.modulation.api.tweaks;

import com.evandev.modulation.api.AbstractTweak;
import com.google.gson.JsonObject;

public class DoubleTweak extends AbstractTweak<Double, DoubleTweak> {
    private double min = -Double.MAX_VALUE;
    private double max = Double.MAX_VALUE;

    public DoubleTweak(String id, Double defaultValue) {
        super(id, defaultValue);
    }

    public DoubleTweak range(double min, double max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public Double get() {
        double value = getValue();
        if (Double.isNaN(value)) return getDefaultValue();
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public void readFromJson(JsonObject json) {
        if (json.has(getId())) setValue(json.get(getId()).getAsDouble());
    }

    @Override
    public void writeToJson(JsonObject json) {
        json.addProperty(getId(), getValue());
    }
}
