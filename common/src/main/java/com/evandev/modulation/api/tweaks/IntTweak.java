package com.evandev.modulation.api.tweaks;

import com.evandev.modulation.api.AbstractTweak;
import com.google.gson.JsonObject;

public class IntTweak extends AbstractTweak<Integer> {
    public IntTweak(String id, Integer defaultValue) {
        super(id, defaultValue);
    }

    @Override
    public void readFromJson(JsonObject json) {
        if (json.has(getId())) setValue(json.get(getId()).getAsInt());
    }

    @Override
    public void writeToJson(JsonObject json) {
        json.addProperty(getId(), getValue());
    }
}