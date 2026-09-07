package com.evandev.modulation.api;

import com.evandev.modulation.platform.Services;
import com.google.gson.JsonObject;

public abstract class AbstractTweak<T> {
    private final String id;
    private final T defaultValue;
    private T value;
    private String group;
    private String[] conflicts = new String[0];

    public AbstractTweak(String id, T defaultValue) {
        this.id = id;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public String getId() {
        return id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setConflicts(String... conflicts) {
        this.conflicts = conflicts;
    }

    public String getBlockingMod() {
        for (String modId : conflicts) {
            if (Services.PLATFORM.isModLoaded(modId)) {
                return modId;
            }
        }
        return null;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public void onApply() {
    }

    public abstract void readFromJson(JsonObject json);

    public abstract void writeToJson(JsonObject json);
}