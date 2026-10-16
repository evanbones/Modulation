package com.evandev.modulation.api;

import com.evandev.modulation.platform.Services;
import com.google.gson.JsonObject;

import java.util.function.Consumer;

public abstract class AbstractTweak<T, SELF extends AbstractTweak<T, SELF>> {
    private final String id;
    private final T defaultValue;
    private T value;
    private String group;
    private String[] conflicts = new String[0];
    private ModuleDef owner;
    private Consumer<T> applyHook;

    public AbstractTweak(String id, T defaultValue) {
        this.id = id;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    @SuppressWarnings("unchecked")
    protected SELF self() {
        return (SELF) this;
    }

    public String getId() {
        return id;
    }

    public String getGroup() {
        return group;
    }

    public SELF group(String group) {
        this.group = group;
        return self();
    }

    public SELF conflicts(String... conflicts) {
        this.conflicts = conflicts;
        return self();
    }

    public SELF onApply(Consumer<T> applyHook) {
        this.applyHook = applyHook;
        return self();
    }

    public ModuleDef getOwner() {
        return owner;
    }

    public void setOwner(ModuleDef owner) {
        this.owner = owner;
    }

    public String getBlockingMod() {
        for (String modId : conflicts) {
            if (Services.PLATFORM.isModLoaded(modId)) {
                return modId;
            }
        }
        return null;
    }

    public boolean isAvailable() {
        return (owner == null || owner.isLoaded()) && getBlockingMod() == null;
    }

    public T get() {
        return value;
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
        if (applyHook != null) {
            applyHook.accept(getValue());
        }
    }

    public abstract void readFromJson(JsonObject json);

    public abstract void writeToJson(JsonObject json);
}
