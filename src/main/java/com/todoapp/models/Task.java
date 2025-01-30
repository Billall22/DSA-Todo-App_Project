package com.todoapp.models;

import java.time.LocalDate;

public class Task {
    private int id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private String priority;
    private boolean completed;
    private String category;
    private String recurrence;

    public Task(int id, String title, String description, LocalDate dueDate, String priority, boolean completed, String category, String recurrence) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = completed;
        this.category = category;
        this.recurrence = recurrence;
    }
    public LocalDate getDueDate() {
        return dueDate != null ? dueDate : LocalDate.now();
    }
    public LocalDate getNextDueDate() {
        return switch (recurrence) {
            case "Daily" -> dueDate.plusDays(1);
            case "Weekly" -> dueDate.plusWeeks(1);
            case "Monthly" -> dueDate.plusMonths(1);
            default -> null;
        };
    }

    public boolean isRecurring() {
        return !recurrence.equals("None");
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
    public boolean isCompleted() {
        return completed;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    public String getCategory() {
        return category;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getRecurrence() {
        return recurrence;
    }
    @Override
    public String toString() {
        return "Task [category=" + category + ", completed=" + completed + ", description=" + description + ", dueDate="
                + dueDate + ", id=" + id + ", priority=" + priority + ", title=" + title + "]";
    }
}
