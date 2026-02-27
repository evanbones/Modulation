package com.evandev.modulation.api.tweaks;

import com.evandev.modulation.api.AbstractTweak;
import com.google.gson.JsonObject;

public class DoubleTweak extends AbstractTweak<Double> {
    public DoubleTweak(String id, Double defaultValue) {
        super(id, defaultValue);
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