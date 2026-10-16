package com.evandev.modulation.api.tweaks;

import com.evandev.modulation.api.AbstractTweak;
import com.google.gson.JsonObject;

public class BooleanTweak extends AbstractTweak<Boolean, BooleanTweak> {
    private BooleanTweak[] requires = new BooleanTweak[0];

    public BooleanTweak(String id, Boolean defaultValue) {
        super(id, defaultValue);
    }

    public BooleanTweak requires(BooleanTweak... requires) {
        this.requires = requires;
        return this;
    }

    public boolean on() {
        if (!isAvailable() || !getValue()) return false;
        for (BooleanTweak parent : requires) {
            if (!parent.on()) return false;
        }
        return true;
    }

    @Override
    public void readFromJson(JsonObject json) {
        if (json.has(getId())) setValue(json.get(getId()).getAsBoolean());
    }

    @Override
    public void writeToJson(JsonObject json) {
        json.addProperty(getId(), getValue());
    }
}
