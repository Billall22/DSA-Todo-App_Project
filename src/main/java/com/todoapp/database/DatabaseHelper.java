package com.todoapp.database;

import com.todoapp.models.Task;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String URL = "jdbc:sqlite:todo.db"; // Database URL

    public static void initDB() // Helper method to initialize the database
    {
        String sql = "CREATE TABLE IF NOT EXISTS tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "description TEXT, " +
                "dueDate TEXT, " +
                "priority TEXT, " +
                "completed BOOLEAN, " +
                "category TEXT, " +
                "recurrence TEXT)";

        executeStatement(sql);
    }

    public static void addTask(Task task) // Helper method to add a task to the database
    {
        String sql = "INSERT INTO tasks (title, description, dueDate, priority, completed, category, recurrence) VALUES (?, ?, ?, ?, ?, ?, ?)";
        executeUpdate(sql, task.getTitle(), task.getDescription(), task.getDueDate().toString(), task.getPriority(), task.isCompleted(), task.getCategory(), task.getRecurrence());
    }

    public static void updateTask(Task task) // Helper method to update a task in the database
    {
        String sql = "UPDATE tasks SET title = ?, dueDate = ?, priority = ?, completed =?, category =?,recurrence =? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDueDate().toString());
            pstmt.setString(3, task.getPriority());
            pstmt.setBoolean(4, task.isCompleted());
            pstmt.setString(5, task.getCategory());
            pstmt.setString(6, task.getRecurrence());
            pstmt.setInt(7, task.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Task> getTasks() // Helper method to get all tasks from the database
    {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tasks.add(new Task(rs.getInt("id"), rs.getString("title"), rs.getString("description"),
                        LocalDate.parse(rs.getString("dueDate")), rs.getString("priority"),
                        rs.getBoolean("completed"), rs.getString("category"), rs.getString("recurrence")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    private static void executeStatement(String sql) // Helper method to execute SQL statements
    {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void executeUpdate(String sql, Object... params) // Helper method to execute SQL updates
    {
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) pstmt.setObject(i + 1, params[i]);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void markTaskCompleted(int id)   // Helper method to mark a task as completed
    {
        String sql = "UPDATE tasks SET completed = 1 WHERE id = ?";
        executeUpdate(sql, id);
    }


    public static void updateTaskCompletionStatus(int taskId, boolean completed) {
        String sql = "UPDATE tasks SET completed = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, completed);
            pstmt.setInt(2, taskId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
