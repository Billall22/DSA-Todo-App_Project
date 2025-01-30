package com.todoapp.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.time.LocalDate;

public class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject jsonObject = json.getAsJsonObject();
        int year = jsonObject.get("year").getAsInt();
        int month = jsonObject.get("month").getAsInt();
        int day = jsonObject.get("day").getAsInt();
        return LocalDate.of(year, month, day);
    }
}
