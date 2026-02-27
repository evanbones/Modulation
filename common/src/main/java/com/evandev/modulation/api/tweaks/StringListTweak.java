package com.evandev.modulation.api.tweaks;

import com.evandev.modulation.api.AbstractTweak;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class StringListTweak extends AbstractTweak<List<String>> {
    public StringListTweak(String id, List<String> defaultValue) {
        super(id, defaultValue);
    }

    @Override
    public void readFromJson(JsonObject json) {
        if (json.has(getId())) {
            JsonArray array = json.getAsJsonArray(getId());
            List<String> list = new ArrayList<>();
            for (JsonElement element : array) {
                list.add(element.getAsString());
            }
            setValue(list);
        }
    }

    @Override
    public void writeToJson(JsonObject json) {
        JsonArray array = new JsonArray();
        for (String item : getValue()) {
            array.add(item);
        }
        json.add(getId(), array);
    }
}