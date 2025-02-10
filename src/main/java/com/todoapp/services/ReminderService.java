package com.todoapp.services;

import com.todoapp.database.DatabaseHelper;
import com.todoapp.models.Task;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class ReminderService {
    private static final int CHECK_INTERVAL = 3600; // Check every hour

    public static void startReminderService() // start the reminder service
    {
        checkTasks(); // Check tasks immediately when the app starts
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(CHECK_INTERVAL), event -> checkTasks()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private static void checkTasks() //remind user of incomplete tasks everytime the user opens the app and every hour nad in the notification it shows the tasks and there name and days left till their due date in a list based on their priority urgent then wor then personal
    {
        List<Task> tasks = DatabaseHelper.getTasks();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        List<Task> upcomingTasks = tasks.stream()
                .filter(task -> !task.isCompleted() && task.getDueDate().isAfter(LocalDate.now()))
                .sorted(Comparator.comparing(Task::getPriority))
                .toList();

        if (!upcomingTasks.isEmpty()) {
            StringBuilder message = new StringBuilder("Upcoming Tasks:\n");
            for (Task task : upcomingTasks) {
                long daysLeft = LocalDate.now().until(task.getDueDate()).getDays();
                message.append(task.getTitle())
                        .append(" - Due in ")
                        .append(daysLeft)
                        .append(" days\n");
            }
            showNotification("Task Reminder", message.toString());
        }
    }

    private static void showNotification(String title, String message) //show notification
    {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
            alert.setTitle(title);
            alert.show();
        });
    }
}