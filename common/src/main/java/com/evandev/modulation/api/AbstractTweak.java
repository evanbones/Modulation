package com.evandev.modulation.api;

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
}