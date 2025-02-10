package com.todoapp.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.todoapp.models.Task;

import java.lang.reflect.Type;

public class TaskSerializer implements JsonSerializer<Task> {
    @Override
    public JsonElement serialize(Task task, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", task.getId());
        jsonObject.addProperty("title", task.getTitle());
        jsonObject.addProperty("description", task.getDescription());
        jsonObject.addProperty("dueDate", task.getDueDate().toString());
        jsonObject.addProperty("priority", task.getPriority());
        jsonObject.addProperty("completed", task.isCompleted());
        jsonObject.addProperty("category", task.getCategory());
        jsonObject.addProperty("recurrence", task.getRecurrence());
        return jsonObject;
    }
}