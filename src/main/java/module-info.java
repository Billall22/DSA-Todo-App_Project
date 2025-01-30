module com.todoapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;

    opens com.todoapp to javafx.fxml;
    opens com.todoapp.controllers to javafx.fxml;
    opens com.todoapp.models to com.google.gson;
    exports com.todoapp;
    exports com.todoapp.controllers;
}