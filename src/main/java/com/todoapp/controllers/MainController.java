package com.todoapp.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.todoapp.database.DatabaseHelper;
import com.todoapp.models.Task;
import com.todoapp.utils.LocalDateDeserializer;
import com.todoapp.utils.LocalDateSerializer;
import com.todoapp.utils.TaskDeserializer;
import com.todoapp.utils.TaskSerializer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainController {
    private static MainController instance;
    @FXML
    private TextField taskTitle;
    @FXML
    private DatePicker dueDate;
    @FXML
    private ChoiceBox<String> priority;
    @FXML
    private ChoiceBox<String> recurrence;
    @FXML
    private ChoiceBox<String> category;
    @FXML
    private ProgressBar taskProgress;
    @FXML
    private List<Task> tasks;
    @FXML
    private ListView<Task> taskList;
    @FXML
    private Scene scene;
    @FXML
    private ToggleButton darkModeToggle;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Task.class, new TaskSerializer())
            .registerTypeAdapter(Task.class, new TaskDeserializer())
            .registerTypeAdapter(LocalDate.class, new LocalDateSerializer())
            .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
            .setPrettyPrinting()
            .create();

    public MainController() {
        instance = this;
    }

    public static void updateStatistics() {
        if (instance != null) {
            instance.updateStatisticsInstance();
        }
    }

    @FXML
    public void handleAddTask() // Add a new task
    {
        String title = taskTitle.getText();
        LocalDate date = dueDate.getValue();
        String priorityValue = priority.getValue();
        String categoryValue = category.getValue();

        if (title.isEmpty() || date == null || priorityValue == null || categoryValue == null) {
            showAlert("All fields must be filled!");
            return;
        }

        Task task = new Task(0, title, "", date, priorityValue, false, categoryValue, "None");
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

        Task selectedTask = taskList.getItems().get(index); // Ensure this is from taskList
        selectedTask.setTitle(taskTitle.getText());
        selectedTask.setDueDate(dueDate.getValue());
        selectedTask.setPriority(priority.getValue());
        selectedTask.setCategory(category.getValue());
        selectedTask.setRecurrence(recurrence.getValue());

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

        Task selectedTask = taskList.getItems().get(index);
        DatabaseHelper.deleteTask(selectedTask.getId());
        taskList.getItems().remove(index); // Ensure the task is removed from the UI
        refreshTasks();
    }

    @FXML
    public void handleImportTasks() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        fileChooser.setTitle("Select JSON File to Import");

        Path todoAppFolderPath = Paths.get(System.getProperty("user.home"), "Documents", "TodoApp");
        if (Files.exists(todoAppFolderPath)) {
            fileChooser.setInitialDirectory(todoAppFolderPath.toFile());
        }

        File selectedFile = fileChooser.showOpenDialog(scene.getWindow());
        if (selectedFile != null) {
            try {
                String json = new String(Files.readAllBytes(selectedFile.toPath()));
                if (json.trim().isEmpty()) {
                    showAlert("The selected file is empty.");
                    return;
                }

                List<Task> importedTasks = gson.fromJson(json, new TypeToken<List<Task>>() {
                }.getType());
                if (importedTasks == null) {
                    showAlert("No tasks found in the file.");
                    return;
                }

                List<Task> existingTasks = DatabaseHelper.getTasks();

                for (Task task : importedTasks) {
                    boolean isDuplicate = existingTasks.stream()
                            .anyMatch(existingTask -> existingTask.getTitle().equals(task.getTitle()) &&
                                    existingTask.getDueDate().equals(task.getDueDate()) &&
                                    existingTask.getPriority().equals(task.getPriority()) &&
                                    existingTask.getCategory().equals(task.getCategory()) &&
                                    existingTask.getRecurrence().equals(task.getRecurrence()) &&
                                    existingTask.isCompleted() == task.isCompleted());

                    if (!isDuplicate) {
                        DatabaseHelper.addTask(task);
                    }
                }
                refreshTasks();
                showAlert("Tasks imported successfully from " + selectedFile.getPath());
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Failed to import tasks.");
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                showAlert("The file contains invalid JSON.");
            }
        }
    }

    @FXML
    public void handleExportTasks() {
        try {
            Path todoAppFolderPath = Paths.get(System.getProperty("user.home"), "Documents", "TodoApp");
            if (!Files.exists(todoAppFolderPath)) {
                Files.createDirectories(todoAppFolderPath);
            }

            String defaultFileName = "tasks_" + LocalDate.now().toString();
            TextInputDialog dialog = new TextInputDialog(defaultFileName);
            dialog.setTitle("Export Tasks");
            dialog.setHeaderText("Enter the name for the JSON file");
            dialog.setContentText("File name:");

            String fileName = dialog.showAndWait().orElse(defaultFileName);
            if (!fileName.endsWith(".json")) {
                fileName += ".json";
            }

            Path filePath = todoAppFolderPath.resolve(fileName);
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                List<Task> tasksToExport = new ArrayList<>(taskList.getItems());
                gson.toJson(tasksToExport, writer);
                showAlert("Tasks exported successfully to " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to export tasks.");
        }
    }

    public void setScene(Scene scene)// set the scene
    {
        this.scene = scene;
    }

    private void updateStatisticsInstance() {
        int totalTasks = taskList.getItems().size();
        long completedTasks = taskList.getItems().stream().filter(Task::isCompleted).count();

        if (totalTasks > 0) {
            taskProgress.setProgress((double) completedTasks / totalTasks);
        } else {
            taskProgress.setProgress(0);
        }
    }


    @FXML
    public void handleMarkComplete() {
        int index = taskList.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            showAlert("Select a task to mark as completed!");
            return;
        }

        Task task = taskList.getItems().get(index);
        task.setCompleted(true);
        DatabaseHelper.updateTaskCompletionStatus(task.getId(), true);

        if (task.isRecurring()) {
            Task newTask = task.createNextRecurringTask();
            if (newTask != null) {
                DatabaseHelper.addTask(newTask);
            }
        }

        refreshTasks();
    }

    @FXML
    public void initialize() {
        taskList.setCellFactory(listView -> {
            CheckBoxListCell<Task> cell = new CheckBoxListCell<>(Task::completedProperty) {
                @Override
                public void updateItem(Task task, boolean empty) {
                    super.updateItem(task, empty);
                    if (task != null) {
                        setText(task.getTitle() + " - " + task.getDueDate());
                        task.completedProperty().addListener((observable, oldValue, newValue) -> {
                            if (newValue && !oldValue) {
                                DatabaseHelper.updateTaskCompletionStatus(task.getId(), newValue);
                                updateStatisticsInstance();
                                refreshTasks();
                            }
                        });
                    } else {
                        setText(null);
                    }
                }
            };

            // Drag and drop handlers (unchanged)
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
        category.getItems().addAll("Work", "Personal", "Urgent");
        refreshTasks();
    }

    public void refreshTasks() {
        List<Task> tasks = DatabaseHelper.getTasks();
        taskList.getItems().setAll(tasks);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.show();
    }

    private void handleDragAndDrop() {
        taskList.setOnDragOver(event -> {
            if (event.getGestureSource() != taskList && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        taskList.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasString()) {
                Task task = new Task(0, dragboard.getString(), "", LocalDate.now(), "Low", false, "Personal", "None");
                DatabaseHelper.addTask(task);
                refreshTasks();
                event.setDropCompleted(true);
            }
            event.consume();
        });
    }

    @FXML
    private void handledeleteall() {
        taskList.getItems().clear();
        DatabaseHelper.deleteAllTasks();
    }

    @FXML
    public void toggleDarkMode()// toggle dark mode
    {
        if (darkModeToggle != null && darkModeToggle.isSelected()) {
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/dark-mode.css")).toExternalForm());
        } else {
            scene.getStylesheets().clear();
        }
    }
}