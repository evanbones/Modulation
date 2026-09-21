package com.evandev.modulation.api.tweaks;

import com.evandev.modulation.api.AbstractTweak;
import com.google.gson.JsonObject;

public class IntTweak extends AbstractTweak<Integer, IntTweak> {
    private int min = Integer.MIN_VALUE;
    private int max = Integer.MAX_VALUE;

    public IntTweak(String id, Integer defaultValue) {
        super(id, defaultValue);
    }

    public IntTweak range(int min, int max) {
        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public Integer get() {
        return Math.max(min, Math.min(max, getValue()));
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
