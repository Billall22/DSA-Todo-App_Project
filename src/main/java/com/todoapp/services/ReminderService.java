package com.todoapp.services;

import com.todoapp.database.DatabaseHelper;
import com.todoapp.models.Task;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReminderService {
    private static final int CHECK_INTERVAL = 60; // Check every 60 seconds

    public static void startReminderService() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(CHECK_INTERVAL), event -> checkTasks()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private static void checkTasks() {
        List<Task> tasks = DatabaseHelper.getTasks();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Task task : tasks) {
            LocalDateTime taskTime = task.getDueDate().atTime(12, 0); // Assume Noon if no time
            if (!task.isCompleted() && taskTime.isBefore(now.plusHours(1)) && taskTime.isAfter(now)) {
                showNotification("Task Reminder", "Upcoming: " + task.getTitle() + " is due at " + taskTime.format(formatter));
            }
        }
    }

    private static void showNotification(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
            alert.setTitle(title);
            alert.show();
        });
    }
}
