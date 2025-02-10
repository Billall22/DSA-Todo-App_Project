module com.todoapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;

    opens com.todoapp to javafx.fxml, com.google.gson;
    exports com.todoapp;
    exports com.todoapp.controllers;
    opens com.todoapp.controllers to javafx.fxml, com.google.gson;
    exports com.todoapp.models;
    opens com.todoapp.models to com.google.gson;
}