package com.todoapp.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDate;

public class LocalDateSerializer implements JsonSerializer<LocalDate> {
    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) // Serialize LocalDate
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("day", src.getDayOfMonth());
        jsonObject.addProperty("month", src.getMonthValue());
        jsonObject.addProperty("year", src.getYear());
        return jsonObject;
    }
}