package com.todoapp.models;

import com.todoapp.controllers.MainController;
import com.todoapp.database.DatabaseHelper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class Task {
    private int id;
    private final StringProperty title = new SimpleStringProperty();
    private String description;
    private LocalDate dueDate;
    private String priority;
    private final BooleanProperty completed = new SimpleBooleanProperty();
    private String category;
    private String recurrence;

    public Task(int id, String title, String description, LocalDate dueDate, String priority, boolean completed, String category, String recurrence) {
        this.id = id;
        this.title.set(title);
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.category = category;
        this.recurrence = recurrence;
        this.completed.set(completed);
        this.completed.addListener((observable, oldValue, newValue) -> {
            DatabaseHelper.updateTaskCompletionStatus(this.id, newValue);
            MainController.updateStatistics();
            if (newValue && isRecurring()) {
                LocalDate nextDueDate = getNextDueDate();
                if (nextDueDate != null) {
                    Task newTask = new Task(0, this.title.get(), this.description, nextDueDate, this.priority, false, this.category, this.recurrence);
                    DatabaseHelper.addTask(newTask);
                }
            }
        });
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
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate != null ? dueDate : LocalDate.now();
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }


    public Task createNextRecurringTask() {
        LocalDate nextDueDate = getNextDueDate();
        if (nextDueDate != null) {
            return new Task(0, this.title.get(), this.description, nextDueDate, this.priority, false, this.category, this.recurrence);
        }
        return null;
    }

    public LocalDate getNextDueDate() {
        switch (this.recurrence) {
            case "Daily":
                return this.dueDate.plusDays(1);
            case "Weekly":
                return this.dueDate.plusWeeks(1);
            case "Monthly":
                return this.dueDate.plusMonths(1);
            default:
                return null;
        }
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public BooleanProperty completedProperty() {
        return completed;
    }

    public boolean isCompleted() {
        return completed.get();
    }

    public void setCompleted(boolean completed) {
        this.completed.set(completed);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }
}