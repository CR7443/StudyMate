package edu.westga.comp2320.studymate;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("StudyOrganizer-gui.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 750, 230);
        stage.setTitle("Study Mate by: Caleb Rogers");
        stage.setScene(scene);
        stage.show();
    }
}
