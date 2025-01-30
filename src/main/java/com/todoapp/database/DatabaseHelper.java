package com.todoapp.database;

import com.todoapp.models.Task;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String URL = "jdbc:sqlite:todo.db";

    public static void initDB() {
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

    public static void addTask(Task task) {
        String sql = "INSERT INTO tasks (title, description, dueDate, priority, completed, category, recurrence) VALUES (?, ?, ?, ?, ?, ?, ?)";
        executeUpdate(sql, task.getTitle(), task.getDescription(), task.getDueDate().toString(), task.getPriority(), task.isCompleted(), task.getCategory(), task.getRecurrence());
    }

    public static void updateTask(Task task) {
        String sql = "UPDATE tasks SET title = ?, dueDate = ?, priority = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDueDate().toString());
            pstmt.setString(3, task.getPriority());
            pstmt.setInt(4, task.getId());
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

    public static List<Task> getTasks() {
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
        } catch (SQLException e) { e.printStackTrace(); }
        return tasks;
    }
    private static void executeStatement(String sql) {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static void executeUpdate(String sql, Object... params) {
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) pstmt.setObject(i + 1, params[i]);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void markTaskCompleted(int id) {
        String sql = "UPDATE tasks SET completed = 1 WHERE id = ?";
        executeUpdate(sql, id);
    }
}
