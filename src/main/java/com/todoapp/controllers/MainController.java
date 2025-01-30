package com.todoapp.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.todoapp.database.DatabaseHelper;
import com.todoapp.models.Task;
import com.todoapp.utils.LocalDateDeserializer;
import com.todoapp.utils.LocalDateSerializer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

public class MainController {
    @FXML private TextField taskTitle;
    @FXML private DatePicker dueDate;
    @FXML private ChoiceBox<String> priority;
    @FXML private List<Task> tasks;
    @FXML private ToggleButton darkModeToggle;
    @FXML private Scene scene;
    @FXML private ChoiceBox<String> recurrence;
    @FXML private ListView<Task> taskList;
    @FXML private ProgressBar taskProgress;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
            .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
            .create();

    @FXML
    public void handleAddTask() {
        String title = taskTitle.getText();
        LocalDate date = dueDate.getValue();
        String priorityValue = priority.getValue();

        if (title.isEmpty() || date == null || priorityValue == null) {
            showAlert("All fields must be filled!");
            return;
        }

        Task task = new Task(0, title, "", date, priorityValue, false, "General", "None");
        DatabaseHelper.addTask(task);
        refreshTasks();
    }
    @FXML
    public void handleEditTask() {
        int index = taskList.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            showAlert("Select a task to edit!");
            return;
        }

        Task selectedTask = tasks.get(index);
        selectedTask.setTitle(taskTitle.getText());
        selectedTask.setDueDate(dueDate.getValue());
        selectedTask.setPriority(priority.getValue());

        DatabaseHelper.updateTask(selectedTask);
        refreshTasks();
    }
    @FXML
    public void handleDeleteTask() {
        int index = taskList.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            showAlert("Select a task to delete!");
            return;
        }

        Task selectedTask = tasks.get(index);
        DatabaseHelper.deleteTask(selectedTask.getId());
        refreshTasks();
    }
    @FXML
    public void handleExportTasks() {
        try {
            Path documentsPath = Paths.get(System.getProperty("user.home"), "Documents", "tasks.json");
            try (FileWriter writer = new FileWriter(documentsPath.toFile())) {
                gson.toJson(tasks, writer);
                showAlert("Tasks exported successfully to " + documentsPath.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void handleImportTasks() {
        try {
            Path documentsPath = Paths.get(System.getProperty("user.home"), "Documents", "tasks.json");
            String json = new String(Files.readAllBytes(documentsPath));
            List<Task> importedTasks = gson.fromJson(json, new TypeToken<List<Task>>(){}.getType());
            for (Task task : importedTasks) {
                DatabaseHelper.addTask(task);
            }
            refreshTasks();
            showAlert("Tasks imported successfully from " + documentsPath.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void handleMarkComplete() {
        int index = taskList.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            showAlert("Select a task to mark as completed!");
            return;
        }

        Task task = tasks.get(index);
        DatabaseHelper.markTaskCompleted(task.getId());

        if (task.isRecurring()) {
            Task newTask = new Task(0, task.getTitle(), task.getDescription(), task.getNextDueDate(), task.getPriority(), false, task.getCategory(), task.getRecurrence());
            DatabaseHelper.addTask(newTask);
        }

        refreshTasks();
    }
    private void refreshTasks() {
        taskList.getItems().clear();
        tasks = DatabaseHelper.getTasks();
        for (Task task : tasks) {
            taskList.getItems().add(task);
        }
        updateStatistics();
    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.show();
    }
    public void initialize() {
        taskList.setCellFactory(lv -> {
            ListCell<Task> cell = new ListCell<Task>() {
                @Override
                protected void updateItem(Task task, boolean empty) {
                    super.updateItem(task, empty);
                    setText(empty ? null : task.getTitle() + " - " + task.getDueDate());
                }
            };

            cell.setOnDragDetected(event -> {
                if (cell.getItem() == null) return;
                Dragboard dragboard = cell.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(cell.getItem().getTitle());
                dragboard.setContent(content);
                event.consume();
            });

            cell.setOnDragOver(event -> {
                if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            cell.setOnDragDropped(event -> {
                Dragboard dragboard = event.getDragboard();
                if (dragboard.hasString()) {
                    int draggedIdx = taskList.getItems().indexOf(cell.getItem());
                    int droppedIdx = taskList.getSelectionModel().getSelectedIndex();

                    Task draggedTask = taskList.getItems().remove(draggedIdx);
                    taskList.getItems().add(droppedIdx, draggedTask);

                    event.setDropCompleted(true);
                    taskList.refresh();
                }
                event.consume();
            });

            return cell;
        });
        priority.getItems().addAll("Low", "Medium", "High");
        recurrence.getItems().addAll("None", "Daily", "Weekly", "Monthly");
        refreshTasks();
    }
    public void setScene(Scene scene) {
        this.scene = scene;
    }
    private void updateStatistics() {
        int totalTasks = tasks.size();
        long completedTasks = tasks.stream().filter(Task::isCompleted).count();

        if (totalTasks > 0) {
            taskProgress.setProgress((double) completedTasks / totalTasks);
        } else {
            taskProgress.setProgress(0);
        }
    }

    @FXML
    public void toggleDarkMode() {
        if (darkModeToggle != null && darkModeToggle.isSelected()) {
            scene.getStylesheets().add(getClass().getResource("/styles/dark-mode.css").toExternalForm());
        } else {
            scene.getStylesheets().clear();
        }
    }
}