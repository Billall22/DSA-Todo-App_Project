package com.todoapp.utils;

import com.google.gson.*;
import com.todoapp.models.Task;

import java.lang.reflect.Type;
import java.time.LocalDate;

public class TaskDeserializer implements JsonDeserializer<Task> {
    @Override
    public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        int id = jsonObject.get("id").getAsInt();
        String title = jsonObject.get("title").getAsString();
        String description = jsonObject.get("description").getAsString();
        LocalDate dueDate = LocalDate.parse(jsonObject.get("dueDate").getAsString());
        String priority = jsonObject.get("priority").getAsString();
        boolean completed = jsonObject.get("completed").getAsBoolean();
        String category = jsonObject.get("category").getAsString();
        String recurrence = jsonObject.get("recurrence").getAsString();

        return new Task(id, title, description, dueDate, priority, completed, category, recurrence);
    }
}