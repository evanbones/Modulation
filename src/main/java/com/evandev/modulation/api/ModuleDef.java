package com.evandev.modulation.api;

import com.evandev.modulation.api.tweaks.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModuleDef {
    private final String id;
    private final List<AbstractTweak<?, ?>> tweaks = new ArrayList<>();
    private boolean loaded;

    private ModuleDef(String id) {
        this.id = id;
    }

    public static ModuleDef of(String id) {
        return new ModuleDef(id);
    }

    public String getId() {
        return id;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public List<AbstractTweak<?, ?>> getTweaks() {
        return Collections.unmodifiableList(tweaks);
    }

    public BooleanTweak bool(String id, boolean defaultValue) {
        return add(new BooleanTweak(id, defaultValue));
    }

    public IntTweak integer(String id, int defaultValue) {
        return add(new IntTweak(id, defaultValue));
    }

    public DoubleTweak decimal(String id, double defaultValue) {
        return add(new DoubleTweak(id, defaultValue));
    }

    public StringTweak string(String id, String defaultValue) {
        return add(new StringTweak(id, defaultValue));
    }

    public StringListTweak stringList(String id, List<String> defaultValue) {
        return add(new StringListTweak(id, defaultValue));
    }

    private <X extends AbstractTweak<?, ?>> X add(X tweak) {
        tweak.setOwner(this);
        tweaks.add(tweak);
        return tweak;
    }
}
