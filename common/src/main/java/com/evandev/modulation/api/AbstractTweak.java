package com.evandev.modulation.api;

import com.google.gson.JsonObject;

public abstract class AbstractTweak<T> {
    private final String id;
    private final T defaultValue;
    private T value;

    public AbstractTweak(String id, T defaultValue) {
        this.id = id;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public String getId() {
        return id;
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