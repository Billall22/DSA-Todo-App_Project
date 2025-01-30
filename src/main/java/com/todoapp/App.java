package com.todoapp;

import com.todoapp.controllers.MainController;
import com.todoapp.database.DatabaseHelper;
import com.todoapp.services.ReminderService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage PrimraryStage) throws IOException {

        DatabaseHelper.initDB();
        ReminderService.startReminderService();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        MainController controller = loader.getController();
        controller.setScene(scene);

        PrimraryStage.setTitle("To-Do List App");
        PrimraryStage.setScene(scene);
        PrimraryStage.show();
}

    public static void main(String[] args) {
        launch();
    }
}