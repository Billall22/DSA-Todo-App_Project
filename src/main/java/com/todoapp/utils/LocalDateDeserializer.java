package com.todoapp.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.time.LocalDate;

public class LocalDateDeserializer implements JsonDeserializer<LocalDate> {
    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) // Deserialize LocalDate
    {
        JsonObject jsonObject = json.getAsJsonObject();
        int day = jsonObject.get("day").getAsInt();
        int month = jsonObject.get("month").getAsInt();
        int year = jsonObject.get("year").getAsInt();
        return LocalDate.of(day, month, year);
    }
}
