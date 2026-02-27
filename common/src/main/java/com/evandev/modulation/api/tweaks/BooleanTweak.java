package com.evandev.modulation.api.tweaks;

import com.evandev.modulation.api.AbstractTweak;

public class BooleanTweak extends AbstractTweak<Boolean> {
    public BooleanTweak(String id, Boolean defaultValue) {
        super(id, defaultValue);
    }
}